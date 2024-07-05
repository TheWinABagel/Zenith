package dev.shadowsoffire.apotheosis.adventure.affix.augmenting;

import dev.shadowsoffire.apotheosis.Apotheosis;
import dev.shadowsoffire.apotheosis.adventure.affix.Affix;
import dev.shadowsoffire.apotheosis.adventure.affix.AffixRegistry;
import dev.shadowsoffire.placebo.reload.DynamicHolder;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;

public class RerollResultMessage {

    public static ResourceLocation ID = Apotheosis.loc("reroll_result");

    public static void init() {
        ClientPlayNetworking.registerGlobalReceiver(ID, ((client, handler, buf, responseSender) -> {
            AugmentingScreen.handleRerollResult(AffixRegistry.INSTANCE.holder(buf.readResourceLocation()));
        }));
    }

    public static void sendTo(DynamicHolder<? extends Affix> newAffix, Player p) {
        FriendlyByteBuf buf = PacketByteBufs.create();
        buf.writeResourceLocation(newAffix.getId());
        ServerPlayNetworking.send((ServerPlayer) p, ID, buf);
    }

/*    public static class Provider implements MessageProvider<RerollResultMessage> {

        @Override
        public Class<?> getMsgClass() {
            return RerollResultMessage.class;
        }

        @Override
        public void write(RerollResultMessage msg, FriendlyByteBuf buf) {
            buf.writeResourceLocation(msg.newAffix.getId());
        }

        @Override
        public RerollResultMessage read(FriendlyByteBuf buf) {
            return new RerollResultMessage(AffixRegistry.INSTANCE.holder(buf.readResourceLocation()));
        }

        @Override
        public void handle(RerollResultMessage msg, Supplier<Context> ctx) {
            MessageHelper.handlePacket(() -> {
                AugmentingScreen.handleRerollResult(msg.newAffix());
            }, ctx);
        }

    }*/

}
