package dev.shadowsoffire.apotheosis.potion;

import io.github.fabricators_of_create.porting_lib.enchant.CustomEnchantingBehaviorItem;
import io.github.fabricators_of_create.porting_lib.item.DamageableItem;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.ChatFormatting;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffectUtil;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.item.alchemy.PotionUtils;
import net.minecraft.world.item.alchemy.Potions;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.level.Level;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class PotionCharmItem extends Item implements CustomEnchantingBehaviorItem, DamageableItem {

    public static final Set<ResourceLocation> EXTENDED_POTIONS = new HashSet<>();
    public static final Set<ResourceLocation> DISABLED_POTIONS = new HashSet<>();

    public PotionCharmItem() {
        super(new Properties().stacksTo(1).durability(192));
    }

    @Override
    public ItemStack getDefaultInstance() {
        return PotionUtils.setPotion(super.getDefaultInstance(), Potions.LONG_INVISIBILITY);
    }

    @Override
    public void inventoryTick(ItemStack stack, Level world, Entity entity, int slot, boolean isSelected) {
        if (PotionModule.charmsInTrinketsOnly) return;
        charmLogic(stack, world, entity, slot , isSelected);
    }

    public void charmLogic(ItemStack stack, Level world, Entity entity, int slot, boolean isSelected){
        if (!hasPotion(stack)) return;
        if (stack.getOrCreateTag().getBoolean("charm_enabled") && entity instanceof ServerPlayer serverPlayer) {
            Potion p = PotionUtils.getPotion(stack);
            MobEffectInstance contained = p.getEffects().get(0);
            MobEffectInstance active = serverPlayer.getEffect(contained.getEffect());
            if (active == null || active.getDuration() < getCriticalDuration(active.getEffect())) {
                int durationOffset = getCriticalDuration(contained.getEffect());
                if (contained.getEffect() == MobEffects.REGENERATION) durationOffset += 50 >> contained.getAmplifier();
                MobEffectInstance newEffect = new MobEffectInstance(contained.getEffect(), (int) Math.ceil(contained.getDuration() / 24D) + durationOffset, contained.getAmplifier(), false, false);
                serverPlayer.addEffect(newEffect);
                if (!serverPlayer.isCreative()) {
                    if (stack.hurt(contained.getEffect() == MobEffects.REGENERATION ? 2 : 1, world.random, (ServerPlayer) entity)) stack.shrink(1);
                }
            }
        }
    }

    private static int getCriticalDuration(MobEffect effect) {
        return EXTENDED_POTIONS.contains(BuiltInRegistries.MOB_EFFECT.getKey(effect)) ? 210 : 5;
    }

    @Override
    public boolean isFoil(ItemStack stack) {
        return stack.getOrCreateTag().getBoolean("charm_enabled");
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level world, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        if (!world.isClientSide) {
            stack.getOrCreateTag().putBoolean("charm_enabled", !stack.getTag().getBoolean("charm_enabled"));
        }
        else if (!stack.getTag().getBoolean("charm_enabled")) world.playSound(player, player.blockPosition(), SoundEvents.EXPERIENCE_ORB_PICKUP, SoundSource.PLAYERS, 1, 0.3F);
        return InteractionResultHolder.success(stack);
    }

    @Override
    public boolean isEnchantable(ItemStack stack) {
        return false;
    }

    @Override
    public boolean canApplyAtEnchantingTable(ItemStack stack, Enchantment enchantment) {
        return false;
    }

    @Override
    @Environment(EnvType.CLIENT)
    public void appendHoverText(ItemStack stack, Level world, List<Component> tooltip, TooltipFlag flag) {
        if (PotionModule.charmsInTrinketsOnly) {
            tooltip.add(Component.translatable(this.getDescriptionId() + ".trinkets_only").withStyle(ChatFormatting.RED));
        }
        if (hasPotion(stack)) {
            Potion p = PotionUtils.getPotion(stack);
            MobEffectInstance effect = p.getEffects().get(0);
            if (DISABLED_POTIONS.contains(BuiltInRegistries.MOB_EFFECT.getKey(effect.getEffect()))){
                tooltip.add(Component.translatable(this.getDescriptionId() + ".crafting_disabled").withStyle(ChatFormatting.RED));
            }
            MutableComponent potionCmp = Component.translatable(effect.getDescriptionId());
            if (effect.getAmplifier() > 0) {
                potionCmp = Component.translatable("potion.withAmplifier", potionCmp, Component.translatable("potion.potency." + effect.getAmplifier()));
            }
            potionCmp.withStyle(effect.getEffect().getCategory().getTooltipFormatting());
            tooltip.add(Component.translatable(this.getDescriptionId() + ".desc", potionCmp).withStyle(ChatFormatting.GRAY));
            boolean enabled = stack.getOrCreateTag().getBoolean("charm_enabled");
            MutableComponent enabledCmp = Component.translatable(this.getDescriptionId() + (enabled ? ".enabled" : ".disabled"));
            enabledCmp.withStyle(enabled ? ChatFormatting.BLUE : ChatFormatting.RED);
            if (effect.getDuration() > 20) {
                potionCmp = Component.translatable("potion.withDuration", potionCmp, MobEffectUtil.formatDuration(effect, 1));
            }
            potionCmp.withStyle(effect.getEffect().getCategory().getTooltipFormatting());
            tooltip.add(Component.translatable(this.getDescriptionId() + ".desc3", potionCmp).withStyle(ChatFormatting.GRAY));
        }
    }

    @Override
    public int getMaxDamage(ItemStack stack) {
        if (!hasPotion(stack)) return 1;
        return 192;
    }

    @Override
    public Component getName(ItemStack stack) {
        if (!hasPotion(stack)) return Component.translatable("item.zenith.potion_charm_broke");
        Potion p = PotionUtils.getPotion(stack);
        MobEffectInstance effect = p.getEffects().get(0);
        MutableComponent potionCmp = Component.translatable(effect.getDescriptionId());
        if (effect.getAmplifier() > 0) {
            potionCmp = Component.translatable("potion.withAmplifier", potionCmp, Component.translatable("potion.potency." + effect.getAmplifier()));
        }
        return Component.translatable("item.zenith.potion_charm", potionCmp);
    }

    public static boolean hasPotion(ItemStack stack) {
        return PotionUtils.getPotion(stack) != Potions.EMPTY;
    }

    public static void fillItemCategory(CreativeModeTab.Output out) {
        for (Potion potion : BuiltInRegistries.POTION) {
            if (potion.getEffects().size() == 1 && !potion.getEffects().get(0).getEffect().isInstantenous()) {
                out.accept(PotionUtils.setPotion(new ItemStack(PotionModule.POTION_CHARM), potion));
            }
        }
    }

    @Override
    public String getCreatorModId(ItemStack itemStack) {
        Potion potionType = PotionUtils.getPotion(itemStack);
        ResourceLocation resourceLocation = BuiltInRegistries.POTION.getKey(potionType);
        if (resourceLocation != null) {
            return resourceLocation.getNamespace();
        }
        return BuiltInRegistries.ITEM.getKey(this).getNamespace();
    }

    @Override
    public int getEnchantmentValue() {
        return 0;
    }

}
