package dev.shadowsoffire.apotheosis.mixin.ench.anvil;

import com.llamalad7.mixinextras.sugar.Share;
import com.llamalad7.mixinextras.sugar.ref.LocalRef;
import dev.shadowsoffire.apotheosis.Apotheosis;
import dev.shadowsoffire.apotheosis.ench.EnchModule;
import dev.shadowsoffire.apotheosis.ench.asm.EnchHooks;
import dev.shadowsoffire.apotheosis.util.Events;
import io.github.fabricators_of_create.porting_lib.entity.events.living.LivingEntityDamageEvents;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.*;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@Mixin(AnvilMenu.class)
public abstract class AnvilMenuMixin extends ItemCombinerMenu {


    @Shadow private @Nullable String itemName;
    @Unique private static @Nullable String eventItemName;
    @Unique private static Player p = null;
    @Unique private static ItemStack left = ItemStack.EMPTY;
    @Unique private static ItemStack right = ItemStack.EMPTY;
    @Unique private static ItemStack output;
    @Unique private static Events.RepairEvent event;

    public AnvilMenuMixin(@Nullable MenuType<?> type, int containerId, Inventory playerInventory, ContainerLevelAccess access) {
        super(type, containerId, playerInventory, access);
    }

    @ModifyConstant(method = "createResult()V", constant = @Constant(intValue = 40))
    public int apoth_removeLevelCap(int old) {
        return Integer.MAX_VALUE;
    }

    @Inject(method = "createResult", at = @At(value = "INVOKE", target = "net/minecraft/world/item/ItemStack.isEmpty ()Z", ordinal = 1, shift = At.Shift.AFTER), cancellable = true, locals = LocalCapture.CAPTURE_FAILHARD)
    private void initUpdateAnvilEvent(CallbackInfo ci, ItemStack left, int i, int j, int k, ItemStack left2, ItemStack right) {
        p = player;
        AnvilMenuMixin.left = left;
        AnvilMenuMixin.right = right;
        output = this.resultSlots.getItem(0);
        if (Apotheosis.enableEnch){
            if (!onAnvilChange(left, right, this.resultSlots, itemName, ths().cost.get(), p)) ci.cancel();
        }
    }

    @Inject(method = "onTake", at = @At("HEAD"))
    private void initRepairEvent(Player player, ItemStack stack, CallbackInfo ci){
        event = new Events.RepairEvent(p, output, left, right);
    }

    @ModifyConstant(method = "method_24922", constant = @Constant(floatValue = 0.12F))
    private static float zenithBreakChance(float chance) {
        if (Apotheosis.enableEnch && p != null) {
            EnchModule.LOGGER.info("anvil repair");
            Events.ANVIL_REPAIR.invoker().onRepair(event);
            return event.breakChance;
        }
        return chance;
    }

    @Unique
    private boolean onAnvilChange(ItemStack left, ItemStack right, Container outputSlot, String name, int baseCost, Player player) {
        Events.AnvilUpdate.UpdateAnvilEvent e = new Events.AnvilUpdate.UpdateAnvilEvent(left, right, name, baseCost, player);
        Events.AnvilUpdate.UPDATE_ANVIL.invoker().onUpdate(e);
        if (e.output.isEmpty()) return true;
        outputSlot.setItem(0, e.output);
        ths().cost.set(e.cost);
        ths().repairItemCountCost = e.materialCost;
        return false;
    }

    @Redirect(method = "createResult", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/enchantment/Enchantment;getMaxLevel()I"))
    private int zenithModifyMaxLevel(Enchantment enchantment) {
        if (!Apotheosis.enableEnch) return enchantment.getMaxLevel();
        return EnchHooks.getMaxLevel(enchantment);
    }

    @Unique
    private AnvilMenu ths(){
        return (AnvilMenu) (Object) this;
    }
}
