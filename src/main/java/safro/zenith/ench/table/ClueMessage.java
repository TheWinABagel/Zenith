package safro.zenith.ench.table;

import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.inventory.EnchantmentScreen;
import net.minecraft.core.Registry;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentInstance;
import safro.zenith.Zenith;

import java.util.ArrayList;
import java.util.List;

public class ClueMessage {
    public static ResourceLocation ID = new ResourceLocation(Zenith.MODID, "enchant_clue");

    public static void init() {
        ClientPlayNetworking.registerGlobalReceiver(ID, ((client, handler, buf, responseSender) -> {
            int size = buf.readByte();
            List<EnchantmentInstance> clues = new ArrayList<>(size);
            for (int i = 0; i < size; i++) {
                Enchantment ench = Registry.ENCHANTMENT.byIdOrThrow(buf.readShort());
                clues.add(new EnchantmentInstance(ench, buf.readByte()));
            }
            int slot = buf.readByte();
            boolean all = buf.readBoolean();

            if (Minecraft.getInstance().screen instanceof ApothEnchantScreen es) {
                es.acceptClues(slot, clues, all);
            }
        }));
    }

    public static void sendTo(Player p, int slot, List<EnchantmentInstance> clues, boolean all) {
        FriendlyByteBuf buf = PacketByteBufs.create();
        buf.writeByte(clues.size());
        for (EnchantmentInstance e : clues) {
            buf.writeShort(Registry.ENCHANTMENT.getId(e.enchantment));
            buf.writeByte(e.level);
        }
        buf.writeByte(slot);
        buf.writeBoolean(all);
        ServerPlayNetworking.send((ServerPlayer) p, ID, buf);
    }


}
