package dev.shadowsoffire.apotheosis.adventure;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import dev.shadowsoffire.apotheosis.Apoth;
import dev.shadowsoffire.apotheosis.adventure.Adventure.Items;
import dev.shadowsoffire.apotheosis.adventure.affix.AffixHelper;
import dev.shadowsoffire.apotheosis.adventure.affix.AffixInstance;
import dev.shadowsoffire.apotheosis.adventure.affix.effect.TelepathicAffix;
import dev.shadowsoffire.apotheosis.adventure.affix.socket.gem.GemRegistry;
import dev.shadowsoffire.apotheosis.adventure.commands.*;
import dev.shadowsoffire.apotheosis.adventure.compat.GameStagesCompat.IStaged;
import dev.shadowsoffire.apotheosis.adventure.loot.LootCategory;
import dev.shadowsoffire.apotheosis.adventure.loot.LootController;
import dev.shadowsoffire.apotheosis.util.Events;
import dev.shadowsoffire.apotheosis.util.ItemAccess;
import dev.shadowsoffire.attributeslib.api.ItemAttributeModifierEvent;
import dev.shadowsoffire.placebo.events.AnvilLandCallback;
import dev.shadowsoffire.placebo.events.GetEnchantmentLevelEvent;
import dev.shadowsoffire.placebo.events.ItemUseEvent;
import dev.shadowsoffire.placebo.reload.WeightedDynamicRegistry.IDimensional;
import io.github.fabricators_of_create.porting_lib.entity.events.EntityEvents;
import io.github.fabricators_of_create.porting_lib.entity.events.living.LivingEntityDamageEvents;
import io.github.fabricators_of_create.porting_lib.entity.events.living.LivingEntityEvents;
import io.github.fabricators_of_create.porting_lib.entity.events.living.LivingEntityLootEvents;
import io.github.fabricators_of_create.porting_lib.entity.events.player.PlayerEvents;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.entity.FakePlayer;
import net.fabricmc.fabric.api.event.player.PlayerBlockBreakEvents;
import net.fabricmc.fabric.api.util.TriState;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.AttributeModifier.Operation;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.AABB;
import net.spell_engine.api.spell.SpellEvents;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

public class AdventureEvents {

    public static void init(){
        cmds();
        affixModifiers();
        preventBossSuffocate();
        impact();
        onDamage();
        onItemUse();
        shieldBlock();
        blockBreak();
        dropsHigh();
        drops();
        deathMark();
        speed();
        onBreak();
        special();
        gemSmashing();
        enchLevels();
        update();
        if (FabricLoader.getInstance().isModLoaded("spell_engine")) onSpellCast();
    }

