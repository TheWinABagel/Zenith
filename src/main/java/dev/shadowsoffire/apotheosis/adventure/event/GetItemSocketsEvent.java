package dev.shadowsoffire.apotheosis.adventure.event;

import dev.shadowsoffire.apotheosis.adventure.socket.SocketHelper;
import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.world.item.ItemStack;

/**
 * Fired from {@link SocketHelper#getSockets(ItemStack)} to allow modification of the number of sockets an item has.
 */
public class GetItemSocketsEvent {
    public static Event<GetItemSockets> GET_ITEM_SOCKETS = EventFactory.createArrayBacked(GetItemSockets.class, callbacks -> event -> {
        for (GetItemSockets e : callbacks)
            e.getSockets(event);
    });

    protected final ItemStack stack;
    protected int sockets;

    public GetItemSocketsEvent(ItemStack stack, int sockets) {
        this.stack = stack;
        this.sockets = sockets;
    }

    /**
     * @return The item whose socket value is being calculated.
     */
    public ItemStack getStack() {
        return this.stack;
    }

    /**
     * @return The (possibly event-modified) number of sockets this item has.
     */
    public int getSockets() {
        return this.sockets;
    }

    /**
     * Sets the number of sockets the item will have to a given amount.
     *
     * @param sockets The new socket count.
     */
    public void setSockets(int sockets) {
        this.sockets = sockets;
    }

    @FunctionalInterface
    public interface GetItemSockets {
        void getSockets(GetItemSocketsEvent socketingEvent);
    }
}
