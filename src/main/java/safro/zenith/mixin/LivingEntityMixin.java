package safro.zenith.mixin;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.stats.Stats;
import net.minecraft.world.damagesource.CombatRules;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import safro.zenith.Zenith;
import safro.zenith.adventure.AdventureEvents;
import safro.zenith.adventure.AdventureModule;
import safro.zenith.adventure.affix.Affix;
import safro.zenith.adventure.affix.AffixHelper;
import safro.zenith.adventure.affix.AffixInstance;
import safro.zenith.ench.enchantments.ReflectiveEnchant;
import safro.zenith.ench.enchantments.corrupted.LifeMendingEnchant;
import safro.zenith.potion.PotionModule;

import java.util.Map;

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
    private void zenithUseTickEvent(CallbackInfo ci) {
        LivingEntity entity = (LivingEntity) (Object) this;
        if (Zenith.enableAdventure) {
            if (!useItem.isEmpty()) {
                useItemRemaining = AdventureEvents.drawSpeed(entity, useItem, useItemRemaining);
           }
        }
    }

    @Inject(method = "heal", at = @At("HEAD"), cancellable = true)
    private void zenithHealEvent(float f, CallbackInfo ci) {
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
//TODO create Inject for shield
    public void shieldBlock(DamageSource damageSource, float f, CallbackInfoReturnable<Boolean> cir) {
        LivingEntity source= ((LivingEntity) damageSource.getEntity());
        ItemStack stack = source.getUseItem();
        Map<Affix, AffixInstance> affixes = AffixHelper.getAffixes(stack);
        float blocked = f;
        for (AffixInstance inst : affixes.values()) {
            blocked = inst.onShieldBlock(source, damageSource, blocked);
        }
        if (blocked != f)
            f = blocked;
    }

    @Inject(method = "hurt", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/LivingEntity;hurtCurrentlyUsedShield(F)V", shift = At.Shift.BEFORE))
    private void zenithShieldBlock(DamageSource damageSource, float f, CallbackInfoReturnable<Boolean> cir) {
        LivingEntity entity = (LivingEntity) (Object) this;
        if (Zenith.enableEnch) {
            ReflectiveEnchant.reflect(entity, damageSource, f);
        }
    }

    // TODO: Simplify to better inject
    @Inject(method = "getDamageAfterMagicAbsorb", at = @At("HEAD"), cancellable = true)
    public void zenithGetDamageAfterMagicAbsorb(DamageSource source, float damage, CallbackInfoReturnable<Float> ci) {
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
    @Inject(method = "createLivingAttributes", at = @At("RETURN"))
    private static void createAttributes(CallbackInfoReturnable<AttributeSupplier.Builder> cir) {
        AttributeSupplier.Builder builder = cir.getReturnValue();
        if (Zenith.enableAdventure) {
            addIfExists(builder, AdventureModule.COLD_DAMAGE, AdventureModule.CRIT_CHANCE, AdventureModule.CRIT_DAMAGE, AdventureModule.CURRENT_HP_DAMAGE, AdventureModule.DRAW_SPEED, AdventureModule.FIRE_DAMAGE, AdventureModule.LIFE_STEAL, AdventureModule.OVERHEAL, AdventureModule.PIERCING, AdventureModule.GHOST_HEALTH, AdventureModule.MINING_SPEED, AdventureModule.ARROW_DAMAGE, AdventureModule.ARROW_VELOCITY, AdventureModule.EXPERIENCE_GAINED);
        }
    }


    private static void addIfExists(AttributeSupplier.Builder builder, Attribute... attribs) {
        for (Attribute attrib : attribs)
            if (attrib != null) builder.add(attrib);
    }
}
