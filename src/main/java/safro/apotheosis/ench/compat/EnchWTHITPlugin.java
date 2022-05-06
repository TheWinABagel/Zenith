package safro.apotheosis.ench.compat;

import mcp.mobius.waila.api.*;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.block.AnvilBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import safro.apotheosis.Apotheosis;
import safro.apotheosis.ench.anvil.AnvilTile;

import java.util.Map;

public class EnchWTHITPlugin implements IWailaPlugin, IBlockComponentProvider, IServerDataProvider<BlockEntity> {

    @Override
    public void register(IRegistrar reg) {
        if (!Apotheosis.enableEnch) return;
        reg.addBlockData(this, AnvilTile.class);
        reg.addComponent(this, TooltipPosition.BODY, AnvilBlock.class);
    }

    @Override
    public void appendBody(ITooltip tooltip, IBlockAccessor accessor, IPluginConfig config) {
        if (!Apotheosis.enableEnch) return;
        CompoundTag tag = accessor.getServerData();
        Map<Enchantment, Integer> enchants = EnchantmentHelper.deserializeEnchantments(tag.getList("enchantments", Tag.TAG_COMPOUND));
        for (Map.Entry<Enchantment, Integer> e : enchants.entrySet()) {
            tooltip.addLine(e.getKey().getFullname(e.getValue()));
        }
    }

    @Override
    public void appendServerData(CompoundTag tag, IServerAccessor<BlockEntity> accessor, IPluginConfig config) {
        if (!Apotheosis.enableEnch) return;
        if (accessor.getTarget() instanceof AnvilTile) {
            ItemStack stack = new ItemStack(Items.ANVIL);
            EnchantmentHelper.setEnchantments(((AnvilTile) accessor.getTarget()).getEnchantments(), stack);
            tag.put("enchantments", stack.getEnchantmentTags());
        }
    }
}
