package dev.shadowsoffire.apotheosis.ench.enchantments.twisted;

import io.github.fabricators_of_create.porting_lib.entity.events.player.PlayerEvents;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.DiggingEnchantment;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;

public class MinersFervorEnchant extends DiggingEnchantment {

    public MinersFervorEnchant() {
        super(Rarity.RARE, new EquipmentSlot[] { EquipmentSlot.MAINHAND });
    }

    @Override
    public int getMinCost(int enchantmentLevel) {
        return 45 + (enchantmentLevel - 1) * 30;
    }

    @Override
    public int getMaxCost(int enchantmentLevel) {
        return this.getMinCost(enchantmentLevel) + 50;
    }

    @Override
    public int getMaxLevel() {
        return 5;
    }

    @Override
    public Component getFullname(int level) {
        return ((MutableComponent) super.getFullname(level)).withStyle(ChatFormatting.DARK_PURPLE);
    }

    @Override
    protected boolean checkCompatibility(Enchantment e) {
        return super.checkCompatibility(e) && e != Enchantments.BLOCK_EFFICIENCY;
    }

    public void breakSpeed() {
        PlayerEvents.BREAK_SPEED.register((p, state, pos, speed) -> {
            ItemStack stack = p.getMainHandItem();
            if (stack.isEmpty()) return speed;
            int level = EnchantmentHelper.getItemEnchantmentLevel(this, stack);
            if (level > 0) {
                if (stack.getDestroySpeed(state) > 1.0F) {
                    float hardness = state.getDestroySpeed(p.level(), pos);
                    return (Math.min(29.9999F, 7.5F + 4.5F * level) * hardness);
                }
            }
            return speed;
        });

    }


}
