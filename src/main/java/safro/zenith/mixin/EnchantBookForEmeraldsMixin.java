package safro.zenith.mixin;

import net.minecraft.world.item.enchantment.Enchantment;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import safro.zenith.ench.asm.EnchHooks;

@Mixin(targets = {"net/minecraft/world/entity/npc/VillagerTrades$EnchantBookForEmeralds"})
public class EnchantBookForEmeraldsMixin {

    @Redirect(method = "getOffer", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/enchantment/Enchantment;isTreasureOnly()Z"))
    private boolean zenithIsTreasure(Enchantment instance) {
        return EnchHooks.isTreasureOnly(instance);
    }
}
