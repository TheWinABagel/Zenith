package dev.shadowsoffire.apotheosis.potion.compat;

import dev.emi.emi.api.stack.EmiStack;
import dev.shadowsoffire.apotheosis.Apotheosis;
import dev.shadowsoffire.apotheosis.ench.compat.EnchantingEMIRecipe;
import dev.shadowsoffire.apotheosis.ench.table.EnchantingRecipe;
import dev.shadowsoffire.apotheosis.potion.PotionModule;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.item.alchemy.PotionUtils;

public class PotionCharmEnchintingEMIRecipe extends EnchantingEMIRecipe {
    private final ResourceLocation id;
    public PotionCharmEnchintingEMIRecipe(EnchantingRecipe recipe, ItemStack potion) {
        super(recipe);
        input = EmiStack.of(fixPotion(potion));
        output = EmiStack.of(fixPotionOutput(potion));
        id = getRecipeId(potion);
    }

    private ItemStack fixPotion(ItemStack potionStack) {
        Potion pot = PotionUtils.getPotion(potionStack);
        ItemStack charmStack = new ItemStack(PotionModule.POTION_CHARM);
        PotionUtils.setPotion(charmStack, pot);
        return charmStack;
    }

    private ItemStack fixPotionOutput(ItemStack potionStack) {
        ItemStack charm = fixPotion(potionStack);
        charm.getOrCreateTag().putByte("Unbreakable", (byte) 1);
        return charm;
    }

    private ResourceLocation getRecipeId(ItemStack stack) {
        Potion pot = PotionUtils.getPotion(stack);
        MobEffectInstance contained = pot.getEffects().get(0);
        return Apotheosis.syntheticLoc(BuiltInRegistries.MOB_EFFECT.getKey(contained.getEffect()).getPath() + "_" + contained.getAmplifier() + "_" + contained.getDuration());
    }

    @Override
    public ResourceLocation getId() {
        return id;
    }
}
