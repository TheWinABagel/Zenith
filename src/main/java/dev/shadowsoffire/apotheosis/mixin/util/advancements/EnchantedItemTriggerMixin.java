package dev.shadowsoffire.apotheosis.mixin.util.advancements;

import com.google.gson.JsonObject;
import dev.shadowsoffire.apotheosis.advancements.EnchAdvancementInstance;
import dev.shadowsoffire.apotheosis.advancements.EnchTrigger;
import net.minecraft.advancements.critereon.*;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(EnchantedItemTrigger.class)
public abstract class EnchantedItemTriggerMixin extends SimpleCriterionTrigger<EnchantedItemTrigger.TriggerInstance> implements EnchTrigger {

    @Inject(method = "createInstance(Lcom/google/gson/JsonObject;Lnet/minecraft/advancements/critereon/ContextAwarePredicate;Lnet/minecraft/advancements/critereon/DeserializationContext;)Lnet/minecraft/advancements/critereon/EnchantedItemTrigger$TriggerInstance;", at = @At("RETURN"), cancellable = true)
    private void zenith$createCustomEnchTriggerInstance(JsonObject json, ContextAwarePredicate predicate, DeserializationContext deserializationContext, CallbackInfoReturnable<EnchantedItemTrigger.TriggerInstance> cir) {
        ItemPredicate item = ItemPredicate.fromJson(json.get("item"));
        MinMaxBounds.Ints levels = MinMaxBounds.Ints.fromJson(json.get("levels"));
        MinMaxBounds.Doubles eterna = MinMaxBounds.Doubles.fromJson(json.get("eterna"));
        MinMaxBounds.Doubles quanta = MinMaxBounds.Doubles.fromJson(json.get("quanta"));
        MinMaxBounds.Doubles arcana = MinMaxBounds.Doubles.fromJson(json.get("arcana"));
        MinMaxBounds.Doubles rectification = MinMaxBounds.Doubles.fromJson(json.get("rectification"));
        cir.setReturnValue(new EnchAdvancementInstance(item, levels, eterna, quanta, arcana, rectification));
    }

    @Override
    public void zenith$trigger(ServerPlayer player, ItemStack stack, int level, float eterna, float quanta, float arcana, float rectification) {
        this.trigger(player, inst -> {
            if (inst instanceof EnchAdvancementInstance) return ((EnchAdvancementInstance) inst).test(stack, level, eterna, quanta, arcana, rectification);
            return inst.matches(stack, level);
        });
    }
}
