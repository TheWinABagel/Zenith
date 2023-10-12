package dev.shadowsoffire.apotheosis.compat;

import dev.shadowsoffire.apotheosis.Apotheosis;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.Item;
import vazkii.patchouli.api.PatchouliAPI;

public class PatchouliCompat {
    public static void registerPatchouli() {
        PatchouliAPI.IPatchouliAPI api = PatchouliAPI.get();
        if (!api.isStub()) {
            api.setConfigFlag("zenith:enchanting", Apotheosis.enableEnch);
            api.setConfigFlag("zenith:adventure", Apotheosis.enableAdventure);
            api.setConfigFlag("zenith:spawner", Apotheosis.enableSpawner);
            api.setConfigFlag("zenith:garden", Apotheosis.enableGarden);
            api.setConfigFlag("zenith:potion", Apotheosis.enablePotion);
            api.setConfigFlag("zenith:village", Apotheosis.enableVillage);
            api.setConfigFlag("zenith:wstloaded", FabricLoader.getInstance().isModLoaded("wstweaks"));
            api.setConfigFlag("zenith:curiosloaded", FabricLoader.getInstance().isModLoaded("trinkets"));
        }
    }
}
