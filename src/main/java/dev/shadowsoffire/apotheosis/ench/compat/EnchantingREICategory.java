package dev.shadowsoffire.apotheosis.ench.compat;

import com.mojang.blaze3d.systems.RenderSystem;
import dev.shadowsoffire.apotheosis.Apotheosis;
import dev.shadowsoffire.apotheosis.compat.ApotheosisREICatgeory;
import dev.shadowsoffire.apotheosis.ench.Ench;
import dev.shadowsoffire.apotheosis.ench.EnchModule;
import dev.shadowsoffire.apotheosis.ench.table.EnchantingRecipe;
import dev.shadowsoffire.apotheosis.ench.table.EnchantingStatRegistry;
import me.shedaniel.math.Point;
import me.shedaniel.math.Rectangle;
import me.shedaniel.rei.api.client.gui.Renderer;
import me.shedaniel.rei.api.client.gui.widgets.Widget;
import me.shedaniel.rei.api.client.gui.widgets.Widgets;
import me.shedaniel.rei.api.client.registry.display.DisplayCategory;
import me.shedaniel.rei.api.common.category.CategoryIdentifier;
import me.shedaniel.rei.api.common.display.Display;
import me.shedaniel.rei.api.common.util.EntryStacks;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.EnchantmentNames;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.world.level.block.Blocks;

import java.util.ArrayList;
import java.util.List;

public class EnchantingREICategory extends ApotheosisREICatgeory<EnchantingREIDisplay> {

    public static final ResourceLocation TEXTURES = Apotheosis.loc( "textures/gui/enchanting_jei.png");

    @Override
    public CategoryIdentifier<? extends EnchantingREIDisplay> getCategoryIdentifier() {
        return EnchantingREIDisplay.ID;
    }

    @Override
    public Component getTitle() {
        return Component.translatable("apotheosis.recipes.enchanting");

    }

    @Override
    public Renderer getIcon() {
        return EntryStacks.of(Blocks.ENCHANTING_TABLE);
    }

    @Override
    public int getXOffset() {
        return -4;
    }

    @Override
    public Widget getBackground(Rectangle bounds) {
        return Widgets.createTexturedWidget(TEXTURES, bounds.getX() + this.getXOffset(), bounds.getY(), 0, 0, 170, 56);
    }



