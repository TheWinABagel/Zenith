package dev.shadowsoffire.apotheosis.mixin.adventure;

import dev.shadowsoffire.apotheosis.adventure.affix.socket.gem.bonus.special.AllStatsBonus;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = Player.class, priority = 10)
public class CollectAttributesPlayerMixin {

    @Inject(method = "createAttributes", at = @At("RETURN"))
    private static void zenithCollectPlayerAttributes(CallbackInfoReturnable<AttributeSupplier.Builder> cir){
        AllStatsBonus.playerAttributes = cir.getReturnValue().builder.keySet().stream().toList();
    }
}
