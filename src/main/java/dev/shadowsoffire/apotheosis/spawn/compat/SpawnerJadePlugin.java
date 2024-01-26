package dev.shadowsoffire.apotheosis.spawn.compat;

import dev.shadowsoffire.apotheosis.Apotheosis;
import dev.shadowsoffire.apotheosis.mixin.accessors.SpawnerBlockEntityAccessor;
import dev.shadowsoffire.apotheosis.spawn.modifiers.SpawnerStats;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SpawnerBlock;
import net.minecraft.world.level.block.entity.SpawnerBlockEntity;
import snownee.jade.api.*;
import snownee.jade.api.config.IPluginConfig;

@WailaPlugin
public class SpawnerJadePlugin implements IWailaPlugin, IBlockComponentProvider, IServerDataProvider<BlockAccessor> {
    private static final SpawnerBlockEntity tooltipTile = new SpawnerBlockEntity(BlockPos.ZERO, Blocks.AIR.defaultBlockState());

    public static final String STATS = "spw_stats";

    @Override
    public void register(IWailaCommonRegistration reg) {
        if (Apotheosis.enableSpawner) reg.registerBlockDataProvider(this, SpawnerBlockEntity.class);
    }

    @Override
    public void registerClient(IWailaClientRegistration reg) {
        if (Apotheosis.enableSpawner) reg.registerBlockComponent(this, SpawnerBlock.class);
    }

    @Override
    public void appendTooltip(ITooltip tooltip, BlockAccessor accessor, IPluginConfig config) {
        if (!Apotheosis.enableSpawner) return;
        if (Screen.hasControlDown()) {
            tooltipTile.load(accessor.getServerData());
            SpawnerStats.generateTooltip(tooltipTile, tooltip::add);
        }
        else tooltip.add(Component.translatable("misc.zenith.ctrl_stats"));
    }

    @Override
    public void appendServerData(CompoundTag tag, BlockAccessor access) {
        if (!Apotheosis.enableSpawner) return;
        if (access.getBlockEntity() instanceof SpawnerBlockEntity spw) {
            ((SpawnerBlockEntityAccessor) spw).callSaveAdditional(tag);
        }
    }

    @Override
    public ResourceLocation getUid() {
        return Apotheosis.loc("spawner");
    }

}
