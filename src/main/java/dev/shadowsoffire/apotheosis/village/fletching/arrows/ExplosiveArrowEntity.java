package dev.shadowsoffire.apotheosis.village.fletching.arrows;

import dev.shadowsoffire.apotheosis.village.VillageModule;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;

public class ExplosiveArrowEntity extends AbstractArrow {

    public ExplosiveArrowEntity(EntityType<? extends AbstractArrow> t, Level world) {
        super(t, world);
    }

    public ExplosiveArrowEntity(Level world) {
        super(VillageModule.EXPLOSIVE_ARROW_ENTITY, world);
    }

    public ExplosiveArrowEntity(LivingEntity shooter, Level world) {
        super(VillageModule.EXPLOSIVE_ARROW_ENTITY, shooter, world);
    }

    public ExplosiveArrowEntity(Level world, double x, double y, double z) {
        super(VillageModule.EXPLOSIVE_ARROW_ENTITY, x, y, z, world);
    }

    @Override
    protected ItemStack getPickupItem() {
        return new ItemStack(VillageModule.EXPLOSIVE_ARROW);
    }

    @Override
    protected void doPostHurtEffects(LivingEntity living) {
        if (!this.level().isClientSide) {
            this.level().explode(this, living.getX(), living.getY(), living.getZ(), 2, false, VillageModule.expArrowMode);
            this.discard();
        }
    }

    @Override
    protected void onHitBlock(BlockHitResult res) {
        super.onHitBlock(res);
        Vec3 vec = res.getLocation();
        if (!this.level().isClientSide) {
            this.level().explode(this, vec.x(), vec.y(), vec.z(), 3, false, VillageModule.expArrowMode);
            this.discard();
        }
    }
}