    public static void cmds() {
        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> {
            LiteralArgumentBuilder<CommandSourceStack> root = Commands.literal("zenith");
            RarityCommand.register(root);
            CategoryCheckCommand.register(root);
            LootifyCommand.register(root);
            ModifierCommand.register(root);
            GemCommand.register(root);
            SocketCommand.register(root);
            BossCommand.register(root);
            AddGemCommand.register(root);
            dispatcher.register(root);
        });
    }

    private static final UUID HEAVY_WEAPON_AS = UUID.fromString("f8c3de3d-1fea-4d7c-a8b0-29f63c4c3454");

    public static void affixModifiers() {
        ItemAttributeModifierEvent.GATHER_TOOLTIPS.register(e -> {
            ItemStack stack = e.stack;
            if (stack.hasTag()) {
                var affixes = AffixHelper.getAffixes(stack);
                affixes.forEach((afx, inst) -> inst.addModifiers(e.slot, e::addModifier));
                if (!affixes.isEmpty() && LootCategory.forItem(stack) == LootCategory.HEAVY_WEAPON && e.slot == EquipmentSlot.MAINHAND) {
                    double amt = -0.15 - 0.10 * affixes.values().stream().findAny().get().rarity().get().ordinal();
                    AttributeModifier baseAS = e.originalModifiers.get(Attributes.ATTACK_SPEED).stream().filter(a -> ItemAccess.getBaseAS() == a.getId()).findFirst().orElse(null);

                    if (baseAS != null) {
                        // Try to not reduce attack speed below 0.4 if possible.
                        double value = 4 + baseAS.getAmount();
                        double clampedAmt = 0.4F / value - 1;
                        amt = Math.max(amt, clampedAmt);
                        if (amt >= 0) return;
                    }
                    e.addModifier(Attributes.ATTACK_SPEED, new AttributeModifier(HEAVY_WEAPON_AS, "Heavy Weapon AS", amt, Operation.MULTIPLY_TOTAL));
                }
            }
        });
    }

    public static void preventBossSuffocate() {
        LivingEntityDamageEvents.HURT.register(e -> {
            if (e.damageSource.is(DamageTypes.IN_WALL) && e.damaged.getCustomData().contains("apoth.boss")) {
                e.setCanceled(true);
            }
        });
    }

    /**
     * This event handler allows affixes to react to arrows being fired to trigger additional actions.
     * Arrows marked as "zenith.generated" will not trigger the affix hook, so affixes can fire arrows without recursion.
     */

    public static void fireArrow(AbstractArrow arrow) {
        if (!arrow.getCustomData().getBoolean("apoth.generated")) {
            Entity shooter = arrow.getOwner();
            if (shooter instanceof LivingEntity living) {
                ItemStack bow = living.getUseItem();
                if (bow.isEmpty()) {
                    bow = living.getMainHandItem();
                    if (bow.isEmpty() || !LootCategory.forItem(bow).isRanged()) {
                        bow = living.getOffhandItem();
                    }
                }
                if (bow.isEmpty()) return;

                var affixes = AffixHelper.getAffixes(bow);
                affixes.values().forEach(a -> {
                    a.onArrowFired(living, arrow);
                });
                AffixHelper.copyFrom(bow, arrow);
            }
        }
    }

    /**
     * This event handler allows affixes to react to arrows hitting something.
     */
    public static void impact() {
        EntityEvents.PROJECTILE_IMPACT.register((projectile, hitResult) -> {
            if (projectile instanceof AbstractArrow arrow) {
                var affixes = AffixHelper.getAffixes(arrow);
                affixes.values().forEach(inst -> inst.onArrowImpact(arrow, hitResult, hitResult.getType()));
            }
            return false;
        });
    }

    public static void onDamage() {
        LivingEntityDamageEvents.HURT.register(e -> {
            Adventure.Affixes.MAGICAL.getOptional().ifPresent(afx -> afx.onHurt(e));
            DamageSource src = e.damageSource;
            LivingEntity ent = e.damaged;
            float amount = e.damageAmount;
            for (ItemStack s : ent.getAllSlots()) {
                var affixes = AffixHelper.getAffixes(s);
                for (AffixInstance inst : affixes.values()) {
                    amount = inst.onHurt(src, ent, amount);
                }
            }
            e.damageAmount = amount;
        });
    }

    public static void onItemUse() {
        ItemUseEvent.ItemUse.ITEM_USE_EVENT.register(event -> {
            ItemStack s = event.stack;
            var affixes = AffixHelper.getAffixes(s);
            for (AffixInstance inst : affixes.values()) {
                InteractionResult type = inst.onItemUse(event.ctx);
                if (type != null) {
                    event.cancellationResult = type;
                    return true;
                }
            }
            return false;
        });
    }

    public static void onSpellCast() {
        SpellEvents.PROJECTILE_SHOOT.register(projectileLaunchEvent -> {
            var affixes = AffixHelper.getAffixes(projectileLaunchEvent.caster().getMainHandItem());
            affixes.values().forEach(inst -> inst.onCast(projectileLaunchEvent));
        });
    }

    public static void shieldBlock() {
        EntityEvents.SHIELD_BLOCK.register(e -> {
            ItemStack stack = e.blocker.getUseItem();
            var affixes = AffixHelper.getAffixes(stack);
            float blocked = e.damageBlocked;
            for (AffixInstance inst : affixes.values()) {
                blocked = inst.onShieldBlock(e.blocker, e.source, blocked);
            }
            if (blocked != e.damageBlocked) e.damageBlocked = blocked;
        });
    }

    public static void blockBreak() {
        PlayerBlockBreakEvents.AFTER.register((world, player, pos, state, blockEntity) -> {
            ItemStack stack = player.getMainHandItem();
            var affixes = AffixHelper.getAffixes(stack);
            for (AffixInstance inst : affixes.values()) {
                inst.onBlockBreak(player, world, pos, state);
            }
        });
    }

    public static void dropsHigh() {
        LivingEntityLootEvents.DROPS.register((target, source, drops, lootingLevel, recentlyHit) -> {
            if (source.getEntity() instanceof ServerPlayer p && target instanceof Monster && drops != null) {
                if (p instanceof FakePlayer) return false;
                float chance = AdventureConfig.gemDropChance + (target.getCustomData().contains("apoth.boss") ? AdventureConfig.gemBossBonus : 0);
                if (p.getRandom().nextFloat() <= chance) {
                    Entity ent = target;
                    drops.add(new ItemEntity(ent.level(), ent.getX(), ent.getY(), ent.getZ(), GemRegistry.createRandomGemStack(p.getRandom(), (ServerLevel) p.level(), p.getLuck(), IDimensional.matches(p.level()), IStaged.matches(p)), 0, 0, 0));
                }
            }
            return false;
        });
    }

    public static void drops() {
        LivingEntityLootEvents.DROPS.register((target, source, drops, lootingLevel, recentlyHit) -> {
            if (drops == null) return false;
            Adventure.Affixes.FESTIVE.getOptional().ifPresent(afx -> afx.drops(target, source, drops));
            TelepathicAffix.drops(source, drops);
            Adventure.Affixes.FESTIVE.getOptional().ifPresent(afx -> afx.removeMarker(drops));
            return false;
        });
    }

    public static void deathMark() {
        Events.OnEntityDeath.LIVING_DEATH.register((entity, source) -> {
            Adventure.Affixes.FESTIVE.getOptional().ifPresent(afx -> afx.markEquipment(entity, source));
        });
    }

    public static void speed() {
        PlayerEvents.BREAK_SPEED.register((player, state, pos, speed) -> {
            AtomicReference<Float> finalSpeed = new AtomicReference<>(speed);
            Adventure.Affixes.OMNETIC.getOptional().ifPresent(afx -> finalSpeed.set(afx.speed(player, state, pos, speed)));
            return finalSpeed.get();
        });

    }

    public static void onBreak() {
        PlayerBlockBreakEvents.AFTER.register((world, player, pos, state, blockEntity) -> {
            Adventure.Affixes.RADIAL.getOptional().ifPresent(afx -> afx.onBreak(world, player, pos, state, blockEntity));
        });
    }

    public static void special() {
        LivingEntityEvents.NATURAL_SPAWN.register((mob, x, y, z, level, spawner, type) -> {
            if (level.getRandom().nextFloat() <= AdventureConfig.randomAffixItem && mob instanceof Monster) {
                Player player = level.getNearestPlayer(x, y, z, -1, false);
                if (player == null) return TriState.DEFAULT;
                ItemStack affixItem = LootController.createRandomLootItem(level.getRandom(), null, player, (ServerLevel) mob.level());
                if (affixItem.isEmpty()) return TriState.DEFAULT;
                affixItem.getOrCreateTag().putBoolean("apoth_rspawn", true);
                LootCategory cat = LootCategory.forItem(affixItem);
                EquipmentSlot slot = cat.getSlots()[0];
                mob.setItemSlot(slot, affixItem);
                mob.setGuaranteedDrop(slot);
            }
            return TriState.DEFAULT;
        });
    }

    public static void gemSmashing() {
        AnvilLandCallback.EVENT.register((level, pos, newState, oldState, entity) -> {
            List<ItemEntity> items = level.getEntitiesOfClass(ItemEntity.class, new AABB(pos, pos.offset(1, 1, 1)));
            for (ItemEntity ent : items) {
                ItemStack stack = ent.getItem();
                if (stack.is(Items.GEM)) {
                    ent.setItem(new ItemStack(Items.GEM_DUST, stack.getCount()));
                }
            }
            return false;
        });
    }

    /**
     * {@link AffixHelper#getAffixesImpl} can cause infinite loops when doing validation that ends up depending on the enchantments of an item.<br>
     * We use this to disable enchantment level boosting when recurring (it shouldn't be relevant for these cases anyway).
     */
    private static ThreadLocal<AtomicBoolean> reentrantLock = ThreadLocal.withInitial(() -> new AtomicBoolean(false));

    public static void enchLevels() {

        GetEnchantmentLevelEvent.GET_ENCHANTMENT_LEVEL.register(((enchantments, stack) -> {
            boolean isReentrant = reentrantLock.get().getAndSet(true);
            if (isReentrant) return enchantments;
            if (stack.is(Apoth.Tags.ENCHANT_LEVEL_MODIFIER_BLACKLIST)) return enchantments;
            AffixHelper.streamAffixes(stack).forEach(inst -> inst.getEnchantmentLevels(enchantments));
            reentrantLock.get().set(false);
            return enchantments;
        }));
    }

    @SuppressWarnings("deprecation")
    public static void update() {
        LivingEntityEvents.TICK.register(entity -> {
            if (entity.getCustomData().contains("apoth.burns_in_sun")) {
                // Copy of Mob#isSunBurnTick()
                if (entity.level().isDay() && !entity.level().isClientSide) {
                    float f = entity.getLightLevelDependentMagicValue();
                    BlockPos blockpos = BlockPos.containing(entity.getX(), entity.getEyeY(), entity.getZ());
                    boolean flag = entity.isInWaterRainOrBubble() || entity.isInPowderSnow || entity.wasInPowderSnow;
                    if (f > 0.5F && entity.getRandom().nextFloat() * 30.0F < (f - 0.4F) * 2.0F && !flag && entity.level().canSeeSky(blockpos)) {
                        entity.setSecondsOnFire(8);
                    }
                }
            }
        });
    }

}
