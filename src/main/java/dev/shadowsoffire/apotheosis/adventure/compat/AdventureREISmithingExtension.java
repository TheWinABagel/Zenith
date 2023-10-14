package dev.shadowsoffire.apotheosis.adventure.compat;

import dev.shadowsoffire.apotheosis.Apotheosis;
import dev.shadowsoffire.apotheosis.adventure.affix.socket.*;
import dev.shadowsoffire.apotheosis.util.IGetRecipe;
import me.shedaniel.math.Point;
import me.shedaniel.math.Rectangle;
import me.shedaniel.rei.api.client.gui.DisplayRenderer;
import me.shedaniel.rei.api.client.gui.screen.DisplayScreen;
import me.shedaniel.rei.api.client.gui.widgets.Widget;
import me.shedaniel.rei.api.client.gui.widgets.Widgets;
import me.shedaniel.rei.api.client.registry.category.extension.CategoryExtensionProvider;
import me.shedaniel.rei.api.client.registry.display.DisplayCategory;
import me.shedaniel.rei.api.client.registry.display.DisplayCategoryView;
import me.shedaniel.rei.api.common.entry.EntryStack;
import me.shedaniel.rei.api.common.util.EntryStacks;
import me.shedaniel.rei.plugin.common.displays.DefaultSmithingDisplay;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.SmithingRecipe;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;

@SuppressWarnings("UnstableApiUsage")
public class AdventureREISmithingExtension implements CategoryExtensionProvider<DefaultSmithingDisplay> {

    private static final Stream<ItemStack> DUMMY_INPUTS = Arrays.asList(Items.GOLDEN_SWORD, Items.DIAMOND_PICKAXE, Items.STONE_AXE, Items.IRON_CHESTPLATE, Items.TRIDENT).stream().map(ItemStack::new);

    @Override
    public DisplayCategoryView<DefaultSmithingDisplay> provide(DefaultSmithingDisplay display, DisplayCategory<DefaultSmithingDisplay> category, DisplayCategoryView<DefaultSmithingDisplay> lastView) {


        if (Minecraft.getInstance().screen instanceof DisplayScreen displayScreen && Objects.equals(display.getOutputEntries().get(0).get(0).getIdentifier(), new ResourceLocation("minecraft:air"))) {
            SmithingRecipe recipe = ((IGetRecipe) display).getRecipe();
            return new AdventureSmithingREI(lastView, recipe);
        }
        return lastView;
    }
        private final class AdventureSmithingREI implements DisplayCategoryView<DefaultSmithingDisplay> {

            private final DisplayCategoryView<DefaultSmithingDisplay> lastView;
            private final SmithingRecipe recipe;
            private static final List<ItemStack> DUMMY_INPUTS = Arrays.asList(Items.GOLDEN_SWORD, Items.DIAMOND_PICKAXE, Items.STONE_AXE, Items.IRON_CHESTPLATE, Items.TRIDENT).stream().map(ItemStack::new).toList();
            private static final List<EntryStack<ItemStack>> DUMMY_INPUT = Arrays.asList(Items.GOLDEN_SWORD, Items.DIAMOND_PICKAXE, Items.STONE_AXE, Items.IRON_CHESTPLATE, Items.TRIDENT).stream().map(ItemStack::new).map(EntryStacks::of).toList();
            private static final List<EntryStack<ItemStack>> DUMMY_OUTPUTS = DUMMY_INPUTS.stream().map(ItemStack::copy).map(s -> {
                SocketHelper.setSockets(s, 1);
                return s;
            }).map(EntryStacks::of).toList();


            private AdventureSmithingREI(DisplayCategoryView<DefaultSmithingDisplay> lastView, SmithingRecipe recipe) {
                this.lastView = lastView;
                this.recipe = recipe;
            }

            @Override
            public DisplayRenderer getDisplayRenderer(DefaultSmithingDisplay display) {
                return lastView.getDisplayRenderer(display);
            }

