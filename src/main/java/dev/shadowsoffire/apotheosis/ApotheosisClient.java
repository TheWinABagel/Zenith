package dev.shadowsoffire.apotheosis;

import dev.shadowsoffire.apotheosis.ench.EnchModuleClient;
import dev.shadowsoffire.apotheosis.spawn.SpawnerModuleClient;
import dev.shadowsoffire.apotheosis.village.VillageModuleClient;
import net.fabricmc.api.ClientModInitializer;

public class ApotheosisClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        if (Apotheosis.enableVillage) VillageModuleClient.init();
        if (Apotheosis.enableSpawner) SpawnerModuleClient.init();
        if (Apotheosis.enableEnch) EnchModuleClient.init();
        //if (Apotheosis.enableAdventure) AdventureModuleClient.init();
    }
}
