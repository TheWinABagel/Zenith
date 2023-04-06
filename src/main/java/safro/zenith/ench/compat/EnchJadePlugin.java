package safro.zenith.ench.compat;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import safro.zenith.Zenith;
import snownee.jade.api.*;
import snownee.jade.api.config.IPluginConfig;

public class EnchJadePlugin implements IBlockComponentProvider, IWailaPlugin {

    public static final ResourceLocation ZENITH_ENCHANTMENT_POWER = new ResourceLocation(Zenith.MODID, "enchantment_power");

    @Override
    public void appendTooltip(ITooltip iTooltip, BlockAccessor blockAccessor, IPluginConfig iPluginConfig) {
        if (!Zenith.enableEnch) return;
        CommonTooltipUtil.appendBlockStats(blockAccessor.getLevel(), blockAccessor.getBlockState(), iTooltip::add);
        if (blockAccessor.getBlock() == Blocks.ENCHANTING_TABLE)
            CommonTooltipUtil.appendTableStats(blockAccessor.getLevel(), blockAccessor.getPosition(), iTooltip::add);


    }


    @Override
    public void registerClient(IWailaClientRegistration registration) {

        registration.registerBlockComponent(this, Block.class);
        registration.addConfig(ZENITH_ENCHANTMENT_POWER, true);
    }

    @Override
    public ResourceLocation getUid() {
        return ZENITH_ENCHANTMENT_POWER;
    }
}
