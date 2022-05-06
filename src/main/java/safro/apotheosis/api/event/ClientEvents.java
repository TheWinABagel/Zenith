package safro.apotheosis.api.event;

import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;

public class ClientEvents {
    private static long ticks = 0;

    public static void init() {
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            ticks++;
        });
    }

    public static long getTicks() {
        return ticks;
    }
}