            @Override
            public List<Widget> setupDisplay(DefaultSmithingDisplay display, Rectangle bounds) {
                    int pX = bounds.x - 5;
                    int pY = bounds.y - 5;
                    int size = 5;

                    var widgets = lastView.setupDisplay(display, bounds);
                    widgets.add(Widgets.createDrawableWidget(
                            (guiGraphics, mX, mY, delta) -> {
                            //    renderIndicator(guiGraphics, pX, pY, size);

                            //    guiGraphics.renderItem(Items.DIAMOND_SWORD.getDefaultInstance(), pX+30, pY+20);
                            }
                    ));

                Point startPoint = new Point(bounds.getCenterX() - 31, bounds.getCenterY() - 13);
                //widgets.add(Widgets.createRecipeBase(bounds));
                int offsetX = 5;
        //        widgets.add(Widgets.createArrow(new Point(startPoint.x + 27 + offsetX, startPoint.y + 4)));
        //        widgets.add(Widgets.createResultSlotBackground(new Point(startPoint.x + 61 + offsetX, startPoint.y + 5)));

            //        widgets.add(Widgets.createSlot(new Point(startPoint.x + 4 - 18 * 2 + offsetX, startPoint.y + 5)).entries(List.of(EntryStacks.of(Items.GOLDEN_APPLE.getDefaultInstance()))).markInput());

                if (recipe instanceof AddSocketsRecipe) {
                    widgets.add(Widgets.createSlot(new Point(startPoint.x + 4 - 18 + offsetX, startPoint.y + 5)).entries(DUMMY_INPUT).markInput());
                    widgets.add(Widgets.createSlot(new Point(startPoint.x + 4 + offsetX, startPoint.y + 5)).entries(display.getInputEntries().get(2)).markInput());

                    widgets.add(Widgets.createSlot(new Point(startPoint.x + 61 + offsetX, startPoint.y + 5)).entries(DUMMY_OUTPUTS).disableBackground().markOutput());
                } else if (recipe instanceof SocketingRecipe) {
                    widgets.add(Widgets.createSlot(new Point(startPoint.x + 4 - 18 + offsetX, startPoint.y + 5)).entries(List.of(EntryStacks.of(Items.GOLDEN_APPLE.getDefaultInstance()))).markInput());
                    widgets.add(Widgets.createSlot(new Point(startPoint.x + 4 + offsetX, startPoint.y + 5)).entries(display.getInputEntries().get(2)).markInput());

                    widgets.add(Widgets.createSlot(new Point(startPoint.x + 61 + offsetX, startPoint.y + 5)).entries(List.of(EntryStacks.of(Items.GOLDEN_APPLE.getDefaultInstance()))).disableBackground().markOutput());
                } else if (recipe instanceof ExpulsionRecipe) {
                    widgets.add(Widgets.createSlot(new Point(startPoint.x + 4 - 18 + offsetX, startPoint.y + 5)).entries(List.of(EntryStacks.of(Items.GOLDEN_SWORD.getDefaultInstance()))).markInput());
                    widgets.add(Widgets.createSlot(new Point(startPoint.x + 4 + offsetX, startPoint.y + 5)).entries(display.getInputEntries().get(2)).markInput());

                    widgets.add(Widgets.createSlot(new Point(startPoint.x + 61 + offsetX, startPoint.y + 5)).entries(List.of(EntryStacks.of(Items.GOLDEN_APPLE.getDefaultInstance()))).disableBackground().markOutput());
                } else if (recipe instanceof ExtractionRecipe) {
                    widgets.add(Widgets.createSlot(new Point(startPoint.x + 4 - 18 + offsetX, startPoint.y + 5)).entries(List.of(EntryStacks.of(Items.IRON_CHESTPLATE.getDefaultInstance()))).markInput());
                    widgets.add(Widgets.createSlot(new Point(startPoint.x + 4 + offsetX, startPoint.y + 5)).entries(display.getInputEntries().get(2)).markInput());

                    widgets.add(Widgets.createSlot(new Point(startPoint.x + 61 + offsetX, startPoint.y + 5)).entries(List.of(EntryStacks.of(Items.GOLDEN_APPLE.getDefaultInstance()))).disableBackground().markOutput());
                } else if (recipe instanceof UnnamingRecipe) {
                    widgets.add(Widgets.createSlot(new Point(startPoint.x + 4 - 18 + offsetX, startPoint.y + 5)).entries(List.of(EntryStacks.of(Items.DIAMOND_BOOTS.getDefaultInstance()))).markInput());
                    widgets.add(Widgets.createSlot(new Point(startPoint.x + 4 + offsetX, startPoint.y + 5)).entries(display.getInputEntries().get(2)).markInput());

                    widgets.add(Widgets.createSlot(new Point(startPoint.x + 61 + offsetX, startPoint.y + 5)).entries(List.of(EntryStacks.of(Items.GOLDEN_APPLE.getDefaultInstance()))).disableBackground().markOutput());
                }


                var tooltipArea = new Rectangle(pX, pY, size, size);
                    widgets.add(Widgets.createTooltip(tooltipArea, Component.literal("woooo")));

                    return widgets;
                }
            }

            static void renderIndicator(GuiGraphics guiGraphics, int pX, int pY, int size) {
                var poseStack = guiGraphics.pose();
                poseStack.pushPose();

                poseStack.translate(pX, pY, 0);
                var scale = size / (float) 16;
                poseStack.scale(scale, scale, scale);
                guiGraphics.blit(Apotheosis.loc("textures/gui/gem_cutting_jei.png"), 0, 0, 0, 0, 148, 78, 148, 78);

                poseStack.popPose();
            }
    }


