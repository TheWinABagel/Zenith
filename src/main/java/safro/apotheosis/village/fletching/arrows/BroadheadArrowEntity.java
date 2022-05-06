package safro.apotheosis.village.fletching.arrows;

import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.Arrow;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import safro.apotheosis.village.VillageModule;

public class BroadheadArrowEntity extends Arrow {

	public BroadheadArrowEntity(EntityType<? extends Arrow> t, Level world) {
		super(t, world);
	}

	public BroadheadArrowEntity(Level world) {
		super(VillageModule.BROADHEAD, world);
	}

	public BroadheadArrowEntity(LivingEntity shooter, Level world) {
		super(world, shooter);
	}

	public BroadheadArrowEntity(Level world, double x, double y, double z) {
		super(world, x, y, z);
	}

	@Override
	protected ItemStack getPickupItem() {
		return new ItemStack(VillageModule.BROADHEAD_ARROW);
	}

	@Override
	public EntityType<?> getType() {
		return VillageModule.BROADHEAD;
	}

	@Override
	public int getColor() {
		return -1;
	}

	@Override
	protected void doPostHurtEffects(LivingEntity living) {
		MobEffectInstance bleed = living.getEffect(VillageModule.BLEEDING);
		if (bleed != null) {
			living.addEffect(new MobEffectInstance(VillageModule.BLEEDING, bleed.getDuration() + 60, bleed.getAmplifier() + 1));
		} else {
			living.addEffect(new MobEffectInstance(VillageModule.BLEEDING, 300));
		}
	}

	public BroadheadArrowEntity bleed() {
		this.addEffect(new MobEffectInstance(VillageModule.BLEEDING, 300));
		return this;
	}
}