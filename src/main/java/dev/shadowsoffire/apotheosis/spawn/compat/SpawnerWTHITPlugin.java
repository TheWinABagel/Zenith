package dev.shadowsoffire.apotheosis.spawn.compat;

import dev.shadowsoffire.apotheosis.Apotheosis;
import dev.shadowsoffire.apotheosis.spawn.modifiers.SpawnerStats;
import dev.shadowsoffire.apotheosis.spawn.spawner.IBaseSpawner;
import mcp.mobius.waila.api.*;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.BaseSpawner;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SpawnerBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.SpawnerBlockEntity;

public class SpawnerWTHITPlugin implements IWailaPlugin, IBlockComponentProvider, IDataProvider<BlockEntity> {

    private static final SpawnerBlockEntity tooltipTile = new SpawnerBlockEntity(BlockPos.ZERO, Blocks.AIR.defaultBlockState());

    @Override
    public void register(IRegistrar registrar) {
        if (!Apotheosis.enableSpawner) return;
        registrar.addBlockData(this, SpawnerBlockEntity.class);
        registrar.addComponent(this, TooltipPosition.BODY, SpawnerBlock.class);
    }

    @Override
    public void appendBody(ITooltip tooltip, IBlockAccessor accessor, IPluginConfig config) {
        if (!Apotheosis.enableSpawner) return;
        if (Screen.hasControlDown()) {
            tooltipTile.load(accessor.getData().raw());
            SpawnerStats.generateTooltip(tooltipTile, tooltip::addLine);
        }
        else tooltip.addLine(Component.translatable("misc.zenith.ctrl_stats"));
    }

    @Override
    public void appendData(IDataWriter data, IServerAccessor<BlockEntity> accessor, IPluginConfig config) {
        if (!Apotheosis.enableSpawner) return;
        if (accessor.getTarget() instanceof SpawnerBlockEntity spw) {
            spw.saveAdditional(data.raw());
        }
    }

    public static Component concat(Object... args) {
        return Component.translatable("misc.zenith.value_concat", args[0], Component.literal(args[1].toString()).withStyle(ChatFormatting.GRAY)).withStyle(ChatFormatting.GREEN);
    }
}
