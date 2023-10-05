package dev.shadowsoffire.apotheosis.mixin.ench.enchantment;

import dev.shadowsoffire.apotheosis.Apotheosis;
import dev.shadowsoffire.apotheosis.ench.Ench;
import dev.shadowsoffire.apotheosis.ench.enchantments.ChromaticEnchant;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.animal.Sheep;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.ItemLike;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Sheep.class)
public class SheepMixin { //TODO Sheep enchants mixin

    @Unique private Player player;
    @Unique private InteractionHand hand;
 /*   @Inject(method = "Lnet/minecraft/world/entity/animal/Sheep;mobInteract(Lnet/minecraft/world/entity/player/Player;Lnet/minecraft/world/InteractionHand;)Lnet/minecraft/world/InteractionResult;",
            at = @At(value = "INVOKE", target = "net/minecraft/world/entity/animal/Sheep.shear (Lnet/minecraft/sounds/SoundSource;)V", shift = At.Shift.BEFORE), remap = false, cancellable = true)
    public void onSheared(Player player, InteractionHand hand, CallbackInfoReturnable<InteractionResult> cir) {
        if (Apotheosis.enableEnch) {
            this.player = player;
            this.hand = hand;
            ItemStack item = player.getItemInHand(hand);
            //cir.setReturnValue(Ench.Enchantments.CHROMATIC.molestSheepItems((Sheep) (Object) this, item, cir.getReturnValue()));
            //cir.setReturnValue(Ench.Enchantments.EXPLOITATION.molestSheepItems((Sheep) (Object) this, item, cir.getReturnValue()));
            Ench.Enchantments.GROWTH_SERUM.unshear((Sheep) (Object) this, item);
        }
    }

    @Redirect(method = "shear", at = @At(value = "INVOKE", target = "net/minecraft/world/entity/animal/Sheep.spawnAtLocation (Lnet/minecraft/world/level/ItemLike;I)Lnet/minecraft/world/entity/item/ItemEntity;"), remap = false)
    private ItemEntity test(Sheep instance, ItemLike itemLike, int i){
        if (Apotheosis.enableEnch && EnchantmentHelper.getItemEnchantmentLevel(Ench.Enchantments.EXPLOITATION, player.getItemInHand(hand)) > 0) {
            return ((Sheep) (Object) this).spawnAtLocation(ChromaticEnchant.ITEM_BY_DYE.get(((Sheep) (Object) this).random.nextInt(16)));
        }
        return (ItemEntity) itemLike;
    }

    @ModifyConstant(method = "shear", constant = @Constant(intValue = 3), remap = false)
    public int onSheared(int oldVal) {
        if (Apotheosis.enableEnch) return oldVal + (EnchantmentHelper.getItemEnchantmentLevel(Enchantments.BLOCK_FORTUNE, player.getItemInHand(hand))) * 2;
        return oldVal;
    }*/

}
