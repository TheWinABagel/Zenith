package safro.apotheosis.compat;

import mcjty.theoneprobe.api.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import safro.apotheosis.Apotheosis;
import safro.apotheosis.ench.compat.EnchTOPPlugin;
import safro.apotheosis.spawn.compat.SpawnerTOPPlugin;

public class ApothTOPPlugin implements ITheOneProbePlugin {

    @Override
    public void onLoad(ITheOneProbe probe) {
        probe.registerProvider(new IProbeInfoProvider() {
            @Override
            public ResourceLocation getID() {
                return new ResourceLocation(Apotheosis.MODID, "plugin");
            }

            @Override
            public void addProbeInfo(ProbeMode probeMode, IProbeInfo iProbeInfo, Player player, Level level, BlockState blockState, IProbeHitData iProbeHitData) {
                if (Apotheosis.enableEnch) EnchTOPPlugin.addProbeInfo(probeMode, iProbeInfo, player, level, blockState, iProbeHitData);
                if (Apotheosis.enableSpawner) SpawnerTOPPlugin.addProbeInfo(probeMode, iProbeInfo, player, level, blockState, iProbeHitData);
            }
        });
    }
}
