package dev.shadowsoffire.apotheosis.adventure;

import com.google.common.base.Predicates;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import dev.shadowsoffire.apotheosis.Apoth;
import dev.shadowsoffire.apotheosis.adventure.Adventure.Items;
import dev.shadowsoffire.apotheosis.adventure.affix.AffixHelper;
import dev.shadowsoffire.apotheosis.adventure.affix.AffixInstance;
import dev.shadowsoffire.apotheosis.adventure.affix.effect.TelepathicAffix;
import dev.shadowsoffire.apotheosis.adventure.commands.*;
import dev.shadowsoffire.apotheosis.adventure.compat.GameStagesCompat.IStaged;
import dev.shadowsoffire.apotheosis.adventure.loot.LootCategory;
import dev.shadowsoffire.apotheosis.adventure.loot.LootController;
import dev.shadowsoffire.apotheosis.adventure.socket.SocketHelper;
import dev.shadowsoffire.apotheosis.adventure.socket.gem.GemRegistry;
import dev.shadowsoffire.apotheosis.cca.ZenithComponents;
import dev.shadowsoffire.apotheosis.util.Events;
import dev.shadowsoffire.apotheosis.util.ZenithModCompat;
import dev.shadowsoffire.attributeslib.api.events.LivingHurtEvent;
import dev.shadowsoffire.placebo.events.AnvilLandCallback;
import dev.shadowsoffire.placebo.events.GetEnchantmentLevelEvent;
import dev.shadowsoffire.placebo.events.ItemUseEvent;
import dev.shadowsoffire.placebo.reload.WeightedDynamicRegistry.IDimensional;
import io.github.fabricators_of_create.porting_lib.entity.events.EntityEvents;
import io.github.fabricators_of_create.porting_lib.entity.events.LivingEntityEvents;
import io.github.fabricators_of_create.porting_lib.entity.events.PlayerEvents;
import io.github.fabricators_of_create.porting_lib.entity.events.ShieldBlockEvent;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.entity.FakePlayer;
import net.fabricmc.fabric.api.event.player.PlayerBlockBreakEvents;
import net.fabricmc.fabric.api.item.v1.ModifyItemAttributeModifiersCallback;
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
        ZenithModCompat.Adventure.spellEngineCast();
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
            dispatcher.register(root);
            AffixCommand.register(root);
        });
    }

    public static void affixModifiers() {
        ModifyItemAttributeModifiersCallback.EVENT.register((stack, slot, attributeModifiers) -> {
            if (stack.hasTag()) {
                SocketHelper.getGems(stack).addModifiers(LootCategory.forItem(stack), slot, attributeModifiers::put);

                var affixes = AffixHelper.getAffixes(stack);
                affixes.forEach((afx, inst) -> inst.addModifiers(slot, attributeModifiers::put));
            }
        });
    }

    public static void preventBossSuffocate() {
        LivingHurtEvent.EVENT.register((source, damaged, amount) -> {
            if (damaged.getCustomData().contains("apoth.boss")) {
                ZenithComponents.BOSS_DATA.get(damaged).setIsBoss(damaged.getCustomData().getBoolean("apoth.boss"));
                damaged.getCustomData().remove("apoth.boss");
            }
            if (source.is(DamageTypes.IN_WALL) && ZenithComponents.BOSS_DATA.get(damaged).getIsBoss()) {
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
        if (!ZenithComponents.GENERATED_ARROW.get(arrow).getValue()) {
            if (arrow.getOwner() instanceof LivingEntity user) {
                ItemStack bow = user.getUseItem();
                if (bow.isEmpty()) {
                    bow = user.getMainHandItem();
                    if (bow.isEmpty() || !LootCategory.forItem(bow).isRanged()) {
                        bow = user.getOffhandItem();
                    }
                }
                if (bow.isEmpty()) return;
                SocketHelper.getGems(bow).onArrowFired(user, arrow);
                AffixHelper.streamAffixes(bow).forEach(a -> {
                    a.onArrowFired(user, arrow);
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
                SocketHelper.getGemInstances(arrow).forEach(inst -> inst.onArrowImpact(arrow, event.getRayTraceResult()));
                var affixes = AffixHelper.getAffixes(arrow);
                HitResult hitResult = event.getRayTraceResult();
                affixes.values().forEach(inst -> inst.onArrowImpact(arrow, hitResult, hitResult.getType()));
            }
        });
    }

    public static void onDamage() {
        LivingHurtEvent.EVENT.register((source, damaged, amount) -> {
            float finalAmount = amount;
            Adventure.Affixes.MAGICAL.getOptional().ifPresent(afx -> afx.onHurt(source, damaged, finalAmount));
            amount = finalAmount;
            for (ItemStack s : damaged.getAllSlots()) {
                amount = SocketHelper.getGems(s).onHurt(source, damaged, amount);
                var affixes = AffixHelper.getAffixes(s);
                for (AffixInstance inst : affixes.values()) {
                    amount = inst.onHurt(source, damaged, amount);
                }
            }

            return amount;
        });
    }

    public static void onItemUse() {
        ItemUseEvent.ItemUse.ITEM_USE_EVENT.register(e -> {
            ItemStack s = e.stack;
            boolean cancelled = false;
            InteractionResult socketRes = SocketHelper.getGems(s).onItemUse(e.ctx);
            if (socketRes != null) {
                cancelled = true;
                e.cancellationResult = socketRes;
            }

            InteractionResult afxRes = AffixHelper.streamAffixes(s).map(afx -> afx.onItemUse(e.ctx)).filter(Predicates.notNull()).findFirst().orElse(null);
            if (afxRes != null) {
                cancelled = true;
                e.cancellationResult = afxRes;
            }
            return cancelled;
        });
    }

    public static void onSpellCast() {
        SpellEvents.PROJECTILE_SHOOT.register(projectileLaunchEvent -> {
            var affixes = AffixHelper.getAffixes(projectileLaunchEvent.caster().getMainHandItem());
            affixes.values().forEach(inst -> inst.onCast(projectileLaunchEvent));
        });
    }

    public static void shieldBlock() {
        ShieldBlockEvent.EVENT.register(e -> {
            ItemStack stack = e.getEntity().getUseItem();
            var affixes = AffixHelper.getAffixes(stack);
            float blocked = e.getBlockedDamage();
            blocked = SocketHelper.getGems(stack).onShieldBlock(e.getEntity(), e.getDamageSource(), blocked);

            for (AffixInstance inst : affixes.values()) {
                blocked = inst.onShieldBlock(e.getEntity(), e.getDamageSource(), blocked);
            }
            if (blocked != e.getBlockedDamage()) e.setBlockedDamage(blocked);
        });
    }

    public static void blockBreak() {
        PlayerBlockBreakEvents.AFTER.register((world, player, pos, state, blockEntity) -> {
            ItemStack stack = player.getMainHandItem();
            SocketHelper.getGems(stack).onBlockBreak(player, world, pos, state);
            AffixHelper.streamAffixes(stack).forEach(inst -> {
                inst.onBlockBreak(player, world, pos, state);
            });
        });
    }

    public static void dropsHigh() {
        LivingEntityEvents.DROPS.register((target, source, drops, lootingLevel, recentlyHit) -> {
            if (source.getEntity() instanceof ServerPlayer p && target instanceof Monster && drops != null) {
                if (p instanceof FakePlayer) return false;
                float chance = AdventureConfig.gemDropChance + (ZenithComponents.BOSS_DATA.get(target).getIsBoss() ? AdventureConfig.gemBossBonus : 0);
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
                if (player == null) return true;
                ItemStack affixItem = LootController.createRandomLootItem(level.getRandom(), null, player, (ServerLevel) mob.level());
                if (affixItem.isEmpty()) return true;
                affixItem.getOrCreateTag().putBoolean("zenith_rspawn", true);
                LootCategory cat = LootCategory.forItem(affixItem);
                EquipmentSlot slot = cat.getSlots()[0];
                mob.setItemSlot(slot, affixItem);
                mob.setGuaranteedDrop(slot);
            }
            return true;
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
            SocketHelper.getGems(stack).getEnchantmentLevels(enchantments);

            AffixHelper.streamAffixes(stack).forEach(inst -> inst.getEnchantmentLevels(enchantments));
            reentrantLock.get().set(false);
            return enchantments;
        }));
    }

    @SuppressWarnings("deprecation")
    public static void update() {
        LivingEntityEvents.LivingTickEvent.TICK.register(e -> {
            LivingEntity entity = e.getEntity();
            if (entity.getCustomData().contains("apoth.burns_in_sun")) {
                ZenithComponents.BURNS.get(entity).setValue(entity.getCustomData().getBoolean("apoth.burns_in_sun"));
                entity.getCustomData().remove("apoth.burns_in_sun");
            }
            if (ZenithComponents.BURNS.get(entity).getValue()) {
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
