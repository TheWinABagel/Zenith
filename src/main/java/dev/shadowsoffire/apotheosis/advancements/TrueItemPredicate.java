package dev.shadowsoffire.apotheosis.advancements;

import net.minecraft.advancements.critereon.ItemPredicate;
import net.minecraft.world.item.ItemStack;

import java.util.function.Predicate;

public class TrueItemPredicate extends ItemPredicate {

    Predicate<ItemStack> predicate;

    public TrueItemPredicate(Predicate<ItemStack> predicate) {
        this.predicate = predicate;
    }

    @Override
    public boolean matches(ItemStack item) {
        return this.predicate.test(item);
    }
}