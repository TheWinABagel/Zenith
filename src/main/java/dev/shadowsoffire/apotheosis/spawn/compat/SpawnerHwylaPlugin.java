package dev.shadowsoffire.apotheosis.spawn.compat;

import dev.shadowsoffire.apotheosis.Apotheosis;
import dev.shadowsoffire.apotheosis.spawn.modifiers.SpawnerStats;
import dev.shadowsoffire.apotheosis.spawn.spawner.ApothSpawnerBlock;
import dev.shadowsoffire.apotheosis.spawn.spawner.ApothSpawnerTile;
import dev.shadowsoffire.apotheosis.spawn.spawner.IBaseSpawner;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.BaseSpawner;
import net.minecraft.world.level.block.SpawnerBlock;
import net.minecraft.world.level.block.entity.SpawnerBlockEntity;
import snownee.jade.api.*;
import snownee.jade.api.config.IPluginConfig;

@WailaPlugin
public class SpawnerHwylaPlugin implements IWailaPlugin, IBlockComponentProvider, IServerDataProvider<BlockAccessor> {

    public static final String STATS = "spw_stats";

    @Override
    public void register(IWailaCommonRegistration reg) {
        reg.registerBlockDataProvider(this, SpawnerBlockEntity.class);
    }

    @Override
    public void registerClient(IWailaClientRegistration reg) {
        reg.registerBlockComponent(this, SpawnerBlock.class);
    }

    @Override
    public void appendTooltip(ITooltip tooltip, BlockAccessor accessor, IPluginConfig config) {
        if (!Apotheosis.enableSpawner) return;
        if (Screen.hasControlDown()) {
            int[] stats = accessor.getServerData().getIntArray(STATS);
            if (stats.length != 12) return;
            tooltip.add(concat(SpawnerStats.MIN_DELAY.name(), stats[0]));
            tooltip.add(concat(SpawnerStats.MAX_DELAY.name(), stats[1]));
            tooltip.add(concat(SpawnerStats.SPAWN_COUNT.name(), stats[2]));
            tooltip.add(concat(SpawnerStats.MAX_NEARBY_ENTITIES.name(), stats[3]));
            tooltip.add(concat(SpawnerStats.REQ_PLAYER_RANGE.name(), stats[4]));
            tooltip.add(concat(SpawnerStats.SPAWN_RANGE.name(), stats[5]));
            if (stats[6] == 1) tooltip.add(SpawnerStats.IGNORE_PLAYERS.name().withStyle(ChatFormatting.DARK_GREEN));
            if (stats[7] == 1) tooltip.add(SpawnerStats.IGNORE_CONDITIONS.name().withStyle(ChatFormatting.DARK_GREEN));
            if (stats[8] == 1) tooltip.add(SpawnerStats.REDSTONE_CONTROL.name().withStyle(ChatFormatting.DARK_GREEN));
            if (stats[9] == 1) tooltip.add(SpawnerStats.IGNORE_LIGHT.name().withStyle(ChatFormatting.DARK_GREEN));
            if (stats[10] == 1) tooltip.add(SpawnerStats.NO_AI.name().withStyle(ChatFormatting.DARK_GREEN));
            if (stats[11] == 1) tooltip.add(SpawnerStats.SILENT.name().withStyle(ChatFormatting.DARK_GREEN));
        }
        else tooltip.add(Component.translatable("misc.zenith.ctrl_stats"));
    }

    @Override
    public void appendServerData(CompoundTag tag, BlockAccessor access) {
        if (!Apotheosis.enableSpawner) return;
        if (access.getBlockEntity() instanceof SpawnerBlockEntity spw) {
            BaseSpawner logic = spw.getSpawner();
            IBaseSpawner spawner = (IBaseSpawner) spw;
            tag.putIntArray(STATS,
                new int[] {
                    logic.minSpawnDelay,
                    logic.maxSpawnDelay,
                    logic.spawnCount,
                    logic.maxNearbyEntities,
                    logic.requiredPlayerRange,
                    logic.spawnRange,
                        ((IBaseSpawner) spw).getIgnorePlayers() ? 1 : 0,
                        ((IBaseSpawner) spw).getIgnoresConditions() ? 1 : 0,
                        ((IBaseSpawner) spw).getRedstoneControl() ? 1 : 0,
                        ((IBaseSpawner) spw).getIgnoreLight() ? 1 : 0,
                        ((IBaseSpawner) spw).getNoAi() ? 1 : 0,
                        ((IBaseSpawner) spw).getSilent() ? 1 : 0
                });

        }
    }

    @Override
    public ResourceLocation getUid() {
        return Apotheosis.loc("spawner");
    }

    public static Component concat(Object... args) {
        return Component.translatable("misc.zenith.value_concat", args[0], Component.literal(args[1].toString()).withStyle(ChatFormatting.GRAY)).withStyle(ChatFormatting.GREEN);
    }
}
