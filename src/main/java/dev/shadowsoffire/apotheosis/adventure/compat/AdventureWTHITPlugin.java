package dev.shadowsoffire.apotheosis.adventure.compat;

import com.google.common.base.Predicates;
import dev.shadowsoffire.apotheosis.Apotheosis;
import dev.shadowsoffire.apotheosis.cca.ZenithComponents;
import dev.shadowsoffire.apotheosis.util.CommonTooltipUtil;
import mcp.mobius.waila.api.*;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeMap;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;

public class AdventureWTHITPlugin implements IWailaPlugin, IEntityComponentProvider, IDataProvider<Entity> {
    @Override
    public void register(IRegistrar registrar) {
        if (Apotheosis.enableAdventure) {
            registrar.addEntityData(this, Entity.class);
            registrar.addComponent(this, TooltipPosition.BODY, Entity.class);
        }
    }

    @Override
    public void appendData(IDataWriter data, IServerAccessor access, IPluginConfig config) {
        if (Apotheosis.enableAdventure && access.getTarget() instanceof LivingEntity living && ZenithComponents.BOSS_DATA.get(living).getIsBoss()) {
            data.raw().putBoolean("zenith.boss", true);
            data.raw().putString("zenith.rarity", ZenithComponents.BOSS_DATA.get(living).getRarity());

            AttributeMap map = living.getAttributes();
            ListTag bossAttribs = new ListTag();
            BuiltInRegistries.ATTRIBUTE.stream().map(map::getInstance).filter(Predicates.notNull()).forEach(inst -> {
                for (AttributeModifier modif : inst.getModifiers()) {
                    if (modif.getName().startsWith("placebo_random_modifier_")) {
                        bossAttribs.add(inst.save());
                    }
                }
            });
            data.raw().put("zenith.modifiers", bossAttribs);
        }
    }

    @Override
    public void appendBody(ITooltip tooltip, IEntityAccessor accessor, IPluginConfig config) {
        if (Apotheosis.enableAdventure && accessor.getEntity() instanceof LivingEntity living && accessor.getData().raw().getBoolean("apoth.boss")) {
            ListTag bossAttribs = accessor.getData().raw().getList("zenith.modifiers", Tag.TAG_COMPOUND);
            AttributeMap map = living.getAttributes();
            for (Tag t : bossAttribs) {
                CompoundTag tag = (CompoundTag) t;
                Attribute attrib = BuiltInRegistries.ATTRIBUTE.get(new ResourceLocation(tag.getString("Name")));
                map.getInstance(attrib).load(tag);
            }
            accessor.getData().raw().remove("zenith.modifiers");
//            living.getCustomData().merge(accessor.getData().raw());

            CommonTooltipUtil.appendBossData(living.level(), living, tooltip::addLine);
        }
    }
}
