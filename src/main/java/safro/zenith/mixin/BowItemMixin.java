package safro.zenith.mixin;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.BowItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import safro.zenith.Zenith;
import safro.zenith.ench.EnchModule;
import safro.zenith.ench.enchantments.masterwork.EndlessQuiverEnchant;

@Mixin(BowItem.class)
public class BowItemMixin {

    @Redirect(method = "releaseUsing", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/ItemStack;is(Lnet/minecraft/world/item/Item;)Z"))
    private boolean apothEndlessQuiver(ItemStack stack, Item item, ItemStack bow, Level level, LivingEntity livingEntity, int i) {
        if (Zenith.enableEnch) {
            return EndlessQuiverEnchant.isTrulyInfinite(stack, bow) || stack.is(Items.ARROW);
        }
        return stack.is(Items.ARROW);
    }

    @Redirect(method = "releaseUsing", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/enchantment/EnchantmentHelper;getItemEnchantmentLevel(Lnet/minecraft/world/item/enchantment/Enchantment;Lnet/minecraft/world/item/ItemStack;)I", ordinal = 0))
    private int apothEndlessQuiver(Enchantment enchantment, ItemStack itemStack) {
        if (Zenith.enableEnch) {
            int i = EnchantmentHelper.getItemEnchantmentLevel(EnchModule.ENDLESS_QUIVER, itemStack);
            return i > 0 ? i : EnchantmentHelper.getItemEnchantmentLevel(Enchantments.INFINITY_ARROWS, itemStack);
        }
        return EnchantmentHelper.getItemEnchantmentLevel(Enchantments.INFINITY_ARROWS, itemStack);
    }
}
