package safro.zenith.mixin;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.stats.Stats;
import net.minecraft.world.damagesource.CombatRules;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import safro.zenith.Zenith;
import safro.zenith.ench.enchantments.ReflectiveEnchant;
import safro.zenith.ench.enchantments.corrupted.LifeMendingEnchant;
import safro.zenith.potion.PotionModule;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin {

    @Shadow public abstract Iterable<ItemStack> getArmorSlots();

    @Shadow protected ItemStack useItem;

    @Shadow protected int useItemRemaining;

    @Shadow public abstract void setHealth(float f);

    @Shadow public abstract float getHealth();

    @Shadow
    public abstract boolean hasEffect(MobEffect ef);

    @Shadow
    public abstract MobEffectInstance getEffect(MobEffect ef);

    @Inject(method = "updatingUsingItem", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/LivingEntity;updateUsingItem(Lnet/minecraft/world/item/ItemStack;)V", shift = At.Shift.BEFORE))
    private void apotheosisUseTickEvent(CallbackInfo ci) {
        LivingEntity entity = (LivingEntity) (Object) this;
//        if (Zenith.enableAdventure) {
//            if (!useItem.isEmpty()) {
//                useItemRemaining = DeadlyModuleEvents.drawSpeed(entity, useItem, useItemRemaining);
//            }
//        }
    }

    @Inject(method = "heal", at = @At("HEAD"), cancellable = true)
    private void apotheosisHealEvent(float f, CallbackInfo ci) {
        LivingEntity entity = (LivingEntity) (Object) this;
        if (Zenith.enableEnch) {
            float a = LifeMendingEnchant.lifeMend(entity, f);
            if (a > -1) {
                float g = this.getHealth();
                if (g > 0.0F) {
                    this.setHealth(g + a);
                }
                ci.cancel();
            }
        }
    }

    @Inject(method = "hurt", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/LivingEntity;hurtCurrentlyUsedShield(F)V", shift = At.Shift.BEFORE))
    private void apotheosisShieldBlock(DamageSource damageSource, float f, CallbackInfoReturnable<Boolean> cir) {
        LivingEntity entity = (LivingEntity) (Object) this;
        if (Zenith.enableEnch) {
            ReflectiveEnchant.reflect(entity, damageSource, f);
        }
    }

    // TODO: Simplify to better inject
    @Inject(method = "getDamageAfterMagicAbsorb", at = @At("HEAD"), cancellable = true)
    public void apoGetDamageAfterMagicAbsorb(DamageSource source, float damage, CallbackInfoReturnable<Float> ci) {
        if (Zenith.enablePotion) {
            if (source.isBypassMagic()) {
                ci.setReturnValue(damage);
            } else {
                float mult = 1;
                if (this.hasEffect(MobEffects.DAMAGE_RESISTANCE) && source != DamageSource.OUT_OF_WORLD) {
                    int level = this.getEffect(MobEffects.DAMAGE_RESISTANCE).getAmplifier() + 1;
                    mult -= 0.2 * level;
                }
                if (PotionModule.SUNDERING_EFFECT != null && this.hasEffect(PotionModule.SUNDERING_EFFECT) && source != DamageSource.OUT_OF_WORLD) {
                    int level = this.getEffect(PotionModule.SUNDERING_EFFECT).getAmplifier() + 1;
                    mult += 0.2 * level;
                }

                float newDamage = damage * mult;
                float resisted = damage - newDamage;

                if (resisted > 0.0F && resisted < 3.4028235E37F) {
                    if ((Object) this instanceof ServerPlayer sp) {
                        sp.awardStat(Stats.CUSTOM.get(Stats.DAMAGE_RESISTED), Math.round(resisted * 10.0F));
                    } else if (source.getEntity() instanceof ServerPlayer sp) {
                        sp.awardStat(Stats.CUSTOM.get(Stats.DAMAGE_DEALT_RESISTED), Math.round(resisted * 10.0F));
                    }
                }

                damage = newDamage;

                if (damage <= 0.0F) {
                    ci.setReturnValue(0.0F);
                } else {
                    int k = EnchantmentHelper.getDamageProtection(this.getArmorSlots(), source);

                    if (k > 0) {
                        damage = CombatRules.getDamageAfterMagicAbsorb(damage, k);
                    }

                    ci.setReturnValue(damage);
                }
            }
        }
    }
}
