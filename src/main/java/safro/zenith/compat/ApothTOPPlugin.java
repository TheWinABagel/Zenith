package safro.zenith.compat;

import mcjty.theoneprobe.api.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import safro.zenith.Zenith;
import safro.zenith.ench.compat.EnchTOPPlugin;
import safro.zenith.spawn.compat.SpawnerTOPPlugin;

public class ApothTOPPlugin implements ITheOneProbePlugin {

    @Override
    public void onLoad(ITheOneProbe probe) {
        probe.registerProvider(new IProbeInfoProvider() {
            @Override
            public ResourceLocation getID() {
                return new ResourceLocation(Zenith.MODID, "plugin");
            }

            @Override
            public void addProbeInfo(ProbeMode probeMode, IProbeInfo iProbeInfo, Player player, Level level, BlockState blockState, IProbeHitData iProbeHitData) {
                if (Zenith.enableEnch) EnchTOPPlugin.addProbeInfo(probeMode, iProbeInfo, player, level, blockState, iProbeHitData);
                if (Zenith.enableSpawner) SpawnerTOPPlugin.addProbeInfo(probeMode, iProbeInfo, player, level, blockState, iProbeHitData);
            }
        });
    }
}
