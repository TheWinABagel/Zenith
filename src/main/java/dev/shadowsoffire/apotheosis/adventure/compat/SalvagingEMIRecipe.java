package dev.shadowsoffire.apotheosis.adventure.compat;

import com.mojang.blaze3d.vertex.PoseStack;
import dev.emi.emi.api.recipe.EmiRecipe;
import dev.emi.emi.api.recipe.EmiRecipeCategory;
import dev.emi.emi.api.stack.EmiIngredient;
import dev.emi.emi.api.stack.EmiStack;
import dev.emi.emi.api.widget.WidgetHolder;
import dev.shadowsoffire.apotheosis.Apotheosis;
import dev.shadowsoffire.apotheosis.adventure.affix.salvaging.SalvagingRecipe;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class SalvagingEMIRecipe implements EmiRecipe {
    private static final ResourceLocation TEXTURES = new ResourceLocation(Apotheosis.MODID, "textures/gui/salvage_jei.png");

    private final List<SalvagingRecipe.OutputData> outputs;
    private final ResourceLocation id;
    private final EmiIngredient input;

    public SalvagingEMIRecipe(SalvagingRecipe recipe) {
        this.input = EmiIngredient.of(recipe.getInput());
        this.outputs = recipe.getOutputs();
        this.id = recipe.getId();
    }

    @Override
    public EmiRecipeCategory getCategory() {
        return AdventureEMIPlugin.SALVAGING;
    }

    @Override
    public @Nullable ResourceLocation getId() {
        return id;
    }

    @Override
    public List<EmiIngredient> getInputs() {
        return List.of(input);
    }

    @Override
    public List<EmiStack> getOutputs() {
        return outputs.stream().map(SalvagingRecipe.OutputData::getStack).map(EmiStack::of).toList();
    }

    @Override
    public int getDisplayWidth() {
        return 98;
    }

    @Override
    public int getDisplayHeight() {
        return 74;
    }

    @Override
    public void addWidgets(WidgetHolder widgets) {
        widgets.addTexture(TEXTURES, 1, 1, 98, 74, 0, 0);
        widgets.addSlot(input, 5, 29).drawBack(false);
        int indx = 0;
        for (var data : outputs) {
            int pX = 59 + 18 * (indx % 2);
            int pY = 11 + 18 * (indx / 2);
            widgets.addSlot(EmiStack.of(data.getStack()), pX, pY).recipeContext(this).drawBack(false);
            indx++;
        }

        widgets.addDrawable(0, 0, 100, 100, (gfx, mouseX, mouseY, delta) -> {
            Font font = Minecraft.getInstance().font;
            PoseStack pose = gfx.pose();

            int idx = 0;
            for (var data : outputs) {
                pose.pushPose();
                pose.translate(0, 0, 200);
                String text = String.format("%d-%d", data.getMin(), data.getMax());

                float x = 59 + 18 * (idx % 2) + (16 - font.width(text) * 0.5F);
                float y = 23F + 18 * (idx / 2);

                float scale = 0.5F;

                pose.scale(scale, scale, 1);
                gfx.drawString(font, text, (int) (x / scale), (int) (y / scale), 0xFFFFFF);

                idx++;
                pose.popPose();
            }
        });
    }
}
