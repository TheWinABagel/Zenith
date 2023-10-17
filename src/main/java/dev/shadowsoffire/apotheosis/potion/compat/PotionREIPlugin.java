package dev.shadowsoffire.apotheosis.potion.compat;

import dev.shadowsoffire.apotheosis.Apotheosis;
import me.shedaniel.rei.api.client.plugins.REIClientPlugin;
import me.shedaniel.rei.api.client.registry.category.CategoryRegistry;
import me.shedaniel.rei.api.client.registry.category.extension.CategoryExtensionProvider;
import me.shedaniel.rei.api.client.registry.display.DisplayCategory;
import me.shedaniel.rei.api.client.registry.display.DisplayCategoryView;
import me.shedaniel.rei.api.common.entry.comparison.ComparisonContext;
import me.shedaniel.rei.api.common.entry.comparison.EntryComparator;
import me.shedaniel.rei.api.common.entry.comparison.ItemComparatorRegistry;
import me.shedaniel.rei.api.common.plugins.PluginManager;
import me.shedaniel.rei.api.common.registry.ReloadStage;
import me.shedaniel.rei.plugin.common.BuiltinPlugin;
import me.shedaniel.rei.plugin.common.displays.crafting.DefaultCraftingDisplay;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.item.alchemy.PotionUtils;

import java.util.function.Function;

public class PotionREIPlugin implements REIClientPlugin {



//    @Override
 //   public void registerItemComparators(ItemComparatorRegistry registry) {
    /*    EntryComparator<Tag> nbtHasher = EntryComparator.nbt();
        EntryComparator<ItemStack> charm = (context, stack) -> {
            if (!(stack.getItem() instanceof PotionCharmItem)) {
                return 0;

            //    return nbtHasher.hash(context, tag);
            }
            //PotionModule.LOGGER.warn("isExact: {}", context.isExact());
            CompoundTag tag = stack.getTag();
            long test = nbtHasher.hash(context, tag);
            PotionModule.LOGGER.warn("hash: {}", test);
            return test;
        };
            registry.register(charm, PotionModule.POTION_CHARM);*/
    /*    EntryComparator<Tag> nbtHasher = EntryComparator.nbt();
        Function<ItemStack, Tag> charm = stack -> {
            CompoundTag tag = stack.getTag();
            if (!PotionCharmItem.hasPotion(stack)) return null;
            Potion p = PotionUtils.getPotion(stack);
            MobEffectInstance contained = p.getEffects().get(0);
            PotionModule.LOGGER.warn("normal return, tag: {}", tag);

            return tag.get("Potion");
        };
        registry.register((context, stack) -> {
            var test = nbtHasher.hash(context, charm.apply(stack));
            PotionModule.LOGGER.warn("hash: {}", test);
            return test;
        }, PotionModule.POTION_CHARM);*/
        /*
        EntryComparator<ItemStack> charmProvider = ((context, stack) -> {

            Potion p = PotionUtils.getPotion(stack);
            MobEffectInstance contained = p.getEffects().get(0);


            return 0;
        });*/

    //registry.registerNbt(PotionModule.POTION_CHARM);

  //  }

    @Override
    public void postStage(PluginManager<REIClientPlugin> manager, ReloadStage stage) {
        if (!Apotheosis.enablePotion) return;
        if (stage != ReloadStage.END || !manager.equals(PluginManager.getClientInstance())) return;
    //    CategoryRegistry.getInstance().get(BuiltinPlugin.CRAFTING).registerExtension(new PotionJEIExtension());

    }

}
