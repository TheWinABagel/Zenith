package dev.shadowsoffire.apotheosis.adventure.client;

import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.blaze3d.vertex.VertexFormat;
import com.mojang.datafixers.util.Either;
import dev.shadowsoffire.apotheosis.Apoth;
import dev.shadowsoffire.apotheosis.Apotheosis;
import dev.shadowsoffire.apotheosis.adventure.Adventure.Menus;
import dev.shadowsoffire.apotheosis.adventure.AdventureConfig;
import dev.shadowsoffire.apotheosis.adventure.AdventureModule;
import dev.shadowsoffire.apotheosis.adventure.affix.Affix;
import dev.shadowsoffire.apotheosis.adventure.affix.AffixHelper;
import dev.shadowsoffire.apotheosis.adventure.affix.AffixInstance;
import dev.shadowsoffire.apotheosis.adventure.affix.AffixRegistry;
import dev.shadowsoffire.apotheosis.adventure.affix.reforging.ReforgingScreen;
import dev.shadowsoffire.apotheosis.adventure.affix.reforging.ReforgingTableTileRenderer;
import dev.shadowsoffire.apotheosis.adventure.affix.salvaging.SalvagingScreen;
import dev.shadowsoffire.apotheosis.adventure.affix.socket.SocketHelper;
import dev.shadowsoffire.apotheosis.adventure.affix.socket.gem.GemItem;
import dev.shadowsoffire.apotheosis.adventure.affix.socket.gem.cutting.GemCuttingScreen;
import dev.shadowsoffire.apotheosis.adventure.client.BossSpawnMessage.BossSpawnData;
import dev.shadowsoffire.apotheosis.adventure.client.SocketTooltipRenderer.SocketComponent;
import dev.shadowsoffire.apotheosis.util.Events;
import dev.shadowsoffire.apotheosis.util.events.ModifyComponents;
import dev.shadowsoffire.attributeslib.api.client.AddAttributeTooltipsEvent;
import dev.shadowsoffire.attributeslib.api.client.GatherEffectScreenTooltipsEvent;
import dev.shadowsoffire.attributeslib.api.client.GatherSkippedAttributeTooltipsEvent;
import dev.shadowsoffire.placebo.reload.DynamicHolder;
import io.github.fabricators_of_create.porting_lib.event.client.ModelLoadCallback;
import io.github.fabricators_of_create.porting_lib.event.client.PreRenderTooltipCallback;
import io.github.fabricators_of_create.porting_lib.util.client.ClientHooks;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.item.v1.ItemTooltipCallback;
import net.fabricmc.fabric.api.client.rendering.v1.TooltipComponentCallback;
import net.fabricmc.fabric.impl.client.model.loading.ModelLoaderHooks;
import net.minecraft.ChatFormatting;
import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderStateShard;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.ShaderInstance;
import net.minecraft.client.renderer.block.model.BlockModel;
import net.minecraft.client.renderer.blockentity.BeaconRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderers;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.ModelBakery;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.core.BlockPos;
import net.minecraft.locale.Language;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.network.chat.contents.LiteralContents;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.tooltip.TooltipComponent;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;
import org.apache.commons.lang3.mutable.MutableInt;

