package safro.zenith.mixin.anvil;

import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import safro.zenith.ench.EnchModule;

@Mixin(value = Item.class, priority = 1500)
public class ItemMixin  {

    @Inject(method = "isEnchantable", at = @At("HEAD"), cancellable = true)
    private void zenithAnvilEnchantable(ItemStack stack, CallbackInfoReturnable<Boolean> cir) {
        if (stack.is(ItemTags.ANVIL) || stack.is(Items.ANVIL)|| stack.is(Items.CHIPPED_ANVIL)|| stack.is(Items.DAMAGED_ANVIL) || stack.is(EnchModule.UNBREAK_ANVIL)) {
            cir.setReturnValue(stack.getCount() == 1);
        }
    }
}