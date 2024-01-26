package dev.shadowsoffire.apotheosis.advancements;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;

public interface EnchTrigger {

    public void zenith$trigger(ServerPlayer player, ItemStack stack, int level, float eterna, float quanta, float arcana, float rectification);
}
