package dev.shadowsoffire.apotheosis.potion;

import dev.emi.trinkets.api.TrinketsApi;
import dev.shadowsoffire.apotheosis.Apoth;
import dev.shadowsoffire.apotheosis.Apotheosis;
import dev.shadowsoffire.apotheosis.ench.objects.GlowyBlockItem;
import dev.shadowsoffire.attributeslib.api.ALObjects;
import dev.shadowsoffire.placebo.config.Configuration;
import io.github.fabricators_of_create.porting_lib.entity.events.living.LivingEntityEvents;
import io.github.fabricators_of_create.porting_lib.entity.events.living.LivingEntityLootEvents;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.ResourceLocationException;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.animal.Rabbit;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.PotionBrewing;
import net.minecraft.world.item.alchemy.Potions;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;

public class PotionModule {

    public static final Logger LOGGER = LogManager.getLogger("Zenith : Potion");
    public static final PotionCharmItem POTION_CHARM = new PotionCharmItem();
    public static final Item LUCKY_FOOT = new GlowyBlockItem.GlowyItem(new Item.Properties());
    public static int knowledgeMult = 4;
    public static boolean charmsInTrinketsOnly = false;

    public static boolean RESISTANCE_ENABLED = true;
    public static boolean SUNDERING_ENABLED = true;
    public static boolean ABSORPTION_ENABLED = true;
    public static boolean HASTE_ENABLED = true;
    public static boolean FATIGUE_ENABLED = true;
    public static boolean WITHER_ENABLED = true;
    public static boolean KNOWLEDGE_ENABLED = true;
    public static boolean LUCK_ENABLED = true;
    public static boolean VITALITY_ENABLED = true;
    public static boolean GRIEVOUS_ENABLED = true;
    public static boolean LEVITATION_ENABLED = true;
    public static boolean FLYING_ENABLED = true;


    public static void init() {
        potions();
        items();
        serializers();
        drops();

        if (FabricLoader.getInstance().isModLoaded("trinkets")) {
            LivingEntityEvents.TICK.register(entity -> {
                TrinketsApi.getTrinketComponent(entity).ifPresent(c -> c.forEach((slotReference, stack) -> {
                    if (stack.getItem() instanceof PotionCharmItem charm) {
                        charm.charmLogic(stack, entity.level(), entity, slotReference.index(), false);
                    }
                }));
            });
        }

        reload(false);
    }

    public static void items() {
        Apoth.registerItem(LUCKY_FOOT, "lucky_foot");
        Apoth.registerItem(POTION_CHARM, "potion_charm");
        ItemGroupEvents.modifyEntriesEvent(CreativeModeTabs.TOOLS_AND_UTILITIES).register(PotionCharmItem::fillItemCategory);
        ItemGroupEvents.modifyEntriesEvent(CreativeModeTabs.INGREDIENTS).register(entries -> entries.accept(LUCKY_FOOT));
    }

    public static void serializers() {
        Apoth.registerSerializer("potion_charm", PotionCharmRecipe.Serializer.INSTANCE);
        Apoth.registerSerializer("potion_charm_enchanting", PotionEnchantingRecipe.SERIALIZER);
    }

    public static void drops() {
        LivingEntityLootEvents.DROPS.register((target, source, drops, lootingLevel, recentlyHit) -> {
            if (target instanceof Rabbit rabbit && drops != null) {
                if (rabbit.level().random.nextFloat() < 0.045F + 0.045F * lootingLevel) {
                    drops.clear();
                    drops.add(new ItemEntity(rabbit.level(), rabbit.getX(), rabbit.getY(), rabbit.getZ(), new ItemStack(PotionModule.LUCKY_FOOT)));
                }
            }
            return false;
        });
    }

