package dev.shadowsoffire.apotheosis.adventure.client;

import dev.shadowsoffire.apotheosis.Apotheosis;
import dev.shadowsoffire.apotheosis.adventure.AdventureModule;
import dev.shadowsoffire.attributeslib.client.AttributesLibClient;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.FabricPacket;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.PacketType;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
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

    }

    public static void sendTo(ServerPlayer player, BossSpawnMessage msg) {
        FriendlyByteBuf buf = PacketByteBufs.create();
        buf.writeInt(msg.color);
        buf.writeBlockPos(msg.pos);
        AdventureModule.LOGGER.warn("Message SENT Pos {}", msg.pos);
        //ClientPlayNetworking.send(ID, buf);
        ServerPlayNetworking.send(player, ID, buf);
    }

    public static float[] toFloats(int color) {
        float[] arr = new float[3];
        arr[0] = (color >> 16 & 0xFF) / 255F;
        arr[1] = (color >> 8 & 0xFF) / 255F;
        arr[2] = (color & 0xFF) / 255F;
        return arr;
    }


    public static record BossSpawnData(BlockPos pos, float[] color, MutableInt ticks) {
    }

}
