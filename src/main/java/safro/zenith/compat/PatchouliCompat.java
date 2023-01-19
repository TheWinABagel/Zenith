package safro.zenith.compat;

import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import safro.zenith.Zenith;
import vazkii.patchouli.api.PatchouliAPI;
import vazkii.patchouli.api.PatchouliAPI.IPatchouliAPI;

public class PatchouliCompat {

    public static void register() {
        Registry.register(Registry.ITEM, new ResourceLocation(Zenith.MODID, "book"), new ApothChronicleItem(new Item.Properties().tab(Zenith.APOTH_GROUP).stacksTo(1)));

        IPatchouliAPI api = PatchouliAPI.get();
        if (!api.isStub()) {
            api.setConfigFlag("zenith:enchanting", Zenith.enableEnch);
//            api.setConfigFlag("zenith:deadly", Zenith.enableAdventure);
            api.setConfigFlag("zenith:spawner", Zenith.enableSpawner);
            api.setConfigFlag("zenith:garden", Zenith.enableGarden);
            api.setConfigFlag("zenith:potion", Zenith.enablePotion);
            api.setConfigFlag("zenith:village", Zenith.enableVillage);
        //    api.setConfigFlag("zenith:wstloaded", ModList.get().isLoaded("wstweaks"));
            api.setConfigFlag("zenith:curiosloaded", FabricLoader.getInstance().isModLoaded("trinkets"));
        }
    }

}
