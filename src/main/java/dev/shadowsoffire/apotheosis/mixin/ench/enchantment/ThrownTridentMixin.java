package dev.shadowsoffire.apotheosis.mixin.ench.enchantment;

import dev.shadowsoffire.apotheosis.ench.EnchModuleEvents.TridentGetter;
import dev.shadowsoffire.apotheosis.mixin.accessors.AbstractArrowAccessor;
import it.unimi.dsi.fastutil.ints.IntOpenHashSet;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.entity.projectile.ThrownTrident;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Mixin to tridents to enable Piercing to work.
 */
@Mixin(ThrownTrident.class)
public abstract class ThrownTridentMixin extends AbstractArrow implements TridentGetter {

    @Unique
    Vec3 oldVel = null;

    @Shadow
    private boolean dealtDamage;

    protected ThrownTridentMixin(EntityType<? extends AbstractArrow> type, Level level) {
        super(type, level);
    }

    @Override
    @Accessor
    public abstract ItemStack getTridentItem();

    @Inject(method = "<init>*", at = @At("TAIL"), require = 1, remap = false)
    private void init(CallbackInfo ci) {
        this.setPierceLevel((byte) EnchantmentHelper.getItemEnchantmentLevel(Enchantments.PIERCING, this.getTridentItem()));
    }

    @Inject(method = "onHitEntity(Lnet/minecraft/world/phys/EntityHitResult;)V", at = @At("HEAD"), cancellable = true, require = 1)
    public void startHitEntity(EntityHitResult res, CallbackInfo ci) {
        if (this.getPierceLevel() > 0) {
            if (((AbstractArrowAccessor) this).getPiercingIgnoreEntityIds() == null) {
                ((AbstractArrowAccessor) this).setPiercingIgnoreEntityIds(new IntOpenHashSet(this.getPierceLevel()));
            }
            if (((AbstractArrowAccessor) this).getPiercingIgnoreEntityIds().contains(res.getEntity().getId())) ci.cancel();
        }

        this.oldVel = this.getDeltaMovement();
    }

    @Inject(method = "onHitEntity(Lnet/minecraft/world/phys/EntityHitResult;)V", at = @At("TAIL"), require = 1)
    public void endHitEntity(EntityHitResult res, CallbackInfo ci) {
        if (this.getPierceLevel() > 0) {
            ((AbstractArrowAccessor) this).getPiercingIgnoreEntityIds().add(res.getEntity().getId());

            if (((AbstractArrowAccessor) this).getPiercingIgnoreEntityIds().size() <= this.getPierceLevel()) {
                this.dealtDamage = false;
                this.setDeltaMovement(this.oldVel);
            }
        }
    }
}
