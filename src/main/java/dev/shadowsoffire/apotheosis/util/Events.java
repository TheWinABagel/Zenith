package dev.shadowsoffire.apotheosis.util;

import dev.shadowsoffire.apotheosis.Apotheosis;
import dev.shadowsoffire.apotheosis.village.util.WandererTradeEvent;
import io.github.fabricators_of_create.porting_lib.event.client.ModelLoadCallback;
import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.minecraft.client.color.block.BlockColors;
import net.minecraft.client.renderer.block.model.BlockModel;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.ModelBakery;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Map;
import java.util.Set;

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
            if (callback.onRepair(event)) return true;
        }
        return false;
    });

    @FunctionalInterface
    public interface AnvilRepair {
        boolean onRepair(RepairEvent event);
    }

    public static class RepairEvent  {
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
            for (onLivingDeath callback : callbacks)
                callback.onDeath(entity, source);
        });

        @FunctionalInterface
        public interface onLivingDeath {
            void onDeath(LivingEntity entity, DamageSource source);
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
