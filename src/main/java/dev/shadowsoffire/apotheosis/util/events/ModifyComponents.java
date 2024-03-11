package dev.shadowsoffire.apotheosis.util.events;

import com.mojang.datafixers.util.Either;
import io.github.fabricators_of_create.porting_lib.core.event.BaseEvent;
import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.world.inventory.tooltip.TooltipComponent;
import net.minecraft.world.item.ItemStack;

import java.util.List;

public class ModifyComponents {

    public static final Event<ModifyComponentsCallback> MODIFY_COMPONENTS = EventFactory.createArrayBacked(ModifyComponentsCallback.class, callbacks -> event -> {
        for (ModifyComponentsCallback callback : callbacks) {
            callback.modifyComponents(event);
            if (event.isCanceled()) return;
        }
    });

    @FunctionalInterface
    public interface ModifyComponentsCallback {
        void modifyComponents(ModifyComponentsEvent event);
    }

    public static class ModifyComponentsEvent extends BaseEvent {
        public final ItemStack stack;
        public final int screenWidth;
        public final int screenHeight;
        public final List<Either<FormattedText, TooltipComponent>> tooltipElements;
        public int maxWidth;

        public ModifyComponentsEvent(ItemStack stack, int screenWidth, int screenHeight, List<Either<FormattedText, TooltipComponent>> tooltipElements, int maxWidth) {
            this.stack = stack;
            this.screenWidth = screenWidth;
            this.screenHeight = screenHeight;
            this.tooltipElements = tooltipElements;
            this.maxWidth = maxWidth;
        }

        @Override
        public void sendEvent() {
            MODIFY_COMPONENTS.invoker().modifyComponents(this);
        }
    }
}