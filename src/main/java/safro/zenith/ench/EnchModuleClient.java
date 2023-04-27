package safro.zenith.ench;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.item.v1.ItemTooltipCallback;
import net.fabricmc.fabric.api.client.screenhandler.v1.ScreenRegistry;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.*;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import safro.zenith.ench.library.EnchLibraryScreen;
import safro.zenith.ench.table.ApothEnchantScreen;
import safro.zenith.ench.table.EnchantingStatManager;

public class EnchModuleClient{
    static BlockHitResult res = BlockHitResult.miss(Vec3.ZERO, Direction.NORTH, BlockPos.ZERO);

    public static void tooltips() {
        ItemTooltipCallback.EVENT.register((stack, context, lines) -> {
            Item i = stack.getItem();
            if (i == Items.COBWEB) lines.add(Component.translatable("info.zenith.cobweb").withStyle(ChatFormatting.GRAY));
            else if (i == EnchModule.PRISMATIC_WEB) lines.add(Component.translatable("info.zenith.prismatic_cobweb").withStyle(ChatFormatting.GRAY));
            else if (i instanceof BlockItem) {
                Block block = ((BlockItem) i).getBlock();
                Level world = Minecraft.getInstance().level;
                if (world == null || Minecraft.getInstance().player == null) return;
                BlockPlaceContext ctx = new BlockPlaceContext(world, Minecraft.getInstance().player, InteractionHand.MAIN_HAND, stack, res) {};
                BlockState state = null;
                try {
                    state = block.getStateForPlacement(ctx);
                } catch (Exception ex) {
                    EnchModule.LOGGER.debug(ex.getMessage());
                    StackTraceElement[] trace = ex.getStackTrace();
                    for (StackTraceElement traceElement : trace)
                        EnchModule.LOGGER.debug("\tat " + traceElement);
                }

                if (state == null) state = block.defaultBlockState();
                float maxEterna = EnchantingStatManager.getMaxEterna(state, world, BlockPos.ZERO);
                float eterna = EnchantingStatManager.getEterna(state, world, BlockPos.ZERO);
                float quanta = EnchantingStatManager.getQuanta(state, world, BlockPos.ZERO);
                float arcana = EnchantingStatManager.getArcana(state, world, BlockPos.ZERO);
                float rectification = EnchantingStatManager.getQuantaRectification(state, world, BlockPos.ZERO);
                int clues = EnchantingStatManager.getBonusClues(state, world, BlockPos.ZERO);
                if (eterna != 0 || quanta != 0 || arcana != 0 || rectification != 0 || clues != 0) {
                    lines.add(Component.translatable("info.zenith.ench_stats").withStyle(ChatFormatting.GOLD));
                }
                if (eterna != 0) {
                    if (eterna > 0) {
                        lines.add(Component.translatable("info.zenith.eterna.p", String.format("%.2f", eterna), String.format("%.2f", maxEterna)).withStyle(ChatFormatting.GREEN));
                    } else lines.add(Component.translatable("info.zenith.eterna", String.format("%.2f", eterna)).withStyle(ChatFormatting.GREEN));
                }
                if (quanta != 0) {
                    lines.add(Component.translatable("info.zenith.quanta" + (quanta > 0 ? ".p" : ""), String.format("%.2f", quanta)).withStyle(ChatFormatting.RED));
                }
                if (arcana != 0) {
                    lines.add(Component.translatable("info.zenith.arcana" + (arcana > 0 ? ".p" : ""), String.format("%.2f", arcana)).withStyle(ChatFormatting.DARK_PURPLE));
                }
                if (rectification != 0) {
                    lines.add(Component.translatable("info.zenith.rectification" + (rectification > 0 ? ".p" : ""), String.format("%.2f", rectification)).withStyle(ChatFormatting.YELLOW));
                }
                if (clues != 0) {
                    lines.add(Component.translatable("info.zenith.clues" + (clues > 0 ? ".p" : ""), String.format("%d", clues)).withStyle(ChatFormatting.DARK_AQUA));
                }
            }

        });
    }

    public static void init() {
        MenuScreens.register(EnchModule.LIBRARY_CONTAINER, EnchLibraryScreen::new);
        MenuScreens.register(EnchModule.ENCHANTING_TABLE_MENU, ApothEnchantScreen::new);
        tooltips();
    }
}
