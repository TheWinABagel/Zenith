package dev.shadowsoffire.apotheosis.ench.enchantments.corrupted;

import dev.shadowsoffire.apotheosis.util.Events;
import io.github.fabricators_of_create.porting_lib.tool.ToolActions;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentCategory;
import net.minecraft.world.item.enchantment.EnchantmentHelper;

import java.util.List;

public class LifeMendingEnchant extends Enchantment {

    public LifeMendingEnchant() {
        super(Rarity.VERY_RARE, EnchantmentCategory.BREAKABLE, EquipmentSlot.values());
    }

    @Override
    public int getMinCost(int level) {
        return 65 + (level - 1) * 35;
    }

    @Override
    public int getMaxCost(int level) {
        return this.getMinCost(level) + 50;
    }

    @Override
    public int getMaxLevel() {
        return 3;
    }

    @Override
    public boolean isCurse() {
        return true;
    }
/*
    @Override
    public boolean canApplyAtEnchantingTable(ItemStack stack) {
        return super.canApplyAtEnchantingTable(stack) || stack.canPerformAction(ToolActions.SHIELD_BLOCK);
    }*/

    @Override
    public Component getFullname(int level) {
        return ((MutableComponent) super.getFullname(level)).withStyle(ChatFormatting.DARK_RED);
    }

    private static final EquipmentSlot[] SLOTS = EquipmentSlot.values();

    private float lifeMend(Entity entity, float amount, ItemStack stack) {
        if (!stack.isEmpty() && stack.isDamaged()) {
            int level = EnchantmentHelper.getItemEnchantmentLevel(this, stack);
            if (level <= 0) return amount;
            float cost = 1.0F / (1 << level - 1);
            int maxRestore = Math.min(Mth.floor(amount / cost), stack.getDamageValue());
            stack.setDamageValue(stack.getDamageValue() - maxRestore);
            return (amount - maxRestore * cost);
        }
        return amount;
    }

    public void lifeMend() {
        Events.LivingHealEvent.EVENT.register((entity, amount) -> {
            if (entity.getType() == EntityType.ARMOR_STAND) return amount;
            if (entity.level().isClientSide) return amount;
            if (amount <= 0F) return 0f;
            for (EquipmentSlot slot : SLOTS) {
                if (!(entity instanceof LivingEntity living)) continue;
                ItemStack stack = living.getItemBySlot(slot);
                if (this.lifeMend(entity, amount, stack) == amount) return amount;

            }
          /*  if (FabricLoader.getInstance().isModLoaded("trinkets")) { TODO reenable with adventure
                List<ItemStack> stacks = AdventureCuriosCompat.getLifeMendingCurios(e.getEntity());
                for (ItemStack stack : stacks) {
                    if (this.lifeMend(e, stack)) return;
                }
            }*/
            return amount;
        });

    }

}
