package safro.zenith.mixin.anvil;

import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AnvilMenu;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.inventory.ItemCombinerMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.enchantment.Enchantment;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import safro.zenith.Zenith;
import safro.zenith.ench.asm.EnchHooks;

@Mixin(value = AnvilMenu.class, priority = 1001)
public abstract class AnvilMenuMixinHigherPriority extends ItemCombinerMenu {



    public AnvilMenuMixinHigherPriority(int i, Inventory inventory, ContainerLevelAccess containerLevelAccess) {
        super(MenuType.ANVIL, i, inventory, containerLevelAccess);
    }

    @Redirect(method = "createResult", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/enchantment/Enchantment;getMaxLevel()I"))
    private int zenithModifyMaxLevel(Enchantment enchantment) {
        if (!Zenith.enableEnch)
            return enchantment.getMaxLevel();
        return EnchHooks.getMaxLevel(enchantment);
    }
}