    @Override
    public void draw(EnchantingREIDisplay display, Point origin, double mouseX, double mouseY, GuiGraphics gfx) {
        EnchantingRecipe recipe = display.getRecipe();
        int x = origin.getX();
        int y = origin.getY();

        boolean hover = false;
        if (mouseX > 57 + x && mouseX <= 57 + 108 + x && mouseY > 4 + y && mouseY <= 4 + 19 + y) {
            gfx.blit(TEXTURES, 57 + x, 4 + y, 0, 0, 71, 108, 19, 256, 256);
            hover = true;
        }

        Font font = Minecraft.getInstance().font;
        EnchantingStatRegistry.Stats stats = recipe.getRequirements();
        EnchantingStatRegistry.Stats maxStats = recipe.getMaxRequirements();
        gfx.drawString(font, I18n.get("gui.apotheosis.enchant.eterna"), 16 + x, 26 + y, 0x3DB53D, false);
        gfx.drawString(font, I18n.get("gui.apotheosis.enchant.quanta"), 16 + x, 36 + y, 0xFC5454, false);
        gfx.drawString(font, I18n.get("gui.apotheosis.enchant.arcana"), 16 + x, 46 + y, 0xA800A8, false);
        int level = (int) (stats.eterna() * 2);

        String s = "" + level;
        int width = 86 - font.width(s);
        EnchantmentNames.getInstance().initSeed(recipe.getId().hashCode());
        FormattedText itextproperties = EnchantmentNames.getInstance().getRandomName(font, width);
        int color = hover ? 16777088 : 6839882;
        drawWordWrap(font, itextproperties, 77 + x, 6 + y, width, color, gfx);
        color = 8453920;
        gfx.drawString(font, s, 77 + width + x, 13 + y, color);

        int[] pos = { (int) (stats.eterna() / EnchantingStatRegistry.getAbsoluteMaxEterna() * 110), (int) (stats.quanta() / 100 * 110), (int) (stats.arcana() / 100 * 110) };
        if (stats.eterna() > 0) {
            gfx.blit(TEXTURES, 56 + x, 27 + y, 0, 56, pos[0], 5, 256, 256);
        }
        if (stats.quanta() > 0) {
            gfx.blit(TEXTURES, 56 + x, 37 + y, 0, 61, pos[1], 5, 256, 256);
        }
        if (stats.arcana() > 0) {
            gfx.blit(TEXTURES, 56 + x, 47 + y, 0, 66, pos[2], 5, 256, 256);
        }
        RenderSystem.enableBlend();
        if (maxStats.eterna() > 0) {
            gfx.blit(TEXTURES, 56 + pos[0] + x, 27 + y, pos[0], 90, (int) ((maxStats.eterna() - stats.eterna()) / EnchantingStatRegistry.getAbsoluteMaxEterna() * 110), 5, 256, 256);
        }
        if (maxStats.quanta() > 0) {
            gfx.blit(TEXTURES, 56 + pos[1] + x, 37 + y, pos[1], 95, (int) ((maxStats.quanta() - stats.quanta()) / 100 * 110), 5, 256, 256);
        }
        if (maxStats.arcana() > 0) {
            gfx.blit(TEXTURES, 56 + pos[2] + x, 47 + y, pos[2], 100, (int) ((maxStats.arcana() - stats.arcana()) / 100 * 110), 5, 256, 256);
        }
        RenderSystem.disableBlend();
        Screen scn = Minecraft.getInstance().screen;
        if (scn == null) return; // We need this to render tooltips, bail if its not there.
        if (hover) {
            List<Component> list = new ArrayList<>();
            list.add(Component.translatable("container.enchant.clue", Ench.Enchantments.INFUSION.getFullname(1).getString()).withStyle(ChatFormatting.GRAY, ChatFormatting.ITALIC));
            gfx.renderComponentTooltip(font, list, (int) mouseX, (int) mouseY);
        }
        else if (mouseX > 56 + x && mouseX <= 56 + 110 + x && mouseY > 26 + y && mouseY <= 27 + 5 + y) {
            List<Component> list = new ArrayList<>();
            list.add(Component.translatable("gui.apotheosis.enchant.eterna").withStyle(ChatFormatting.GREEN));
            if (maxStats.eterna() == stats.eterna()) {
                list.add(Component.translatable("info.apotheosis.eterna_exact", stats.eterna(), EnchantingStatRegistry.getAbsoluteMaxEterna()).withStyle(ChatFormatting.GRAY));
            }
            else {
                list.add(Component.translatable("info.apotheosis.eterna_at_least", stats.eterna(), EnchantingStatRegistry.getAbsoluteMaxEterna()).withStyle(ChatFormatting.GRAY));
                if (maxStats.eterna() > -1) list.add(Component.translatable("info.apotheosis.eterna_at_most", maxStats.eterna(), EnchantingStatRegistry.getAbsoluteMaxEterna()).withStyle(ChatFormatting.GRAY));
            }
            gfx.renderComponentTooltip(font, list, (int) mouseX, (int) mouseY);
        }
        else if (mouseX > 56 + x && mouseX <= 56 + 110 + x && mouseY > 36 + y && mouseY <= 37 + 5 + y) {
            List<Component> list = new ArrayList<>();
            list.add(Component.translatable("gui.apotheosis.enchant.quanta").withStyle(ChatFormatting.RED));
            if (maxStats.quanta() == stats.quanta()) {
                list.add(Component.translatable("info.apotheosis.percent_exact", stats.quanta()).withStyle(ChatFormatting.GRAY));
            }
            else {
                list.add(Component.translatable("info.apotheosis.percent_at_least", stats.quanta()).withStyle(ChatFormatting.GRAY));
                if (maxStats.quanta() > -1) list.add(Component.translatable("info.apotheosis.percent_at_most", maxStats.quanta()).withStyle(ChatFormatting.GRAY));
            }
            gfx.renderComponentTooltip(font, list, (int) mouseX, (int) mouseY);
        }
        else if (mouseX > 56 + x && mouseX <= 56 + 110 + x && mouseY > 46 + y && mouseY <= 47 + 5 + y) {
            List<Component> list = new ArrayList<>();
            list.add(Component.translatable("gui.apotheosis.enchant.arcana").withStyle(ChatFormatting.DARK_PURPLE));
            if (maxStats.arcana() == stats.arcana()) {
                list.add(Component.translatable("info.apotheosis.percent_exact", stats.arcana()).withStyle(ChatFormatting.GRAY));
            }
            else {
                list.add(Component.translatable("info.apotheosis.percent_at_least", stats.arcana()).withStyle(ChatFormatting.GRAY));
                if (maxStats.arcana() > -1) list.add(Component.translatable("info.apotheosis.percent_at_most", maxStats.arcana()).withStyle(ChatFormatting.GRAY));
            }
            gfx.renderComponentTooltip(font, list, (int) mouseX, (int) mouseY);
        }
    }


    @Override
    public void setRecipe(EnchantingREIDisplay display, List<Widget> widgets, Point origin) {
        widgets.add(slot(6, 6, origin, display.getInputEntries().get(0), false).markInput());
        widgets.add(slot(37, 6, origin, display.getOutputEntries().get(0), false).markOutput());
    }

    public static void drawWordWrap(Font font, FormattedText pText, int pX, int pY, int pMaxWidth, int pColor, GuiGraphics gfx) {
        for (FormattedCharSequence formattedcharsequence : font.split(pText, pMaxWidth)) {
            gfx.drawString(font, formattedcharsequence, pX, pY, pColor, false);
            pY += 9;
        }
    }
}
