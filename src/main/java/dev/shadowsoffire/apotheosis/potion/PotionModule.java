package dev.shadowsoffire.apotheosis.potion;

import dev.shadowsoffire.apotheosis.Apoth;
import dev.shadowsoffire.apotheosis.Apotheosis;
import dev.shadowsoffire.attributeslib.api.ALObjects;
import dev.shadowsoffire.placebo.config.Configuration;
import io.github.fabricators_of_create.porting_lib.entity.events.living.LivingEntityLootEvents;
import net.minecraft.ResourceLocationException;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.animal.Rabbit;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.item.alchemy.PotionBrewing;
import net.minecraft.world.item.alchemy.Potions;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;

public class PotionModule {

    public static final Logger LOG = LogManager.getLogger("Apotheosis : Potion");
    public static final ResourceLocation POTION_TEX = new ResourceLocation(Apotheosis.MODID, "textures/potions.png");

    public static int knowledgeMult = 4;
    static boolean charmsInTrinketsOnly = false;

    public static void init() {
        RegisteredPotions.init();
        items();
        serializers();
        drops();
        reload(false);
    }

    public static void items() {
        Apoth.registerItem("lucky_foot", Apoth.Items.LUCKY_FOOT);
        Apoth.registerItem("potion_charm", Apoth.Items.POTION_CHARM);
    }

    public static void serializers() {
        Apoth.registerSerializer("potion_charm", PotionCharmRecipe.Serializer.INSTANCE);
        Apoth.registerSerializer("potion_charm_enchanting", PotionEnchantingRecipe.SERIALIZER);
    }

    public static void drops() {
        LivingEntityLootEvents.DROPS.register((target, source, drops, lootingLevel, recentlyHit) -> {
            if (target instanceof Rabbit rabbit) {
                if (rabbit.level().random.nextFloat() < 0.045F + 0.045F * lootingLevel) {
                    drops.clear();
                    drops.add(new ItemEntity(rabbit.level(), rabbit.getX(), rabbit.getY(), rabbit.getZ(), new ItemStack(Apoth.Items.LUCKY_FOOT)));
                }
            }
            return true;
        });
    }

    public static void reload(boolean e) {
        Configuration config = new Configuration(new File(Apotheosis.configDir, "potion.cfg"));
        config.setTitle("Apotheosis Potion Module Configuration");
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
                LOG.error("Invalid extended potion charm entry {} will be ignored.", s);
            }
        }

        if (!e && config.hasChanged()) config.save();
    }

}
