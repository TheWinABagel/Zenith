package dev.shadowsoffire.apotheosis.util;

import dev.shadowsoffire.apotheosis.Apotheosis;
import dev.shadowsoffire.apotheosis.village.util.WandererTradeEvent;
import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;

public class Events {
    public static void init(){
        ServerLifecycleEvents.SERVER_STARTING.register(listener -> {
            if (Apotheosis.enableVillage) {
                WandererTradeEvent.postWandererEvent();
            }
        });
    }

    @FunctionalInterface
    public interface AnvilRepair {
        Event<AnvilRepair> ANVIL_REPAIR = EventFactory.createArrayBacked(AnvilRepair.class, callbacks -> event -> {
            for (AnvilRepair callback : callbacks) {
                callback.onRepair(event);
            }
        });

        void onRepair(RepairEvent event);
    }

    public static class RepairEvent  {
        public final Player player;
        public final ItemStack output;
        public final ItemStack left;
        public final ItemStack right;
        public float breakChance;

        public RepairEvent(Player player, ItemStack output, ItemStack left, ItemStack right) {
            this.player = player;
            this.output = output;
            this.left = left;
            this.right = right;
            this.breakChance = 0.12f;
        }
    }

    public static class AnvilUpdate {
        public static final Event<UpdateAnvil> UPDATE_ANVIL = EventFactory.createArrayBacked(UpdateAnvil.class, callbacks -> event -> {
            for (UpdateAnvil callback : callbacks) {
                if (callback.onUpdate(event)) return true;
            }
            return false;
        });

        @FunctionalInterface
        public interface UpdateAnvil {
            boolean onUpdate(UpdateAnvilEvent event);
        }

        public static class UpdateAnvilEvent {
            public final ItemStack left;
            public final ItemStack right;
            public final String name;
            public final Player player;
            public ItemStack output;
            public int cost;
            public int materialCost;

            public UpdateAnvilEvent(ItemStack left, ItemStack right, String name, int cost, Player player) {
                this.player = player;
                this.output = ItemStack.EMPTY;
                this.left = left;
                this.right = right;
                this.name = name;
                this.cost = cost;
                this.materialCost = 0;
            }
        }
    }

    public interface OnEntityDeath {
        Event<OnEntityDeath> LIVING_DEATH = EventFactory.createArrayBacked(OnEntityDeath.class, callbacks -> (entity, source) -> {
            for (OnEntityDeath callback : callbacks)
                callback.onDeath(entity, source);
        });
        void onDeath(LivingEntity entity, DamageSource source);
    }

}
