package safro.zenith.mixin.anvil;

import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AnvilMenu;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.inventory.ItemCombinerMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import safro.zenith.Zenith;
import safro.zenith.ench.EnchModuleEvents;
import safro.zenith.ench.asm.EnchHooks;
import safro.zenith.ench.objects.ExtractionTomeItem;
import safro.zenith.util.ZenithUtil;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import java.util.Collections;

import static safro.zenith.ench.objects.ExtractionTomeItem.giveItem;

@Mixin(value = AnvilMenu.class, priority = 999)
    public abstract class AnvilMenuMixin extends ItemCombinerMenu {

        @Shadow
        private String itemName;
        private static Player p = null;



        public AnvilMenuMixin(int i, Inventory inventory, ContainerLevelAccess containerLevelAccess) {
            super(MenuType.ANVIL, i, inventory, containerLevelAccess);
        }

        @ModifyConstant(method = "createResult", constant = @Constant(intValue = 40, ordinal = 2))
        public int zenith_removeLevelCap(int old) {
            if (!Zenith.enableEnch)
                return old;
            return Integer.MAX_VALUE;
        }


    @Redirect(method = "createResult", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/enchantment/Enchantment;getMaxLevel()I"))
    private int zenithModifyMaxLevel(Enchantment enchantment) {
        if (!Zenith.enableEnch)
            return enchantment.getMaxLevel();
        return EnchHooks.getMaxLevel(enchantment);
    }

    @Inject(method = "createResult", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/ItemStack;isEmpty()Z", ordinal = 1, shift = At.Shift.AFTER), cancellable = true, locals = LocalCapture.CAPTURE_FAILHARD)
    private void zenithAnvilChange(CallbackInfo ci, ItemStack stack, int i, int j, int k, ItemStack itemStack1, ItemStack itemStack2) {
        AnvilMenu menu = (AnvilMenu) (Object) this;
        if (!ZenithUtil.anvilChanged(menu, stack, itemStack2, resultSlots, itemName, j, player) && Zenith.enableEnch) {
            ci.cancel();
        }
    }

        @Inject(method = "onTake", at = @At("HEAD"))
        private void zenithExtractionTome(Player player, ItemStack itemStack, CallbackInfo ci) {
            if (Zenith.enableEnch) {
            ItemStack left = inputSlots.getItem(0);
            ItemStack right = inputSlots.getItem(1);
                if ((right.getItem() instanceof ExtractionTomeItem) && left.isEnchanted() && !right.isEnchanted()) {
                    EnchantmentHelper.setEnchantments(Collections.emptyMap(), left);
                    giveItem(player, left);
                }
            }
            p = player;
        }

        @ModifyConstant(method = "method_24922", constant = @Constant(floatValue = 0.12F))
        private static float zenithBreakChance(float chance) {
            if (Zenith.enableEnch && p != null) {
                return EnchModuleEvents.anvilRepair(p, chance);
            }
            return chance;
        }


}
