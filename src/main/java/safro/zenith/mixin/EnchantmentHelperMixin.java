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

    @Inject(method = "getAvailableEnchantmentResults", at = @At("HEAD"), cancellable = true)
    private static void apothGetAvailableEnchantmentResults(int power, ItemStack stack, boolean allowTreasure, CallbackInfoReturnable<List<EnchantmentInstance>> cir) {
        if (Zenith.enableEnch) cir.setReturnValue(RealEnchantmentHelper.getAvailableEnchantmentResults(power, stack, allowTreasure));
    }

    @Inject(method = "selectEnchantment", at = @At("HEAD"), cancellable = true)
    private static void apothSelectEnchantment(RandomSource randomSource, ItemStack itemStack, int i, boolean bl, CallbackInfoReturnable<List<EnchantmentInstance>> cir) {
        if (Zenith.enableEnch) cir.setReturnValue(RealEnchantmentHelper.selectEnchantment((RandomSource) randomSource, itemStack, i, 0, 0, 0, bl));
    }

}
