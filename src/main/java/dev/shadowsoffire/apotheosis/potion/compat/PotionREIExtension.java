package dev.shadowsoffire.apotheosis.potion.compat;

import dev.shadowsoffire.apotheosis.potion.PotionCharmRecipe;
import dev.shadowsoffire.apotheosis.potion.PotionModule;
import me.shedaniel.math.Rectangle;
import me.shedaniel.rei.api.client.gui.screen.DisplayScreen;
import me.shedaniel.rei.api.client.registry.category.extension.CategoryExtensionProvider;
import me.shedaniel.rei.api.client.registry.display.DisplayCategory;
import me.shedaniel.rei.api.client.registry.display.DisplayCategoryView;
import me.shedaniel.rei.api.common.display.basic.BasicDisplay;
import me.shedaniel.rei.api.common.entry.EntryIngredient;
import me.shedaniel.rei.api.common.util.EntryIngredients;
import me.shedaniel.rei.plugin.common.displays.crafting.DefaultCraftingDisplay;
import net.minecraft.client.Minecraft;
import net.minecraft.core.NonNullList;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.item.alchemy.PotionUtils;
import net.minecraft.world.item.alchemy.Potions;
import net.minecraft.world.item.crafting.CraftingRecipe;
import net.minecraft.world.item.crafting.Ingredient;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@SuppressWarnings("UnstableApiUsage")
class PotionJEIExtension implements CategoryExtensionProvider<DefaultCraftingDisplay<?>> {