import java.io.IOException;
import java.util.*;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class AdventureModuleClient {

    public static List<BossSpawnData> BOSS_SPAWNS = new ArrayList<>();

    public static void init() {
    //    MenuScreens.register(Menus.REFORGING, ReforgingScreen::new);
    //    MenuScreens.register(Menus.SALVAGE, SalvagingScreen::new);
        MenuScreens.register(Menus.REFORGING, GemCuttingScreen::new);
        MenuScreens.register(Menus.SALVAGE, GemCuttingScreen::new);
        MenuScreens.register(Menus.GEM_CUTTING, GemCuttingScreen::new);
        //BlockEntityRenderers.register(Apoth.Tiles.REFORGING_TABLE, new ReforgingTableTileRenderer()); // Also model
        time();
        tooltips();
        ignoreSocketUUIDS();
        affixTooltips();
        comps(); // implement event
        ModBusSub.addGemModels();
        ModBusSub.hammer();
        ModBusSub.replaceGemModel();
        ModBusSub.tooltipComps();
    }

    public static void onBossSpawn(BlockPos pos, float[] color) {
        BOSS_SPAWNS.add(new BossSpawnData(pos, color, new MutableInt()));
        Minecraft.getInstance().getSoundManager()
            .play(new SimpleSoundInstance(SoundEvents.END_PORTAL_SPAWN, SoundSource.HOSTILE, AdventureConfig.bossAnnounceVolume, 1.25F, Minecraft.getInstance().player.random, Minecraft.getInstance().player.blockPosition()));
    }
 // TODO completely redo models...
    public static class ModBusSub {
        public static void hammer(){
            Events.AddModelCallback.EVENT.register(addedModels -> {
                addedModels.add((new ResourceLocation(Apotheosis.MODID, "item/hammer")));
            });
        }

     public static void addGemModels() {
         Events.AddModelCallback.EVENT.register(addedModels -> {
             Set<ResourceLocation> locs = Minecraft.getInstance().getResourceManager().listResources("models", loc -> Apotheosis.MODID.equals(loc.getNamespace()) && loc.getPath().contains("/gems/") && loc.getPath().endsWith(".json"))
                     .keySet();
             for (ResourceLocation s : locs) {
                 String path = s.getPath().substring("models/".length(), s.getPath().length() - ".json".length());
                 addedModels.add(Apotheosis.loc(path));
             }
         });
     }

     public static void replaceGemModel() {
         Events.ModifyBakedModelCallback.EVENT.register((bakery, topModels) -> {
             ModelResourceLocation key = new ModelResourceLocation(Apotheosis.loc("gem"), "inventory");
             BakedModel oldModel = topModels.get(key);
             if (oldModel != null) {
                 topModels.put(key, new GemModel(oldModel, bakery));
             }
         });

     }

     public static void tooltipComps() {

     }

    /*

    public static void comps(RenderTooltipEvent.GatherComponents e) {
        int sockets = SocketHelper.getSockets(e.getItemStack());
        if (sockets == 0) return;
        List<Either<FormattedText, TooltipComponent>> list = e.getTooltipElements();
        int rmvIdx = -1;
        for (int i = 0; i < list.size(); i++) {
            Optional<FormattedText> o = list.get(i).left();
            if (o.isPresent() && o.get() instanceof Component comp && comp.getContents() instanceof LiteralContents tc) {
                if ("APOTH_REMOVE_MARKER".equals(tc.text())) {
                    rmvIdx = i;
                    list.remove(i);
                    break;
                }
            }
        }
        if (rmvIdx == -1) return;
        e.getTooltipElements().add(rmvIdx, Either.right(new SocketComponent(e.getItemStack(), SocketHelper.getGems(e.getItemStack()))));
    }

        public static void tooltipComps(RegisterClientTooltipComponentFactoriesEvent e) {
            e.register(SocketComponent.class, SocketTooltipRenderer::new);
        }

        @SubscribeEvent
        public static void shaderRegistry(RegisterShadersEvent event) throws IOException {
            // Adds a shader to the list, the callback runs when loading is complete.
            event.registerShader(new ShaderInstance(event.getResourceProvider(), new ResourceLocation("apotheosis:gray"), DefaultVertexFormat.NEW_ENTITY), shaderInstance -> {
                CustomRenderTypes.grayShader = shaderInstance;
            });
        }*/
    }

    // This renders a beacon beam when a boss spawns
/*
    public static void render(RenderLevelStageEvent e) {
        if (e.getStage() != Stage.AFTER_TRIPWIRE_BLOCKS) return;
        PoseStack stack = e.getPoseStack();
        MultiBufferSource.BufferSource buf = MultiBufferSource.immediate(Tesselator.getInstance().getBuilder());
        Player p = Minecraft.getInstance().player;
        for (int i = 0; i < BOSS_SPAWNS.size(); i++) {
            BossSpawnData data = BOSS_SPAWNS.get(i);
            stack.pushPose();
            float partials = e.getPartialTick();
            Vec3 vec = Minecraft.getInstance().getCameraEntity().getEyePosition(partials);
            stack.translate(-vec.x, -vec.y, -vec.z);
            stack.translate(data.pos().getX(), data.pos().getY(), data.pos().getZ());
            BeaconRenderer.renderBeaconBeam(stack, buf, BeaconRenderer.BEAM_LOCATION, partials, 1, p.level().getGameTime(), 0, 64, data.color(), 0.166F, 0.33F);
            stack.popPose();
        }
        buf.endBatch();
    }*/

    public static void time() {
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            for (int i = 0; i < BOSS_SPAWNS.size(); i++) {
                BossSpawnData data = BOSS_SPAWNS.get(i);
                if (data.ticks().getAndIncrement() > 400) {
                    BOSS_SPAWNS.remove(i--);
                }
            }
        });

    }

    public static void tooltips() {
        AddAttributeTooltipsEvent.EVENT.register((stack, player, tooltip, attributeTooltipIterator, flag) -> {
            int sockets = SocketHelper.getSockets(stack);
            if (sockets > 0) attributeTooltipIterator.add(Component.literal("APOTH_REMOVE_MARKER"));
        });

    }

    public static void ignoreSocketUUIDS() {
        GatherSkippedAttributeTooltipsEvent.EVENT.register((stack, player, skips, flag) -> {
            for (ItemStack gem : SocketHelper.getGems(stack)) {
                skips.addAll(GemItem.getUUIDs(gem));
            }
        });

    }

    public static void comps() {
        TooltipComponentCallback.EVENT.register(data -> {
            if (data instanceof SocketComponent comp){
                return new SocketTooltipRenderer(comp);
            }
            return null;
        });
        ModifyComponents.MODIFY_COMPONENTS.register(e -> {
            int sockets = SocketHelper.getSockets(e.stack);
            if (sockets == 0) return;
            List<Either<FormattedText, TooltipComponent>> list = e.tooltipElements;
            int rmvIdx = -1;
            for (int i = 0; i < list.size(); i++) {
                Optional<FormattedText> o = list.get(i).left();
                if (o.isPresent() && o.get() instanceof Component comp && comp.getContents() instanceof LiteralContents tc) {
                    if ("APOTH_REMOVE_MARKER".equals(tc.text())) {
                        rmvIdx = i;
                        list.remove(i);
                        break;
                    }
                }
            }
            if (rmvIdx == -1) return;
            e.tooltipElements.add(rmvIdx, Either.right(new SocketComponent(e.stack, SocketHelper.getGems(e.stack))));
        });

    }

    public static void affixTooltips() {
        ItemTooltipCallback.EVENT.register((stack, context, lines) -> {
            if (stack.hasTag()) {
                Map<DynamicHolder<? extends Affix>, AffixInstance> affixes = AffixHelper.getAffixes(stack);
                List<Component> components = new ArrayList<>();
                Consumer<Component> dotPrefixer = afxComp -> {
                    components.add(Component.translatable("text.apotheosis.dot_prefix", afxComp).withStyle(ChatFormatting.YELLOW));
                };
                affixes.values().stream().sorted(Comparator.comparingInt(a -> a.affix().get().getType().ordinal())).forEach(inst -> inst.addInformation(dotPrefixer));
                lines.addAll(1, components);
            }
        });

    }
    // there has to be a better way to do this...
    public static List<ClientTooltipComponent> gatherTooltipComponents(ItemStack stack, List<? extends FormattedText> textElements, Optional<TooltipComponent> itemComponent, int mouseX, int screenWidth, int screenHeight, Font font) {

        List<Either<FormattedText, TooltipComponent>> elements = textElements.stream()
                .map((Function<FormattedText, Either<FormattedText, TooltipComponent>>) Either::left)
                .collect(Collectors.toCollection(ArrayList::new));
        itemComponent.ifPresent(c -> elements.add(1, Either.right(c)));

        var event = new ModifyComponents.ModifyComponentsEvent(stack, screenWidth, screenHeight, elements, -1);
        ModifyComponents.MODIFY_COMPONENTS.invoker().modifyComponents(event);
        if (event.isCanceled()) return List.of();

        // text wrapping
        int tooltipTextWidth = event.tooltipElements.stream()
                .mapToInt(either -> either.map(font::width, component -> 0))
                .max()
                .orElse(0);

        boolean needsWrap = false;

        int tooltipX = mouseX + 12;
        if (tooltipX + tooltipTextWidth + 4 > screenWidth)
        {
            tooltipX = mouseX - 16 - tooltipTextWidth;
            if (tooltipX < 4) // if the tooltip doesn't fit on the screen
            {
                if (mouseX > screenWidth / 2)
                    tooltipTextWidth = mouseX - 12 - 8;
                else
                    tooltipTextWidth = screenWidth - 16 - mouseX;
                needsWrap = true;
            }
        }

        if (event.maxWidth > 0 && tooltipTextWidth > event.maxWidth)
        {
            tooltipTextWidth = event.maxWidth;
            needsWrap = true;
        }

        int tooltipTextWidthF = tooltipTextWidth;
        if (needsWrap)
        {
            return event.tooltipElements.stream()
                    .flatMap(either -> either.map(
                            text -> splitLine(text, font, tooltipTextWidthF),
                            component -> Stream.of(ClientTooltipComponent.create(component))
                    ))
                    .toList();
        }
        return event.tooltipElements.stream()
                .map(either -> either.map(
                        text -> ClientTooltipComponent.create(text instanceof Component ? ((Component) text).getVisualOrderText() : Language.getInstance().getVisualOrder(text)),
                        ClientTooltipComponent::create
                ))
                .toList();
    }

    private static Stream<ClientTooltipComponent> splitLine(FormattedText text, Font font, int maxWidth) {
        if (text instanceof Component component && component.getString().isEmpty()) {
            return Stream.of(component.getVisualOrderText()).map(ClientTooltipComponent::create);
        }
        return font.split(text, maxWidth).stream().map(ClientTooltipComponent::create);
    }

    // Accessor functon, ensures that you don't use the raw methods below unintentionally.
    public static RenderType gray(ResourceLocation texture) {
        return CustomRenderTypes.GRAY.apply(texture);
    }

    // Keep private because this stuff isn't meant to be public
    private static class CustomRenderTypes extends RenderType {
        // Holds the object loaded via RegisterShadersEvent
        private static ShaderInstance grayShader;

        // Shader state for use in the render type, the supplier ensures it updates automatically with resource reloads
        private static final ShaderStateShard RENDER_TYPE_GRAY = new ShaderStateShard(() -> grayShader);

        // The memoize caches the output value for each input, meaning the expensive registration process doesn't have to rerun
        public static Function<ResourceLocation, RenderType> GRAY = Util.memoize(CustomRenderTypes::gray);

        // Defines the RenderType. Make sure the name is unique by including your MODID in the name.
        private static RenderType gray(ResourceLocation loc) {

            CompositeState rendertype$state = CompositeState.builder()
                .setShaderState(RENDER_TYPE_GRAY)
                .setTextureState(new TextureStateShard(loc, false, false))
                .setTransparencyState(TRANSLUCENT_TRANSPARENCY)
                .setOutputState(ITEM_ENTITY_TARGET)
                .setLightmapState(LIGHTMAP).setOverlayState(OVERLAY)
                .setWriteMaskState(RenderStateShard.COLOR_DEPTH_WRITE)
                .createCompositeState(true);
            return create("gray", DefaultVertexFormat.NEW_ENTITY, VertexFormat.Mode.QUADS, 256, true, false, rendertype$state);

        }

        // Dummy constructor needed to make java happy
        private CustomRenderTypes(String s, VertexFormat v, VertexFormat.Mode m, int i, boolean b, boolean b2, Runnable r, Runnable r2) {
            super(s, v, m, i, b, b2, r, r2);
            throw new IllegalStateException("This class is not meant to be constructed!");
        }
    }

    public static void checkAffixLangKeys() {
        StringBuilder sb = new StringBuilder("Missing Affix Lang Keys:\n");
        boolean any = false;
        String json = "\"%s\": \"\",";
        for (Affix a : AffixRegistry.INSTANCE.getValues()) {
            ResourceLocation id = AffixRegistry.INSTANCE.getKey(a);
            if (!I18n.exists("affix." + id)) {
                sb.append(json.formatted("affix." + id) + "\n");
                any = true;
            }
            if (!I18n.exists("affix." + id + ".suffix")) {
                sb.append(json.formatted("affix." + id + ".suffix") + "\n");
                any = true;
            }
        }
        if (any) AdventureModule.LOGGER.error(sb.toString());
    }

}
