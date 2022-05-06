package safro.apotheosis.network;

import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import safro.apotheosis.Apotheosis;

public class ButtonClickPacket {
    public static ResourceLocation ID = new ResourceLocation(Apotheosis.MODID, "button_click");

    public static void init() {
        ServerPlayNetworking.registerGlobalReceiver(ID, (server, player, handler, buf, responseSender) -> {
            int button = buf.readInt();
            if (player.containerMenu instanceof IButtonContainer) {
                ((IButtonContainer) player.containerMenu).onButtonClick(button);
            }
        });
    }

    public static void sendToServer(int ench) {
        FriendlyByteBuf buf = PacketByteBufs.create();
        buf.writeInt(ench);
        ClientPlayNetworking.send(ID, buf);
    }

    public interface IButtonContainer {
        void onButtonClick(int id);
    }
}
