package safro.zenith.spawn.compat;

import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.BaseSpawner;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.SpawnerBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.SpawnerBlockEntity;
import safro.zenith.Zenith;
import safro.zenith.spawn.SpawnerModule;
import safro.zenith.spawn.modifiers.SpawnerStats;
import safro.zenith.util.IBaseSpawner;
import snownee.jade.api.BlockAccessor;
import snownee.jade.api.IBlockComponentProvider;
import snownee.jade.api.IServerDataProvider;
import snownee.jade.api.ITooltip;
import snownee.jade.api.IWailaClientRegistration;
import snownee.jade.api.IWailaCommonRegistration;
import snownee.jade.api.IWailaPlugin;
import snownee.jade.api.WailaPlugin;
import snownee.jade.api.config.IPluginConfig;

@WailaPlugin
public class SpawnerJadePlugin implements IWailaPlugin, IBlockComponentProvider, IServerDataProvider<BlockEntity> {

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
        if (!Zenith.enableSpawner) return;
        if (Screen.hasControlDown()) {
            int[] stats = accessor.getServerData().getIntArray(STATS);
            if (stats.length != 12) return;
            tooltip.add(SpawnerModule.concat(SpawnerStats.MIN_DELAY.name(), stats[0]));
            tooltip.add(SpawnerModule.concat(SpawnerStats.MAX_DELAY.name(), stats[1]));
            tooltip.add(SpawnerModule.concat(SpawnerStats.SPAWN_COUNT.name(), stats[2]));
            tooltip.add(SpawnerModule.concat(SpawnerStats.MAX_NEARBY_ENTITIES.name(), stats[3]));
            tooltip.add(SpawnerModule.concat(SpawnerStats.REQ_PLAYER_RANGE.name(), stats[4]));
            tooltip.add(SpawnerModule.concat(SpawnerStats.SPAWN_RANGE.name(), stats[5]));
            if (stats[6] == 1) tooltip.add(SpawnerStats.IGNORE_PLAYERS.name().withStyle(ChatFormatting.DARK_GREEN));
            if (stats[7] == 1) tooltip.add(SpawnerStats.IGNORE_CONDITIONS.name().withStyle(ChatFormatting.DARK_GREEN));
            if (stats[8] == 1) tooltip.add(SpawnerStats.REDSTONE_CONTROL.name().withStyle(ChatFormatting.DARK_GREEN));
            if (stats[9] == 1) tooltip.add(SpawnerStats.IGNORE_LIGHT.name().withStyle(ChatFormatting.DARK_GREEN));
            if (stats[10] == 1) tooltip.add(SpawnerStats.NO_AI.name().withStyle(ChatFormatting.DARK_GREEN));
            if (stats[11] == 1) tooltip.add(SpawnerStats.SILENT.name().withStyle(ChatFormatting.DARK_GREEN));
        } else tooltip.add(Component.translatable("misc.zenith.ctrl_stats"));
    }

    @Override
    public void appendServerData(CompoundTag tag, ServerPlayer player, Level world, BlockEntity te, boolean arg4) {
        if (!Zenith.enableSpawner) return;
        if (te instanceof SpawnerBlockEntity spw) {
            BaseSpawner logic = spw.getSpawner();
            //Formatter::off
            tag.putIntArray(STATS,
                    new int[] {
                            logic.minSpawnDelay,
                            logic.maxSpawnDelay,
                            logic.spawnCount,
                            logic.maxNearbyEntities,
                            logic.requiredPlayerRange,
                            logic.spawnRange,
                            (((IBaseSpawner) spw).getIgnoresPlayers()) ? 1 : 0,
                            (((IBaseSpawner) spw).getIgnoresConditions()) ? 1 : 0,
                            (((IBaseSpawner) spw).getRedstoneControl()) ? 1 : 0,
                            (((IBaseSpawner) spw).getIgnoreLight()) ? 1 : 0,
                            (((IBaseSpawner) spw).getNoAi()) ? 1 : 0,
                            (((IBaseSpawner) spw).getSilent()) ? 1 : 0
                    });
            //Formatter::on
        }
    }

    @Override
    public ResourceLocation getUid() {
        return Zenith.loc("spawner");
    }

}