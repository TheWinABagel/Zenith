package dev.shadowsoffire.apotheosis.mixin.adventure;

import dev.shadowsoffire.apotheosis.Apotheosis;
import dev.shadowsoffire.apotheosis.adventure.affix.socket.ReactiveSmithingRecipe;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.inventory.ItemCombinerMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.SmithingMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.SmithingRecipe;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(SmithingMenu.class)
public abstract class SmithingMenuMixin extends ItemCombinerMenu {

    public SmithingMenuMixin(MenuType<?> menuType, int i, Inventory inventory, ContainerLevelAccess containerLevelAccess) {
        super(menuType, i, inventory, containerLevelAccess);
    }

    @Shadow
    @Nullable
    private SmithingRecipe selectedRecipe;

    @Inject(at = @At("HEAD"), method = "onTake")
    protected void onTake(Player player, ItemStack stack, CallbackInfo ci) {
        if (Apotheosis.enableAdventure)
            if (this.selectedRecipe instanceof ReactiveSmithingRecipe ext) ext.onCraft(this.inputSlots, player, stack);
    }
}
