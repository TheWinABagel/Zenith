package dev.shadowsoffire.apotheosis.mixin.adventure;

import com.llamalad7.mixinextras.injector.WrapWithCondition;
import dev.shadowsoffire.apotheosis.Apotheosis;
import dev.shadowsoffire.apotheosis.adventure.AdventureEvents;
import dev.shadowsoffire.apotheosis.adventure.AdventureModule;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.item.BowItem;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(BowItem.class)
public class BowItemMixin {

    @WrapWithCondition(method = "releaseUsing", at = @At(value = "INVOKE",target = "net/minecraft/world/level/Level.addFreshEntity (Lnet/minecraft/world/entity/Entity;)Z"))
    private boolean arrowEvent(Level instance, Entity entity) {
        if (Apotheosis.enableDebug) AdventureModule.LOGGER.info("Current arrow base damage pre event: {}", ((AbstractArrow) entity).getBaseDamage());
        AdventureEvents.fireArrow((AbstractArrow) entity);
        return true;
    }
}
