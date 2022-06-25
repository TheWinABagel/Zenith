package safro.apotheosis.mixin.anvil;

import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AnvilMenu;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.inventory.ItemCombinerMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;
import safro.apotheosis.Apotheosis;
import safro.apotheosis.ench.EnchModuleEvents;
import safro.apotheosis.ench.asm.EnchHooks;
import safro.apotheosis.util.ApotheosisUtil;

@Mixin(value = AnvilMenu.class, priority = 999)
public abstract class AnvilMenuMixin extends ItemCombinerMenu {
    private static Player p = null;

    @Shadow private String itemName;

    public AnvilMenuMixin(int i, Inventory inventory, ContainerLevelAccess containerLevelAccess) {
        super(MenuType.ANVIL, i, inventory, containerLevelAccess);
    }

    @ModifyConstant(method = "createResult", constant = @Constant(intValue = 40, ordinal = 2))
    public int apoth_removeLevelCap(int old) {
        return Integer.MAX_VALUE;
    }

    @Inject(method = "createResult", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/ItemStack;isEmpty()Z", ordinal = 1, shift = At.Shift.AFTER), cancellable = true, locals = LocalCapture.CAPTURE_FAILHARD)
    private void apothAnvilChange(CallbackInfo ci, ItemStack stack, int i, int j, int k, ItemStack itemStack1, ItemStack itemStack2) {
        AnvilMenu menu = (AnvilMenu) (Object) this;
        if (!ApotheosisUtil.anvilChanged(menu, stack, itemStack2, resultSlots, itemName, j, player)) {
            ci.cancel();
        }
    }

    @Inject(method = "onTake", at = @At("HEAD"))
    private void apothGetVars(Player player, ItemStack itemStack, CallbackInfo ci) {
        p = player;
    }

    @ModifyConstant(method = "method_24922", constant = @Constant(floatValue = 0.12F))
    private static float apothBreakChance(float chance) {
        if (Apotheosis.enableEnch && p != null) {
            return EnchModuleEvents.anvilRepair(p, chance);
        }
        return chance;
    }

    @Redirect(method = "createResult", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/enchantment/Enchantment;getMaxLevel()I"))
    private int apothModifyMaxLevel(Enchantment enchantment) {
        return EnchHooks.getMaxLevel(enchantment);
    }
}
