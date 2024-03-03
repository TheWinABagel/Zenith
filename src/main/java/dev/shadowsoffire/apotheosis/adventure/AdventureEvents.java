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
import dev.shadowsoffire.attributeslib.api.ItemAttributeModifierEvent;
import dev.shadowsoffire.placebo.events.AnvilLandCallback;
import dev.shadowsoffire.placebo.events.GetEnchantmentLevelEvent;
import dev.shadowsoffire.placebo.events.ItemUseEvent;
import dev.shadowsoffire.placebo.reload.WeightedDynamicRegistry.IDimensional;
import io.github.fabricators_of_create.porting_lib.entity.events.EntityEvents;
import io.github.fabricators_of_create.porting_lib.entity.events.LivingEntityEvents;
import io.github.fabricators_of_create.porting_lib.entity.events.PlayerEvents;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.entity.FakePlayer;
import net.fabricmc.fabric.api.event.player.PlayerBlockBreakEvents;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.HitResult;
import net.spell_engine.api.spell.SpellEvents;

import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

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

    public static void affixModifiers() {
        ItemAttributeModifierEvent.GATHER_TOOLTIPS.register(e -> {
            ItemStack stack = e.stack;
            if (stack.hasTag()) {
                var affixes = AffixHelper.getAffixes(stack);
                affixes.forEach((afx, inst) -> inst.addModifiers(e.slot, e::addModifier));
            }
        });
    }

    public static void preventBossSuffocate() {
        LivingEntityEvents.HURT.register((source, damaged, amount) -> {
            if (source.is(DamageTypes.IN_WALL) && damaged.getCustomData().contains("apoth.boss")) {
                return 0f;
            }
            return amount;
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
                AffixHelper.streamAffixes(bow).forEach(a -> {
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
        EntityEvents.PROJECTILE_IMPACT.register((event) -> {
            if (event.getProjectile() instanceof AbstractArrow arrow) {
                var affixes = AffixHelper.getAffixes(arrow);
                HitResult hitResult = event.getRayTraceResult();
                affixes.values().forEach(inst -> inst.onArrowImpact(arrow, hitResult, hitResult.getType()));
            }
        });
    }

    public static void onDamage() {
        LivingEntityEvents.HURT.register((source, damaged, amount) -> {
            float finalAmount = amount;
            Adventure.Affixes.MAGICAL.getOptional().ifPresent(afx -> afx.onHurt(source, damaged, finalAmount));
            amount = finalAmount;
            for (ItemStack s : damaged.getAllSlots()) {
                var affixes = AffixHelper.getAffixes(s);
                for (AffixInstance inst : affixes.values()) {
                    amount = inst.onHurt(source, damaged, amount);
                }
            }

            return amount;
        });
    }

    public static void onItemUse() {
        ItemUseEvent.ItemUse.ITEM_USE_EVENT.register(event -> {
            ItemStack s = event.stack;
            AtomicBoolean result = new AtomicBoolean(false);
            AffixHelper.streamAffixes(s).forEach(inst -> {
                InteractionResult type = inst.onItemUse(event.ctx);
                if (type != null) {
                    event.cancellationResult = type;
                    result.set(true);
                }
            });
            return result.get();
        });
    }

    public static void onSpellCast() {
        SpellEvents.PROJECTILE_SHOOT.register(projectileLaunchEvent -> {
            var affixes = AffixHelper.getAffixes(projectileLaunchEvent.caster().getMainHandItem());
            affixes.values().forEach(inst -> inst.onCast(projectileLaunchEvent));
        });
    }

    public static void shieldBlock() {
        //todo Shield block event was removed from porting lib... reimplement?
//        EntityEvents.SHIELD_BLOCK.register(e -> {
//            ItemStack stack = e.blocker.getUseItem();
//            var affixes = AffixHelper.getAffixes(stack);
//            float blocked = e.damageBlocked;
//            for (AffixInstance inst : affixes.values()) {
//                blocked = inst.onShieldBlock(e.blocker, e.source, blocked);
//            }
//            if (blocked != e.damageBlocked) e.damageBlocked = blocked;
//        });
    }

    public static void blockBreak() {
        PlayerBlockBreakEvents.AFTER.register((world, player, pos, state, blockEntity) -> {
            ItemStack stack = player.getMainHandItem();
            AffixHelper.streamAffixes(stack).forEach(inst -> {
                inst.onBlockBreak(player, world, pos, state);
            });
        });
    }

    public static void dropsHigh() {
        LivingEntityEvents.DROPS.register((target, source, drops, lootingLevel, recentlyHit) -> {
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
        LivingEntityEvents.DROPS.register((target, source, drops, lootingLevel, recentlyHit) -> {
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
        PlayerEvents.BREAK_SPEED.register(e -> {
            Adventure.Affixes.OMNETIC.getOptional().ifPresent(afx -> afx.speed(e));
        });

    }

    public static void onBreak() {
        PlayerBlockBreakEvents.AFTER.register((world, player, pos, state, blockEntity) -> {
            Adventure.Affixes.RADIAL.getOptional().ifPresent(afx -> afx.onBreak(world, player, pos, state, blockEntity));
        });
    }

    public static void special() {
        LivingEntityEvents.CHECK_SPAWN.register((mob, level, x, y, z, spawner, type) -> {
            if (level.getRandom().nextFloat() <= AdventureConfig.randomAffixItem && mob instanceof Monster) {
                Player player = level.getNearestPlayer(x, y, z, -1, false);
                if (player == null) return false;
                ItemStack affixItem = LootController.createRandomLootItem(level.getRandom(), null, player, (ServerLevel) mob.level());
                if (affixItem.isEmpty()) return false;
                affixItem.getOrCreateTag().putBoolean("zenith_rspawn", true);
                LootCategory cat = LootCategory.forItem(affixItem);
                EquipmentSlot slot = cat.getSlots()[0];
                mob.setItemSlot(slot, affixItem);
                mob.setGuaranteedDrop(slot);
            }
            return false;
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
