package dev.shadowsoffire.apotheosis.village.fletching.arrows;

import dev.shadowsoffire.apotheosis.village.VillageModule;
import dev.shadowsoffire.attributeslib.api.ALObjects;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.Arrow;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public class BroadheadArrowEntity extends Arrow {

    public BroadheadArrowEntity(EntityType<? extends Arrow> t, Level world) {
        super(t, world);
    }

    public BroadheadArrowEntity(Level world) {
        super(VillageModule.BROADHEAD_ARROW_ENTITY, world);
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
/*
    @Override
    public Packet<ClientGamePacketListener> getAddEntityPacket() {
        return NetworkHooks.getEntitySpawningPacket(this);
    }*/

    @Override
    public EntityType<?> getType() {
        return VillageModule.BROADHEAD_ARROW_ENTITY;
    }

    @Override
    public int getColor() {
        return -1;
    }

    @Override
    protected void doPostHurtEffects(LivingEntity living) {
        MobEffect bleeding = ALObjects.MobEffects.BLEEDING;
        MobEffectInstance bleed = living.getEffect(bleeding);
        if (bleed != null) {
            living.addEffect(new MobEffectInstance(bleeding, bleed.getDuration() + 60, bleed.getAmplifier() + 1));
        }
        else {
            living.addEffect(new MobEffectInstance(bleeding, 300));
        }
    }

    public BroadheadArrowEntity bleed() {
    //    this.addEffect(new MobEffectInstance(ALObjects.MobEffects.BLEEDING.get(), 300));
        return this;
    }
}
