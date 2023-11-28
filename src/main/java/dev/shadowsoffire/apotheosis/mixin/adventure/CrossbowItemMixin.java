package dev.shadowsoffire.apotheosis.mixin.adventure;

import com.llamalad7.mixinextras.injector.WrapWithCondition;
import dev.shadowsoffire.apotheosis.Apotheosis;
import dev.shadowsoffire.apotheosis.adventure.AdventureEvents;
import dev.shadowsoffire.apotheosis.adventure.AdventureModule;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.item.CrossbowItem;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(CrossbowItem.class)
public class CrossbowItemMixin {

    @WrapWithCondition(method = "shootProjectile", at = @At(value = "INVOKE",target = "net/minecraft/world/level/Level.addFreshEntity (Lnet/minecraft/world/entity/Entity;)Z"))
    private static boolean arrowEvent(Level instance, Entity entity) {
        if (Apotheosis.enableAdventure && entity instanceof AbstractArrow arrow) {
            if (Apotheosis.enableDebug) AdventureModule.LOGGER.info("Current arrow base damage pre event: {}", arrow.getBaseDamage());
            AdventureEvents.fireArrow(arrow);
        }
        return true;
    }
}
