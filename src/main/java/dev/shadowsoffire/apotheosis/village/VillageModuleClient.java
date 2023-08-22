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
        EntityRenderers.register(Apoth.Entities.OBSIDIAN_ARROW, ObsidianArrowRenderer::new);
        EntityRenderers.register(Apoth.Entities.BROADHEAD_ARROW, BroadheadArrowRenderer::new);
        EntityRenderers.register(Apoth.Entities.EXPLOSIVE_ARROW, ExplosiveArrowRenderer::new);
        EntityRenderers.register(Apoth.Entities.MINING_ARROW, MiningArrowRenderer::new);
    }
}
