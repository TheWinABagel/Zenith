package dev.shadowsoffire.apotheosis.ench.table;

import dev.shadowsoffire.apotheosis.Apotheosis;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.client.Minecraft;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentInstance;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;

public class ClueMessage {
    public static ResourceLocation ID = Apotheosis.loc("enchant_clue");
    protected final int slot;
    protected final List<EnchantmentInstance> clues;
    protected final boolean all;

    /**
     * Sends a clue message to the client.
     *
     * @param slot
     * @param clues The clues.
     * @param all   If this is all of the enchantments being received.
     */
    public ClueMessage(int slot, List<EnchantmentInstance> clues, boolean all) {
        this.slot = slot;
        this.clues = clues;
        this.all = all;
    }



    public static void init() {
        ClientPlayNetworking.registerGlobalReceiver(ID, ((client, handler, buf, responseSender) -> {
            int size = buf.readByte();
            List<EnchantmentInstance> clues = new ArrayList<>(size);
            for (int i = 0; i < size; i++) {
                Enchantment ench = BuiltInRegistries.ENCHANTMENT.byIdOrThrow(buf.readShort());
                clues.add(new EnchantmentInstance(ench, buf.readByte()));
            }
            int slot = buf.readByte();
            boolean all = buf.readBoolean();

            if (Minecraft.getInstance().screen instanceof ApothEnchScreen es) {
                es.acceptClues(slot, clues, all);
            }
        }));
    }

    public static void sendTo(int slot, List<EnchantmentInstance> clues, boolean all, Player p) {
        FriendlyByteBuf buf = PacketByteBufs.create();
        buf.writeByte(clues.size());
        for (EnchantmentInstance e : clues) {
            buf.writeShort(BuiltInRegistries.ENCHANTMENT.getId(e.enchantment));
            buf.writeByte(e.level);
        }
        buf.writeByte(slot);
        buf.writeBoolean(all);
        ServerPlayNetworking.send((ServerPlayer) p, ID, buf);
    }





/* //TODO reimplement with fabric packets
    public static class Provider implements MessageProvider<ClueMessage> {

        @Override
        public Class<?> getMsgClass() {
            return ClueMessage.class;
        }

        @Override
        @SuppressWarnings("deprecation")
        public void write(ClueMessage msg, FriendlyByteBuf buf) {
            buf.writeByte(msg.clues.size());
            for (EnchantmentInstance e : msg.clues) {
                buf.writeShort(BuiltInRegistries.ENCHANTMENT.getId(e.enchantment));
                buf.writeByte(e.level);
            }
            buf.writeByte(msg.slot);
            buf.writeBoolean(msg.all);
        }

        @Override
        @SuppressWarnings("deprecation")
        public ClueMessage read(FriendlyByteBuf buf) {
            int size = buf.readByte();
            List<EnchantmentInstance> clues = new ArrayList<>(size);
            for (int i = 0; i < size; i++) {
                Enchantment ench = BuiltInRegistries.ENCHANTMENT.byIdOrThrow(buf.readShort());
                clues.add(new EnchantmentInstance(ench, buf.readByte()));
            }
            return new ClueMessage(buf.readByte(), clues, buf.readBoolean());
        }

        @Override
        public void handle(ClueMessage msg, Supplier<Context> ctx) {
            MessageHelper.handlePacket(() -> {
                if (Minecraft.getInstance().screen instanceof ApothEnchantScreen es) {
                    es.acceptClues(msg.slot, msg.clues, msg.all);
                }
            }, ctx);
        }

        @Override
        public Optional<NetworkDirection> getNetworkDirection() {
            return Optional.of(NetworkDirection.PLAY_TO_CLIENT);
        }
    }
*/
}
