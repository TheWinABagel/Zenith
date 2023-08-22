package dev.shadowsoffire.apotheosis.util;

import dev.shadowsoffire.apotheosis.Apotheosis;
import dev.shadowsoffire.apotheosis.village.util.WandererTradeEvent;
import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

import static dev.shadowsoffire.apotheosis.ench.EnchModuleEvents.breakChance;

public class Events {
    public static void init(){
        ServerLifecycleEvents.SERVER_STARTING.register(listener -> {
            if (Apotheosis.enableVillage) {
                WandererTradeEvent.postWandererEvent();
            }
        });
    }

    public interface AnvilRepairEvent {

        Event<AnvilRepairEvent> EVENT = EventFactory.createArrayBacked(AnvilRepairEvent.class, //TODO logic+math, my worst enemy
                (listeners) -> (player, output, left, right) -> {
                    for (AnvilRepairEvent listener : listeners) {
                        float result = listener.onAnvilRepair(player, output, left, right);
                        if (result == .12f){
                            return breakChance;
                        }
                        else {
                            breakChance = result;
                            return result;
                        }
                    }
                    return breakChance;
                });

        float onAnvilRepair(Player player, @NotNull ItemStack output, @NotNull ItemStack left, @NotNull ItemStack right);
    }

    public interface AnvilUpdateEvent {
        Event<AnvilUpdateEvent> EVENT = EventFactory.createArrayBacked(AnvilUpdateEvent.class, //TODO logic+math, my worst enemy
                (listeners) -> (left, right, name, baseCost, player) -> {
                    for (AnvilUpdateEvent listener : listeners) {
                        boolean result = listener.onAnvilChange(left, right, name, baseCost, player);
                        return result;
                    }
                    return false;
                });

        boolean onAnvilChange(ItemStack left, ItemStack right, String name, int baseCost, Player player);
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
