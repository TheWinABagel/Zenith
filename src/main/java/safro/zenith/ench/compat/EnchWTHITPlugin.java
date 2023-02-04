package safro.zenith.ench.compat;

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
import safro.zenith.Zenith;

import java.util.Map;

public class EnchWTHITPlugin implements IWailaPlugin, IBlockComponentProvider, IServerDataProvider<BlockEntity> {

    @Override
    public void register(IRegistrar reg) {
        if (!Zenith.enableEnch) return;
        reg.addComponent(this, TooltipPosition.BODY, Block.class);
    }

    @Override
    public void appendBody(ITooltip tooltip, IBlockAccessor accessor, IPluginConfig config) {
        if (!Zenith.enableEnch) return;
        if (accessor.getBlock() instanceof AnvilBlock) {
            CompoundTag tag = accessor.getServerData();
            Map<Enchantment, Integer> enchants = EnchantmentHelper.deserializeEnchantments(tag.getList("enchantments", Tag.TAG_COMPOUND));
            for (Map.Entry<Enchantment, Integer> e : enchants.entrySet()) {
                tooltip.addLine(e.getKey().getFullname(e.getValue()));
            }
        }
        CommonTooltipUtil.appendBlockStats(accessor.getWorld(), accessor.getBlockState(), tooltip::addLine);
        if (accessor.getBlock() == Blocks.ENCHANTING_TABLE) CommonTooltipUtil.appendTableStats(accessor.getWorld(), accessor.getPosition(), tooltip::addLine);
    }

    @Override
    public void appendServerData(CompoundTag tag, IServerAccessor<BlockEntity> accessor, IPluginConfig config) {
        if (!Zenith.enableEnch) return;
    }
}
