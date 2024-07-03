package dev.shadowsoffire.apotheosis.mixin.util.events;

import com.google.common.collect.Sets;
import dev.shadowsoffire.apotheosis.Apotheosis;
import dev.shadowsoffire.apotheosis.adventure.affix.augmenting.AugmentingTableTileRenderer;
import dev.shadowsoffire.apotheosis.adventure.affix.reforging.ReforgingTableTileRenderer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.color.block.BlockColors;
import net.minecraft.client.resources.model.ModelBakery;
import net.minecraft.client.resources.model.UnbakedModel;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.profiling.ProfilerFiller;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Map;
import java.util.Set;

@Mixin(ModelBakery.class)
public abstract class ModelBakeryMixin {

    @Shadow @Final private Map<ResourceLocation, UnbakedModel> unbakedCache;
    @Shadow @Final private  Map<ResourceLocation, UnbakedModel> topLevelModels;

    @Inject(method = "<init>", at = @At(value = "INVOKE", target = "net/minecraft/client/resources/model/ModelBakery.loadTopLevel (Lnet/minecraft/client/resources/model/ModelResourceLocation;)V", ordinal = 3))
    private void zenith$initCustomModels(BlockColors blockColors, ProfilerFiller profilerFiller, Map map, Map map2, CallbackInfo ci){
        Set<ResourceLocation> extraModels = Sets.newHashSet();

        Set<ResourceLocation> locs = Minecraft.getInstance().getResourceManager().listResources("models", loc -> Apotheosis.MODID.equals(loc.getNamespace()) && loc.getPath().contains("/gems/") && loc.getPath().endsWith(".json"))
                .keySet();
        for (ResourceLocation s : locs) {
            String path = s.getPath().substring("models/".length(), s.getPath().length() - ".json".length());
            extraModels.add(Apotheosis.loc(path));
        }

        extraModels.add(ReforgingTableTileRenderer.HAMMER);
        extraModels.add(AugmentingTableTileRenderer.STAR_CUBE);

        for (ResourceLocation resourceLocation : extraModels) {
            UnbakedModel unbakedmodel = ((ModelBakery) (Object) this).getModel(resourceLocation);
            unbakedCache.put(resourceLocation, unbakedmodel);
            topLevelModels.put(resourceLocation, unbakedmodel);
        }
    }
}
