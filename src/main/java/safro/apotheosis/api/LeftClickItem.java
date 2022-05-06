package safro.apotheosis.api;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

public interface LeftClickItem {
    boolean onLeftClickEntity(ItemStack stack, Player player, Entity entity);
}
