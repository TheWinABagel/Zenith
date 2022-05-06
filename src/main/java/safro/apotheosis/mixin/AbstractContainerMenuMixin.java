package safro.apotheosis.mixin;

import it.unimi.dsi.fastutil.ints.IntList;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerSynchronizer;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import safro.apotheosis.api.container.ApotheosisContainerMenu;

@Mixin(AbstractContainerMenu.class)
public class AbstractContainerMenuMixin {

    @Shadow private boolean suppressRemoteUpdates;

    @Shadow @Final private IntList remoteDataSlots;

    @Shadow @Nullable private ContainerSynchronizer synchronizer;

    @Inject(method = "synchronizeDataSlotToRemote", at = @At(value = "HEAD"), cancellable = true)
    private void apothDataSync(int i, int j, CallbackInfo ci) {
        AbstractContainerMenu menu = (AbstractContainerMenu) (Object) this;
        if (menu instanceof ApotheosisContainerMenu am) {
            if (!this.suppressRemoteUpdates) {
                int k = this.remoteDataSlots.getInt(i);
                if (k != j) {
                    this.remoteDataSlots.set(i, j);
                    if (this.synchronizer != null) {
                        this.synchronizer.sendDataChange(am, i, am.getSyncTransformers().get(i).applyAsInt(j));
                    }
                }

            }
            ci.cancel();
        }
    }
}
