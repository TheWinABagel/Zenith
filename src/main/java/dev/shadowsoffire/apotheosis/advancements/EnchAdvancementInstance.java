package dev.shadowsoffire.apotheosis.advancements;

import com.google.gson.JsonObject;
import net.minecraft.advancements.critereon.*;
import net.minecraft.world.item.ItemStack;

public class EnchAdvancementInstance extends EnchantedItemTrigger.TriggerInstance {

    protected final MinMaxBounds.Doubles eterna, quanta, arcana, rectification;

    public EnchAdvancementInstance(ItemPredicate item, MinMaxBounds.Ints levels, MinMaxBounds.Doubles eterna, MinMaxBounds.Doubles quanta, MinMaxBounds.Doubles arcana, MinMaxBounds.Doubles rectification) {
        super(ContextAwarePredicate.ANY, item, levels);
        this.eterna = eterna;
        this.quanta = quanta;
        this.arcana = arcana;
        this.rectification = rectification;
    }

    public static EnchantedItemTrigger.TriggerInstance any() {
        return new EnchantedItemTrigger.TriggerInstance(ContextAwarePredicate.ANY, ItemPredicate.ANY, MinMaxBounds.Ints.ANY);
    }

    public boolean test(ItemStack stack, int level, float eterna, float quanta, float arcana, float rectification) {
        return super.matches(stack, level) && this.eterna.matches(eterna) && this.quanta.matches(quanta) && this.arcana.matches(arcana) && this.rectification.matches(rectification);
    }

    @Override
    public boolean matches(ItemStack stack, int level) {
        return this.test(stack, level, 0, 0, 0, 0);
    }

    @Override
    public JsonObject serializeToJson(SerializationContext serializer) {
        JsonObject jsonobject = super.serializeToJson(serializer);
        jsonobject.add("eterna", this.eterna.serializeToJson());
        jsonobject.add("quanta", this.quanta.serializeToJson());
        jsonobject.add("arcana", this.arcana.serializeToJson());
        jsonobject.add("rectification", this.rectification.serializeToJson());
        return jsonobject;
    }
}
