package dev.shadowsoffire.apotheosis.potion.compat;

import dev.emi.emi.api.EmiRegistry;
import dev.emi.emi.api.stack.Comparison;
import dev.emi.emi.api.stack.EmiStack;
import dev.shadowsoffire.apotheosis.Apotheosis;
import dev.shadowsoffire.apotheosis.ench.table.EnchantingRecipe;
import dev.shadowsoffire.apotheosis.potion.PotionCharmRecipe;
import dev.shadowsoffire.apotheosis.potion.PotionModule;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.alchemy.PotionUtils;
import net.minecraft.world.item.crafting.ShapedRecipe;

import java.util.ArrayList;
import java.util.List;

public class PotionEMIPlugin {

    public static void register(EmiRegistry registry) {
        Comparison charmComparison = Comparison.of(((a, b) -> {
            CompoundTag tagA = a.getItemStack().getOrCreateTag();
            CompoundTag tagB = b.getItemStack().getOrCreateTag();
            if (PotionUtils.getPotion(tagA) != PotionUtils.getPotion(tagB)) return false;
            if (tagA.getByte("Unbreakable") != tagB.getByte("Unbreakable")) return false;
            return true;
        }));
        registry.setDefaultComparison(EmiStack.of(PotionModule.POTION_CHARM), charmComparison);
        registry.removeRecipes(Apotheosis.loc("potion_charm"));
        List<ItemStack> validPotions = new ArrayList<>();
        PotionCharmRecipe.getValidPotions(validPotions);
        registry.getRecipeManager().byKey(Apotheosis.loc("potion_charm")).ifPresent(charmRecipe -> {
            for (ItemStack potionStack : validPotions) {
                if (potionStack == null) continue;
                registry.addRecipe(new PotionCharmEMIRecipe((ShapedRecipe) charmRecipe, potionStack));
            }
        });
        if (!Apotheosis.enableEnch) return;
        registry.removeRecipes(Apotheosis.loc("potion_charm_enchanting"));
        registry.getRecipeManager().byKey(Apotheosis.loc("potion_charm_enchanting")).ifPresent(charmRecipe -> {
            for (ItemStack potionStack : validPotions) {
                if (potionStack == null) continue;
                registry.addRecipe(new PotionCharmEnchintingEMIRecipe((EnchantingRecipe) charmRecipe, potionStack));
            }
        });
    }
}
