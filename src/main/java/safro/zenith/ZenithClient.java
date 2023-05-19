package safro.zenith;

import net.fabricmc.api.ClientModInitializer;
import safro.zenith.adventure.client.AdventureModuleClient;
import safro.zenith.api.event.ClientEvents;
import safro.zenith.ench.EnchModuleClient;
import safro.zenith.network.NetworkUtil;
import safro.zenith.potion.PotionModuleClient;
import safro.zenith.spawn.SpawnerModuleClient;
import safro.zenith.village.VillageModuleClient;

public class ZenithClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        ClientEvents.init();
        NetworkUtil.initClient();

        if (Zenith.enableAdventure) AdventureModuleClient.init();
        if (Zenith.enableEnch) EnchModuleClient.init();
        if (Zenith.enablePotion) PotionModuleClient.init();
        if (Zenith.enableVillage) VillageModuleClient.init();
        if (Zenith.enableSpawner) SpawnerModuleClient.init();
    }
}
