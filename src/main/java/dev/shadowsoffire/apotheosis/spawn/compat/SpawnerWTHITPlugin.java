package dev.shadowsoffire.apotheosis.spawn.compat;

import dev.shadowsoffire.apotheosis.Apotheosis;
import dev.shadowsoffire.apotheosis.spawn.modifiers.SpawnerStats;
import dev.shadowsoffire.apotheosis.spawn.spawner.IBaseSpawner;
import mcp.mobius.waila.api.*;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.BaseSpawner;
import net.minecraft.world.level.block.SpawnerBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.SpawnerBlockEntity;

public class SpawnerWTHITPlugin implements IWailaPlugin, IBlockComponentProvider, IDataProvider<BlockEntity> {

    public static final String STATS = "spw_stats";

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
            int[] stats = accessor.getData().raw().getIntArray(STATS);
            if (stats.length != 12) return;
            tooltip.addLine(concat(SpawnerStats.MIN_DELAY.name(), stats[0]));
            tooltip.addLine(concat(SpawnerStats.MAX_DELAY.name(), stats[1]));
            tooltip.addLine(concat(SpawnerStats.SPAWN_COUNT.name(), stats[2]));
            tooltip.addLine(concat(SpawnerStats.MAX_NEARBY_ENTITIES.name(), stats[3]));
            tooltip.addLine(concat(SpawnerStats.REQ_PLAYER_RANGE.name(), stats[4]));
            tooltip.addLine(concat(SpawnerStats.SPAWN_RANGE.name(), stats[5]));
            if (stats[6] == 1) tooltip.addLine(SpawnerStats.IGNORE_PLAYERS.name().withStyle(ChatFormatting.DARK_GREEN));
            if (stats[7] == 1) tooltip.addLine(SpawnerStats.IGNORE_CONDITIONS.name().withStyle(ChatFormatting.DARK_GREEN));
            if (stats[8] == 1) tooltip.addLine(SpawnerStats.REDSTONE_CONTROL.name().withStyle(ChatFormatting.DARK_GREEN));
            if (stats[9] == 1) tooltip.addLine(SpawnerStats.IGNORE_LIGHT.name().withStyle(ChatFormatting.DARK_GREEN));
            if (stats[10] == 1) tooltip.addLine(SpawnerStats.NO_AI.name().withStyle(ChatFormatting.DARK_GREEN));
            if (stats[11] == 1) tooltip.addLine(SpawnerStats.SILENT.name().withStyle(ChatFormatting.DARK_GREEN));
        }
        else tooltip.addLine(Component.translatable("misc.zenith.ctrl_stats"));
    }

    @Override
    public void appendData(IDataWriter data, IServerAccessor<BlockEntity> accessor, IPluginConfig config) {
        if (!Apotheosis.enableSpawner) return;
        if (accessor.getTarget() instanceof SpawnerBlockEntity spw) {
            BaseSpawner logic = spw.getSpawner();
            IBaseSpawner spawner = (IBaseSpawner) spw;
            data.raw().putIntArray(STATS,
                    new int[] {
                            logic.minSpawnDelay,
                            logic.maxSpawnDelay,
                            logic.spawnCount,
                            logic.maxNearbyEntities,
                            logic.requiredPlayerRange,
                            logic.spawnRange,
                            spawner.getIgnorePlayers() ? 1 : 0,
                            spawner.getIgnoresConditions() ? 1 : 0,
                            spawner.getRedstoneControl() ? 1 : 0,
                            spawner.getIgnoreLight() ? 1 : 0,
                            spawner.getNoAi() ? 1 : 0,
                            spawner.getSilent() ? 1 : 0
                    });
        }
    }

    public static Component concat(Object... args) {
        return Component.translatable("misc.zenith.value_concat", args[0], Component.literal(args[1].toString()).withStyle(ChatFormatting.GRAY)).withStyle(ChatFormatting.GREEN);
    }
}