    public static void reload(boolean e) {
        Configuration config = new Configuration(new File(Apotheosis.configDir, "potion.cfg"));
        config.setTitle("Zenith Potion Module Configuration");
        knowledgeMult = config.getInt("Knowledge XP Multiplier", "general", knowledgeMult, 1, Integer.MAX_VALUE,
            "The strength of Ancient Knowledge.  This multiplier determines how much additional xp is granted.\nServer-authoritative.");
        charmsInTrinketsOnly = config.getBoolean("Restrict Charms to Trinkets", "general", charmsInTrinketsOnly, "If Potion Charms will only work when in a trinkets slot, instead of in the inventory.");

        String[] defExt = { BuiltInRegistries.MOB_EFFECT.getKey(MobEffects.NIGHT_VISION).toString(), BuiltInRegistries.MOB_EFFECT.getKey(MobEffects.HEALTH_BOOST).toString() };
        String[] names = config.getStringList("Extended Potion Charms", "general", defExt,
            "A list of effects that, when as charms, will be applied and reapplied at a longer threshold to avoid issues at low durations, like night vision.\nServer-authoritative.");
        PotionCharmItem.EXTENDED_POTIONS.clear();
        for (String s : names) {
            try {
                PotionCharmItem.EXTENDED_POTIONS.add(new ResourceLocation(s));
            }
            catch (ResourceLocationException ex) {
                LOGGER.error("Invalid extended potion charm entry {} will be ignored.", s);
            }
        }
        String[] defDis = { "modid:charm_id" };
        String[] disabled = config.getStringList("Disabled Potion Charms", "general", defDis,
                "A list of effects that will be unable to be crafted into charms.\nServer-authoritative.");
        PotionCharmItem.DISABLED_POTIONS.clear();
        for (String s : disabled) {
            try {
                PotionCharmItem.DISABLED_POTIONS.add(new ResourceLocation(s));
            }
            catch (ResourceLocationException ex) {
                LOGGER.error("Invalid disabled potion charm entry {} will be ignored.", s);
            }
        }
        config.setCategoryComment("brewing", "All brewing recipe disables are Server-authoritative.");
        RESISTANCE_ENABLED = config.getBoolean("Resistance", "brewing", true, "If this potion type will be craftable in the brewing stand.");
        SUNDERING_ENABLED = config.getBoolean("Sundering", "brewing", true, "If this potion type will be craftable in the brewing stand.");
        ABSORPTION_ENABLED = config.getBoolean("Absorption", "brewing", true, "If this potion type will be craftable in the brewing stand.");
        HASTE_ENABLED = config.getBoolean("Haste", "brewing", true, "If this potion type will be craftable in the brewing stand.");
        FATIGUE_ENABLED = config.getBoolean("Fatigue", "brewing", true, "If this potion type will be craftable in the brewing stand.");
        WITHER_ENABLED = config.getBoolean("Wither", "brewing", true, "If this potion type will be craftable in the brewing stand.");
        KNOWLEDGE_ENABLED = config.getBoolean("Knowledge", "brewing", true, "If this potion type will be craftable in the brewing stand.");
        LUCK_ENABLED = config.getBoolean("Luck", "brewing", true, "If this potion type will be craftable in the brewing stand.");
        VITALITY_ENABLED = config.getBoolean("Vitality", "brewing", true, "If this potion type will be craftable in the brewing stand.");
        GRIEVOUS_ENABLED = config.getBoolean("Grievous", "brewing", true, "If this potion type will be craftable in the brewing stand.");
        LEVITATION_ENABLED = config.getBoolean("Levitation", "brewing", true, "If this potion type will be craftable in the brewing stand.");
        FLYING_ENABLED = config.getBoolean("Flying", "brewing", true, "If this potion type will be craftable in the brewing stand.");
        if (!e && config.hasChanged()) config.save();
    }

