package dev.shadowsoffire.apotheosis.mixin.compat.spectrum.present;

import de.dafuqs.spectrum.inventories.BedrockAnvilScreenHandler;
import dev.shadowsoffire.apotheosis.Apotheosis;
import dev.shadowsoffire.apotheosis.ench.asm.EnchHooks;
import dev.shadowsoffire.apotheosis.util.Events;
import dev.shadowsoffire.placebo.util.EnchantmentUtils;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.DataSlot;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.ResultContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = BedrockAnvilScreenHandler.class, remap = false)
public abstract class BedrockAnvilMenuMixin extends AbstractContainerMenu {

    protected BedrockAnvilMenuMixin(@Nullable MenuType<?> menuType, int containerId) {
        super(menuType, containerId);
    }
    @Shadow private @Nullable String newItemName;
    @Shadow @Final protected Player player;
    @Shadow @Final private DataSlot levelCost;
    @Shadow @Final protected ResultContainer output;
    @Shadow @Final protected Container input;

    @Shadow private int repairItemCount;
    @Unique private static ItemStack zenith$leftItem = ItemStack.EMPTY;
    @Unique private static ItemStack zenith$rightItem = ItemStack.EMPTY;
    @Unique private static ItemStack zenith$outputStack = ItemStack.EMPTY;


    /**
     * Reduces the XP cost to the "optimal" cost (the amount of XP that would have been used if the player had exactly that level).
     *
     * @param player The player using the anvil.
     * @param level  The negative of the cost of performing the anvil operation.
     */
    @Redirect(method = "onTakeOutput", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/player/Player;giveExperienceLevels(I)V", remap = true))
    public void zenith$chargeOptimalLevelsBedrockAnvil(Player player, int level) {
        EnchantmentUtils.chargeExperience(player, EnchantmentUtils.getTotalExperienceForLevel(-level));
    }

    @Inject(method = "updateResult", at = @At(value = "INVOKE", target = "net/minecraft/world/item/ItemStack.isEmpty ()Z", ordinal = 2, remap = true), cancellable = true)
    private void zenith$initUpdateAnvilEventBedrockAnvil(CallbackInfo ci) {
        if (!Apotheosis.enableEnch) return;
        zenith$leftItem = this.input.getItem(0);
        zenith$rightItem = this.input.getItem(1);
        zenith$outputStack = this.output.getItem(0);
        if (!zenith$setupAnvilChangeEvent(zenith$leftItem, zenith$rightItem, this.output, newItemName, this.levelCost.get(), player)) {
            ci.cancel();
        }
    }

    @Inject(method = "onTakeOutput", at = @At("HEAD"))
    private void zenith$initRepairEventBedrockAnvil(Player player, ItemStack stack, CallbackInfo ci) {
        if (Apotheosis.enableEnch) {
            Events.RepairEvent event = new Events.RepairEvent(this.player, zenith$outputStack, zenith$leftItem, zenith$rightItem);
            Events.AnvilRepair.ANVIL_REPAIR.invoker().onRepair(event);
        }
    }

    @Unique
    private boolean zenith$setupAnvilChangeEvent(ItemStack left, ItemStack right, Container outputSlot, String name, int cost, Player player) {
        Events.AnvilUpdate.UpdateAnvilEvent e = new Events.AnvilUpdate.UpdateAnvilEvent(left, right, name, cost, player);
        Events.AnvilUpdate.UPDATE_ANVIL.invoker().onUpdate(e);
        if (e.output.isEmpty()) return true;
        outputSlot.setItem(0, e.output);
        this.levelCost.set(e.cost);
        this.repairItemCount = e.materialCost;
        return false;
    }

    @Redirect(method = "updateResult", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/enchantment/Enchantment;getMaxLevel()I", remap = true))
    private int zenith$ModifyMaxLevelBedrockAnvil(Enchantment enchantment) {
        if (!Apotheosis.enableEnch) return enchantment.getMaxLevel();
        return EnchHooks.getMaxLevel(enchantment);
    }
}
