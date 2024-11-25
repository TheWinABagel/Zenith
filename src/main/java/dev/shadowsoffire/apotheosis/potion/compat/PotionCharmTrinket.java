package dev.shadowsoffire.apotheosis.potion.compat;

import dev.emi.trinkets.api.SlotReference;
import dev.emi.trinkets.api.Trinket;
import dev.shadowsoffire.apotheosis.potion.PotionCharmItem;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;

public class PotionCharmTrinket implements Trinket {
    @Override
    public void tick(ItemStack stack, SlotReference slot, LivingEntity entity) {
        if (stack.getItem() instanceof PotionCharmItem charm) {
            charm.charmLogic(stack, entity.level(), entity, slot.index(), false);
        }
    }
}