    public static void potions() {

        if (RESISTANCE_ENABLED) {
            PotionBrewing.addMix(Potions.AWKWARD, Items.SHULKER_SHELL, Potion.RESISTANCE);
            PotionBrewing.addMix(Potion.RESISTANCE, Items.REDSTONE, Potion.LONG_RESISTANCE);
            PotionBrewing.addMix(Potion.RESISTANCE, Items.GLOWSTONE_DUST, Potion.STRONG_RESISTANCE);
        }
        if (SUNDERING_ENABLED) {
            PotionBrewing.addMix(Potion.RESISTANCE, Items.FERMENTED_SPIDER_EYE, Potion.SUNDERING);
            PotionBrewing.addMix(Potion.LONG_RESISTANCE, Items.FERMENTED_SPIDER_EYE, Potion.LONG_SUNDERING);
            PotionBrewing.addMix(Potion.STRONG_RESISTANCE, Items.FERMENTED_SPIDER_EYE, Potion.STRONG_SUNDERING);
            PotionBrewing.addMix(Potion.SUNDERING, Items.REDSTONE, Potion.LONG_SUNDERING);
            PotionBrewing.addMix(Potion.SUNDERING, Items.GLOWSTONE_DUST, Potion.STRONG_SUNDERING);
        }
        if (ABSORPTION_ENABLED) {
            PotionBrewing.addMix(Potions.AWKWARD, Items.GOLDEN_APPLE, Potion.ABSORPTION);
            PotionBrewing.addMix(Potion.ABSORPTION, Items.REDSTONE, Potion.LONG_ABSORPTION);
            PotionBrewing.addMix(Potion.ABSORPTION, Items.GLOWSTONE_DUST, Potion.STRONG_ABSORPTION);
        }
        if (HASTE_ENABLED) {
            PotionBrewing.addMix(Potions.AWKWARD, Items.MUSHROOM_STEW, Potion.HASTE);
            PotionBrewing.addMix(Potion.HASTE, Items.REDSTONE, Potion.LONG_HASTE);
            PotionBrewing.addMix(Potion.HASTE, Items.GLOWSTONE_DUST, Potion.STRONG_HASTE);
        }
        if (FATIGUE_ENABLED) {
            PotionBrewing.addMix(Potion.HASTE, Items.FERMENTED_SPIDER_EYE, Potion.FATIGUE);
            PotionBrewing.addMix(Potion.LONG_HASTE, Items.FERMENTED_SPIDER_EYE, Potion.LONG_FATIGUE);
            PotionBrewing.addMix(Potion.STRONG_HASTE, Items.FERMENTED_SPIDER_EYE, Potion.STRONG_FATIGUE);
            PotionBrewing.addMix(Potion.FATIGUE, Items.REDSTONE, Potion.LONG_FATIGUE);
            PotionBrewing.addMix(Potion.FATIGUE, Items.GLOWSTONE_DUST, Potion.STRONG_FATIGUE);
        }
        if (WITHER_ENABLED) {
            PotionBrewing.addMix(Potions.AWKWARD, Items.WITHER_SKELETON_SKULL, Potion.WITHER);
            PotionBrewing.addMix(Potion.WITHER, Items.REDSTONE, Potion.LONG_WITHER);
            PotionBrewing.addMix(Potion.WITHER, Items.GLOWSTONE_DUST, Potion.STRONG_WITHER);
        }
        if (KNOWLEDGE_ENABLED) {
            PotionBrewing.addMix(Potions.AWKWARD, Items.EXPERIENCE_BOTTLE, Potion.KNOWLEDGE);
            PotionBrewing.addMix(Potion.KNOWLEDGE, Items.REDSTONE, Potion.LONG_KNOWLEDGE);
            PotionBrewing.addMix(Potion.KNOWLEDGE, Items.EXPERIENCE_BOTTLE, Potion.STRONG_KNOWLEDGE);
        }
        if (LUCK_ENABLED) {
            PotionBrewing.addMix(Potions.AWKWARD, LUCKY_FOOT, Potions.LUCK);
        }
        if (VITALITY_ENABLED) {
            PotionBrewing.addMix(Potions.AWKWARD, Items.SWEET_BERRIES, Potion.VITALITY);
            PotionBrewing.addMix(Potion.VITALITY, Items.REDSTONE, Potion.LONG_VITALITY);
            PotionBrewing.addMix(Potion.VITALITY, Items.GLOWSTONE_DUST, Potion.STRONG_VITALITY);
        }
        if (GRIEVOUS_ENABLED) {
            PotionBrewing.addMix(Potion.VITALITY, Items.FERMENTED_SPIDER_EYE, Potion.GRIEVOUS);
            PotionBrewing.addMix(Potion.LONG_VITALITY, Items.FERMENTED_SPIDER_EYE, Potion.LONG_GRIEVOUS);
            PotionBrewing.addMix(Potion.STRONG_VITALITY, Items.FERMENTED_SPIDER_EYE, Potion.STRONG_GRIEVOUS);
            PotionBrewing.addMix(Potion.GRIEVOUS, Items.REDSTONE, Potion.LONG_GRIEVOUS);
            PotionBrewing.addMix(Potion.GRIEVOUS, Items.GLOWSTONE_DUST, Potion.STRONG_GRIEVOUS);
        }
        if (LEVITATION_ENABLED) {
            PotionBrewing.addMix(Potions.SLOW_FALLING, Items.FERMENTED_SPIDER_EYE, Potion.LEVITATION);
        }
        if (FLYING_ENABLED) {
            PotionBrewing.addMix(Potion.LEVITATION, Items.POPPED_CHORUS_FRUIT, Potion.FLYING);
            PotionBrewing.addMix(Potion.FLYING, Items.REDSTONE, Potion.LONG_FLYING);
            PotionBrewing.addMix(Potion.LONG_FLYING, Items.REDSTONE, Potion.EXTRA_LONG_FLYING);
        }
    }

