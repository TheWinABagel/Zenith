package safro.apotheosis.api;

import net.minecraft.network.Connection;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;

public interface DataPacketBlockEntity {
    void onDataPacket(Connection connection, ClientboundBlockEntityDataPacket packet);
}
