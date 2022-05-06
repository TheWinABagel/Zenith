package safro.apotheosis.api.event;

import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.server.MinecraftServer;
import safro.apotheosis.Apotheosis;
import safro.apotheosis.util.TradeManager;

import javax.annotation.Nullable;

public class ServerEvents {
    private static MinecraftServer current;

    public static void init() {
        ServerLifecycleEvents.SERVER_STARTED.register(listener -> {
            current = listener;

            TaskQueue.started();
        });

        ServerLifecycleEvents.SERVER_STOPPED.register(listener -> {
            current = null;

            TaskQueue.stopped();
        });

        ServerTickEvents.END_SERVER_TICK.register(listener -> {
            TaskQueue.tick();
        });

        ServerLifecycleEvents.SERVER_STARTING.register(listener -> {
            if (Apotheosis.enableVillage) {
                TradeManager.postWanderer();
            }
        });
    }

    @Nullable
    public static MinecraftServer getCurrentServer() {
        return current;
    }
}
