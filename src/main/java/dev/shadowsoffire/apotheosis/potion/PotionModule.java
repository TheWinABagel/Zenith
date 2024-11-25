package dev.shadowsoffire.apotheosis.potion;

import dev.emi.trinkets.api.Trinket;
import dev.emi.trinkets.api.TrinketsApi;
import dev.shadowsoffire.apotheosis.Apoth;
import dev.shadowsoffire.apotheosis.Apotheosis;
import dev.shadowsoffire.apotheosis.ench.objects.GlowyBlockItem;
import dev.shadowsoffire.apotheosis.potion.compat.PotionCharmTrinket;
import dev.shadowsoffire.apotheosis.util.ZenithModCompat;
import dev.shadowsoffire.attributeslib.api.ALObjects;
import dev.shadowsoffire.placebo.config.Configuration;
import io.github.fabricators_of_create.porting_lib.entity.events.LivingEntityEvents;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.ResourceLocationException;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
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
    public static boolean yeetInvalidCharms = false;

    public static boolean resistanceEnabled = true;
    public static boolean sunderingEnabled = true;
    public static boolean absorptionEnabled = true;
    public static boolean hasteEnabled = true;
    public static boolean fatigueEnabled = true;
    public static boolean witherEnabled = true;
    public static boolean knowledgeEnabled = true;
    public static boolean luckEnabled = true;
    public static boolean vitalityEnabled = true;
    public static boolean grievousEnabled = true;
    public static boolean levitationEnabled = true;
    public static boolean flyingEnabled = true;

    public static void init() {
        reload(false);
        potions();
        items();
        serializers();
        drops();

        ZenithModCompat.Potion.registerTrinkets();
//        if (FabricLoader.getInstance().isModLoaded("trinkets")) {
//            LivingEntityEvents.LivingTickEvent.TICK.register(e -> {
//                LivingEntity entity = e.getEntity();
//                TrinketsApi.getTrinketComponent(entity).ifPresent(c -> c.forEach((slotReference, stack) -> {
//                    if (stack.getItem() instanceof PotionCharmItem charm) {
//                        charm.charmLogic(stack, entity.level(), entity, slotReference.index(), false);
//                    }
//                }));
//            });
//        }
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
        LivingEntityEvents.DROPS.register((target, source, drops, lootingLevel, recentlyHit) -> {
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
        yeetInvalidCharms = config.getBoolean("Yeet Uncraftable Charms", "general", yeetInvalidCharms, "If charms that are uncraftable are removed from the user.");
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
        resistanceEnabled = config.getBoolean("Resistance", "brewing", resistanceEnabled, "If this potion type will be craftable in the brewing stand.");
        sunderingEnabled = config.getBoolean("Sundering", "brewing", sunderingEnabled, "If this potion type will be craftable in the brewing stand.");
        absorptionEnabled = config.getBoolean("Absorption", "brewing", absorptionEnabled, "If this potion type will be craftable in the brewing stand.");
        hasteEnabled = config.getBoolean("Haste", "brewing", hasteEnabled, "If this potion type will be craftable in the brewing stand.");
        fatigueEnabled = config.getBoolean("Fatigue", "brewing", fatigueEnabled, "If this potion type will be craftable in the brewing stand.");
        witherEnabled = config.getBoolean("Wither", "brewing", witherEnabled, "If this potion type will be craftable in the brewing stand.");
        knowledgeEnabled = config.getBoolean("Knowledge", "brewing", knowledgeEnabled, "If this potion type will be craftable in the brewing stand.");
        luckEnabled = config.getBoolean("Luck", "brewing", luckEnabled, "If this potion type will be craftable in the brewing stand.");
        vitalityEnabled = config.getBoolean("Vitality", "brewing", vitalityEnabled, "If this potion type will be craftable in the brewing stand.");
        grievousEnabled = config.getBoolean("Grievous", "brewing", grievousEnabled, "If this potion type will be craftable in the brewing stand.");
        levitationEnabled = config.getBoolean("Levitation", "brewing", levitationEnabled, "If this potion type will be craftable in the brewing stand.");
        flyingEnabled = config.getBoolean("Flying", "brewing", flyingEnabled, "If this potion type will be craftable in the brewing stand.");
        if (!e && config.hasChanged()) config.save();
    }

    public static void potions() {

        if (resistanceEnabled) {
            PotionBrewing.addMix(Potions.AWKWARD, Items.SHULKER_SHELL, Potion.RESISTANCE);
            PotionBrewing.addMix(Potion.RESISTANCE, Items.REDSTONE, Potion.LONG_RESISTANCE);
            PotionBrewing.addMix(Potion.RESISTANCE, Items.GLOWSTONE_DUST, Potion.STRONG_RESISTANCE);
        }
        if (sunderingEnabled) {
            PotionBrewing.addMix(Potion.RESISTANCE, Items.FERMENTED_SPIDER_EYE, Potion.SUNDERING);
            PotionBrewing.addMix(Potion.LONG_RESISTANCE, Items.FERMENTED_SPIDER_EYE, Potion.LONG_SUNDERING);
            PotionBrewing.addMix(Potion.STRONG_RESISTANCE, Items.FERMENTED_SPIDER_EYE, Potion.STRONG_SUNDERING);
            PotionBrewing.addMix(Potion.SUNDERING, Items.REDSTONE, Potion.LONG_SUNDERING);
            PotionBrewing.addMix(Potion.SUNDERING, Items.GLOWSTONE_DUST, Potion.STRONG_SUNDERING);
        }
        if (absorptionEnabled) {
            PotionBrewing.addMix(Potions.AWKWARD, Items.GOLDEN_APPLE, Potion.ABSORPTION);
            PotionBrewing.addMix(Potion.ABSORPTION, Items.REDSTONE, Potion.LONG_ABSORPTION);
            PotionBrewing.addMix(Potion.ABSORPTION, Items.GLOWSTONE_DUST, Potion.STRONG_ABSORPTION);
        }
        if (hasteEnabled) {
            PotionBrewing.addMix(Potions.AWKWARD, Items.MUSHROOM_STEW, Potion.HASTE);
            PotionBrewing.addMix(Potion.HASTE, Items.REDSTONE, Potion.LONG_HASTE);
            PotionBrewing.addMix(Potion.HASTE, Items.GLOWSTONE_DUST, Potion.STRONG_HASTE);
        }
        if (fatigueEnabled) {
            PotionBrewing.addMix(Potion.HASTE, Items.FERMENTED_SPIDER_EYE, Potion.FATIGUE);
            PotionBrewing.addMix(Potion.LONG_HASTE, Items.FERMENTED_SPIDER_EYE, Potion.LONG_FATIGUE);
            PotionBrewing.addMix(Potion.STRONG_HASTE, Items.FERMENTED_SPIDER_EYE, Potion.STRONG_FATIGUE);
            PotionBrewing.addMix(Potion.FATIGUE, Items.REDSTONE, Potion.LONG_FATIGUE);
            PotionBrewing.addMix(Potion.FATIGUE, Items.GLOWSTONE_DUST, Potion.STRONG_FATIGUE);
        }
        if (witherEnabled) {
            PotionBrewing.addMix(Potions.AWKWARD, Items.WITHER_SKELETON_SKULL, Potion.WITHER);
            PotionBrewing.addMix(Potion.WITHER, Items.REDSTONE, Potion.LONG_WITHER);
            PotionBrewing.addMix(Potion.WITHER, Items.GLOWSTONE_DUST, Potion.STRONG_WITHER);
        }
        if (knowledgeEnabled) {
            PotionBrewing.addMix(Potions.AWKWARD, Items.EXPERIENCE_BOTTLE, Potion.KNOWLEDGE);
            PotionBrewing.addMix(Potion.KNOWLEDGE, Items.REDSTONE, Potion.LONG_KNOWLEDGE);
            PotionBrewing.addMix(Potion.KNOWLEDGE, Items.EXPERIENCE_BOTTLE, Potion.STRONG_KNOWLEDGE);
        }
        if (luckEnabled) {
            PotionBrewing.addMix(Potions.AWKWARD, LUCKY_FOOT, Potions.LUCK);
        }
        if (vitalityEnabled) {
            PotionBrewing.addMix(Potions.AWKWARD, Items.SWEET_BERRIES, Potion.VITALITY);
            PotionBrewing.addMix(Potion.VITALITY, Items.REDSTONE, Potion.LONG_VITALITY);
            PotionBrewing.addMix(Potion.VITALITY, Items.GLOWSTONE_DUST, Potion.STRONG_VITALITY);
        }
        if (grievousEnabled) {
            PotionBrewing.addMix(Potion.VITALITY, Items.FERMENTED_SPIDER_EYE, Potion.GRIEVOUS);
            PotionBrewing.addMix(Potion.LONG_VITALITY, Items.FERMENTED_SPIDER_EYE, Potion.LONG_GRIEVOUS);
            PotionBrewing.addMix(Potion.STRONG_VITALITY, Items.FERMENTED_SPIDER_EYE, Potion.STRONG_GRIEVOUS);
            PotionBrewing.addMix(Potion.GRIEVOUS, Items.REDSTONE, Potion.LONG_GRIEVOUS);
            PotionBrewing.addMix(Potion.GRIEVOUS, Items.GLOWSTONE_DUST, Potion.STRONG_GRIEVOUS);
        }
        if (levitationEnabled) {
            PotionBrewing.addMix(Potions.SLOW_FALLING, Items.FERMENTED_SPIDER_EYE, Potion.LEVITATION);
        }
        if (flyingEnabled) {
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
