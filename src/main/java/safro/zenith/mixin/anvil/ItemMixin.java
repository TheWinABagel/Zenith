package safro.zenith.mixin.anvil;

import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Item.class)
public class ItemMixin  {

    @Inject(method = "isEnchantable", at = @At("HEAD"), cancellable = true)
    private void apothAnvilEnchantable(ItemStack stack, CallbackInfoReturnable<Boolean> cir) {
        if (stack.is(ItemTags.ANVIL)) {
            cir.setReturnValue(stack.getCount() == 1);
        }
    }
}