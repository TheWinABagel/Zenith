package dev.shadowsoffire.apotheosis.mixin.ench;

import dev.shadowsoffire.apotheosis.Apoth;
import dev.shadowsoffire.apotheosis.Apotheosis;
import dev.shadowsoffire.apotheosis.ench.table.IEnchantableItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Item.class)
public class ItemMixin implements IEnchantableItem {

    /**
     * @author Shadows
     * @reason Enables all items to be enchantable by default.
     * @return
     */
    @Overwrite
    public int getEnchantmentValue() {
        return Apotheosis.enableEnch ? 1 : 0;
    }

    @Inject(method = "isEnchantable",at = @At("RETURN"), cancellable = true)
    private void addCustomEnchantableItems(ItemStack stack, CallbackInfoReturnable<Boolean> cir){
        if (Apotheosis.enableEnch){
            boolean old = cir.getReturnValue();
            cir.setReturnValue(old || stack.is(Apoth.Tags.CUSTOM_ENCHANTABLES));
        }
    }

}

