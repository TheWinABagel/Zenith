package dev.shadowsoffire.apotheosis.ench;

import dev.shadowsoffire.apotheosis.Apotheosis;
import dev.shadowsoffire.apotheosis.ench.api.IEnchantingBlock;
import dev.shadowsoffire.apotheosis.ench.library.EnchLibraryScreen;
import dev.shadowsoffire.apotheosis.ench.table.ApothEnchScreen;
import dev.shadowsoffire.apotheosis.ench.table.ClueMessage;
import dev.shadowsoffire.apotheosis.ench.table.EnchantingStatRegistry;
import dev.shadowsoffire.apotheosis.ench.table.StatsMessage;
import dev.shadowsoffire.apotheosis.util.DrawsOnLeft;
import dev.shadowsoffire.placebo.util.EnchantmentUtils;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.item.v1.ItemTooltipCallback;
import net.fabricmc.fabric.api.client.particle.v1.ParticleFactoryRegistry;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.client.gui.screens.inventory.AnvilScreen;
import net.minecraft.client.particle.EnchantmentTableParticle;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.world.item.*;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Environment(EnvType.CLIENT)
@SuppressWarnings("deprecation")
public class EnchModuleClient {

    static BlockHitResult res = BlockHitResult.miss(Vec3.ZERO, Direction.NORTH, BlockPos.ZERO);

