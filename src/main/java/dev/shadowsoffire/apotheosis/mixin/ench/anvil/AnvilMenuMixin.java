package dev.shadowsoffire.apotheosis.mixin.ench.anvil;

import dev.shadowsoffire.apotheosis.Apotheosis;
import dev.shadowsoffire.apotheosis.ench.asm.EnchHooks;
import dev.shadowsoffire.apotheosis.util.Events;
import dev.shadowsoffire.placebo.util.EnchantmentUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.*;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(AnvilMenu.class)
public abstract class AnvilMenuMixin extends ItemCombinerMenu {

    @Shadow private @Nullable String itemName;
    @Shadow @Final private DataSlot cost;
    @Shadow private int repairItemCountCost;
    @Unique private static ItemStack zenith$leftItem = ItemStack.EMPTY;
    @Unique private static ItemStack zenith$rightItem = ItemStack.EMPTY;
    @Unique private static ItemStack zenith$output;
    @Unique private static Events.RepairEvent zenith$event;

    public AnvilMenuMixin(@Nullable MenuType<?> type, int containerId, Inventory playerInventory, ContainerLevelAccess access) {
        super(type, containerId, playerInventory, access);
    }

    @ModifyConstant(method = "createResult()V", constant = @Constant(intValue = 40))
    public int zenith$removeLevelCap(int old) {
        if (!Apotheosis.enableEnch) return old;
        return Integer.MAX_VALUE;
    }

    /**
     * Reduces the XP cost to the "optimal" cost (the amount of XP that would have been used if the player had exactly that level).
     *
     * @param player The player using the anvil.
     * @param level  The negative of the cost of performing the anvil operation.
     */
    @Redirect(method = "onTake", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/player/Player;giveExperienceLevels(I)V"))
    public void zenith$chargeOptimalLevels(Player player, int level) {
        EnchantmentUtils.chargeExperience(player, EnchantmentUtils.getTotalExperienceForLevel(-level));
    }

    @Inject(method = "createResult", at = @At(value = "INVOKE", target = "net/minecraft/world/item/ItemStack.isEmpty ()Z", ordinal = 1, shift = At.Shift.AFTER), cancellable = true)
    private void initUpdateAnvilEvent(CallbackInfo ci) {
        zenith$leftItem = this.inputSlots.getItem(0);
        zenith$rightItem = this.inputSlots.getItem(1);
        zenith$output = this.resultSlots.getItem(0);
        if (Apotheosis.enableEnch && onAnvilChange(zenith$leftItem, zenith$rightItem, this.resultSlots, itemName, this.cost.get(), player)) {
            ci.cancel();
        }
    }

    @Inject(method = "onTake", at = @At("HEAD"))
    private void initRepairEvent(Player player, ItemStack stack, CallbackInfo ci) {
        zenith$event = new Events.RepairEvent(player, zenith$output, zenith$leftItem, zenith$rightItem);
    }

    @ModifyConstant(method = "method_24922", constant = @Constant(floatValue = 0.12F))
    private static float zenith$InitAnvilUse(float chance) {
        if (Apotheosis.enableEnch && zenith$event.player != null) {
            Events.AnvilRepair.ANVIL_REPAIR.invoker().onRepair(zenith$event);
            return zenith$event.breakChance;
        }
        return chance;
    }

    @Inject(method = "method_24922", at = @At("HEAD"))
    private static void zenith$InitAnvilUseCreative(Player player, Level level, BlockPos blockPos, CallbackInfo ci) {
        if (Apotheosis.enableEnch && zenith$event.player != null && zenith$event.player.getAbilities().instabuild) {
            Events.AnvilRepair.ANVIL_REPAIR.invoker().onRepair(zenith$event);
        }
    }

    @Unique
    private boolean onAnvilChange(ItemStack left, ItemStack right, Container outputSlot, String name, int cost, Player player) {
        Events.AnvilUpdate.UpdateAnvilEvent e = new Events.AnvilUpdate.UpdateAnvilEvent(left, right, name, cost, player);
        Events.AnvilUpdate.UPDATE_ANVIL.invoker().onUpdate(e);
        if (e.output.isEmpty()) return false;
        outputSlot.setItem(0, e.output);
        this.cost.set(e.cost);
        this.repairItemCountCost = e.materialCost;
        return true;
    }

    @Redirect(method = "createResult", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/enchantment/Enchantment;getMaxLevel()I"))
    private int zenithModifyMaxLevel(Enchantment enchantment) {
        if (!Apotheosis.enableEnch) return enchantment.getMaxLevel();
        return EnchHooks.getMaxLevel(enchantment);
    }

}