    public static class Potion {

        public static final net.minecraft.world.item.alchemy.Potion RESISTANCE = Apoth.registerPot(new net.minecraft.world.item.alchemy.Potion("resistance", new MobEffectInstance(MobEffects.DAMAGE_RESISTANCE, 3600)), "resistance");
        public static final net.minecraft.world.item.alchemy.Potion LONG_RESISTANCE = Apoth.registerPot(new net.minecraft.world.item.alchemy.Potion("resistance", new MobEffectInstance(MobEffects.DAMAGE_RESISTANCE, 9600)), "long_resistance");
        public static final net.minecraft.world.item.alchemy.Potion STRONG_RESISTANCE = Apoth.registerPot(new net.minecraft.world.item.alchemy.Potion("resistance", new MobEffectInstance(MobEffects.DAMAGE_RESISTANCE, 1800, 1)), "strong_resistance");
        public static final net.minecraft.world.item.alchemy.Potion ABSORPTION = Apoth.registerPot(new net.minecraft.world.item.alchemy.Potion("absorption", new MobEffectInstance(MobEffects.ABSORPTION, 1200, 1)), "absorption");
        public static final net.minecraft.world.item.alchemy.Potion LONG_ABSORPTION = Apoth.registerPot(new net.minecraft.world.item.alchemy.Potion("absorption", new MobEffectInstance(MobEffects.ABSORPTION, 3600, 1)), "long_absorption");
        public static final net.minecraft.world.item.alchemy.Potion STRONG_ABSORPTION = Apoth.registerPot(new net.minecraft.world.item.alchemy.Potion("absorption", new MobEffectInstance(MobEffects.ABSORPTION, 600, 3)), "strong_absorption");
        public static final net.minecraft.world.item.alchemy.Potion HASTE = Apoth.registerPot(new net.minecraft.world.item.alchemy.Potion("haste", new MobEffectInstance(MobEffects.DIG_SPEED, 3600)), "haste");
        public static final net.minecraft.world.item.alchemy.Potion LONG_HASTE = Apoth.registerPot(new net.minecraft.world.item.alchemy.Potion("haste", new MobEffectInstance(MobEffects.DIG_SPEED, 9600)), "long_haste");
        public static final net.minecraft.world.item.alchemy.Potion STRONG_HASTE = Apoth.registerPot(new net.minecraft.world.item.alchemy.Potion("haste", new MobEffectInstance(MobEffects.DIG_SPEED, 1800, 1)), "strong_haste");
        public static final net.minecraft.world.item.alchemy.Potion FATIGUE = Apoth.registerPot(new net.minecraft.world.item.alchemy.Potion("fatigue", new MobEffectInstance(MobEffects.DIG_SLOWDOWN, 3600)), "fatigue");
        public static final net.minecraft.world.item.alchemy.Potion LONG_FATIGUE = Apoth.registerPot(new net.minecraft.world.item.alchemy.Potion("fatigue", new MobEffectInstance(MobEffects.DIG_SLOWDOWN, 9600)), "long_fatigue");
        public static final net.minecraft.world.item.alchemy.Potion STRONG_FATIGUE = Apoth.registerPot(new net.minecraft.world.item.alchemy.Potion("fatigue", new MobEffectInstance(MobEffects.DIG_SLOWDOWN, 1800, 1)), "strong_fatigue");
        public static final net.minecraft.world.item.alchemy.Potion WITHER = Apoth.registerPot(new net.minecraft.world.item.alchemy.Potion("wither", new MobEffectInstance(MobEffects.WITHER, 3600)), "wither");
        public static final net.minecraft.world.item.alchemy.Potion LONG_WITHER = Apoth.registerPot(new net.minecraft.world.item.alchemy.Potion("wither", new MobEffectInstance(MobEffects.WITHER, 9600)), "long_wither");
        public static final net.minecraft.world.item.alchemy.Potion STRONG_WITHER = Apoth.registerPot(new net.minecraft.world.item.alchemy.Potion("wither", new MobEffectInstance(MobEffects.WITHER, 1800, 1)), "strong_wither");
        public static final net.minecraft.world.item.alchemy.Potion SUNDERING = Apoth.registerPot(new net.minecraft.world.item.alchemy.Potion("sundering", new MobEffectInstance(ALObjects.MobEffects.SUNDERING, 3600)), "sundering");
        public static final net.minecraft.world.item.alchemy.Potion LONG_SUNDERING = Apoth.registerPot(new net.minecraft.world.item.alchemy.Potion("sundering", new MobEffectInstance(ALObjects.MobEffects.SUNDERING, 9600)), "long_sundering");
        public static final net.minecraft.world.item.alchemy.Potion STRONG_SUNDERING = Apoth.registerPot(new net.minecraft.world.item.alchemy.Potion("sundering", new MobEffectInstance(ALObjects.MobEffects.SUNDERING, 1800, 1)), "strong_sundering");
        public static final net.minecraft.world.item.alchemy.Potion KNOWLEDGE = Apoth.registerPot(new net.minecraft.world.item.alchemy.Potion("knowledge", new MobEffectInstance(ALObjects.MobEffects.KNOWLEDGE, 2400)), "knowledge");
        public static final net.minecraft.world.item.alchemy.Potion LONG_KNOWLEDGE = Apoth.registerPot(new net.minecraft.world.item.alchemy.Potion("knowledge", new MobEffectInstance(ALObjects.MobEffects.KNOWLEDGE, 4800)), "long_knowledge");
        public static final net.minecraft.world.item.alchemy.Potion STRONG_KNOWLEDGE = Apoth.registerPot(new net.minecraft.world.item.alchemy.Potion("knowledge", new MobEffectInstance(ALObjects.MobEffects.KNOWLEDGE, 1200, 1)), "strong_knowledge");
        public static final net.minecraft.world.item.alchemy.Potion VITALITY = Apoth.registerPot(new net.minecraft.world.item.alchemy.Potion("vitality", new MobEffectInstance(ALObjects.MobEffects.VITALITY, 4800)), "vitality");
        public static final net.minecraft.world.item.alchemy.Potion LONG_VITALITY = Apoth.registerPot(new net.minecraft.world.item.alchemy.Potion("vitality", new MobEffectInstance(ALObjects.MobEffects.VITALITY, 14400)), "long_vitality");
        public static final net.minecraft.world.item.alchemy.Potion STRONG_VITALITY = Apoth.registerPot(new net.minecraft.world.item.alchemy.Potion("vitality", new MobEffectInstance(ALObjects.MobEffects.VITALITY, 3600, 1)), "strong_vitality");
        public static final net.minecraft.world.item.alchemy.Potion GRIEVOUS = Apoth.registerPot(new net.minecraft.world.item.alchemy.Potion("grievous", new MobEffectInstance(ALObjects.MobEffects.GRIEVOUS, 4800)), "grievous");
        public static final net.minecraft.world.item.alchemy.Potion LONG_GRIEVOUS = Apoth.registerPot(new net.minecraft.world.item.alchemy.Potion("grievous", new MobEffectInstance(ALObjects.MobEffects.GRIEVOUS, 14400)), "long_grievous");
        public static final net.minecraft.world.item.alchemy.Potion STRONG_GRIEVOUS = Apoth.registerPot(new net.minecraft.world.item.alchemy.Potion("grievous", new MobEffectInstance(ALObjects.MobEffects.GRIEVOUS, 3600, 1)), "strong_grievous");
        public static final net.minecraft.world.item.alchemy.Potion LEVITATION = Apoth.registerPot(new net.minecraft.world.item.alchemy.Potion("levitation", new MobEffectInstance(MobEffects.LEVITATION, 2400)), "levitation");
        public static final net.minecraft.world.item.alchemy.Potion FLYING = Apoth.registerPot(new net.minecraft.world.item.alchemy.Potion("flying", new MobEffectInstance(ALObjects.MobEffects.FLYING, 9600)), "flying");
        public static final net.minecraft.world.item.alchemy.Potion LONG_FLYING = Apoth.registerPot(new net.minecraft.world.item.alchemy.Potion("flying", new MobEffectInstance(ALObjects.MobEffects.FLYING, 18000)), "long_flying");
        public static final net.minecraft.world.item.alchemy.Potion EXTRA_LONG_FLYING = Apoth.registerPot(new net.minecraft.world.item.alchemy.Potion("flying", new MobEffectInstance(ALObjects.MobEffects.FLYING, 36000)), "extra_long_flying");
    }
}
