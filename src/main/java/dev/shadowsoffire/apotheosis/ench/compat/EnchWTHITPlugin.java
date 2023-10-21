package dev.shadowsoffire.apotheosis.ench.compat;


import dev.shadowsoffire.apotheosis.Apotheosis;
import dev.shadowsoffire.apotheosis.util.CommonTooltipUtil;
import mcp.mobius.waila.api.*;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.block.AnvilBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;

import java.util.Map;

public class EnchWTHITPlugin implements IWailaPlugin, IBlockComponentProvider {

    @Override
    public void register(IRegistrar reg) {
        if (!Apotheosis.enableEnch) return;
        reg.addComponent(this, TooltipPosition.BODY, Block.class);
    }

    @Override
    public void appendBody(ITooltip tooltip, IBlockAccessor accessor, IPluginConfig config) {
        if (!Apotheosis.enableEnch) return;
        if (accessor.getBlock() instanceof AnvilBlock) {
            CompoundTag tag = accessor.getData().raw();
            Map<Enchantment, Integer> enchants = EnchantmentHelper.deserializeEnchantments(tag.getList("enchantments", Tag.TAG_COMPOUND));
            for (Map.Entry<Enchantment, Integer> e : enchants.entrySet()) {
                tooltip.addLine(e.getKey().getFullname(e.getValue()));
            }
        }
        CommonTooltipUtil.appendBlockStats(accessor.getWorld(), accessor.getBlockState(), tooltip::addLine);
        if (accessor.getBlock() == Blocks.ENCHANTING_TABLE) CommonTooltipUtil.appendTableStats(accessor.getWorld(), accessor.getPosition(), tooltip::addLine);
    }

}
