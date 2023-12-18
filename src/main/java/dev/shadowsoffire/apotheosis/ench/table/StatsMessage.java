package dev.shadowsoffire.apotheosis.ench.table;

import dev.shadowsoffire.apotheosis.Apotheosis;
import dev.shadowsoffire.apotheosis.ench.table.ApothEnchantmentMenu.TableStats;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;

public class StatsMessage {
    public static ResourceLocation ID = Apotheosis.loc("stats_message");

    public static void init() {
        ClientPlayNetworking.registerGlobalReceiver(ID, ((client, handler, buf, responseSender) -> {
            if (Minecraft.getInstance().screen instanceof ApothEnchScreen es) {
                es.menu.stats = TableStats.read(buf);
            }
        }));
    }

    public static void sendTo(TableStats stats, Player p) {
        FriendlyByteBuf buf = PacketByteBufs.create();
        stats.write(buf);
        ServerPlayNetworking.send((ServerPlayer) p, ID, buf);
    }
}
