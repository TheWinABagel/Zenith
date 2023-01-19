package safro.zenith.village.compat;

import com.mojang.blaze3d.vertex.PoseStack;
import me.shedaniel.math.Point;
import me.shedaniel.rei.api.client.gui.Renderer;
import me.shedaniel.rei.api.client.gui.widgets.Widget;
import me.shedaniel.rei.api.client.gui.widgets.Widgets;
import me.shedaniel.rei.api.common.category.CategoryIdentifier;
import me.shedaniel.rei.api.common.util.EntryIngredients;
import me.shedaniel.rei.api.common.util.EntryStacks;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Blocks;
import safro.zenith.compat.ApothCategory;

import java.util.Arrays;
import java.util.List;

public class FletchingCategory extends ApothCategory<FletchingDisplay> {


    @Override
    public void draw(FletchingDisplay display, Point origin, PoseStack stack, double mouseX, double mouseY) {
    }

    @Override
    public void setRecipe(FletchingDisplay display, List<Widget> widgets, Point origin) {
        List<List<ItemStack>> inputs = display.getRecipe().getIngredients().stream().map(i -> Arrays.asList(i.getItems())).toList();
        for (int i = 0; i < 3; i++) {
            widgets.add(slot(42, 1 + i * 18, origin, EntryIngredients.ofItemStacks(inputs.get(i)), true).markInput());
        }
        widgets.add(Widgets.createArrow(new Point(origin.getX() + 79, origin.getY() + 18)));
        widgets.add(slot(117, 19, origin, display.getOutputEntries().get(0), true).markOutput());
    }

    @Override
    public int getYOffset() {
        return 5;
    }

    @Override
    public Renderer getIcon() {
        return EntryStacks.of(Blocks.FLETCHING_TABLE);
    }

    @Override
    public Component getTitle() {
        return Component.translatable("zenith.recipes.fletching");
    }

    @Override
    public CategoryIdentifier<? extends FletchingDisplay> getCategoryIdentifier() {
        return FletchingDisplay.ID;
    }
}
