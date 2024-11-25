package dev.shadowsoffire.apotheosis.ench.enchantments.corrupted;

import dev.emi.trinkets.api.TrinketsApi;
import dev.shadowsoffire.apotheosis.util.ZenithModCompat;
import dev.shadowsoffire.attributeslib.api.events.LivingHealEvent;
import io.github.fabricators_of_create.porting_lib.enchant.CustomEnchantingTableBehaviorEnchantment;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ShieldItem;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentCategory;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;

import java.util.concurrent.atomic.AtomicReference;

public class LifeMendingEnchant extends Enchantment implements CustomEnchantingTableBehaviorEnchantment {

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

    @Override
    public boolean canApplyAtEnchantingTable(ItemStack stack) {
        return CustomEnchantingTableBehaviorEnchantment.super.canApplyAtEnchantingTable(stack) || stack.getItem() instanceof ShieldItem;
    }

    @Override
    protected boolean checkCompatibility(Enchantment other) {
        return super.checkCompatibility(other) && other != Enchantments.MENDING;
    }

    @Override
    public Component getFullname(int level) {
        return ((MutableComponent) super.getFullname(level)).withStyle(ChatFormatting.DARK_RED);
    }

    private static final EquipmentSlot[] SLOTS = EquipmentSlot.values();

    public float lifeMend(float amount, ItemStack stack) {
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
         LivingHealEvent.EVENT.register((entity, amount) -> {
            if (entity.getType() == EntityType.ARMOR_STAND) return amount;
            if (entity.level().isClientSide) return amount;
            if (amount <= 0F) return 0f;
            if (!(entity instanceof LivingEntity living)) return amount;
            for (EquipmentSlot slot : SLOTS) {
                ItemStack stack = living.getItemBySlot(slot);
                amount = this.lifeMend(amount, stack);
            }
            amount = ZenithModCompat.Ench.lifeMendTrinkets(entity, amount, this);
            return Math.max(amount, 0F);
         });

    }

}
