package safro.zenith.garden.compat;

import me.shedaniel.rei.api.client.plugins.REIClientPlugin;
import me.shedaniel.rei.api.client.registry.display.DisplayRegistry;
import net.minecraft.resources.ResourceLocation;
import safro.zenith.Zenith;
import safro.zenith.garden.GardenModule;
import safro.zenith.util.REIUtil;

public class GardenREIPlugin implements REIClientPlugin {

    @Override
    public String getPluginProviderName() {
        return new ResourceLocation(Zenith.MODID, "garden").toString();
    }

    @Override
    public void registerDisplays(DisplayRegistry registry) {
        if (!Zenith.enableGarden) return;
        REIUtil.addInfo(registry, GardenModule.ENDER_LEAD, "info.zenith.ender_lead");
    }
}
