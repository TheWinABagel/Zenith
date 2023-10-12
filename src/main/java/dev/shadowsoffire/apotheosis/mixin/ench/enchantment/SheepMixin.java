package dev.shadowsoffire.apotheosis.mixin.ench.enchantment;

import dev.shadowsoffire.apotheosis.Apotheosis;
import dev.shadowsoffire.apotheosis.ench.Ench;
import dev.shadowsoffire.apotheosis.ench.EnchModule;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.animal.Sheep;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import java.util.ArrayList;
import java.util.List;

@Mixin(Sheep.class)
public abstract class SheepMixin {

    @Unique private ItemStack shears;
    @Unique private List<ItemEntity> itemList = new ArrayList<>();

    @Inject(method = "Lnet/minecraft/world/entity/animal/Sheep;mobInteract(Lnet/minecraft/world/entity/player/Player;Lnet/minecraft/world/InteractionHand;)Lnet/minecraft/world/InteractionResult;",
            at = @At(value = "INVOKE", target = "net/minecraft/world/entity/animal/Sheep.shear (Lnet/minecraft/sounds/SoundSource;)V",
            shift = At.Shift.BEFORE))
    private void capture(Player player, InteractionHand hand, CallbackInfoReturnable<InteractionResult> cir) {
            this.shears = player.getItemInHand(hand);
    }

    @Redirect(method = "shear", at = @At(value = "INVOKE", target = "net/minecraft/world/entity/animal/Sheep.setSheared (Z)V"))
    private void bleh(Sheep instance, boolean sheared){
        if (Apotheosis.enableEnch && EnchantmentHelper.getItemEnchantmentLevel(Ench.Enchantments.GROWTH_SERUM, shears) > 0 && ((Sheep)(Object) this).getRandom().nextBoolean()){
            if (Apotheosis.enableDebug) EnchModule.LOGGER.info("Growth serum resetting wool");
            instance.setSheared(false);
            return;
        }
        instance.setSheared(sheared);
    }

    @Inject(method = "shear", at = @At(value = "INVOKE", target = "net/minecraft/world/entity/item/ItemEntity.setDeltaMovement (Lnet/minecraft/world/phys/Vec3;)V"),
            locals = LocalCapture.CAPTURE_FAILSOFT)
    public void testsetsts(SoundSource source, CallbackInfo ci, int i, int j, ItemEntity itemEntity) {
        if (Apotheosis.enableEnch && EnchantmentHelper.getItemEnchantmentLevel(Ench.Enchantments.EXPLOITATION, shears) > 0) {
            itemList.add(itemEntity);
            EnchModule.LOGGER.info("Added {} to extra wool list", itemEntity.getItem());
        }
    }


    //Chromatic
    @Redirect(method = "shear", at = @At(value = "INVOKE", target = "net/minecraft/world/entity/animal/Sheep.getColor ()Lnet/minecraft/world/item/DyeColor;"))
    private DyeColor chromatic(Sheep instance){
        if (Apotheosis.enableEnch && EnchantmentHelper.getItemEnchantmentLevel(Ench.Enchantments.CHROMATIC, shears) > 0) {
            return DyeColor.byId(instance.getRandom().nextInt(16));
        }
        return instance.getColor();
    }

    //Fortune
    @ModifyConstant(method = "shear", constant = @Constant(intValue = 3), remap = false)
    private int fortune(int oldVal) {
        int newval =oldVal + (EnchantmentHelper.getItemEnchantmentLevel(Enchantments.BLOCK_FORTUNE, shears)) * 2;
        EnchModule.LOGGER.info(newval);
        if (Apotheosis.enableEnch) return oldVal + (EnchantmentHelper.getItemEnchantmentLevel(Enchantments.BLOCK_FORTUNE, shears)) * 2;
        return oldVal;
    }

    //Handle Exploitation
    @Inject(method = "shear", at = @At(value = "INVOKE",
            target = "net/minecraft/world/entity/item/ItemEntity.setDeltaMovement (Lnet/minecraft/world/phys/Vec3;)V",
            shift = At.Shift.AFTER))
    public void molestSheepItems(SoundSource source, CallbackInfo ci) {
        if (Apotheosis.enableEnch && EnchantmentHelper.getItemEnchantmentLevel(Ench.Enchantments.EXPLOITATION, shears) > 0) {
            for (ItemEntity item : itemList){
                ((Sheep)(Object)this).spawnAtLocation(item.getItem(), 1);
                EnchModule.LOGGER.info("Worker Exploitation spawning extra wool: {}", item.getItem());
            }
            ((Sheep)(Object)this).hurt(((Sheep)(Object)this).level().damageSources().generic(), 2);
        }
    }

}
