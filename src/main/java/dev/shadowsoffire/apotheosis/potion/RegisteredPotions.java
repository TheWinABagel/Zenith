package dev.shadowsoffire.apotheosis.potion;

import dev.shadowsoffire.apotheosis.Apoth;
import dev.shadowsoffire.apotheosis.Apotheosis;
import dev.shadowsoffire.attributeslib.api.ALObjects;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.item.alchemy.PotionBrewing;
import net.minecraft.world.item.alchemy.Potions;

public class RegisteredPotions {






    public static void init(){
            PotionBrewing.addMix(Potions.AWKWARD, Items.SHULKER_SHELL, Apoth.Potions.RESISTANCE);
            PotionBrewing.addMix(Apoth.Potions.RESISTANCE, Items.REDSTONE, Apoth.Potions.LONG_RESISTANCE);
            PotionBrewing.addMix(Apoth.Potions.RESISTANCE, Items.GLOWSTONE_DUST, Apoth.Potions.STRONG_RESISTANCE);

            PotionBrewing.addMix(Apoth.Potions.RESISTANCE, Items.FERMENTED_SPIDER_EYE, Apoth.Potions.SUNDERING);
            PotionBrewing.addMix(Apoth.Potions.LONG_RESISTANCE, Items.FERMENTED_SPIDER_EYE, Apoth.Potions.LONG_SUNDERING);
            PotionBrewing.addMix(Apoth.Potions.STRONG_RESISTANCE, Items.FERMENTED_SPIDER_EYE, Apoth.Potions.STRONG_SUNDERING);
            PotionBrewing.addMix(Apoth.Potions.SUNDERING, Items.REDSTONE, Apoth.Potions.LONG_SUNDERING);
            PotionBrewing.addMix(Apoth.Potions.SUNDERING, Items.GLOWSTONE_DUST, Apoth.Potions.STRONG_SUNDERING);

            PotionBrewing.addMix(Potions.AWKWARD, Items.GOLDEN_APPLE, Apoth.Potions.ABSORPTION);
            PotionBrewing.addMix(Apoth.Potions.ABSORPTION, Items.REDSTONE, Apoth.Potions.LONG_ABSORPTION);
            PotionBrewing.addMix(Apoth.Potions.ABSORPTION, Items.GLOWSTONE_DUST, Apoth.Potions.STRONG_ABSORPTION);

            PotionBrewing.addMix(Potions.AWKWARD, Items.MUSHROOM_STEW, Apoth.Potions.HASTE);
            PotionBrewing.addMix(Apoth.Potions.HASTE, Items.REDSTONE, Apoth.Potions.LONG_HASTE);
            PotionBrewing.addMix(Apoth.Potions.HASTE, Items.GLOWSTONE_DUST, Apoth.Potions.STRONG_HASTE);

            PotionBrewing.addMix(Apoth.Potions.HASTE, Items.FERMENTED_SPIDER_EYE, Apoth.Potions.FATIGUE);
            PotionBrewing.addMix(Apoth.Potions.LONG_HASTE, Items.FERMENTED_SPIDER_EYE, Apoth.Potions.LONG_FATIGUE);
            PotionBrewing.addMix(Apoth.Potions.STRONG_HASTE, Items.FERMENTED_SPIDER_EYE, Apoth.Potions.STRONG_FATIGUE);
            PotionBrewing.addMix(Apoth.Potions.FATIGUE, Items.REDSTONE, Apoth.Potions.LONG_FATIGUE);
            PotionBrewing.addMix(Apoth.Potions.FATIGUE, Items.GLOWSTONE_DUST, Apoth.Potions.STRONG_FATIGUE);

            if (Apoth.Items.SKULL_FRAGMENT.isPresent()) PotionBrewing.addMix(Potions.AWKWARD, Apoth.Items.SKULL_FRAGMENT.get(), Apoth.Potions.WITHER);
            else PotionBrewing.addMix(Potions.AWKWARD, Items.WITHER_SKELETON_SKULL, Apoth.Potions.WITHER);
            PotionBrewing.addMix(Apoth.Potions.WITHER, Items.REDSTONE, Apoth.Potions.LONG_WITHER);
            PotionBrewing.addMix(Apoth.Potions.WITHER, Items.GLOWSTONE_DUST, Apoth.Potions.STRONG_WITHER);

            PotionBrewing.addMix(Potions.AWKWARD, Items.EXPERIENCE_BOTTLE, Apoth.Potions.KNOWLEDGE);
            PotionBrewing.addMix(Apoth.Potions.KNOWLEDGE, Items.REDSTONE, Apoth.Potions.LONG_KNOWLEDGE);
            PotionBrewing.addMix(Apoth.Potions.KNOWLEDGE, Items.EXPERIENCE_BOTTLE, Apoth.Potions.STRONG_KNOWLEDGE);

            PotionBrewing.addMix(Potions.AWKWARD, Apoth.Items.LUCKY_FOOT, Potions.LUCK);

            PotionBrewing.addMix(Potions.AWKWARD, Items.SWEET_BERRIES, Apoth.Potions.VITALITY);
            PotionBrewing.addMix(Apoth.Potions.VITALITY, Items.REDSTONE, Apoth.Potions.LONG_VITALITY);
            PotionBrewing.addMix(Apoth.Potions.VITALITY, Items.GLOWSTONE_DUST, Apoth.Potions.STRONG_VITALITY);

            PotionBrewing.addMix(Apoth.Potions.VITALITY, Items.FERMENTED_SPIDER_EYE, Apoth.Potions.GRIEVOUS);
            PotionBrewing.addMix(Apoth.Potions.LONG_VITALITY, Items.FERMENTED_SPIDER_EYE, Apoth.Potions.LONG_GRIEVOUS);
            PotionBrewing.addMix(Apoth.Potions.STRONG_VITALITY, Items.FERMENTED_SPIDER_EYE, Apoth.Potions.STRONG_GRIEVOUS);
            PotionBrewing.addMix(Apoth.Potions.GRIEVOUS, Items.REDSTONE, Apoth.Potions.LONG_GRIEVOUS);
            PotionBrewing.addMix(Apoth.Potions.GRIEVOUS, Items.GLOWSTONE_DUST, Apoth.Potions.STRONG_GRIEVOUS);

            PotionBrewing.addMix(Potions.SLOW_FALLING, Items.FERMENTED_SPIDER_EYE, Apoth.Potions.LEVITATION);
            PotionBrewing.addMix(Apoth.Potions.LEVITATION, Items.POPPED_CHORUS_FRUIT, Apoth.Potions.FLYING);
            PotionBrewing.addMix(Apoth.Potions.FLYING, Items.REDSTONE, Apoth.Potions.LONG_FLYING);
            PotionBrewing.addMix(Apoth.Potions.LONG_FLYING, Items.REDSTONE, Apoth.Potions.EXTRA_LONG_FLYING);
    }


}
