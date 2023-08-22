package dev.shadowsoffire.apotheosis.compat;

import dev.shadowsoffire.apotheosis.Apotheosis;
import net.fabricmc.loader.api.FabricLoader;
import vazkii.patchouli.api.PatchouliAPI;

public class PatchouliCompat {
    public static void registerPatchouli() {
        PatchouliAPI.IPatchouliAPI api = PatchouliAPI.get();
        if (!api.isStub()) {
            api.setConfigFlag("apotheosis:enchanting", Apotheosis.enableEnch);
            api.setConfigFlag("apotheosis:adventure", Apotheosis.enableAdventure);
            api.setConfigFlag("apotheosis:spawner", Apotheosis.enableSpawner);
            api.setConfigFlag("apotheosis:garden", Apotheosis.enableGarden);
            api.setConfigFlag("apotheosis:potion", Apotheosis.enablePotion);
            api.setConfigFlag("apotheosis:village", Apotheosis.enableVillage);
            api.setConfigFlag("apotheosis:wstloaded", FabricLoader.getInstance().isModLoaded("wstweaks"));
            api.setConfigFlag("apotheosis:curiosloaded", FabricLoader.getInstance().isModLoaded("trinkets"));
        }
    }
}
