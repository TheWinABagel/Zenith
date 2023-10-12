package dev.shadowsoffire.apotheosis.adventure.client;

import dev.shadowsoffire.apotheosis.Apotheosis;
import dev.shadowsoffire.attributeslib.client.AttributesLibClient;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import org.apache.commons.lang3.mutable.MutableInt;

import java.util.Optional;
import java.util.function.Supplier;

public class BossSpawnMessage {
    public static ResourceLocation ID = Apotheosis.loc("boss_spawn");

    private final BlockPos pos;
    private final int color;

    public BossSpawnMessage(BlockPos pos, int color) {
        this.pos = pos;
        this.color = color;
    }

    public static void init() {
        ServerPlayNetworking.registerGlobalReceiver(ID, (server, player, handler, buf, responseSender) -> {
            int color = buf.readInt();
            BlockPos pos = buf.readBlockPos();
            AdventureModuleClient.onBossSpawn(pos, toFloats(color));
        });
    }

    public static void sendTo(BlockPos pos, int color) {
        FriendlyByteBuf buf = PacketByteBufs.create();
        buf.writeBlockPos(pos);
        buf.writeInt(color);
        ClientPlayNetworking.send(ID, buf);
    }

    private static float[] toFloats(int color) {
        float[] arr = new float[3];
        arr[0] = (color >> 16 & 0xFF) / 255F;
        arr[1] = (color >> 8 & 0xFF) / 255F;
        arr[2] = (color & 0xFF) / 255F;
        return arr;
    }
/* //TODO Boss spawn packet handler
    public static class Provider implements MessageProvider<BossSpawnMessage> {



        @Override
        public void write(BossSpawnMessage msg, FriendlyByteBuf buf) {
            buf.writeBlockPos(msg.pos);
            buf.writeInt(msg.color);
        }

        @Override
        public BossSpawnMessage read(FriendlyByteBuf buf) {
            return new BossSpawnMessage(buf.readBlockPos(), buf.readInt());
        }

        @Override
        public void handle(BossSpawnMessage msg, Supplier<Context> ctx) {
            MessageHelper.handlePacket(() -> {
                AdventureModuleClient.onBossSpawn(msg.pos, toFloats(msg.color));
            }, ctx);
        }

        @Override
        public Optional<NetworkDirection> getNetworkDirection() {
            return Optional.of(NetworkDirection.PLAY_TO_CLIENT);
        }



    }*/

    public static record BossSpawnData(BlockPos pos, float[] color, MutableInt ticks) {

    }

}
