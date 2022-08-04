package safro.zenith.village.fletching.arrows;

import net.minecraft.client.renderer.entity.ArrowRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider.Context;
import net.minecraft.resources.ResourceLocation;
import safro.zenith.Zenith;

public class BroadheadArrowRenderer extends ArrowRenderer<BroadheadArrowEntity> {

	public static final ResourceLocation TEXTURES = new ResourceLocation(Zenith.MODID, "textures/entity/broadhead_arrow.png");

	public BroadheadArrowRenderer(Context renderManagerIn) {
		super(renderManagerIn);
	}

	@Override
	public ResourceLocation getTextureLocation(BroadheadArrowEntity entity) {
		return TEXTURES;
	}

}