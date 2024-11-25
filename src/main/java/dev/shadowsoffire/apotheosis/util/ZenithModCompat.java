package dev.shadowsoffire.apotheosis.util;

import com.anthonyhilyard.iceberg.events.RenderTooltipEvents;
import com.teamremastered.endrem.config.ERConfigHandler;
import com.teamremastered.endrem.registry.ERItems;
import dev.emi.trinkets.api.TrinketsApi;
import dev.shadowsoffire.apotheosis.adventure.AdventureEvents;
import dev.shadowsoffire.apotheosis.compat.PatchouliCompat;
import dev.shadowsoffire.apotheosis.ench.enchantments.corrupted.LifeMendingEnchant;
import dev.shadowsoffire.apotheosis.potion.PotionModule;
import dev.shadowsoffire.apotheosis.potion.compat.PotionCharmTrinket;
import dev.shadowsoffire.apotheosis.util.events.ModifyComponents;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.Enchantment;
import net.spell_power.api.enchantment.EnchantmentRestriction;

import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;

public class ZenithModCompat {
    public static boolean TRINKETS_LOADED = loaded("trinkets");
    public static boolean PATCHOULI_LOADED = loaded("patchouli");


    public static final boolean EASY_MAGIC_LOADED = loaded("easymagic");
    public static final boolean EASY_ANVILS_LOADED = loaded("easyanvils");
    public static boolean SPELL_ENGINE_LOADED = loaded("spell_engine");
    public static boolean END_REMASTERED_LOADED = loaded("endrem");

    public static boolean ICEBERG_LOADED = loaded("endrem");

    public static boolean loaded(String modId) {
        return FabricLoader.getInstance().isModLoaded(modId);
    }

    public static void patchouliCompat() {
        if (PATCHOULI_LOADED) {
            PatchouliCompat.registerPatchouli();
        }
    }

    public static class Potion {
        public static void registerTrinkets() {
            if (TRINKETS_LOADED) {
                TrinketsApi.registerTrinket(PotionModule.POTION_CHARM, new PotionCharmTrinket());
            }
        }
    }

    public static class Adventure {

        public static void spellEngineCast() {
            if (ZenithModCompat.SPELL_ENGINE_LOADED) {
                AdventureEvents.onSpellCast();
            }
        }

        public static void icebergCompat() {
            if (ICEBERG_LOADED) {
                RenderTooltipEvents.GATHER.register((itemStack, screenWidth, screenHeight, tooltipElements, maxWidth, index) -> {
                    ModifyComponents.ModifyComponentsEvent event = new ModifyComponents.ModifyComponentsEvent(itemStack, screenWidth, screenHeight, tooltipElements, maxWidth);
                    ModifyComponents.MODIFY_COMPONENTS.invoker().modifyComponents(event);
                    InteractionResult result = event.isCanceled() ? InteractionResult.CONSUME : InteractionResult.PASS;
                    return new RenderTooltipEvents.GatherResult(result, event.maxWidth, event.tooltipElements);
                });
            }
        }
    }

    public static class Ench {
        public static void easyMagicWarn(Consumer<Component> tooltip) {
            if (EASY_MAGIC_LOADED) {
                tooltip.accept(Component.translatable("zenith.easy_magic").withStyle(ChatFormatting.RED));
            }
        }

        public static void easyAnvilsWarn(Consumer<Component> tooltip) {
            if (EASY_ANVILS_LOADED) {
                tooltip.accept(Component.translatable("zenith.easy_anvils").withStyle(ChatFormatting.RED));
            }
        }

        public static boolean isProhibitedSpellEngine(Enchantment ench, ItemStack stack) {
            if (!SPELL_ENGINE_LOADED || stack.is(Items.BOOK)) return false;
            return EnchantmentRestriction.isProhibited(ench, stack);
        }

        public static boolean isPermittedSpellEngine(Enchantment ench, ItemStack stack) {
            if (!SPELL_ENGINE_LOADED) return false;
            if (stack.is(Items.BOOK)) return true;
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

        public static float lifeMendTrinkets(Entity entity, float amount, LifeMendingEnchant ench) {
            if (ZenithModCompat.TRINKETS_LOADED) {
                if (entity instanceof LivingEntity livingEntity) {
                    AtomicReference<Float> atomicAmount = new AtomicReference<>(amount);
                    TrinketsApi.getTrinketComponent(livingEntity).ifPresent(c -> c.forEach((slotReference, stack) -> {
                        atomicAmount.set(ench.lifeMend(atomicAmount.get(), stack));
                    }));
                    amount = atomicAmount.get();
                }
            }
            return amount;
        }
    }
}
