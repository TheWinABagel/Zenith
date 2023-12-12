package dev.shadowsoffire.apotheosis.mixin.compat.dragonloot.present;

import net.dragonloot.item.DragonBowItem;
import net.minecraft.world.item.Item;
import net.projectile_damage.api.IProjectileWeapon;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Pseudo
@Mixin(value = DragonBowItem.class, remap = false)
public class DragonBowItemMixin {

    @Inject(method = "<init>", at = @At("TAIL"))
    private void setDamage(Item.Properties settings, CallbackInfo ci){
        ((IProjectileWeapon) this).setProjectileDamage(8.5f);
    }
}
