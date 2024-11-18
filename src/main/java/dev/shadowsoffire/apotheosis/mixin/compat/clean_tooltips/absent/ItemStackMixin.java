package dev.shadowsoffire.apotheosis.mixin.compat.clean_tooltips.absent;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import dev.shadowsoffire.apotheosis.ench.asm.EnchHooks;
import dev.shadowsoffire.placebo.events.PlaceboEventFactory;
import net.minecraft.ChatFormatting;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import java.util.*;

@Mixin(value = ItemStack.class, priority = 500)
public abstract class ItemStackMixin {

    private static void zenith$appendModifiedEnchTooltip(List<Component> tooltip, Enchantment ench, int realLevel, int nbtLevel) {
        MutableComponent mc = ench.getFullname(realLevel).copy();
        mc.getSiblings().clear();
        Component nbtLevelComp = Component.translatable("enchantment.level." + nbtLevel);
        Component realLevelComp = Component.translatable("enchantment.level." + realLevel);
        if (realLevel != 1 || EnchHooks.getMaxLevel(ench) != 1) mc.append(CommonComponents.SPACE).append(realLevelComp);

        int diff = realLevel - nbtLevel;
        char sign = diff > 0 ? '+' : '-';
        Component diffComp = Component.translatable("(%s " + sign + " %s)", Component.translatable("enchantment.level." + Math.abs(diff)), nbtLevelComp).withStyle(ChatFormatting.DARK_GRAY);
        mc.append(CommonComponents.SPACE).append(diffComp);
        if (realLevel == 0) {
            mc.withStyle(ChatFormatting.DARK_GRAY);
        }
        tooltip.add(mc);
    }

    /**
     * Modifies the enchantment tooltip lines to include the effective level, as well as the (NBT + bonus) calculation.
     */
    @WrapOperation(method = "getTooltipLines", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/ItemStack;appendEnchantmentNames(Ljava/util/List;Lnet/minecraft/nbt/ListTag;)V"))
    public void zenith$tooltipAddEnchantmentLinesWrapper(List<Component> tooltip, ListTag tagEnchants, Operation<Void> original) {
        ItemStack ths = (ItemStack) (Object) this;
        Map<Enchantment, Integer> realLevels = new HashMap<>(EnchantmentHelper.getEnchantments(ths));
        List<Component> enchTooltips = new ArrayList<>();

        // Iterate backwards to address duplicate enchantment logic (always use the last-positioned copy).
        for (int i = tagEnchants.size() - 1; i >= 0; i--) {
            CompoundTag compoundtag = tagEnchants.getCompound(i);

            Enchantment ench = BuiltInRegistries.ENCHANTMENT.get(EnchantmentHelper.getEnchantmentId(compoundtag));
            if (ench == null || !realLevels.containsKey(ench)) continue;

            int originalLevel = realLevels.remove(ench);
            int nbtLevel = PlaceboEventFactory.getEnchantmentLevelSpecific(originalLevel, ths, ench) - originalLevel;
            int realLevel = originalLevel + nbtLevel;

            if (nbtLevel == realLevel || nbtLevel == 0) {
                // Default logic when levels are the same
                enchTooltips.add(ench.getFullname(EnchantmentHelper.getEnchantmentLevel(compoundtag)));
            }
            else {
                // Show the change vs nbt level
                zenith$appendModifiedEnchTooltip(enchTooltips, ench, realLevel, nbtLevel);
            }
        }

        // Reverse and add to tooltip. Honestly we probably don't even need to reverse, but for consistency's sake.
        Collections.reverse(enchTooltips);
        tooltip.addAll(enchTooltips);
        if(ths.is(Items.ENCHANTED_BOOK)) return;
        // Show the tooltip for any modified enchantments not present in NBT.
        for (Map.Entry<Enchantment, Integer> real : realLevels.entrySet()) {
            if (real.getValue() > 0) zenith$appendModifiedEnchTooltip(tooltip, real.getKey(), real.getValue(), 0);
        }
    }
}
