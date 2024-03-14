package dev.shadowsoffire.apotheosis.mixin.garden;

import dev.shadowsoffire.apotheosis.Apotheosis;
import dev.shadowsoffire.apotheosis.garden.EnderLeadItem;
import dev.shadowsoffire.apotheosis.garden.GardenModule;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(FriendlyByteBuf.class)
public abstract class FriendlyByteBufMixin { //is this needed?

    @ModifyVariable(method = "writeItem", at = @At(value = "INVOKE", target = "net/minecraft/world/item/ItemStack.getTag ()Lnet/minecraft/nbt/CompoundTag;"))
    private CompoundTag fixEnderLeadOnServers(CompoundTag oldTag, ItemStack stack) {
        if (Apotheosis.enableGarden && stack.is(GardenModule.ENDER_LEAD)) {
            return EnderLeadItem.getShareTag(oldTag);
        }
        return oldTag;
    }
}