    @Override
    public DisplayCategoryView<DefaultCraftingDisplay<?>> provide(DefaultCraftingDisplay<?> display, DisplayCategory<DefaultCraftingDisplay<?>> category, DisplayCategoryView<DefaultCraftingDisplay<?>> lastView) {
        if (Minecraft.getInstance().screen instanceof DisplayScreen && display.getOptionalRecipe().isPresent()) {
            CraftingRecipe recipe = (CraftingRecipe) display.getOptionalRecipe().get();
            if (recipe instanceof PotionCharmRecipe charmRecipe) {
                List<EntryIngredient> ingredientList = display.getInputEntries();
                List<EntryIngredient> outputList = display.getOutputEntries();
                NonNullList<Ingredient> input = recipe.getIngredients();
                ItemStack output = recipe.getResultItem(BasicDisplay.registryAccess());
                PotionModule.LOGGER.warn(charmRecipe.getPotionSlots());

                Potion potion = PotionUtils.getPotion((recipe.getIngredients().get(charmRecipe.getPotionSlots().getInt(0))).getItems()[0]);
                List<List<ItemStack>> recipeInputs = recipe.getIngredients().stream().map(i -> Arrays.asList(i.getItems())).collect(Collectors.toCollection(ArrayList::new));
                if (potion != Potions.EMPTY) {
                    for (int i : charmRecipe.getPotionSlots()) {
                        recipeInputs.set(i, Arrays.asList(PotionUtils.setPotion(new ItemStack(Items.POTION), potion)));
                    //    ingredientList.set(i, EntryIngredients.of(PotionUtils.setPotion(new ItemStack(Items.POTION), potion)));
                    }
                }


                PotionUtils.setPotion(output, potion);
                charmRecipe.getPotionSlots().forEach(integer -> input.set(integer, Ingredient.of(PotionUtils.setPotion(new ItemStack(Items.POTION), potion))));


                //craftingGridHelper.createAndSetInputs(builder, VanillaTypes.ITEM_STACK, recipeInputs, this.getWidth(), this.getHeight());
                if (potion != Potions.EMPTY) {

                //    lastView.getDisplayRenderer(display);
                //    craftingGridHelper.createAndSetOutputs(builder, VanillaTypes.ITEM_STACK, Arrays.asList(output));
                }
                else {
                    List<ItemStack> potionStacks = new ArrayList<>();
                    for (Potion p : BuiltInRegistries.POTION) {
                        if (p.getEffects().size() != 1 || p.getEffects().get(0).getEffect().isInstantenous()) continue;
                        ItemStack charm = new ItemStack(PotionModule.POTION_CHARM);
                        PotionUtils.setPotion(charm, p);
                        potionStacks.add(charm);
                    }
                //    craftingGridHelper.createAndSetOutputs(builder, VanillaTypes.ITEM_STACK, potionStacks);
                }



                /*List<ItemStack> listPot = recipe.getIngredients().stream().map(i -> Arrays.asList((i.getItems())).get(0)).toList();
                Potion potion = PotionUtils.getPotion(listPot.get(0));
                //Potion potion = recipe.getIngredients().stream().map(i -> Arrays.asList(Arrays.stream(i.getItems()).findFirst().orElse(ItemStack.EMPTY))).collect(Collectors.);

                //Potion potion = recipe
                List<List<ItemStack>> recipeInputs = charmRecipe.getIngredients().stream().map(i -> Arrays.asList(i.getItems())).collect(Collectors.toCollection(ArrayList::new));
                if (potion != Potions.EMPTY) {
                    for (int i : charmRecipe.getPotionSlots()) {
                        recipeInputs.set(i, Arrays.asList(PotionUtils.setPotion(new ItemStack(Items.POTION), potion)));
                    }
                }
                ItemStack output = new ItemStack(PotionModule.POTION_CHARM);
                PotionUtils.setPotion(output, potion);
                */

                //    charmRecipe
            }
        }
        return lastView;
    }

/*
    private final class PotionREI implements DisplayCategoryView<DefaultShapedDisplay<?>> {

        private final DisplayCategoryView<DefaultShapedDisplay<?>> lastView;
        private final PotionCharmRecipe recipe;

        private PotionREI(DisplayCategoryView<DefaultShapedDisplay<?>> lastView, PotionCharmRecipe recipe) {
            this.lastView = lastView;
            this.recipe = recipe;
        }

        @SuppressWarnings("OverrideOnly")
        @Override
        public DisplayRenderer getDisplayRenderer(DefaultShapedDisplay<?> display) {
            return lastView.getDisplayRenderer(display);
        }

        @Override
        public List<Widget> setupDisplay(DefaultShapedDisplay<?> display, Rectangle bounds) {
            int pX = bounds.x - 5;
            int pY = bounds.y - 5;
            int size = 5;

            var widgets = lastView.setupDisplay(display, bounds);

            Point startPoint = new Point(bounds.getCenterX() - 31, bounds.getCenterY() - 13);
            //widgets.add(Widgets.createRecipeBase(bounds));
            int offsetX = 5;
            //Potion pot = PotionUtils.getPotion(focuses.getFocuses(VanillaTypes.ITEM_STACK).findFirst().map(IFocus::getTypedValue).map(ITypedIngredient::getIngredient).orElse(ItemStack.EMPTY));
           widgets.add(Widgets.createSlot(new Point(startPoint.x + 4 + offsetX, startPoint.y + 5)).entries(List.of(EntryStacks.of(output))).markInput());
            /*
            craftingGridHelper.createAndSetInputs(builder, VanillaTypes.ITEM_STACK, recipeInputs, this.getWidth(), this.getHeight());
            if (potion != Potions.EMPTY) {
                craftingGridHelper.createAndSetOutputs(builder, VanillaTypes.ITEM_STACK, Arrays.asList(output));
            }
            else {
                List<ItemStack> potionStacks = new ArrayList<>();
                for (Potion p : BuiltInRegistries.POTION) {
                    if (p.getEffects().size() != 1 || p.getEffects().get(0).getEffect().isInstantenous()) continue;
                    ItemStack charm = new ItemStack(PotionModule.POTION_CHARM);
                    PotionUtils.setPotion(charm, p);
                    potionStacks.add(charm);
                }
                craftingGridHelper.createAndSetOutputs(builder, VanillaTypes.ITEM_STACK, potionStacks);
            }

            /*
            if (recipe instanceof ExtractionRecipe rec) {
                widgets.add(Widgets.createSlot(new Point(startPoint.x + 4 - 18 + offsetX, startPoint.y + 5)).entries(List.of(EntryStacks.of(Items.IRON_CHESTPLATE.getDefaultInstance()))).markInput());
                widgets.add(Widgets.createSlot(new Point(startPoint.x + 4 + offsetX, startPoint.y + 5)).entries(display.getInputEntries().get(2)).markInput());

                widgets.add(Widgets.createSlot(new Point(startPoint.x + 61 + offsetX, startPoint.y + 5)).entries(List.of(EntryStacks.of(Items.GOLDEN_APPLE.getDefaultInstance()))).disableBackground().markOutput());
            } else if (recipe instanceof UnnamingRecipe) {
                widgets.add(Widgets.createSlot(new Point(startPoint.x + 4 - 18 + offsetX, startPoint.y + 5)).entries(List.of(EntryStacks.of(Items.DIAMOND_BOOTS.getDefaultInstance()))).markInput());
                widgets.add(Widgets.createSlot(new Point(startPoint.x + 4 + offsetX, startPoint.y + 5)).entries(display.getInputEntries().get(2)).markInput());

                widgets.add(Widgets.createSlot(new Point(startPoint.x + 61 + offsetX, startPoint.y + 5)).entries(List.of(EntryStacks.of(Items.GOLDEN_APPLE.getDefaultInstance()))).disableBackground().markOutput());
            }*//*


            var tooltipArea = new Rectangle(pX, pY, size, size);
            widgets.add(Widgets.createTooltip(tooltipArea, Component.literal("woooo")));

            return widgets;
        }
    }*/

}


