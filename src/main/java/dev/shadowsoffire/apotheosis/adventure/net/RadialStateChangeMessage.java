package dev.shadowsoffire.apotheosis.adventure.net;

import dev.shadowsoffire.apotheosis.Apotheosis;
import dev.shadowsoffire.apotheosis.adventure.affix.effect.RadialAffix;
import net.fabricmc.fabric.api.networking.v1.FabricPacket;
import net.fabricmc.fabric.api.networking.v1.PacketType;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.FriendlyByteBuf;

public record RadialStateChangeMessage() implements FabricPacket {
    public static final PacketType<RadialStateChangeMessage> TYPE = PacketType.create(Apotheosis.loc("radial_state_change"), RadialStateChangeMessage::new);
    public RadialStateChangeMessage(FriendlyByteBuf buf) {
        this();
    }

    public static void init() {
        ServerPlayNetworking.registerGlobalReceiver(TYPE, ((packet, player, responseSender) -> {
            if (player == null) return;
            RadialAffix.toggleRadialState(player);
        }));
    }

    @Override
    public void write(FriendlyByteBuf buf) {}

    @Override
    public PacketType<?> getType() {
        return TYPE;
    }

}
