package dev.shadowsoffire.apotheosis.util;

import com.teamremastered.endrem.config.ERConfigHandler;
import com.teamremastered.endrem.registry.ERItems;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.spell_power.api.enchantment.EnchantmentRestriction;

public class ZenithModCompat {
    public static final boolean EASY_MAGIC_LOADED = FabricLoader.getInstance().isModLoaded("easymagic");
    public static final boolean EASY_ANVILS_LOADED = FabricLoader.getInstance().isModLoaded("easyanvils");
    public static boolean SPELL_ENGINE_LOADED = FabricLoader.getInstance().isModLoaded("spell_engine");
    public static boolean END_REMASTERED_LOADED = FabricLoader.getInstance().isModLoaded("endrem");


    public static class Ench {


        public static boolean isProhibitedSpellEngine(Enchantment ench, ItemStack stack) {
            if (!SPELL_ENGINE_LOADED) return false;
            return EnchantmentRestriction.isProhibited(ench, stack);
        }

        public static boolean isPermittedSpellEngine(Enchantment ench, ItemStack stack) {
            if (!SPELL_ENGINE_LOADED) return false;
            return EnchantmentRestriction.isPermitted(ench, stack);
        }

        public static void endRemasteredEnchHook(Player player) {
            if (!END_REMASTERED_LOADED) return;
            int maxValue = 120;
            int randomNumber = player.getRandom().nextInt(maxValue);
            if (!player.level().isClientSide && ERConfigHandler.IS_CRYPTIC_EYE_OBTAINABLE && randomNumber == maxValue - 1) {
                player.addItem(new ItemStack(ERItems.CRYPTIC_EYE));
            }
        }
    }
}
