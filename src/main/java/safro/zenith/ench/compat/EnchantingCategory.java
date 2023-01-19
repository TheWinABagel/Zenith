package safro.zenith.ench.compat;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
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
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.EnchantmentNames;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.world.level.block.Blocks;
import safro.zenith.Zenith;
import safro.zenith.compat.ApothCategory;
import safro.zenith.ench.EnchModule;
import safro.zenith.ench.table.EnchantingRecipe;
import safro.zenith.ench.table.EnchantingStatManager;

import java.util.ArrayList;
import java.util.List;

import static safro.zenith.ench.table.EnchantingStatManager.*;

public class EnchantingCategory extends ApothCategory<EnchantingDisplay> {
    public static final ResourceLocation TEXTURES = new ResourceLocation(Zenith.MODID, "textures/gui/enchanting_jei.png");

    @Override
    public Renderer getIcon() {
        return EntryStacks.of(Blocks.ENCHANTING_TABLE);
    }

    @Override
    public Component getTitle() {
        return Component.translatable("zenith.recipes.enchanting");
    }

    @Override
    public CategoryIdentifier<? extends EnchantingDisplay> getCategoryIdentifier() {
        return EnchantingDisplay.ID;
    }

    @Override
    public Widget getBackground(Rectangle bounds) {
        return Widgets.createTexturedWidget(TEXTURES, bounds.getX() + this.getXOffset(), bounds.getY(), 0, 0, 170, 56);
    }

    @Override
    public int getXOffset() {
        return -4;
    }

