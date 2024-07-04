package dev.shadowsoffire.apotheosis.adventure.client;

import com.anthonyhilyard.iceberg.events.RenderTooltipEvents;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexFormat;
import com.mojang.datafixers.util.Either;
import dev.shadowsoffire.apotheosis.Apotheosis;
import dev.shadowsoffire.apotheosis.adventure.Adventure;
import dev.shadowsoffire.apotheosis.adventure.Adventure.Menus;
import dev.shadowsoffire.apotheosis.adventure.AdventureConfig;
import dev.shadowsoffire.apotheosis.adventure.AdventureModule;
import dev.shadowsoffire.apotheosis.adventure.affix.Affix;
import dev.shadowsoffire.apotheosis.adventure.affix.AffixHelper;
import dev.shadowsoffire.apotheosis.adventure.affix.AffixInstance;
import dev.shadowsoffire.apotheosis.adventure.affix.AffixRegistry;
import dev.shadowsoffire.apotheosis.adventure.affix.augmenting.AugmentingScreen;
import dev.shadowsoffire.apotheosis.adventure.affix.augmenting.AugmentingTableTileRenderer;
import dev.shadowsoffire.apotheosis.adventure.affix.reforging.ReforgingScreen;
import dev.shadowsoffire.apotheosis.adventure.affix.reforging.ReforgingTableTileRenderer;
import dev.shadowsoffire.apotheosis.adventure.affix.salvaging.SalvagingScreen;
import dev.shadowsoffire.apotheosis.adventure.client.BossSpawnMessage.BossSpawnData;
import dev.shadowsoffire.apotheosis.adventure.client.SocketTooltipRenderer.SocketComponent;
import dev.shadowsoffire.apotheosis.adventure.socket.SocketHelper;
import dev.shadowsoffire.apotheosis.adventure.socket.gem.GemInstance;
import dev.shadowsoffire.apotheosis.adventure.socket.gem.cutting.GemCuttingScreen;
import dev.shadowsoffire.apotheosis.util.events.ModifyComponents;
import dev.shadowsoffire.attributeslib.api.client.AddAttributeTooltipsEvent;
import dev.shadowsoffire.attributeslib.api.client.GatherSkippedAttributeTooltipsEvent;
import dev.shadowsoffire.attributeslib.api.client.ItemTooltipCallbackWithPlayer;
import dev.shadowsoffire.placebo.reload.DynamicHolder;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.rendering.v1.CoreShaderRegistrationCallback;
import net.fabricmc.fabric.api.client.rendering.v1.TooltipComponentCallback;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderEvents;
import net.fabricmc.loader.api.FabricLoader;
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
import net.minecraft.client.renderer.blockentity.BeaconRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderers;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.core.BlockPos;
import net.minecraft.locale.Language;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentContents;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.network.chat.contents.LiteralContents;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.tooltip.TooltipComponent;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;
import org.apache.commons.lang3.mutable.MutableInt;

