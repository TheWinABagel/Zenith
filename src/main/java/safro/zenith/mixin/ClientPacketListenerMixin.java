package safro.zenith.mixin;

import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.network.Connection;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import safro.zenith.api.DataPacketBlockEntity;

@Mixin(ClientPacketListener.class)
public class ClientPacketListenerMixin {

    @Shadow @Final private Connection connection;

    @Inject(method = "method_38542", at = @At("HEAD"))
    private void apothHandleData(ClientboundBlockEntityDataPacket packet, BlockEntity blockEntity, CallbackInfo ci) {
        if (blockEntity instanceof DataPacketBlockEntity dp) {
            dp.onDataPacket(connection, packet);
        }
    }
}