    @Override
    public void draw(EnchantingDisplay display, Point origin, PoseStack stack, double mouseX, double mouseY) {
        EnchantingRecipe recipe = display.getRecipe();
        int x = origin.getX();
        int y = origin.getY();

        boolean hover = false;
        if (mouseX > 57 + x && mouseX <= 57 + 108 + x && mouseY > 4 + y && mouseY <= 4 + 19 + y) {
            GuiComponent.blit(stack, 57, 4, 0, 0, 71, 108, 19, 256, 256);
            hover = true;
        }

        Font font = Minecraft.getInstance().font;
        Stats stats = recipe.getRequirements();
        Stats maxStats = recipe.getMaxRequirements();
        font.draw(stack, I18n.get("gui.zenith.enchant.eterna"), 16, 26, 0x3DB53D);
        font.draw(stack, I18n.get("gui.zenith.enchant.quanta"), 16, 36, 0xFC5454);
        font.draw(stack, I18n.get("gui.zenith.enchant.arcana"), 16, 46, 0xA800A8);
        int level = (int) (stats.eterna * 2);

        String s = "" + level;
        int width = 86 - font.width(s);
        EnchantmentNames.getInstance().initSeed(recipe.getId().hashCode());
        FormattedText itextproperties = EnchantmentNames.getInstance().getRandomName(font, width);
        int color = hover ? 16777088 : 6839882;
        drawWordWrap(font, itextproperties, 77, 6, width, color, stack);
        color = 8453920;
        font.drawShadow(stack, s, 77 + width, 13, color);

        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.setShaderTexture(0, TEXTURES);
        int[] pos = { (int) (stats.eterna / EnchantingStatManager.getAbsoluteMaxEterna() * 110), (int) (stats.quanta / 100 * 110), (int) (stats.arcana / 100 * 110) };
        if (stats.eterna > 0) {
            GuiComponent.blit(stack, 56, 27, 0, 56, pos[0], 5, 256, 256);
        }
        if (stats.quanta > 0) {
            GuiComponent.blit(stack, 56, 37, 0, 61, pos[1], 5, 256, 256);
        }
        if (stats.arcana > 0) {
            GuiComponent.blit(stack, 56, 47, 0, 66, pos[2], 5, 256, 256);
        }
        RenderSystem.enableBlend();
        if (maxStats.eterna > 0) {
            GuiComponent.blit(stack, 56 + pos[0], 27, pos[0], 90, (int) ((maxStats.eterna - stats.eterna) / EnchantingStatManager.getAbsoluteMaxEterna() * 110), 5, 256, 256);
        }
        if (maxStats.quanta > 0) {
            GuiComponent.blit(stack, 56 + pos[1], 37, pos[1], 95, (int) ((maxStats.quanta - stats.quanta) / 100 * 110), 5, 256, 256);
        }
        if (maxStats.arcana > 0) {
            GuiComponent.blit(stack, 56 + pos[2], 47, pos[2], 100, (int) ((maxStats.arcana - stats.arcana) / 100 * 110), 5, 256, 256);
        }
        RenderSystem.disableBlend();
        Screen scn = Minecraft.getInstance().screen;
        if (scn == null) return; // We need this to render tooltips, bail if its not there.
        if (hover) {
            List<Component> list = new ArrayList<>();
            list.add(Component.translatable("container.enchant.clue", EnchModule.INFUSION.getFullname(1).getString()).withStyle(ChatFormatting.GRAY, ChatFormatting.ITALIC));
            scn.renderComponentTooltip(stack, list, (int) mouseX - x, (int) mouseY - y);
        } else if (mouseX > 56 + x && mouseX <= 56 + 110 + x && mouseY > 26 + y && mouseY <= 27 + 5 + y) {
            List<Component> list = new ArrayList<>();
            list.add(Component.translatable("gui.zenith.enchant.eterna").withStyle(ChatFormatting.GREEN));
            if (maxStats.eterna == stats.eterna) {
                list.add(Component.translatable("info.zenith.eterna_exact", stats.eterna, EnchantingStatManager.getAbsoluteMaxEterna()).withStyle(ChatFormatting.GRAY));
            } else {
                list.add(Component.translatable("info.zenith.eterna_at_least", stats.eterna, EnchantingStatManager.getAbsoluteMaxEterna()).withStyle(ChatFormatting.GRAY));
                if (maxStats.eterna > -1) list.add(Component.translatable("info.zenith.eterna_at_most", maxStats.eterna, EnchantingStatManager.getAbsoluteMaxEterna()).withStyle(ChatFormatting.GRAY));
            }
            scn.renderComponentTooltip(stack, list, (int) mouseX - x, (int) mouseY - y);
        } else if (mouseX > 56 + x && mouseX <= 56 + 110 + x && mouseY > 36 + y && mouseY <= 37 + 5 + y) {
            List<Component> list = new ArrayList<>();
            list.add(Component.translatable("gui.zenith.enchant.quanta").withStyle(ChatFormatting.RED));
            if (maxStats.quanta == stats.quanta) {
                list.add(Component.translatable("info.zenith.percent_exact", stats.quanta).withStyle(ChatFormatting.GRAY));
            } else {
                list.add(Component.translatable("info.zenith.percent_at_least", stats.quanta).withStyle(ChatFormatting.GRAY));
                if (maxStats.quanta > -1) list.add(Component.translatable("info.zenith.percent_at_most", maxStats.quanta).withStyle(ChatFormatting.GRAY));
            }
            scn.renderComponentTooltip(stack, list, (int) mouseX - x, (int) mouseY - y);
        } else if (mouseX > 56 + x && mouseX <= 56 + 110 + x && mouseY > 46 + y && mouseY <= 47 + 5 + y) {
            List<Component> list = new ArrayList<>();
            list.add(Component.translatable("gui.zenith.enchant.arcana").withStyle(ChatFormatting.DARK_PURPLE));
            if (maxStats.arcana == stats.arcana) {
                list.add(Component.translatable("info.zenith.percent_exact", stats.arcana).withStyle(ChatFormatting.GRAY));
            } else {
                list.add(Component.translatable("info.zenith.percent_at_least", stats.arcana).withStyle(ChatFormatting.GRAY));
                if (maxStats.arcana > -1) list.add(Component.translatable("info.zenith.percent_at_most", maxStats.arcana).withStyle(ChatFormatting.GRAY));
            }
            scn.renderComponentTooltip(stack, list, (int) mouseX - x, (int) mouseY - y);
        }
    }

    @Override
    public void setRecipe(EnchantingDisplay display, List<Widget> widgets, Point origin) {
        widgets.add(slot(6, 6, origin, display.getInputEntries().get(0), false).markInput());
        widgets.add(slot(37, 6, origin, display.getOutputEntries().get(0), false).markOutput());
    }

    public static void drawWordWrap(Font font, FormattedText pText, int pX, int pY, int pMaxWidth, int pColor, PoseStack stack) {
        for (FormattedCharSequence formattedcharsequence : font.split(pText, pMaxWidth)) {
            font.draw(stack, formattedcharsequence, pX, pY, pColor);
            pY += 9;
        }
    }
}
