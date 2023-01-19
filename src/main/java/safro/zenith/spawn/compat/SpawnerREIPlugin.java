package safro.zenith.spawn.compat;

import me.shedaniel.rei.api.client.plugins.REIClientPlugin;
import me.shedaniel.rei.api.client.registry.category.CategoryRegistry;
import me.shedaniel.rei.api.client.registry.display.DisplayRegistry;
import me.shedaniel.rei.api.common.util.EntryIngredients;
import net.minecraft.ChatFormatting;
import net.minecraft.core.Registry;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.SpawnEggItem;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Blocks;
import safro.zenith.Zenith;
import safro.zenith.spawn.SpawnerModule;
import safro.zenith.spawn.modifiers.SpawnerModifier;
import safro.zenith.util.REIUtil;

import java.util.List;

public class SpawnerREIPlugin implements REIClientPlugin {

    @Override
    public String getPluginProviderName() {
        return new ResourceLocation(Zenith.MODID, "spawner").toString();
    }

    @Override
    public void registerCategories(CategoryRegistry registry) {
        if (!Zenith.enableSpawner) return;
        registry.add(new SpawnerCategory());
        registry.addWorkstations(SpawnerDisplay.ID, EntryIngredients.ofItemStacks(stack(Blocks.SPAWNER)));
    }

    @Override
    public void registerDisplays(DisplayRegistry registry) {
        if (!Zenith.enableSpawner) return;
        registry.registerFiller(SpawnerModifier.class, SpawnerDisplay::new);

        if (SpawnerModule.spawnerSilkLevel == -1) {
            REIUtil.addInfo(registry, Blocks.SPAWNER, "info.zenith.spawner.no_silk");
        } else if (SpawnerModule.spawnerSilkLevel == 0) {
            REIUtil.addInfo(registry, Blocks.SPAWNER, "info.zenith.spawner.always_drop");
        } else REIUtil.addInfo(registry, Blocks.SPAWNER, Component.translatable("info.zenith.spawner", ((MutableComponent) Enchantments.SILK_TOUCH.getFullname(SpawnerModule.spawnerSilkLevel)).withStyle(ChatFormatting.DARK_BLUE).getString()));
        for (Item i : Registry.ITEM) {
            if (i instanceof SpawnEggItem) REIUtil.addInfo(registry, i, "info.zenith.capturing");
        }
    }

    public static List<ItemStack> stack(ItemLike item) {
        return List.of(new ItemStack(item));
    }
}
