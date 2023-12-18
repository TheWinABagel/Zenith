package dev.shadowsoffire.apotheosis.ench.table;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;

import dev.shadowsoffire.apotheosis.Apotheosis;
import dev.shadowsoffire.apotheosis.ench.EnchModule;
import dev.shadowsoffire.apotheosis.ench.table.ApothEnchantmentMenu.TableStats;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.client.Minecraft;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentInstance;

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
