package dev.shadowsoffire.apotheosis.mixin.ench;

import dev.shadowsoffire.apotheosis.Apotheosis;
import dev.shadowsoffire.apotheosis.ench.table.RealEnchantmentHelper;
import net.minecraft.util.RandomSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.EnchantmentInstance;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;

@Mixin(value = EnchantmentHelper.class, priority = 1100)
public class EnchantmentHelperMixin {

    /**
     * @param level         The current enchanting power.
     * @param stack         The ItemStack being enchanted.
     * @param allowTreasure If treasure enchantments are allowed.
     * @author Shadows
     * @reason Enables apotheosis special handling of enchanting rules. More lenient injection is not possible.
     */
    @Inject(method = "getAvailableEnchantmentResults", at = @At("HEAD"), cancellable = true)
    private static void getAvailableEnchantmentResults(int level, ItemStack stack, boolean allowTreasure, CallbackInfoReturnable<List<EnchantmentInstance>> cir) {
        if (!Apotheosis.enableEnch) return;
        cir.setReturnValue(RealEnchantmentHelper.getAvailableEnchantmentResults(level, stack, allowTreasure));
    }

    /**
     * @param random        The random
     * @param itemStack     The stack being enchanted
     * @param level         The enchanting level
     * @param allowTreasure If treasure enchantments are allowed.
     * @author Shadows
     * @reason Enables global consistency with the apotheosis enchanting system, even outside the table.
     */
    @Inject(method = "selectEnchantment", at = @At("HEAD"), cancellable = true)
    private static void selectEnchantment(RandomSource random, ItemStack itemStack, int level, boolean allowTreasure, CallbackInfoReturnable<List<EnchantmentInstance>> cir) {
        cir.setReturnValue(RealEnchantmentHelper.selectEnchantment(random, itemStack, level, 15F, 0, 0, allowTreasure));
    }

    /**
     * Injection to {@link EnchantmentHelper#getDamageProtection(Iterable, DamageSource)}
     */
/*    @Inject(at = @At("RETURN"), method = "getDamageProtection(Ljava/lang/Iterable;Lnet/minecraft/world/damagesource/DamageSource;)I", cancellable = true)
    private static void apoth_getDamageProtection(Iterable<ItemStack> stacks, DamageSource source, CallbackInfoReturnable<Integer> cir) {
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
/*    @Inject(at = @At("RETURN"), method = "getDamageBonus(Lnet/minecraft/world/item/ItemStack;Lnet/minecraft/world/entity/MobType;)F", cancellable = true)
    private static void apoth_getDamageBonus(ItemStack stack, MobType type, CallbackInfoReturnable<Float> cir) {
        float dmg = cir.getReturnValueF();
        var affixes = AffixHelper.getAffixes(stack);
        for (AffixInstance inst : affixes.values()) {
            dmg += inst.getDamageBonus(type);
        }
        cir.setReturnValue(dmg);
    }
*/
    /**
     * Injection to {@link EnchantmentHelper#doPostDamageEffects(LivingEntity, Entity)}
     */
/*    @Inject(at = @At("TAIL"), method = "doPostDamageEffects(Lnet/minecraft/world/entity/LivingEntity;Lnet/minecraft/world/entity/Entity;)V")
    private static void apoth_doPostDamageEffects(LivingEntity user, Entity target, CallbackInfo ci) {
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
*/
    /**
     * Injection to {@link EnchantmentHelper#doPostHurtEffects(LivingEntity, Entity)}
     */
/*    @Inject(at = @At("TAIL"), method = "doPostHurtEffects(Lnet/minecraft/world/entity/LivingEntity;Lnet/minecraft/world/entity/Entity;)V")
    private static void apoth_doPostHurtEffects(LivingEntity user, Entity attacker, CallbackInfo ci) {
        if (user == null) return;
        for (ItemStack s : user.getAllSlots()) {
            var affixes = AffixHelper.getAffixes(s);
            for (AffixInstance inst : affixes.values()) {
                inst.doPostHurt(user, attacker);
            }
        }
    }
*/
}
