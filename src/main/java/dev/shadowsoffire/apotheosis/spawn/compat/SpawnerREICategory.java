package dev.shadowsoffire.apotheosis.spawn.compat;

import com.mojang.blaze3d.vertex.PoseStack;
import dev.shadowsoffire.apotheosis.Apotheosis;
import dev.shadowsoffire.apotheosis.compat.ZenithREICatgeory;
import dev.shadowsoffire.apotheosis.spawn.modifiers.SpawnerModifier;
import dev.shadowsoffire.apotheosis.spawn.modifiers.StatModifier;
import me.shedaniel.math.Point;
import me.shedaniel.math.Rectangle;
import me.shedaniel.rei.api.client.gui.Renderer;
import me.shedaniel.rei.api.client.gui.widgets.Widget;
import me.shedaniel.rei.api.client.gui.widgets.Widgets;
import me.shedaniel.rei.api.common.category.CategoryIdentifier;
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

public class SpawnerREICategory extends ZenithREICatgeory<SpawnerREIDisplay> {

    public static final ResourceLocation TEXTURES = Apotheosis.loc("textures/gui/spawner_jei.png");

    @Override
    public Widget getBackground(Rectangle bounds) {
        return Widgets.createTexturedWidget(TEXTURES, bounds.getX() + this.getXOffset(), bounds.getY(), 0, 0, 169, 75);
    }

    @Override
    public int getDisplayWidth(SpawnerREIDisplay display) {
        return 169;
    }

    @Override
    public int getDisplayHeight() {
        return 75;
    }

    @Override
    public void draw(SpawnerREIDisplay display, Point origin, double mouseX, double mouseY, GuiGraphics gfx) {
        SpawnerModifier recipe = display.getRecipe();
        int x = origin.getX();
        int y = origin.getY();
        if (recipe.getOffhandInput() == Ingredient.EMPTY) {
            gfx.blit(TEXTURES, 1 + x, 31 + y, 0, 0, 88, 28, 34, 256, 256);
        }

        Screen scn = Minecraft.getInstance().screen;
        Font font = Minecraft.getInstance().font;
        if (scn == null) return; // We need this to render tooltips, bail if its not there.
        if (mouseX >= -1 + x && mouseX < 9 + x && mouseY >= 13 + y && mouseY < 13 + 12 + y) {
            gfx.blit(TEXTURES, -1 + x, 13 + y, 0, 0, 75, 10, 12, 256, 256);
            gfx.renderComponentTooltip(font, Arrays.asList(Component.translatable("misc.zenith.mainhand")), (int) mouseX, (int) mouseY);
        }
        else if (mouseX >= -1 + x && mouseX < 9 + x && mouseY >= 50 + y && mouseY < 50 + 12 + y && recipe.getOffhandInput() != Ingredient.EMPTY) {
            gfx.blit(TEXTURES, -1 + x, 50 + y, 0, 0, 75, 10, 12, 256, 256);
            gfx.renderComponentTooltip(font, Arrays.asList(Component.translatable("misc.zenith.offhand"), Component.translatable("misc.zenith.not_consumed").withStyle(ChatFormatting.GRAY)), (int) mouseX, (int) mouseY);
        }
        else if (mouseX >= 33 + x && mouseX < 33 + 16 + x && mouseY >= 30 + y && mouseY < 30 + 16 + y) {
            gfx.renderComponentTooltip(font, Arrays.asList(Component.translatable("misc.zenith.rclick_spawner")), (int) mouseX, (int) mouseY);
        }

        PoseStack mvStack = gfx.pose();
        mvStack.pushPose();
        mvStack.translate(0, 0.5, 0);
        gfx.renderFakeItem(new ItemStack(Items.SPAWNER), 31 + x, 29 + y);
        mvStack.popPose();

        int top = 75 / 2 - recipe.getStatModifiers().size() * (font.lineHeight + 2) / 2 + 2;
        int left = 168;
        for (StatModifier<?> s : recipe.getStatModifiers()) {
            String value = s.value.toString();
            if ("true".equals(value)) value = "+";
            else if ("false".equals(value)) value = "-";
            else if (s.value instanceof Number num && num.intValue() > 0) value = "+" + value;
            Component msg = Component.translatable("misc.zenith.concat", value, s.stat.name());
            int width = font.width(msg);
            boolean hover = mouseX >= left - width + x && mouseX < left + x && mouseY >= top + y && mouseY < top + y + font.lineHeight + 1;
            gfx.drawString(font, msg, left - font.width(msg) + x, top + y, hover ? 0x8080FF : 0x333333, false);

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
                renderComponentTooltip(scn, gfx, list, left + 6 , (int) mouseY , maxWidth, font);
            }

            top += font.lineHeight + 2;
        }
    }

    @Override
    public void setRecipe(SpawnerREIDisplay display, List<Widget> widgets, Point origin) {
        widgets.add(slot(11, 11, origin, display.getInputEntries().get(0), false).markInput());
        if (display.getRecipe().getOffhandInput() != Ingredient.EMPTY) widgets.add(slot(11, 48, origin, display.getInputEntries().get(2), false).markInput());
    }

    @Override
    public CategoryIdentifier<? extends SpawnerREIDisplay> getCategoryIdentifier() {
        return SpawnerREIDisplay.ID;
    }

    @Override
    public Component getTitle() {
        return Component.translatable("title.zenith.spawner");
    }

    @Override
    public Renderer getIcon() {
        return EntryStacks.of(Blocks.SPAWNER);
    }

    private static void renderComponentTooltip(Screen scn, GuiGraphics gfx, List<Component> list, int x, int y, int maxWidth, Font font) {
        List<FormattedText> text = list.stream().map(c -> font.getSplitter().splitLines(c, maxWidth, c.getStyle())).flatMap(List::stream).toList();
        gfx.renderComponentTooltip(font, text.stream().map(formatted -> (Component) Component.literal(formatted.getString())).toList(), x, y);
    }
}
