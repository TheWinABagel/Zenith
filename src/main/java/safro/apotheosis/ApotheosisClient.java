package safro.apotheosis;

import net.fabricmc.api.ClientModInitializer;
import safro.apotheosis.api.event.ClientEvents;
import safro.apotheosis.deadly.DeadlyModuleClient;
import safro.apotheosis.ench.EnchModuleClient;
import safro.apotheosis.network.NetworkUtil;
import safro.apotheosis.potion.PotionModuleClient;
import safro.apotheosis.village.VillageModuleClient;

public class ApotheosisClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        ClientEvents.init();
        NetworkUtil.initClient();

        if (Apotheosis.enableDeadly) DeadlyModuleClient.init();
        if (Apotheosis.enableEnch) EnchModuleClient.init();
        if (Apotheosis.enablePotion) PotionModuleClient.init();
        if (Apotheosis.enableVillage) VillageModuleClient.init();
    }
}
