package safro.zenith.mixin;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;
import safro.zenith.Zenith;
import safro.zenith.api.LeftClickItem;
import safro.zenith.ench.enchantments.StableFootingEnchant;
import safro.zenith.ench.enchantments.twisted.MinersFervorEnchant;

@Mixin(Player.class)
public class PlayerMixin {

    @Inject(method = "getDestroySpeed", at = @At("TAIL"), cancellable = true, locals = LocalCapture.CAPTURE_FAILHARD)
    private void apotheosisBreakSpeed(BlockState blockState, CallbackInfoReturnable<Float> cir, float f) {
        Player player = (Player) (Object) this;
        if (Zenith.enableEnch) {
            float m = MinersFervorEnchant.breakSpeed(player, blockState, f);
            float s = StableFootingEnchant.breakSpeed(player, f, f);
            if (m > -1) {
                cir.setReturnValue(m);
            }
            if (s > -1) {
                cir.setReturnValue(s);
            }
        }
    }

    @Inject(method = "attack", at = @At("HEAD"), cancellable = true)
    private void apothLeftClickEntity(Entity entity, CallbackInfo ci) {
        Player player = (Player) (Object) this;
        ItemStack stack = player.getMainHandItem();
        if (!stack.isEmpty()) {
            if (stack.getItem() instanceof LeftClickItem i) {
                if (i.onLeftClickEntity(stack, player, entity)) ci.cancel();
            }
        }
    }

//    @Inject(method = "createAttributes", at = @At("RETURN"))
//    private static void createAttributes(CallbackInfoReturnable<AttributeSupplier.Builder> cir) {
//        AttributeSupplier.Builder builder = cir.getReturnValue();
//        addIfExists(builder, DeadlyModule.COLD_DAMAGE, DeadlyModule.CRIT_CHANCE, DeadlyModule.CRIT_DAMAGE, DeadlyModule.CURRENT_HP_DAMAGE, DeadlyModule.DRAW_SPEED, DeadlyModule.FIRE_DAMAGE, DeadlyModule.LIFE_STEAL, DeadlyModule.OVERHEAL, DeadlyModule.PIERCING);
//    }

    private static void addIfExists(AttributeSupplier.Builder builder, Attribute... attribs) {
        for (Attribute attrib : attribs)
            if (attrib != null) builder.add(attrib);
    }
}
