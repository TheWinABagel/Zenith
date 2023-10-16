package dev.shadowsoffire.apotheosis.adventure.compat;

import com.mojang.blaze3d.vertex.PoseStack;
import dev.shadowsoffire.apotheosis.Apotheosis;
import dev.shadowsoffire.apotheosis.adventure.Adventure;
import dev.shadowsoffire.apotheosis.adventure.AdventureModule;
import dev.shadowsoffire.apotheosis.adventure.affix.salvaging.SalvagingRecipe;
import dev.shadowsoffire.apotheosis.compat.ZenithREICatgeory;
import dev.shadowsoffire.apotheosis.spawn.modifiers.SpawnerModifier;
import dev.shadowsoffire.apotheosis.spawn.modifiers.StatModifier;
import me.shedaniel.errornotifier.launch.render.math.Matrix4f;
import me.shedaniel.math.Point;
import me.shedaniel.math.Rectangle;
import me.shedaniel.rei.api.client.gui.Renderer;
import me.shedaniel.rei.api.client.gui.widgets.Widget;
import me.shedaniel.rei.api.client.gui.widgets.Widgets;
import me.shedaniel.rei.api.common.category.CategoryIdentifier;
import me.shedaniel.rei.api.common.entry.EntryIngredient;
import me.shedaniel.rei.api.common.util.EntryIngredients;
import me.shedaniel.rei.api.common.util.EntryStacks;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.block.Blocks;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class SalvagingREICategory extends ZenithREICatgeory<SalvagingREIDisplay> {

    public static final ResourceLocation TEXTURES = new ResourceLocation(Apotheosis.MODID, "textures/gui/salvage_jei.png");

    @Override
    public Widget getBackground(Rectangle bounds) {
        return Widgets.createTexturedWidget(TEXTURES, bounds.getX() + this.getXOffset(), bounds.getY(), 0, 0, 169, 75);
    }

    @Override
    public int getDisplayWidth(SalvagingREIDisplay display) {
        return 98;
    }

    @Override
    public int getDisplayHeight() {
        return 74;
    }

    @Override
    public void draw(SalvagingREIDisplay display, Point origin, double mouseX, double mouseY, GuiGraphics gfx) {
        var recipe = display.getRecipe();
        int originX = origin.getX();
        int originY = origin.getY();
        List<SalvagingRecipe.OutputData> outputs = recipe.getOutputs();
        Font font = Minecraft.getInstance().font;
        PoseStack pose = gfx.pose();

        int idx = 0;
        for (var data : outputs) {
            pose.pushPose();
            pose.translate(0, 0, 200);
            String text = String.format("%d-%d", data.getMin(), data.getMax());

            float x = 59 + 18 * (idx % 2) + (16 - font.width(text) * 0.5F) + originX;
            float y = 23F + 18 * (idx / 2) + originY;

            float scale = 0.5F;

            pose.scale(scale, scale, 1);
            gfx.drawString(font, text, (int) (x / scale), (int) (y / scale), 0xFFFFFF);

            idx++;
            pose.popPose();
        }
    }

    @Override
    public void setRecipe(SalvagingREIDisplay display, List<Widget> widgets, Point origin) {
        var recipe = display.getRecipe();
        int x = origin.getX();
        int y = origin.getY();
        List<ItemStack> input = Arrays.asList(recipe.getInput().getItems());
        widgets.add(slot(5, 29, origin, EntryIngredient.of((input.stream().map(EntryStacks::of).toList())), false));
        List<SalvagingRecipe.OutputData> outputs = recipe.getOutputs();
        int idx = 0;
        for (var data : outputs) {
            int pX = 59 + 18 * (idx % 2);
            int pY = 11 + 18 * (idx / 2);
            widgets.add(Widgets.createSlot(new Point(pX + x, pY + y)).entry(EntryStacks.of(data.getStack())));
            idx++;
        }
    }

    @Override
    public CategoryIdentifier<? extends SalvagingREIDisplay> getCategoryIdentifier() {
        return SalvagingREIDisplay.ID;
    }

    @Override
    public Component getTitle() {
        return Component.translatable("title.zenith.salvaging");
    }

    @Override
    public Renderer getIcon() {
        return EntryStacks.of(Adventure.Blocks.SALVAGING_TABLE);
    }

}
