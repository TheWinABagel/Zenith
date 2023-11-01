package dev.shadowsoffire.apotheosis.mixin.adventure;

import dev.shadowsoffire.apotheosis.Apotheosis;
import dev.shadowsoffire.apotheosis.adventure.affix.AffixHelper;
import dev.shadowsoffire.apotheosis.adventure.affix.AffixInstance;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobType;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = EnchantmentHelper.class, priority = 1100)
public class EnchantmentHelperMixinAdventure {

    /**
     * Injection to {@link EnchantmentHelper#getDamageProtection(Iterable, DamageSource)}
     */
    @Inject(at = @At("RETURN"), method = "getDamageProtection(Ljava/lang/Iterable;Lnet/minecraft/world/damagesource/DamageSource;)I", cancellable = true)
    private static void zenith_getDamageProtection(Iterable<ItemStack> stacks, DamageSource source, CallbackInfoReturnable<Integer> cir) {
        if (!Apotheosis.enableAdventure) return;
        int prot = cir.getReturnValueI();
        for (ItemStack s : stacks) {
            var affixes = AffixHelper.getAffixes(s);
            for (AffixInstance inst : affixes.values()) {
                prot += inst.getDamageProtection(source);
            }
        }
        cir.setReturnValue(prot);
    }

    /**
     * Injection to {@link EnchantmentHelper#getDamageBonus(ItemStack, MobType)
     */
    @Inject(at = @At("RETURN"), method = "getDamageBonus(Lnet/minecraft/world/item/ItemStack;Lnet/minecraft/world/entity/MobType;)F", cancellable = true)
    private static void zenith_getDamageBonus(ItemStack stack, MobType type, CallbackInfoReturnable<Float> cir) {
        if (!Apotheosis.enableAdventure) return;
        float dmg = cir.getReturnValueF();
        var affixes = AffixHelper.getAffixes(stack);
        for (AffixInstance inst : affixes.values()) {
            dmg += inst.getDamageBonus(type);
        }
        cir.setReturnValue(dmg);
    }

    /**
     * Injection to {@link EnchantmentHelper#doPostDamageEffects(LivingEntity, Entity)}
     */
    @Inject(at = @At("TAIL"), method = "doPostDamageEffects(Lnet/minecraft/world/entity/LivingEntity;Lnet/minecraft/world/entity/Entity;)V")
    private static void zenith_doPostDamageEffects(LivingEntity user, Entity target, CallbackInfo ci) {
        if (!Apotheosis.enableAdventure) return;
        if (user == null) return;
        for (ItemStack s : user.getAllSlots()) {
            var affixes = AffixHelper.getAffixes(s);
            for (AffixInstance inst : affixes.values()) {
                int old = target.invulnerableTime;
                target.invulnerableTime = 0;
                inst.doPostAttack(user, target);
                target.invulnerableTime = old;
            }
        }
    }

    /**
     * Injection to {@link EnchantmentHelper#doPostHurtEffects(LivingEntity, Entity)}
     */
    @Inject(at = @At("TAIL"), method = "doPostHurtEffects(Lnet/minecraft/world/entity/LivingEntity;Lnet/minecraft/world/entity/Entity;)V")
    private static void zenith_doPostHurtEffects(LivingEntity user, Entity attacker, CallbackInfo ci) {
        if (!Apotheosis.enableAdventure) return;
        if (user == null) return;
        for (ItemStack s : user.getAllSlots()) {
            var affixes = AffixHelper.getAffixes(s);
            for (AffixInstance inst : affixes.values()) {
                inst.doPostHurt(user, attacker);
            }
        }
    }

}
