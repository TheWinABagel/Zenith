package safro.zenith.mixin;

import net.minecraft.util.RandomSource;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.EnchantmentInstance;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import safro.zenith.Zenith;
import safro.zenith.ench.table.RealEnchantmentHelper;

import java.util.List;
import java.util.Random;

@Mixin(EnchantmentHelper.class)
public class EnchantmentHelperMixin {

    /**
     * @author Shadows
     * @reason Enables zenith special handling of enchanting rules.  More lenient injection is not possible.
     * @param power The current enchanting power.
     * @param stack The ItemStack being enchanted.
     * @param allowTreasure If treasure enchantments are allowed.
     * @return All possible enchantments that are eligible to be placed on this item at a specific power level.
     */
    @Inject(method = "getAvailableEnchantmentResults", at = @At("HEAD"), cancellable = true)
    private static void zenithGetAvailableEnchantmentResults(int power, ItemStack stack, boolean allowTreasure, CallbackInfoReturnable<List<EnchantmentInstance>> cir) {
        if (Zenith.enableEnch) cir.setReturnValue(RealEnchantmentHelper.getAvailableEnchantmentResults(power, stack, allowTreasure));
    }

    /**
     * @author Shadows
     * @reason Enables global consistency with the apotheosis enchanting system, even outside the table.
     * @param pRandom The random
     * @param pItemStack The stack being enchanted
     * @param pLevel The enchanting level
     * @param pAllowTreasure If treasure enchantments are allowed.
     * @return A list of enchantments to apply to this item.
     */
    @Inject(method = "selectEnchantment", at = @At("HEAD"), cancellable = true)
    private static void zenithSelectEnchantment(RandomSource pRandom, ItemStack pItemStack, int pLevel, boolean pAllowTreasure, CallbackInfoReturnable<List<EnchantmentInstance>> cir) {
        if (Zenith.enableEnch) cir.setReturnValue(RealEnchantmentHelper.selectEnchantment(pRandom, pItemStack, pLevel, 0, 0, 0, pAllowTreasure));
    }


}
