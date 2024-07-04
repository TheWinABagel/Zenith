package dev.shadowsoffire.apotheosis.adventure.compat;

import dev.shadowsoffire.apotheosis.Apotheosis;
import dev.shadowsoffire.apotheosis.adventure.socket.AddSocketsRecipe;
import dev.shadowsoffire.apotheosis.adventure.socket.SocketHelper;
import dev.shadowsoffire.apotheosis.adventure.socket.SocketingRecipe;
import dev.shadowsoffire.apotheosis.adventure.socket.gem.Gem;
import dev.shadowsoffire.apotheosis.adventure.socket.gem.GemRegistry;
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
import me.shedaniel.rei.api.common.util.EntryStacks;
import me.shedaniel.rei.plugin.common.displays.DefaultSmithingDisplay;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.SmithingRecipe;
import net.minecraft.world.level.levelgen.LegacyRandomSource;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

@SuppressWarnings("UnstableApiUsage")
public class AdventureREISmithingExtension implements CategoryExtensionProvider<DefaultSmithingDisplay> {

    @Override
    public DisplayCategoryView<DefaultSmithingDisplay> provide(DefaultSmithingDisplay display, DisplayCategory<DefaultSmithingDisplay> category, DisplayCategoryView<DefaultSmithingDisplay> lastView) {
        if (Minecraft.getInstance().screen instanceof DisplayScreen && Objects.equals(display.getOutputEntries().get(0).get(0).getIdentifier(), new ResourceLocation("minecraft:air"))) {//bleh
            SmithingRecipe recipe = ((IGetRecipe) display).getRecipe();
            return new AdventureSmithingREI(lastView, recipe);
        }
        return lastView;
    }
        private final class AdventureSmithingREI implements DisplayCategoryView<DefaultSmithingDisplay> {

            private final DisplayCategoryView<DefaultSmithingDisplay> lastView;
            private final SmithingRecipe recipe;
            private static final List<ItemStack> DUMMY_INPUTS = Arrays.asList(Items.GOLDEN_SWORD, Items.DIAMOND_PICKAXE, Items.STONE_AXE, Items.IRON_CHESTPLATE, Items.TRIDENT).stream().map(ItemStack::new).toList();
            private static final List<ItemStack> ADD_SOCKET_DUMMY_OUTPUTS = DUMMY_INPUTS.stream().map(ItemStack::copy).map(s -> {
                SocketHelper.setSockets(s, 1);
                return s;
            }).toList();


            private AdventureSmithingREI(DisplayCategoryView<DefaultSmithingDisplay> lastView, SmithingRecipe recipe) {
                this.lastView = lastView;
                this.recipe = recipe;
            }

            @SuppressWarnings("OverrideOnly")
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
                if (recipe instanceof AddSocketsRecipe rec) {
                    widgets.add(Widgets.createSlot(new Point(startPoint.x + 4 - 18 + offsetX, startPoint.y + 5)).entries(DUMMY_INPUTS.stream().map(EntryStacks::of).toList()).markInput());
                    widgets.add(Widgets.createSlot(new Point(startPoint.x + 4 + offsetX, startPoint.y + 5)).entries(display.getInputEntries().get(2)).markInput());

                    widgets.add(Widgets.createSlot(new Point(startPoint.x + 61 + offsetX, startPoint.y + 5)).entries(ADD_SOCKET_DUMMY_OUTPUTS.stream().map(EntryStacks::of).toList()).disableBackground().markOutput());
                } else if (recipe instanceof SocketingRecipe rec) {
                    ItemStack gem = new ItemStack(dev.shadowsoffire.apotheosis.adventure.Adventure.Items.GEM);
                    Gem gemObj = GemRegistry.INSTANCE.getRandomItem(new LegacyRandomSource(1632));


/*
                    GemItem.setGem(gem, gemObj);
                    AffixHelper.setRarity(gem, gemObj.getMaxRarity());
                    List<EntryStack<ItemStack>> DUMMY_GEM = List.of(EntryStacks.of(gem));
                    List<EntryStack<ItemStack>> SOCKETING_DUMMY_OUTPUTS = ADD_SOCKET_DUMMY_OUTPUTS.stream().map(ItemStack::copy).map(s -> {
                        ItemStack stack = s.copy();
                        SocketHelper.setSockets(stack, 1);
                        int socket = SocketHelper.getFirstEmptySocket(stack);
                        List<ItemStack> gems = new ArrayList<>(SocketHelper.getGems(stack));
                        gems.set(socket, gem.copy());
                        SocketHelper.setGems(stack, gems);
                        //SocketHelper.setGems(s, List.of(gem));
                        return stack;
                    }).map(EntryStacks::of).toList();

                    widgets.add(Widgets.createSlot(new Point(startPoint.x + 4 - 18 + offsetX, startPoint.y + 5)).entries(ADD_SOCKET_DUMMY_OUTPUTS.stream().map(EntryStacks::of).toList()).markInput());
                    widgets.add(Widgets.createSlot(new Point(startPoint.x + 4 + offsetX, startPoint.y + 5)).entries(DUMMY_GEM).markInput());

                    widgets.add(Widgets.createSlot(new Point(startPoint.x + 61 + offsetX, startPoint.y + 5)).entries(SOCKETING_DUMMY_OUTPUTS).disableBackground().markOutput());

                } */
/*else if (recipe instanceof ExpulsionRecipe rec) {
                    widgets.add(Widgets.createSlot(new Point(startPoint.x + 4 - 18 + offsetX, startPoint.y + 5)).entries(List.of(EntryStacks.of(Items.GOLDEN_SWORD.getDefaultInstance()))).markInput());
                    widgets.add(Widgets.createSlot(new Point(startPoint.x + 4 + offsetX, startPoint.y + 5)).entries(display.getInputEntries().get(2)).markInput());

                    widgets.add(Widgets.createSlot(new Point(startPoint.x + 61 + offsetX, startPoint.y + 5)).entries(List.of(EntryStacks.of(Items.GOLDEN_APPLE.getDefaultInstance()))).disableBackground().markOutput());
                }*//*
 else if (recipe instanceof WithdrawalRecipe rec) {
                    widgets.add(Widgets.createSlot(new Point(startPoint.x + 4 - 18 + offsetX, startPoint.y + 5)).entries(List.of(EntryStacks.of(Items.IRON_CHESTPLATE.getDefaultInstance()))).markInput());
                    widgets.add(Widgets.createSlot(new Point(startPoint.x + 4 + offsetX, startPoint.y + 5)).entries(display.getInputEntries().get(2)).markInput());

                    widgets.add(Widgets.createSlot(new Point(startPoint.x + 61 + offsetX, startPoint.y + 5)).entries(List.of(EntryStacks.of(Items.GOLDEN_APPLE.getDefaultInstance()))).disableBackground().markOutput());
                } else if (recipe instanceof UnnamingRecipe) {
                    widgets.add(Widgets.createSlot(new Point(startPoint.x + 4 - 18 + offsetX, startPoint.y + 5)).entries(List.of(EntryStacks.of(Items.DIAMOND_BOOTS.getDefaultInstance()))).markInput());
                    widgets.add(Widgets.createSlot(new Point(startPoint.x + 4 + offsetX, startPoint.y + 5)).entries(display.getInputEntries().get(2)).markInput());

                    widgets.add(Widgets.createSlot(new Point(startPoint.x + 61 + offsetX, startPoint.y + 5)).entries(List.of(EntryStacks.of(Items.GOLDEN_APPLE.getDefaultInstance()))).disableBackground().markOutput());
                }
*/
                }
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


