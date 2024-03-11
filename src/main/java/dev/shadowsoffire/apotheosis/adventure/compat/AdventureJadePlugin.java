package dev.shadowsoffire.apotheosis.adventure.compat;

import com.google.common.base.Predicates;
import dev.shadowsoffire.apotheosis.Apotheosis;
import dev.shadowsoffire.apotheosis.cca.ZenithComponents;
import dev.shadowsoffire.apotheosis.util.CommonTooltipUtil;
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
import snownee.jade.api.*;
import snownee.jade.api.config.IPluginConfig;

@WailaPlugin
public class AdventureJadePlugin implements IWailaPlugin, IEntityComponentProvider, IServerDataProvider<EntityAccessor> {

    @Override
    public void register(IWailaCommonRegistration reg) {
        if (Apotheosis.enableAdventure) reg.registerEntityDataProvider(this, LivingEntity.class);
    }

    @Override
    public void registerClient(IWailaClientRegistration reg) {
        if (Apotheosis.enableAdventure) reg.registerEntityComponent(this, Entity.class);
    }

    @Override
    public void appendTooltip(ITooltip tooltip, EntityAccessor accessor, IPluginConfig config) {
        if (accessor.getEntity() instanceof LivingEntity living && accessor.getServerData().getBoolean("zenith.boss")) {
            ListTag bossAttribs = accessor.getServerData().getList("zenith.modifiers", Tag.TAG_COMPOUND);
            AttributeMap map = living.getAttributes();
            for (Tag t : bossAttribs) {
                CompoundTag tag = (CompoundTag) t;
                Attribute attrib = BuiltInRegistries.ATTRIBUTE.get(new ResourceLocation(tag.getString("Name")));
                map.getInstance(attrib).load(tag);
            }
            accessor.getServerData().remove("zenith.modifiers");
//            living.getCustomData().merge(accessor.getServerData());
            CommonTooltipUtil.appendBossData(living.level(), living, tooltip::add);
        }
    }

    @Override
    public void appendServerData(CompoundTag tag, EntityAccessor access) {
        if (access.getEntity() instanceof LivingEntity living && ZenithComponents.BOSS_DATA.get(living).getIsBoss()) {
            tag.putBoolean("zenith.boss", true);
            tag.putString("zenith.rarity", ZenithComponents.BOSS_DATA.get(living).getRarity());
            AttributeMap map = living.getAttributes();
            ListTag bossAttribs = new ListTag();
            BuiltInRegistries.ATTRIBUTE.stream().map(map::getInstance).filter(Predicates.notNull()).forEach(inst -> {
                for (AttributeModifier modif : inst.getModifiers()) {
                    if (modif.getName().startsWith("placebo_random_modifier_")) {
                        bossAttribs.add(inst.save());
                    }
                }
            });
            tag.put("zenith.modifiers", bossAttribs);
        }
    }

    @Override
    public ResourceLocation getUid() {
        return Apotheosis.loc("adventure");
    }

}
