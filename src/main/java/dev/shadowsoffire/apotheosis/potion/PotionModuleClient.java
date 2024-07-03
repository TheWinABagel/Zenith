package dev.shadowsoffire.apotheosis.potion;

import dev.shadowsoffire.apotheosis.Apotheosis;
import net.fabricmc.fabric.api.client.rendering.v1.ColorProviderRegistry;
import net.minecraft.client.renderer.item.ItemProperties;
import net.minecraft.world.item.alchemy.PotionUtils;

public class PotionModuleClient {

    public static void init() {
        ColorProviderRegistry.ITEM.register((stack, tint) -> tint == 0 ? -1 : PotionUtils.getColor(stack), PotionModule.POTION_CHARM);
        ItemProperties.register(PotionModule.POTION_CHARM, Apotheosis.loc("enabled"), (stack, level, entity, tint) -> {
            return stack.hasTag() && stack.getTag().getBoolean("charm_enabled") ? 1 : 0;
        });
    }

}