import java.util.*;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class AdventureModuleClient {

    public static List<BossSpawnData> BOSS_SPAWNS = new ArrayList<>();

    public static void init() {
        MenuScreens.register(Menus.REFORGING, ReforgingScreen::new);
        MenuScreens.register(Menus.SALVAGE, SalvagingScreen::new);
        MenuScreens.register(Menus.GEM_CUTTING, GemCuttingScreen::new);
        MenuScreens.register(Menus.AUGMENTING, AugmentingScreen::new);

        BlockEntityRenderers.register(Adventure.Tiles.REFORGING_TABLE, context -> new ReforgingTableTileRenderer());
        BlockEntityRenderers.register(Adventure.Tiles.AUGMENTING_TABLE, context -> new AugmentingTableTileRenderer());
        time();
        tooltips();
        ignoreSocketUUIDS();
        affixTooltips();
        comps();
        renderBossBeam();
        BossSpawnMessage.init();
        CoreShaderRegistrationCallback.EVENT.register(context -> context.register(Apotheosis.loc("gray"), DefaultVertexFormat.NEW_ENTITY, shaderInstance -> {}));
        AdventureKeys.registerKeys();
        AdventureKeys.handleKeys();
    }

    public static void onBossSpawn(BlockPos pos, float[] color) {
        BOSS_SPAWNS.add(new BossSpawnData(pos, color, new MutableInt()));
        Minecraft.getInstance().getSoundManager()
            .play(new SimpleSoundInstance(SoundEvents.END_PORTAL_SPAWN, SoundSource.HOSTILE, AdventureConfig.bossAnnounceVolume, 1.25F, Minecraft.getInstance().player.getRandom(), Minecraft.getInstance().player.blockPosition()));
    }

    // This renders a beacon beam when a boss spawns
    public static void renderBossBeam() {
        WorldRenderEvents.AFTER_ENTITIES.register((context) -> {
            PoseStack stack = context.matrixStack();

            MultiBufferSource.BufferSource buf = Minecraft.getInstance().renderBuffers().bufferSource();
            Player p = Minecraft.getInstance().player;
            for (int i = 0; i < BOSS_SPAWNS.size(); i++) {
                BossSpawnData data = BOSS_SPAWNS.get(i);

                stack.pushPose();
                float partials = context.tickDelta();

                Vec3 vec = context.camera().getPosition();
                stack.translate(-vec.x, -vec.y, -vec.z);
                stack.translate(data.pos().getX(), data.pos().getY(), data.pos().getZ());
                BeaconRenderer.renderBeaconBeam(stack, buf, BeaconRenderer.BEAM_LOCATION, partials, 1, p.level().getGameTime(), 0, 512, data.color(), 0.166F, 0.33F);
                stack.popPose();
            }
            buf.endLastBatch();
        });
    }

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
            if (sockets > 0) attributeTooltipIterator.add(Component.literal("ZENITH_REMOVE_MARKER"));
        });
    }

    public static void ignoreSocketUUIDS() {
        GatherSkippedAttributeTooltipsEvent.EVENT.register((stack, player, skips, flag) -> {
            for (GemInstance gem : SocketHelper.getGems(stack)) {
                skips.addAll(gem.getUUIDs());
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
        if (FabricLoader.getInstance().isModLoaded("iceberg")) {
            RenderTooltipEvents.GATHER.register((itemStack, screenWidth, screenHeight, tooltipElements, maxWidth, index) -> {
                ModifyComponents.ModifyComponentsEvent event = new ModifyComponents.ModifyComponentsEvent(itemStack, screenWidth, screenHeight, tooltipElements, maxWidth);
                ModifyComponents.MODIFY_COMPONENTS.invoker().modifyComponents(event);
                InteractionResult result = event.isCanceled() ? InteractionResult.CONSUME : InteractionResult.PASS;
                return new RenderTooltipEvents.GatherResult(result, event.maxWidth, event.tooltipElements);
            });
        }
        ModifyComponents.MODIFY_COMPONENTS.register(e -> {
            int sockets = SocketHelper.getSockets(e.stack);
            if (sockets == 0) return;
            List<Either<FormattedText, TooltipComponent>> list = e.tooltipElements;
            int rmvIdx = -1;
            for (int i = 0; i < list.size(); i++) {
                Optional<FormattedText> o = list.get(i).left();
                if (o.isPresent() && o.get() instanceof Component comp && comp.getContents() instanceof LiteralContents tc) {
                    if ("ZENITH_REMOVE_MARKER".equals(tc.text())) {
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
        ItemTooltipCallbackWithPlayer.EVENT.register((stack, context, lines, player) -> {
            if (stack.hasTag()) {
                Map<DynamicHolder<? extends Affix>, AffixInstance> affixes = AffixHelper.getAffixes(stack);
                List<Component> components = new ArrayList<>();
                Consumer<Component> dotPrefixer = afxComp -> {
                    components.add(Component.translatable("text.zenith.dot_prefix", afxComp).withStyle(ChatFormatting.YELLOW));
                };
                affixes.values().stream()
                        .sorted(Comparator.comparingInt(a -> a.affix().get().getType().ordinal()))
                        .map(AffixInstance::getDescription)
                        .filter(c -> c.getContents() != ComponentContents.EMPTY)
                        .forEach(dotPrefixer);


                lines.addAll(1, components);
            }
        });
    }

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
        if (tooltipX + tooltipTextWidth + 4 > screenWidth) {
            tooltipX = mouseX - 16 - tooltipTextWidth;
            if (tooltipX < 4) { // if the tooltip doesn't fit on the screen
                if (mouseX > screenWidth / 2)
                    tooltipTextWidth = mouseX - 12 - 8;
                else
                    tooltipTextWidth = screenWidth - 16 - mouseX;
                needsWrap = true;
            }
        }
        if (event.maxWidth > 0 && tooltipTextWidth > event.maxWidth) {
            tooltipTextWidth = event.maxWidth;
            needsWrap = true;
        }

        int tooltipTextWidthF = tooltipTextWidth;
        if (needsWrap) {
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

    public static class StackStorage { // Need a place to keep this saved
        public static ItemStack hoveredItem = ItemStack.EMPTY;
    }
}
