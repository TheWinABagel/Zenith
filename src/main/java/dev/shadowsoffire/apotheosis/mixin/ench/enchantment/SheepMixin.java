package dev.shadowsoffire.apotheosis.mixin.ench.enchantment;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import dev.shadowsoffire.apotheosis.Apotheosis;
import dev.shadowsoffire.apotheosis.ench.Ench;
import dev.shadowsoffire.apotheosis.ench.EnchModule;
import dev.shadowsoffire.apotheosis.util.IShearHelper;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.animal.Sheep;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.ItemStack;
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
public abstract class SheepMixin implements IShearHelper {

    @Unique private ItemStack shears = ItemStack.EMPTY;
    @Unique private List<ItemEntity> itemList = new ArrayList<>();

    @Inject(method = "Lnet/minecraft/world/entity/animal/Sheep;mobInteract(Lnet/minecraft/world/entity/player/Player;Lnet/minecraft/world/InteractionHand;)Lnet/minecraft/world/InteractionResult;",
            at = @At(value = "INVOKE", target = "net/minecraft/world/entity/animal/Sheep.shear (Lnet/minecraft/sounds/SoundSource;)V",
            shift = At.Shift.BEFORE))
    private void captureItemStack(Player player, InteractionHand hand, CallbackInfoReturnable<InteractionResult> cir) {
            this.shears = (player.getItemInHand(hand));
    }

    @WrapOperation(method = "shear", at = @At(value = "INVOKE", target = "net/minecraft/world/entity/animal/Sheep.setSheared (Z)V"))
    private void zenith_handleGrowthSerum(Sheep instance, boolean sheared, Operation<Boolean> op) {
        if (!Apotheosis.enableEnch || shears.isEmpty()) {
            if (EnchantmentHelper.getItemEnchantmentLevel(Ench.Enchantments.GROWTH_SERUM, shears) > 0 && ((Sheep) (Object) this).getRandom().nextBoolean()) {
                if (Apotheosis.enableDebug) EnchModule.LOGGER.info("Growth serum resetting wool");
                instance.setSheared(false);
                return;
            }
        }
        instance.setSheared(sheared);
    }

    @WrapOperation(method = "shear", at = @At(value = "INVOKE", target = "net/minecraft/world/entity/animal/Sheep.getColor ()Lnet/minecraft/world/item/DyeColor;"))
    private DyeColor zenith_handleChromatic(Sheep instance,  Operation<Void> op) {
        if (!Apotheosis.enableEnch || shears.isEmpty()) return instance.getColor();
        if (EnchantmentHelper.getItemEnchantmentLevel(Ench.Enchantments.CHROMATIC, shears) > 0) {
            return DyeColor.byId(instance.getRandom().nextInt(16));
        }
        return instance.getColor();
    }

    @ModifyConstant(method = "shear", constant = @Constant(intValue = 3), remap = false)
    private int zenith_applyFortune(int oldVal) {
        if (!Apotheosis.enableEnch || shears.isEmpty()) return oldVal;
        return oldVal + (EnchantmentHelper.getItemEnchantmentLevel(Enchantments.BLOCK_FORTUNE, shears)) * 2;
    }

    @Inject(method = "shear", at = @At(value = "INVOKE", target = "net/minecraft/world/entity/item/ItemEntity.setDeltaMovement (Lnet/minecraft/world/phys/Vec3;)V"),
            locals = LocalCapture.CAPTURE_FAILEXCEPTION)
    public void zenith_addExploitationStacks(SoundSource source, CallbackInfo ci, int i, int j, ItemEntity itemEntity) {
        if (!Apotheosis.enableEnch || shears.isEmpty()) return;
        if (EnchantmentHelper.getItemEnchantmentLevel(Ench.Enchantments.EXPLOITATION, shears) > 0) {
            itemList.add(itemEntity);
        }
    }

    @Inject(method = "shear", at = @At(value = "INVOKE",
            target = "net/minecraft/world/entity/item/ItemEntity.setDeltaMovement (Lnet/minecraft/world/phys/Vec3;)V",
            shift = At.Shift.AFTER))
    public void zenith_dropExploitationStacks(SoundSource source, CallbackInfo ci) {
        if (!Apotheosis.enableEnch || shears.isEmpty()) return;
        if (EnchantmentHelper.getItemEnchantmentLevel(Ench.Enchantments.EXPLOITATION, shears) > 0) {
            for (ItemEntity item : itemList){
                ((Sheep)(Object)this).spawnAtLocation(item.getItem(), 1);
                if (Apotheosis.enableDebug) EnchModule.LOGGER.info("Worker Exploitation spawning extra wool: {}", item.getItem());
            }
            ((Sheep)(Object)this).hurt(((Sheep)(Object)this).level().damageSources().generic(), 2);
        }
    }

    @Override
    public void zenithSetShears(ItemStack shears) {
        this.shears = shears;
    }
}