    @SuppressWarnings("NoTranslation")
    public static void tooltips() {
        ItemTooltipCallback.EVENT.register((stack, context, tooltip) -> {
            if (stack.is(Items.COBWEB)) tooltip.add(Component.translatable("info.zenith.cobweb").withStyle(ChatFormatting.GRAY));
            else if (stack.is(Ench.Items.PRISMATIC_WEB)) tooltip.add(Component.translatable("info.zenith.prismatic_cobweb").withStyle(ChatFormatting.GRAY));
            else if (stack.getItem() instanceof BlockItem blockItem) {
                Block block = blockItem.getBlock();
                Level world = Minecraft.getInstance().level;
                if (world == null || Minecraft.getInstance().player == null) return;
            //    BlockPlaceContext ctx = new BlockPlaceContext(world, Minecraft.getInstance().player, InteractionHand.MAIN_HAND, stack, res){};
                BlockState state = null;
            /*    try {
                    state = block.getStateForPlacement(ctx);
                }
                catch (Exception ex) {
                    EnchModule.LOGGER.debug(ex.getMessage());
                    StackTraceElement[] trace = ex.getStackTrace();
                    for (StackTraceElement traceElement : trace)
                        EnchModule.LOGGER.debug("\tat " + traceElement);
                }*/

                if (state == null) state = block.defaultBlockState();
                float maxEterna = EnchantingStatRegistry.getMaxEterna(state, world, BlockPos.ZERO);
                float eterna = EnchantingStatRegistry.getEterna(state, world, BlockPos.ZERO);
                float quanta = EnchantingStatRegistry.getQuanta(state, world, BlockPos.ZERO);
                float arcana = EnchantingStatRegistry.getArcana(state, world, BlockPos.ZERO);
                float rectification = EnchantingStatRegistry.getQuantaRectification(state, world, BlockPos.ZERO);
                int clues = EnchantingStatRegistry.getBonusClues(state, world, BlockPos.ZERO);
                boolean treasure = ((IEnchantingBlock) state.getBlock()).allowsTreasure(state, world, BlockPos.ZERO);
                if (eterna != 0 || quanta != 0 || arcana != 0 || rectification != 0 || clues != 0) {
                    tooltip.add(Component.translatable("info.zenith.ench_stats").withStyle(ChatFormatting.GOLD));
                }
                if (eterna != 0) {
                    if (eterna > 0) {
                        tooltip.add(Component.translatable("info.zenith.eterna.p", String.format("%.2f", eterna), String.format("%.2f", maxEterna)).withStyle(ChatFormatting.GREEN));
                    }
                    else tooltip.add(Component.translatable("info.zenith.eterna", String.format("%.2f", eterna)).withStyle(ChatFormatting.GREEN));
                }
                if (quanta != 0) {
                    tooltip.add(Component.translatable("info.zenith.quanta" + (quanta > 0 ? ".p" : ""), String.format("%.2f", quanta)).withStyle(ChatFormatting.RED));
                }
                if (arcana != 0) {
                    tooltip.add(Component.translatable("info.zenith.arcana" + (arcana > 0 ? ".p" : ""), String.format("%.2f", arcana)).withStyle(ChatFormatting.DARK_PURPLE));
                }
                if (rectification != 0) {
                    tooltip.add(Component.translatable("info.zenith.rectification" + (rectification > 0 ? ".p" : ""), String.format("%.2f", rectification)).withStyle(ChatFormatting.YELLOW));
                }
                if (clues != 0) {
                    tooltip.add(Component.translatable("info.zenith.clues" + (clues > 0 ? ".p" : ""), String.format("%d", clues)).withStyle(ChatFormatting.DARK_AQUA));
                }
                if (treasure) {
                    tooltip.add(Component.translatable("info.zenith.allows_treasure").withStyle(ChatFormatting.GOLD));
                }
                Set<Enchantment> blacklist = ((IEnchantingBlock) state.getBlock()).getBlacklistedEnchantments(state, world, BlockPos.ZERO);
                if (blacklist.size() > 0) {
                    tooltip.add(Component.translatable("info.zenith.filter").withStyle(s -> s.withColor(0x58B0CC)));
                    for (Enchantment ench : blacklist) {
                        MutableComponent name = (MutableComponent) ench.getFullname(1);
                        name.getSiblings().clear();
                        name.withStyle(s -> s.withColor(0x5878AA));
                        tooltip.add(Component.literal(" - ").append(name).withStyle(s -> s.withColor(0x5878AA)));
                    }
                }
            }
            else if (stack.is(Items.ENCHANTED_BOOK)) {
                var enchMap = EnchantmentHelper.getEnchantments(stack);
                if (enchMap.size() == 1) {
                    var ench = enchMap.keySet().iterator().next();
                    int lvl = enchMap.values().iterator().next();
                    if (!FabricLoader.getInstance().isModLoaded("enchdesc")) {
                        if (Apotheosis.MODID.equals(BuiltInRegistries.ENCHANTMENT.getKey(ench).getNamespace())) {
                            tooltip.add(Component.translatable(ench.getDescriptionId() + ".desc").withStyle(ChatFormatting.DARK_GRAY));
                        }
                    }
                    var info = EnchModule.getEnchInfo(ench);
                    Object[] args = new Object[4];
                    args[0] = boolComp("info.zenith.discoverable", info.isDiscoverable());
                    args[1] = boolComp("info.zenith.lootable", info.isLootable());
                    args[2] = boolComp("info.zenith.tradeable", info.isTradeable());
                    args[3] = boolComp("info.zenith.treasure", info.isTreasure());
                    if (context.isAdvanced()) {
                        tooltip.add(Component.translatable("%s \u2507 %s \u2507 %s \u2507 %s", args[0], args[1], args[2], args[3]).withStyle(ChatFormatting.DARK_GRAY));
                        tooltip.add(Component.translatable("info.zenith.book_range", info.getMinPower(lvl), info.getMaxPower(lvl)).withStyle(ChatFormatting.GREEN));
                    }
                    else {
                        tooltip.add(Component.translatable("%s \u2507 %s", args[2], args[3]).withStyle(ChatFormatting.DARK_GRAY));
                    }
                }
            }
        });

    }
/* //TODO implement via mixin
    public void drawAnvilCostBlob(ScreenEvent.Render.Post e) {
        if (e.getScreen() instanceof AnvilScreen anv) {
            int level = anv.getMenu().getCost();
            if (level <= 0 || !anv.getMenu().getSlot(anv.getMenu().getResultSlot()).hasItem()) return;
            List<Component> list = new ArrayList<>();
            list.add(Component.literal(I18n.get("info.zenith.anvil_at", level)).withStyle(ChatFormatting.UNDERLINE, ChatFormatting.GREEN));
            int expCost = EnchantmentUtils.getTotalExperienceForLevel(level);
            list.add(Component.translatable("info.zenith.anvil_xp_cost", Component.literal("" + expCost).withStyle(ChatFormatting.GREEN),
                    Component.literal("" + level).withStyle(ChatFormatting.GREEN)));
            DrawsOnLeft.draw(anv, e.getGuiGraphics(), list, anv.getGuiTop() + 28);
        }
    }*/


    private static Component boolComp(String key, boolean flag) {
        return Component.translatable(key + (flag ? "" : ".not")).withStyle(Style.EMPTY.withColor(flag ? 0x108810 : 0xAA1616));
    }

    public static void init() {
        tooltips();
        ClueMessage.init();
        StatsMessage.init();
        MenuScreens.register(Ench.Menus.ENCHANTING_TABLE, ApothEnchScreen::new);
        MenuScreens.register(Ench.Menus.LIBRARY, EnchLibraryScreen::new);
        particles();
    }

    public static void particles() {
        ParticleFactoryRegistry.getInstance().register(Ench.Particles.ENCHANT_FIRE, EnchantmentTableParticle.Provider::new);
        ParticleFactoryRegistry.getInstance().register(Ench.Particles.ENCHANT_WATER, EnchantmentTableParticle.Provider::new);
        ParticleFactoryRegistry.getInstance().register(Ench.Particles.ENCHANT_SCULK, EnchantmentTableParticle.Provider::new);
        ParticleFactoryRegistry.getInstance().register(Ench.Particles.ENCHANT_END, EnchantmentTableParticle.Provider::new);
    }
}
