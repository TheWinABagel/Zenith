package dev.shadowsoffire.apotheosis.potion;

import net.fabricmc.fabric.api.client.rendering.v1.ColorProviderRegistry;
import net.minecraft.world.item.alchemy.PotionUtils;

public class PotionModuleClient {

    public static void init() {
        ColorProviderRegistry.ITEM.register((stack, tint) -> PotionUtils.getColor(stack), PotionModule.POTION_CHARM);
    }

}
