package safro.zenith.deadly;

import com.jamieswhiteshirt.reachentityattributes.ReachEntityAttributes;
import net.fabricmc.fabric.api.event.registry.FabricRegistryBuilder;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.attributes.RangedAttribute;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import safro.zenith.Zenith;
import safro.zenith.api.config.Configuration;
import safro.zenith.deadly.config.DeadlyConfig;
import safro.zenith.deadly.loot.LootCategory;
import safro.zenith.deadly.loot.LootRarity;
import safro.zenith.deadly.loot.affix.Affix;
import safro.zenith.deadly.loot.affix.AttributeAffix;
import safro.zenith.util.NameHelper;

import java.io.File;

public class DeadlyModule {
    public static final Logger LOGGER = LogManager.getLogger("Apotheosis : Deadly");
    public static final Registry<Affix> AFFIXES = FabricRegistryBuilder.createSimple(Affix.class, new ResourceLocation(Zenith.MODID, "affixes")).buildAndRegister();

    /**
     * Bonus to how fast a ranged weapon is charged. Base Value = (1.0) = 100%
     */
    public static final Attribute DRAW_SPEED = register("draw_speed", new RangedAttribute("apotheosis:draw_speed", 1.0D, 1.0D, 1024.0D).setSyncable(true));
    /**
     * Chance that a non-jump-attack will critically strike.  Base value = (1.0) = 0%
     */
    public static final Attribute CRIT_CHANCE = register("crit_chance", new RangedAttribute("apotheosis:crit_chance", 0.0D, 0.0D, 1024.0D).setSyncable(true));
    /**
     * Amount of damage caused by critical strikes. Base value = (1.0) = 100%
     */
    public static final Attribute CRIT_DAMAGE = register("crit_damage", new RangedAttribute("apotheosis:crit_damage", 0.0D, 0.0D, 1024.0D).setSyncable(true));
    /**
     * Bonus magic damage that slows enemies hit. Base value = (0.0) = 0 damage
     */
    public static final Attribute COLD_DAMAGE = register("cold_damage", new RangedAttribute("apotheosis:cold_damage", 0.0D, 0.0D, 1024.0D).setSyncable(true));
    /**
     * Bonus magic damage that burns enemies hit. Base value = (0.0) = 0 damage
     */
    public static final Attribute FIRE_DAMAGE = register("fire_damage", new RangedAttribute("apotheosis:fire_damage", 0.0D, 0.0D, 1024.0D).setSyncable(true));
    /**
     * Percent of physical damage converted to health. Base value = (1.0) = 0%
     */
    public static final Attribute LIFE_STEAL = register("life_steal", new RangedAttribute("apotheosis:life_steal", 0.0D, 0.0D, 1024.0D).setSyncable(true));
    /**
     * Percent of physical damage that bypasses armor. Base value = (1.0) = 0%
     */
    public static final Attribute PIERCING = register("piercing", new RangedAttribute("apotheosis:piercing", 0.0D, 0.0D, 1024.0D).setSyncable(true));
    /**
     * Bonus physical damage dealt equal to enemy's current health. Base value = (1.0) = 0%
     */
    public static final Attribute CURRENT_HP_DAMAGE = register("current_hp_damage", new RangedAttribute("apotheosis:current_hp_damage", 0.0D, 0.0D, 1024.0D).setSyncable(true));
    /**
     * Percent of physical damage converted to absorption hearts. Base value = (1.0) = 0%
     */
    public static final Attribute OVERHEAL = register("overheal", new RangedAttribute("apotheosis:overheal", 0.0D, 0.0D, 1024.0D).setSyncable(true));

    public static void init() {
        reload(false);
        DeadlyModuleEvents.init();

        // Affixes
        register("common_hp_max", new AttributeAffix.Builder(LootRarity.COMMON).with(Attributes.MAX_HEALTH, AttributeModifier.Operation.ADDITION, (level -> 0.5F + Math.round(level * 3) / 2F)).types(LootCategory::isDefensive).build());
        register("common_armor", new AttributeAffix.Builder(LootRarity.COMMON).with(Attributes.ARMOR, AttributeModifier.Operation.ADDITION, 0.5F, 2).types(LootCategory::isDefensive).build());
        register("common_dmg", new AttributeAffix.Builder(LootRarity.COMMON).with(Attributes.ATTACK_DAMAGE, AttributeModifier.Operation.ADDITION, 0.5F, 2).build());
        register("common_mvspd", new AttributeAffix.Builder(LootRarity.COMMON).with(Attributes.MOVEMENT_SPEED, AttributeModifier.Operation.MULTIPLY_TOTAL, 0.05F, 0.15F).build());
        register("common_aspd", new AttributeAffix.Builder(LootRarity.COMMON).with(Attributes.ATTACK_SPEED, AttributeModifier.Operation.MULTIPLY_TOTAL, 0.1F, 0.25F).build());
        register("common_kb", new AttributeAffix.Builder(LootRarity.COMMON).with(Attributes.ATTACK_KNOCKBACK, AttributeModifier.Operation.ADDITION, 0.25F, 0.5F).build());
        register("common_reach", new AttributeAffix.Builder(LootRarity.COMMON).with(ReachEntityAttributes.REACH, AttributeModifier.Operation.ADDITION, (level -> 0.5F + Math.round(level * 3) / 2F)).build());
    }

    private static Affix register(String name, Affix affix) {
        return Registry.register(AFFIXES, new ResourceLocation(Zenith.MODID, name), affix);
    }

    private static Attribute register(String registry, Attribute attribute) {
        return Registry.register(Registry.ATTRIBUTE, new ResourceLocation(Zenith.MODID, registry), attribute);
    }

    public static void reload(boolean e) {
        Configuration mainConfig = new Configuration(new File(Zenith.configDir, "deadly.cfg"));
        Configuration nameConfig = new Configuration(new File(Zenith.configDir, "names.cfg"));
        DeadlyConfig.load(mainConfig);
        NameHelper.load(nameConfig);
        if (!e && mainConfig.hasChanged()) mainConfig.save();
        if (!e && nameConfig.hasChanged()) nameConfig.save();
    }

}
