package dev.shadowsoffire.apotheosis.village;

import dev.shadowsoffire.apotheosis.Apoth;
import dev.shadowsoffire.apotheosis.village.fletching.FletchingScreen;
import dev.shadowsoffire.apotheosis.village.fletching.arrows.BroadheadArrowRenderer;
import dev.shadowsoffire.apotheosis.village.fletching.arrows.ExplosiveArrowRenderer;
import dev.shadowsoffire.apotheosis.village.fletching.arrows.MiningArrowRenderer;
import dev.shadowsoffire.apotheosis.village.fletching.arrows.ObsidianArrowRenderer;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.client.renderer.entity.EntityRenderers;

public class VillageModuleClient {
    public static void init() {
        MenuScreens.register(Apoth.Menus.FLETCHING, FletchingScreen::new);
        EntityRenderers.register(VillageModule.OBSIDIAN_ARROW_ENTITY, ObsidianArrowRenderer::new);
        EntityRenderers.register(VillageModule.BROADHEAD_ARROW_ENTITY, BroadheadArrowRenderer::new);
        EntityRenderers.register(VillageModule.EXPLOSIVE_ARROW_ENTITY, ExplosiveArrowRenderer::new);
        EntityRenderers.register(VillageModule.MINING_ARROW_ENTITY, MiningArrowRenderer::new);
    }
}
