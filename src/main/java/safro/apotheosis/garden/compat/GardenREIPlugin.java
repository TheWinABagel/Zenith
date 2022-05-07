package safro.apotheosis.garden.compat;

import me.shedaniel.rei.api.client.plugins.REIClientPlugin;
import me.shedaniel.rei.api.client.registry.display.DisplayRegistry;
import net.minecraft.resources.ResourceLocation;
import safro.apotheosis.Apotheosis;
import safro.apotheosis.garden.GardenModule;
import safro.apotheosis.util.ClientUtil;

public class GardenREIPlugin implements REIClientPlugin {

    @Override
    public String getPluginProviderName() {
        return new ResourceLocation(Apotheosis.MODID, "garden").toString();
    }

    @Override
    public void registerDisplays(DisplayRegistry registry) {
        if (!Apotheosis.enableGarden) return;
        ClientUtil.addInfo(registry, GardenModule.ENDER_LEAD, "info.apotheosis.ender_lead");
    }
}
