//package dev.shadowsoffire.apotheosis.mixin.compat.goblintraders.present;
//
//import com.mrcrayfish.goblintraders.Hooks;
//import dev.shadowsoffire.apotheosis.ench.asm.EnchHooks;
//import net.minecraft.world.item.enchantment.Enchantment;
//import org.spongepowered.asm.mixin.Mixin;
//import org.spongepowered.asm.mixin.Pseudo;
//import org.spongepowered.asm.mixin.injection.At;
//import org.spongepowered.asm.mixin.injection.Inject;
//import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
//
//@Pseudo
//@Mixin(value = Hooks.class, remap = false)
//public class HooksMixin {
//    @Inject(method = "getEnchantmentLevel", at = @At("HEAD"), cancellable = true)
//    private static void zenith$redirectMaxEnchLevel(Enchantment enchantment, CallbackInfoReturnable<Integer> cir){
//        cir.setReturnValue(EnchHooks.getMaxLevel(enchantment));
//    }
//}
