package safro.zenith.village.fletching.arrows;

import net.minecraft.client.renderer.entity.ArrowRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider.Context;
import net.minecraft.resources.ResourceLocation;
import safro.zenith.Zenith;

public class ExplosiveArrowRenderer extends ArrowRenderer<ExplosiveArrowEntity> {

	public static final ResourceLocation TEXTURES = new ResourceLocation(Zenith.MODID, "textures/entity/explosive_arrow.png");

	public ExplosiveArrowRenderer(Context renderManagerIn) {
		super(renderManagerIn);
	}

	@Override
	public ResourceLocation getTextureLocation(ExplosiveArrowEntity entity) {
		return TEXTURES;
	}

}