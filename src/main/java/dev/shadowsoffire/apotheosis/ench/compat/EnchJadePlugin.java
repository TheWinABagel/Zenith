package dev.shadowsoffire.apotheosis.ench.compat;

import dev.shadowsoffire.apotheosis.Apotheosis;
import dev.shadowsoffire.apotheosis.util.CommonTooltipUtil;
import dev.shadowsoffire.apotheosis.ench.anvil.AnvilTile;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.block.AnvilBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import snownee.jade.api.*;
import snownee.jade.api.config.IPluginConfig;

import java.util.Map;

@WailaPlugin
public class EnchJadePlugin implements IWailaPlugin, IBlockComponentProvider, IServerDataProvider<BlockAccessor> {

    @Override
    public void register(IWailaCommonRegistration reg) {
        reg.registerBlockDataProvider(this, AnvilTile.class);
    }

    @Override
    public void registerClient(IWailaClientRegistration reg) {
        reg.registerBlockComponent(this, Block.class);
    }

    @Override
    public void appendTooltip(ITooltip tooltip, BlockAccessor accessor, IPluginConfig config) {
        if (!Apotheosis.enableEnch) return;
        if (accessor.getBlock() instanceof AnvilBlock) {
            CompoundTag tag = accessor.getServerData();
            Map<Enchantment, Integer> enchants = EnchantmentHelper.deserializeEnchantments(tag.getList("enchantments", Tag.TAG_COMPOUND));
            for (Map.Entry<Enchantment, Integer> e : enchants.entrySet()) {
                tooltip.add(e.getKey().getFullname(e.getValue()));
            }
        }
        CommonTooltipUtil.appendBlockStats(accessor.getLevel(), accessor.getBlockState(), tooltip::add);
        if (accessor.getBlock() == Blocks.ENCHANTING_TABLE) CommonTooltipUtil.appendTableStats(accessor.getLevel(), accessor.getPosition(), tooltip::add);
    }

    @Override
    public void appendServerData(CompoundTag tag, BlockAccessor access) {
        if (!Apotheosis.enableEnch) return;
        if (access.getBlockEntity() instanceof AnvilTile te) {
            ItemStack stack = new ItemStack(Items.ANVIL);
            EnchantmentHelper.setEnchantments(te.getEnchantments(), stack);
            tag.put("enchantments", stack.getEnchantmentTags());
        }
    }

    @Override
    public ResourceLocation getUid() {
        return Apotheosis.loc("ench");
    }

}
