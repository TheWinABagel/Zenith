package dev.shadowsoffire.apotheosis.util;

import dev.shadowsoffire.apotheosis.Apotheosis;
import dev.shadowsoffire.apotheosis.village.util.WandererTradeEvent;
import io.github.fabricators_of_create.porting_lib.core.event.object.CancellableEvent;
import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
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

    public class onEntityDeath{

        public static final Event<onLivingDeath> LIVING_DEATH = EventFactory.createArrayBacked(onLivingDeath.class, callbacks -> (entity, source) -> {
            for (onLivingDeath callback : callbacks) {
                return !callback.onDeath(entity, source);
            }
            return false;
        });

        @FunctionalInterface
        public interface onLivingDeath {
            boolean onDeath(LivingEntity entity, DamageSource source);
        }
    }

    public class HarvestCheck {

        public static final Event<onHarvestAttempt> ATTEMPT_HARVEST = EventFactory.createArrayBacked(onHarvestAttempt.class, callbacks -> (player, state) -> {
            for (onHarvestAttempt callback : callbacks) {
                return !callback.harvestAttempt(player, state);
            }
            return false;
        });

        @FunctionalInterface
        public interface onHarvestAttempt {
            boolean harvestAttempt(Player player, BlockState state);
        }
    }
}
