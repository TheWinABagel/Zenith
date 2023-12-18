package dev.shadowsoffire.apotheosis.mixin.compat.dragonloot.present;

import com.bawnorton.mixinsquared.TargetHandler;
import dev.shadowsoffire.apotheosis.Apotheosis;
import dev.shadowsoffire.apotheosis.util.Events;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AnvilMenu;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.inventory.ItemCombinerMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Pseudo
@Mixin(value = AnvilMenu.class, priority = 1500)
public abstract class ZenithDragonLootCompatAnvilMenuMixin extends ItemCombinerMenu {

    @Unique private static Player p = null;
    @Unique private static ItemStack DLLeftItem = ItemStack.EMPTY;
    @Unique private static ItemStack DLRightItem = ItemStack.EMPTY;
    @Unique private static ItemStack DLOutput = ItemStack.EMPTY;
    @Unique private static Events.RepairEvent DLRepairEvent;

    public ZenithDragonLootCompatAnvilMenuMixin(@Nullable MenuType<?> type, int containerId, Inventory playerInventory, ContainerLevelAccess access) {
        super(type, containerId, playerInventory, access);
    }

    @Inject(method = "createResult", at = @At(value = "INVOKE", target = "net/minecraft/world/item/ItemStack.isEmpty ()Z", ordinal = 1))
    private void zenith$CollectItemsDragonLoot(CallbackInfo ci) {
        DLLeftItem = this.inputSlots.getItem(0);
        DLRightItem = this.inputSlots.getItem(1);
        DLOutput = this.resultSlots.getItem(0);
    }

    @Inject(method = "onTake", at = @At("HEAD"))
    private void zenith$IntegrateRepairEventDragonLoot(Player player, ItemStack stack, CallbackInfo ci) {
        p = player;
        if (!DLLeftItem.isEmpty() && !DLRightItem.isEmpty()) {
            DLRepairEvent = new Events.RepairEvent(p, DLOutput, DLLeftItem, DLRightItem);
        }
    }

    @SuppressWarnings({"InvalidMemberReference", "MixinAnnotationTarget", "UnresolvedMixinReference"})
    @TargetHandler(
            mixin = "net.dragonloot.mixin.AnvilScreenHandlerMixin",
            name = "lambda$onTakeOutputMixin$0"
    )
    @Inject(method = "@MixinSquared:Handler", at = @At(value = "INVOKE", target = "net/minecraft/world/level/Level.levelEvent (ILnet/minecraft/core/BlockPos;I)V", shift = At.Shift.AFTER), cancellable = true)
    private void zenith$initRepairEventDragonLoot(CallbackInfo originalCi, Level level, BlockPos blockPos, CallbackInfo ci) {
        if (Apotheosis.enableEnch && DLRepairEvent != null && DLRepairEvent.player != null) {
            Events.AnvilRepair.ANVIL_REPAIR.invoker().onRepair(DLRepairEvent);
        }
    }

}
