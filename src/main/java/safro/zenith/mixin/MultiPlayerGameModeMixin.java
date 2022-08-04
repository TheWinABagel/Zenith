package safro.zenith.mixin;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.client.multiplayer.MultiPlayerGameMode;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.protocol.game.ServerboundUseItemOnPacket;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.phys.BlockHitResult;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import safro.zenith.Zenith;
import safro.zenith.ench.enchantments.NaturesBlessingEnchant;
import safro.zenith.spawn.SpawnerModule;

@Mixin(MultiPlayerGameMode.class)
public class MultiPlayerGameModeMixin {

    @Shadow @Final private ClientPacketListener connection;

    @Inject(method = "useItemOn", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/player/LocalPlayer;getItemInHand(Lnet/minecraft/world/InteractionHand;)Lnet/minecraft/world/item/ItemStack;", shift = At.Shift.AFTER), cancellable = true)
    private void apotheosisRightClick(LocalPlayer localPlayer, ClientLevel clientLevel, InteractionHand interactionHand, BlockHitResult blockHitResult, CallbackInfoReturnable<InteractionResult> cir) {
        if (Zenith.enableEnch) {
            InteractionResult result = NaturesBlessingEnchant.rightClick(localPlayer.getItemInHand(interactionHand), localPlayer, clientLevel, blockHitResult.getBlockPos(), interactionHand);
            if (result != null) {
                this.connection.send(new ServerboundUseItemOnPacket(interactionHand, blockHitResult));
                cir.setReturnValue(result);
            }
        }

        if (Zenith.enableSpawner) {
            if (SpawnerModule.handleUseItem(localPlayer.getCommandSenderWorld(), blockHitResult.getBlockPos(), localPlayer.getItemInHand(interactionHand)) == InteractionResult.FAIL) {
                this.connection.send(new ServerboundUseItemOnPacket(interactionHand, blockHitResult));
                cir.setReturnValue(InteractionResult.PASS);
            }
        }
    }
}
