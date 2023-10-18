package dev.shadowsoffire.apotheosis.mixin.util.events;

import dev.shadowsoffire.apotheosis.Apotheosis;
import dev.shadowsoffire.apotheosis.adventure.client.GemModel;
import dev.shadowsoffire.apotheosis.util.Events;
import net.minecraft.client.color.block.BlockColors;
import net.minecraft.client.resources.model.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.profiling.ProfilerFiller;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Map;

@Mixin(ModelManager.class)
public class ModelManagerMixin {

    @Inject(method = "loadModels", at = @At(value = "INVOKE", target = "net/minecraft/util/profiling/ProfilerFiller.popPush (Ljava/lang/String;)V", ordinal = 1, shift = At.Shift.BEFORE))
    private void initModels(ProfilerFiller profilerFiller, Map<ResourceLocation, AtlasSet.StitchResult> map, ModelBakery modelBakery, CallbackInfoReturnable<ModelManager.ReloadState> cir){
        //Events.ModifyBakedModelCallback.EVENT.invoker().modifyBakedModels(modelBakery, modelBakery.getBakedTopLevelModels());


            ModelResourceLocation key = new ModelResourceLocation(Apotheosis.loc("gem"), "inventory");
            BakedModel oldModel = modelBakery.getBakedTopLevelModels().get(key);
            if (oldModel != null) {
                modelBakery.getBakedTopLevelModels().put(key, new GemModel(oldModel, modelBakery));
            }

    }
}
