package dev.shadowsoffire.apotheosis.util;

import dev.shadowsoffire.apotheosis.Apotheosis;
import dev.shadowsoffire.apotheosis.village.util.WandererTradeEvent;
import io.github.fabricators_of_create.porting_lib.core.event.object.CancellableEvent;
import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

public class Events {
    public static void init(){
        ServerLifecycleEvents.SERVER_STARTING.register(listener -> {
            if (Apotheosis.enableVillage) {
                WandererTradeEvent.postWandererEvent();
            }
        });
    }
    public static final Event<AnvilRepair> ANVIL_REPAIR = EventFactory.createArrayBacked(AnvilRepair.class, callbacks -> event -> {
        for (AnvilRepair callback : callbacks) {
            callback.onRepair(event);
            if (event.isCancelled())
                return;
        }
    });

    @FunctionalInterface
    public interface AnvilRepair {
        void onRepair(RepairEvent event);
    }

    public static class RepairEvent extends CancellableEvent {
        public final Player player;
        public final ItemStack output;
        public final ItemStack left;
        public final ItemStack right;
        public float breakChance;

        public RepairEvent(Player player, @NotNull ItemStack output, @NotNull ItemStack left, @NotNull ItemStack right) {
            this.player = player;
            this.output = output;
            this.left = left;
            this.right = right;
            this.setBreakChance(0.12f);
        }

        public void setBreakChance(float chance){
            this.breakChance = chance;
        }
    }
    public class AnvilUpdate{

        public static final Event<UpdateAnvil> UPDATE_ANVIL = EventFactory.createArrayBacked(UpdateAnvil.class, callbacks -> event -> {
            for (UpdateAnvil callback : callbacks) {
                callback.onUpdate(event);
                if (event.isCancelled())
                    return;
            }
        });

        @FunctionalInterface
        public interface UpdateAnvil {
            void onUpdate(UpdateAnvilEvent event);
        }

        public static class UpdateAnvilEvent extends CancellableEvent {
            public final ItemStack left;
            public final ItemStack right;
            public final String name;
            public ItemStack output;
            public int cost;
            public int materialCost;
            public final Player player;

            public UpdateAnvilEvent(ItemStack left, ItemStack right, String name, int cost, Player player) {
                this.player = player;
                this.output = ItemStack.EMPTY;
                this.left = left;
                this.right = right;
                this.name = name;
                this.setCost(cost);
                this.setMaterialCost(0);
            }

            public void setCost(int cost){
                this.cost = cost;
            }

            public void setMaterialCost(int materialCost){
                this.materialCost = materialCost;
            }

            public void setOutput(ItemStack output) {
                this.output = output;
            }
        }
    }
    public interface LivingHealEvent {
        Event<LivingHealEvent> EVENT = EventFactory.createArrayBacked(LivingHealEvent.class, //TODO logic+math, my worst enemy
                (listeners) -> (entity, amount) -> {
                    for (LivingHealEvent listener : listeners) {
                        float result = listener.onLivingHeal(entity, amount);
                        return result;
                    }
                    return amount;
                });

        float onLivingHeal(Entity entity, float amount);
    }
}
