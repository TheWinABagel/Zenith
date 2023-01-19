package safro.zenith.spawn.compat;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Matrix4f;
import me.shedaniel.math.Point;
import me.shedaniel.math.Rectangle;
import me.shedaniel.rei.api.client.gui.Renderer;
import me.shedaniel.rei.api.client.gui.widgets.Widget;
import me.shedaniel.rei.api.client.gui.widgets.Widgets;
import me.shedaniel.rei.api.common.category.CategoryIdentifier;
import me.shedaniel.rei.api.common.util.EntryIngredients;
import me.shedaniel.rei.api.common.util.EntryStacks;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.block.Blocks;
import safro.zenith.Zenith;
import safro.zenith.compat.ApothCategory;
import safro.zenith.spawn.modifiers.SpawnerModifier;
import safro.zenith.spawn.modifiers.StatModifier;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class SpawnerCategory extends ApothCategory<SpawnerDisplay> {
    public static final ResourceLocation TEXTURES = new ResourceLocation(Zenith.MODID, "textures/gui/spawner_jei.png");

    @Override
    public Widget getBackground(Rectangle bounds) {
        return Widgets.createTexturedWidget(TEXTURES, bounds.getX() + this.getXOffset(), bounds.getY(), 0, 0, 169, 75);
    }

    @Override
    public int getDisplayWidth(SpawnerDisplay display) {
        return 169;
    }

    @Override
    public int getDisplayHeight() {
        return 75;
    }

    @Override
    public void draw(SpawnerDisplay display, Point origin, PoseStack stack, double mouseX, double mouseY) {
        SpawnerModifier recipe = display.getRecipe();
        int x = origin.getX();
        int y = origin.getY();

        if (recipe.getOffhandInput() == Ingredient.EMPTY) {
            GuiComponent.blit(stack, 1, 31, 0, 0, 88, 28, 34, 256, 256);
        }
        Screen scn = Minecraft.getInstance().screen;
        if (scn == null) return; // We need this to render tooltips, bail if its not there.
        if (mouseX >= -1 + x && mouseX < 9 + x && mouseY >= 13 + y && mouseY < 13 + 12 + y) {
            GuiComponent.blit(stack, -1, 13, 0, 0, 75, 10, 12, 256, 256);
            scn.renderComponentTooltip(stack, Arrays.asList(Component.translatable("misc.zenith.mainhand")), (int) mouseX - x, (int) mouseY - y);
        } else if (mouseX >= -1 + x && mouseX < 9 + x && mouseY >= 50 + y && mouseY < 50 + 12 + y && recipe.getOffhandInput() != Ingredient.EMPTY) {
            GuiComponent.blit(stack, -1, 50, 0, 0, 75, 10, 12, 256, 256);
            scn.renderComponentTooltip(stack, Arrays.asList(Component.translatable("misc.zenith.offhand"), Component.translatable("misc.zenith.not_consumed").withStyle(ChatFormatting.GRAY)), (int) mouseX - x, (int) mouseY - y);
        } else if (mouseX >= 33 + x && mouseX < 33 + 16 + x && mouseY >= 30 + y && mouseY < 30 + 16 + y) {
            scn.renderComponentTooltip(stack, Arrays.asList(Component.translatable("misc.zenith.rclick_spawner")), (int) mouseX - x, (int) mouseY - y);
        }

        PoseStack mvStack = RenderSystem.getModelViewStack();
        mvStack.pushPose();
        Matrix4f mvMatrix = mvStack.last().pose();
        mvMatrix.setIdentity();
        mvMatrix.multiply(stack.last().pose());
        mvStack.translate(0, 0.5, -2000);
        Minecraft.getInstance().getItemRenderer().renderAndDecorateItem(new ItemStack(Items.SPAWNER), 31, 29);
        mvStack.popPose();
        RenderSystem.applyModelViewMatrix();

        Font font = Minecraft.getInstance().font;
        int top = (75 / 2 - (recipe.getStatModifiers().size() * (font.lineHeight + 2)) / 2 + 2);
        int left = 168;
        for (StatModifier<?> s : recipe.getStatModifiers()) {
            String value = s.value.toString();
            if (value.equals("true")) value = "+";
            else if (value.equals("false")) value = "-";
            else if (s.value instanceof Number num && num.intValue() > 0) value = "+" + value;
            Component msg = Component.translatable("misc.zenith.concat", value, s.stat.name());
            int width = font.width(msg);
            boolean hover = mouseX >= left - width + x && mouseX < left + x && mouseY >= top + y && mouseY < top + y + font.lineHeight + 1;
            font.draw(stack, msg, left - font.width(msg), top, hover ? 0x8080FF : 0x333333);

            int maxWidth = Minecraft.getInstance().getWindow().getGuiScaledWidth();
            maxWidth = maxWidth - (maxWidth - 210) / 2 - 210;

            if (hover) {
                List<Component> list = new ArrayList<>();
                list.add(s.stat.name().withStyle(ChatFormatting.GREEN, ChatFormatting.UNDERLINE));
                list.add(s.stat.desc().withStyle(ChatFormatting.GRAY));
                if (s.value instanceof Number) {
                    if (((Number) s.min).intValue() > 0 || ((Number) s.max).intValue() != Integer.MAX_VALUE) list.add(Component.literal(" "));
                    if (((Number) s.min).intValue() > 0) list.add(Component.translatable("misc.zenith.min_value", s.min).withStyle(ChatFormatting.GRAY));
                    if (((Number) s.max).intValue() != Integer.MAX_VALUE) list.add(Component.translatable("misc.zenith.max_value", s.max).withStyle(ChatFormatting.GRAY));
                }
                renderComponentTooltip(scn, stack, list, left + 6 - x, (int) mouseY - y, maxWidth, font);
            }

            top += font.lineHeight + 2;
        }
    }

    private static void renderComponentTooltip(Screen scn, PoseStack stack, List<Component> list, int x, int y, int maxWidth, Font font) {
        List<FormattedText> text = list.stream().map(c -> font.getSplitter().splitLines(c, maxWidth, c.getStyle())).flatMap(List::stream).toList();
        scn.renderComponentTooltip(stack, text.stream().map(formatted -> (Component) Component.literal(formatted.getString())).toList(), x, y);
    }

    @Override
    public void setRecipe(SpawnerDisplay display, List<Widget> widgets, Point origin) {
        SpawnerModifier recipe = display.getRecipe();
        widgets.add(slot(11, 11, origin, EntryIngredients.ofIngredient(recipe.getMainhandInput()), false).markInput());
        if (recipe.getOffhandInput() != Ingredient.EMPTY) {
            widgets.add(slot(11, 48, origin, EntryIngredients.ofIngredient(recipe.getOffhandInput()), false).markInput());
        }
    //    builder.addInvisibleIngredients(RecipeIngredientRole.CATALYST).addIngredient(VanillaTypes.ITEM, new ItemStack(Blocks.SPAWNER));
    //   builder.addInvisibleIngredients(RecipeIngredientRole.OUTPUT).addIngredient(VanillaTypes.ITEM, new ItemStack(Blocks.SPAWNER));
    }

    @Override
    public int getMaximumDisplaysPerPage() {
        return 1;
    }

    @Override
    public Renderer getIcon() {
        return EntryStacks.of(Blocks.SPAWNER);
    }

    @Override
    public Component getTitle() {
        return Component.translatable("title.zenith.spawner");
    }

    @Override
    public CategoryIdentifier<? extends SpawnerDisplay> getCategoryIdentifier() {
        return SpawnerDisplay.ID;
    }
}
