package dev.shadowsoffire.apotheosis.ench.compat;

import com.mojang.blaze3d.systems.RenderSystem;
import dev.emi.emi.api.recipe.EmiRecipe;
import dev.emi.emi.api.recipe.EmiRecipeCategory;
import dev.emi.emi.api.stack.EmiIngredient;
import dev.emi.emi.api.stack.EmiStack;
import dev.emi.emi.api.widget.WidgetHolder;
import dev.shadowsoffire.apotheosis.Apotheosis;
import dev.shadowsoffire.apotheosis.ench.Ench;
import dev.shadowsoffire.apotheosis.ench.table.EnchantingRecipe;
import dev.shadowsoffire.apotheosis.ench.table.EnchantingStatRegistry;
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
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class EnchantingEMIRecipe implements EmiRecipe {
    private final EnchantingRecipe recipe;
    protected final ResourceLocation id;
    protected EmiIngredient input;
    protected EmiStack output;

    private static final ResourceLocation TEXTURES = Apotheosis.loc( "textures/gui/enchanting_jei.png");

    public EnchantingEMIRecipe(EnchantingRecipe recipe) {
        this.recipe = recipe;
        this.input = EmiIngredient.of(recipe.getInput());
        this.output = EmiStack.of(recipe.getOutput());
        this.id = recipe.getId();
    }

    @Override
    public EmiRecipeCategory getCategory() {
        return EnchEMIPlugin.ENCHANTING;
    }

    @Override
    public ResourceLocation getId() {
        return this.id;
    }

    @Override
    public List<EmiIngredient> getInputs() {
        return List.of(input);
    }

    @Override
    public List<EmiStack> getOutputs() {
        return List.of(output);
    }

    @Override
    public int getDisplayWidth() {
        return 170;
    }

    @Override
    public int getDisplayHeight() {
        return 56;
    }

    @Override
    public void addWidgets(WidgetHolder widgets) {
        widgets.addTexture(TEXTURES, 1, 1, 170, 56, 0, 0);
        widgets.addDrawable(0, 0, 170, 56, (gfx, mouseX, mouseY, delta) -> {
            boolean hover = false;
            if (mouseX > 57 && mouseX <= 57 + 108 && mouseY > 4 && mouseY <= 4 + 19) {
                gfx.blit(TEXTURES, 57, 4, 0, 0, 71, 108, 19, 256, 256);
                hover = true;
            }

            Font font = Minecraft.getInstance().font;
            EnchantingStatRegistry.Stats stats = recipe.getRequirements();
            EnchantingStatRegistry.Stats maxStats = recipe.getMaxRequirements();
            gfx.drawString(font, I18n.get("gui.zenith.enchant.eterna"), 16, 26, 0x3DB53D, false);
            gfx.drawString(font, I18n.get("gui.zenith.enchant.quanta"), 16, 36, 0xFC5454, false);
            gfx.drawString(font, I18n.get("gui.zenith.enchant.arcana"), 16, 46, 0xA800A8, false);
            int level = (int) (stats.eterna() * 2);

            String s = "" + level;
            int width = 86 - font.width(s);
            EnchantmentNames.getInstance().initSeed(recipe.getId().hashCode());
            FormattedText itextproperties = EnchantmentNames.getInstance().getRandomName(font, width);
            int color = hover ? 16777088 : 6839882;
            drawWordWrap(font, itextproperties, 77, 6, width, color, gfx);
            color = 8453920;
            gfx.drawString(font, s, 77 + width, 13, color);

            int[] pos = { (int) (stats.eterna() / EnchantingStatRegistry.getAbsoluteMaxEterna() * 110), (int) (stats.quanta() / 100 * 110), (int) (stats.arcana() / 100 * 110) };
            if (stats.eterna() > 0) {
                gfx.blit(TEXTURES, 57, 28, 0, 56, pos[0], 5, 256, 256);
            }
            if (stats.quanta() > 0) {
                gfx.blit(TEXTURES, 57, 38, 0, 61, pos[1], 5, 256, 256);
            }
            if (stats.arcana() > 0) {
                gfx.blit(TEXTURES, 57, 48, 0, 66, pos[2], 5, 256, 256);
            }
            RenderSystem.enableBlend();
            if (maxStats.eterna() > 0) {
                gfx.blit(TEXTURES, 57 + pos[0], 28, pos[0], 90, (int) ((maxStats.eterna() - stats.eterna()) / EnchantingStatRegistry.getAbsoluteMaxEterna() * 110), 5, 256, 256);
            }
            if (maxStats.quanta() > 0) {
                gfx.blit(TEXTURES, 57 + pos[1], 38, pos[1], 95, (int) ((maxStats.quanta() - stats.quanta()) / 100 * 110), 5, 256, 256);
            }
            if (maxStats.arcana() > 0) {
                gfx.blit(TEXTURES, 57 + pos[2], 48, pos[2], 100, (int) ((maxStats.arcana() - stats.arcana()) / 100 * 110), 5, 256, 256);
            }
            RenderSystem.disableBlend();
            Screen scn = Minecraft.getInstance().screen;
            if (scn == null) return; // We need this to render tooltips, bail if its not there.
            if (hover) {
                List<Component> list = new ArrayList<>();
                list.add(Component.translatable("container.enchant.clue", Ench.Enchantments.INFUSION.getFullname(1).getString()).withStyle(ChatFormatting.GRAY, ChatFormatting.ITALIC));
                gfx.renderComponentTooltip(font, list, mouseX, mouseY);
            }
            else if (mouseX > 57 && mouseX <= 57 + 110 && mouseY > 27 && mouseY <= 28 + 5) {
                List<Component> list = new ArrayList<>();
                list.add(Component.translatable("gui.zenith.enchant.eterna").withStyle(ChatFormatting.GREEN));
                if (maxStats.eterna() == stats.eterna()) {
                    list.add(Component.translatable("info.zenith.eterna_exact", stats.eterna(), EnchantingStatRegistry.getAbsoluteMaxEterna()).withStyle(ChatFormatting.GRAY));
                }
                else {
                    list.add(Component.translatable("info.zenith.eterna_at_least", stats.eterna(), EnchantingStatRegistry.getAbsoluteMaxEterna()).withStyle(ChatFormatting.GRAY));
                    if (maxStats.eterna() > -1) list.add(Component.translatable("info.zenith.eterna_at_most", maxStats.eterna(), EnchantingStatRegistry.getAbsoluteMaxEterna()).withStyle(ChatFormatting.GRAY));
                }
                gfx.renderComponentTooltip(font, list, mouseX, mouseY);
            }
            else if (mouseX > 57 && mouseX <= 57 + 110 && mouseY > 37 && mouseY <= 38 + 5) {
                List<Component> list = new ArrayList<>();
                list.add(Component.translatable("gui.zenith.enchant.quanta").withStyle(ChatFormatting.RED));
                if (maxStats.quanta() == stats.quanta()) {
                    list.add(Component.translatable("info.zenith.percent_exact", stats.quanta()).withStyle(ChatFormatting.GRAY));
                }
                else {
                    list.add(Component.translatable("info.zenith.percent_at_least", stats.quanta()).withStyle(ChatFormatting.GRAY));
                    if (maxStats.quanta() > -1) list.add(Component.translatable("info.zenith.percent_at_most", maxStats.quanta()).withStyle(ChatFormatting.GRAY));
                }
                gfx.renderComponentTooltip(font, list, mouseX, mouseY);
            }
            else if (mouseX > 57 && mouseX <= 57 + 110 && mouseY > 47 && mouseY <= 48 + 5) {
                List<Component> list = new ArrayList<>();
                list.add(Component.translatable("gui.zenith.enchant.arcana").withStyle(ChatFormatting.DARK_PURPLE));
                if (maxStats.arcana() == stats.arcana()) {
                    list.add(Component.translatable("info.zenith.percent_exact", stats.arcana()).withStyle(ChatFormatting.GRAY));
                }
                else {
                    list.add(Component.translatable("info.zenith.percent_at_least", stats.arcana()).withStyle(ChatFormatting.GRAY));
                    if (maxStats.arcana() > -1) list.add(Component.translatable("info.zenith.percent_at_most", maxStats.arcana()).withStyle(ChatFormatting.GRAY));
                }
                gfx.renderComponentTooltip(font, list, mouseX, mouseY);
            }
        });
        widgets.addSlot(input, 6, 6);
        widgets.addSlot(output, 37, 6).recipeContext(this);
    }

    public static void drawWordWrap(Font font, FormattedText pText, int pX, int pY, int pMaxWidth, int pColor, GuiGraphics gfx) {
        for (FormattedCharSequence formattedcharsequence : font.split(pText, pMaxWidth)) {
            gfx.drawString(font, formattedcharsequence, pX, pY, pColor, false);
            pY += 9;
        }
    }

    public float getEterna() {
        return recipe.getRequirements().eterna();
    }
}
