package safro.apotheosis.network;

import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import safro.apotheosis.Apotheosis;
import safro.apotheosis.api.event.ServerEvents;
import safro.apotheosis.api.json.ApothJsonReloadListener;

import java.util.List;

public class ReloadListenerPacket {

    public static class Start {
        public static ResourceLocation ID = new ResourceLocation(Apotheosis.MODID, "reload_listener_start");

        public static void sendToAll(String path) {
            if (ServerEvents.getCurrentServer() != null) {
                List<ServerPlayer> list = ServerEvents.getCurrentServer().getPlayerList().getPlayers();
                for (ServerPlayer p : list) {
                    sendTo(p, path);
                }
            }
        }

        public static void sendTo(ServerPlayer player, String path) {
            FriendlyByteBuf buf = PacketByteBufs.create();
            buf.writeUtf(path, 50);
            ServerPlayNetworking.send(player, ID, buf);
        }

        public static void init() {
            ClientPlayNetworking.registerGlobalReceiver(ID, ((client, handler, buf, responseSender) -> {
                String msg = buf.readUtf(50);
                ApothJsonReloadListener.initSync(msg);
            }));
        }
    }

    public static class Content {
        public static ResourceLocation ID = new ResourceLocation(Apotheosis.MODID, "reload_listener_content");

        public static <V extends ApothJsonReloadListener.TypeKeyed<V>> void sendToAll(String path, ResourceLocation k, V v) {
            if (ServerEvents.getCurrentServer() != null) {
                List<ServerPlayer> list = ServerEvents.getCurrentServer().getPlayerList().getPlayers();
                for (ServerPlayer p : list) {
                    sendTo(p, path, k, v);
                }
            }
        }

        public static <V extends ApothJsonReloadListener.TypeKeyed<V>> void sendTo(ServerPlayer player, String path, ResourceLocation k, V v) {
            FriendlyByteBuf buf = PacketByteBufs.create();
            buf.writeUtf(path, 50);
            buf.writeResourceLocation(k);
            ApothJsonReloadListener.writeItem(path, v, buf);
            ServerPlayNetworking.send(player, ID, buf);
        }

        public static <V extends ApothJsonReloadListener.TypeKeyed<V>> void init() {
            ClientPlayNetworking.registerGlobalReceiver(ID, ((client, handler, buf, responseSender) -> {
                String path = buf.readUtf(50);
                ResourceLocation key = buf.readResourceLocation();
                V item = ApothJsonReloadListener.readItem(path, key, buf);
                ApothJsonReloadListener.acceptItem(path, key, item);
            }));
        }
    }

    public static class End {
        public static ResourceLocation ID = new ResourceLocation(Apotheosis.MODID, "reload_listener_end");

        public static void sendToAll(String path) {
            if (ServerEvents.getCurrentServer() != null) {
                List<ServerPlayer> list = ServerEvents.getCurrentServer().getPlayerList().getPlayers();
                for (ServerPlayer p : list) {
                    sendTo(p, path);
                }
            }
        }

        public static void sendTo(ServerPlayer player, String path) {
            FriendlyByteBuf buf = PacketByteBufs.create();
            buf.writeUtf(path, 50);
            ServerPlayNetworking.send(player, ID, buf);
        }

        public static void init() {
            ClientPlayNetworking.registerGlobalReceiver(ID, ((client, handler, buf, responseSender) -> {
                String path = buf.readUtf(50);
                ApothJsonReloadListener.endSync(path);
            }));
        }
    }
}
