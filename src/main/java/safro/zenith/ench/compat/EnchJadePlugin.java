package safro.zenith.ench.compat;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.AnvilBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import safro.zenith.Zenith;
import safro.zenith.ench.anvil.AnvilBlockEntity;
import snownee.jade.api.*;
import snownee.jade.api.config.IPluginConfig;

import java.util.Map;

public class EnchJadePlugin implements IBlockComponentProvider, IWailaPlugin, IServerDataProvider<BlockEntity> {

    public static final ResourceLocation ZENITH_ENCHANTMENT_POWER = new ResourceLocation(Zenith.MODID, "enchantment_power");

    @Override
    public void register(IWailaCommonRegistration reg) {
        reg.registerBlockDataProvider(this, AnvilBlockEntity.class);
    }

    @Override
    public void registerClient(IWailaClientRegistration registration) {

        registration.registerBlockComponent(this, Block.class);
    }

    @Override
    public void appendTooltip(ITooltip tooltip, BlockAccessor accessor, IPluginConfig config) {
        if ((accessor.getBlock() instanceof AnvilBlock) && Zenith.enableEnch) {
            CompoundTag tag = accessor.getServerData();
            Map<Enchantment, Integer> enchants = EnchantmentHelper.deserializeEnchantments(tag.getList("enchantments", Tag.TAG_COMPOUND));
            for (Map.Entry<Enchantment, Integer> e : enchants.entrySet()) {
                tooltip.add(e.getKey().getFullname(e.getValue()));
            }
        }
        if (Zenith.enableEnch)
            CommonTooltipUtil.appendBlockStats(accessor.getLevel(), accessor.getBlockState(), tooltip::add);
        if ((accessor.getBlock() == Blocks.ENCHANTING_TABLE) && (Zenith.enableEnch))
            CommonTooltipUtil.appendTableStats(accessor.getLevel(), accessor.getPosition(), tooltip::add);
    }

    @Override
    public void appendServerData(CompoundTag tag, ServerPlayer player, Level world, BlockEntity te, boolean something) {
        if ((te instanceof AnvilBlockEntity) && (Zenith.enableEnch)) {
            ItemStack stack = new ItemStack(Items.ANVIL);
            EnchantmentHelper.setEnchantments(((AnvilBlockEntity) te).getEnchantments(), stack);
            tag.put("enchantments", stack.getEnchantmentTags());
        }
    }

    @Override
    public ResourceLocation getUid() {
        return ZENITH_ENCHANTMENT_POWER;
    }
}
