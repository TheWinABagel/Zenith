package dev.shadowsoffire.apotheosis.mixin.compat.spectrum.present;

import de.dafuqs.spectrum.inventories.BedrockAnvilScreen;
import dev.shadowsoffire.apotheosis.util.DrawsOnLeft;
import dev.shadowsoffire.attributeslib.mixin.accessors.AbstractContainerScreenAccessor;
import dev.shadowsoffire.placebo.util.EnchantmentUtils;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.network.chat.Component;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.ArrayList;
import java.util.List;

@Environment(EnvType.CLIENT)
@Mixin(value = BedrockAnvilScreen.class, remap = false)
public abstract class BedrockAnvilScreenMixin {
    @Inject(method = "renderForeground", at = @At("RETURN"))
    private void zenith$renderCustom(GuiGraphics drawContext, int mouseX, int mouseY, float delta, CallbackInfo ci) {
        BedrockAnvilScreen anv = (BedrockAnvilScreen) (Object) this;
        int level = anv.getMenu().getLevelCost();
        if (level <= 0 || !anv.getMenu().getSlot(2).hasItem()) return;
        List<Component> list = new ArrayList<>();
        list.add(Component.literal(I18n.get("info.zenith.anvil_at", level)).withStyle(ChatFormatting.UNDERLINE, ChatFormatting.GREEN));
        int expCost = EnchantmentUtils.getTotalExperienceForLevel(level);
        list.add(Component.translatable("info.zenith.anvil_xp_cost", Component.literal("" + expCost).withStyle(ChatFormatting.GREEN),
                Component.literal("" + level).withStyle(ChatFormatting.GREEN)));
        DrawsOnLeft.draw(anv, drawContext, list, ((AbstractContainerScreenAccessor) anv).getTopPos() + 28);
    }
}
