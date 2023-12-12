package dev.shadowsoffire.apotheosis.mixin.compat.modern_industrialization.present;

import aztech.modern_industrialization.machines.blockentities.ReplicatorMachineBlockEntity;
import dev.shadowsoffire.apotheosis.Apotheosis;
import dev.shadowsoffire.apotheosis.adventure.affix.AffixHelper;
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@Pseudo
@Mixin(value = ReplicatorMachineBlockEntity.class, remap = false)
public class ReplicatorMachineBlockEntityMixin {

    @Inject(method = "replicationStep",
            at = @At(value = "INVOKE", target = "net/fabricmc/fabric/api/transfer/v1/context/ContainerItemContext.withConstant (Lnet/fabricmc/fabric/api/transfer/v1/item/ItemVariant;J)Lnet/fabricmc/fabric/api/transfer/v1/context/ContainerItemContext;"),
            cancellable = true, locals = LocalCapture.CAPTURE_FAILHARD)
    private void zenith$disableAffixItemReplication(boolean simulate, CallbackInfoReturnable<Boolean> cir, ItemVariant itemVariant) {
        if (Apotheosis.enableAdventure && AffixHelper.hasAffixes(itemVariant.toStack())) {
            cir.setReturnValue(false);
        }
    }
}
