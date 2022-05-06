package safro.apotheosis.spawn.compat;

import me.shedaniel.rei.api.client.plugins.REIClientPlugin;
import me.shedaniel.rei.api.client.registry.category.CategoryRegistry;
import me.shedaniel.rei.api.client.registry.display.DisplayRegistry;
import me.shedaniel.rei.api.common.util.EntryIngredients;
import net.minecraft.ChatFormatting;
import net.minecraft.core.Registry;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.SpawnEggItem;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Blocks;
import safro.apotheosis.Apotheosis;
import safro.apotheosis.spawn.SpawnerModule;
import safro.apotheosis.spawn.modifiers.SpawnerModifier;
import safro.apotheosis.util.ScreenUtil;

import java.util.List;

public class SpawnerREIPlugin implements REIClientPlugin {

    @Override
    public String getPluginProviderName() {
        return new ResourceLocation(Apotheosis.MODID, "spawner").toString();
    }

    @Override
    public void registerCategories(CategoryRegistry registry) {
        if (!Apotheosis.enableSpawner) return;
        registry.add(new SpawnerCategory());
        registry.addWorkstations(SpawnerDisplay.ID, EntryIngredients.ofItemStacks(stack(Blocks.SPAWNER)));
    }

    @Override
    public void registerDisplays(DisplayRegistry registry) {
        if (!Apotheosis.enableSpawner) return;
        registry.registerFiller(SpawnerModifier.class, SpawnerDisplay::new);

        if (SpawnerModule.spawnerSilkLevel == -1) {
            ScreenUtil.addInfo(registry, Blocks.SPAWNER, "info.apotheosis.spawner.no_silk");
        } else if (SpawnerModule.spawnerSilkLevel == 0) {
            ScreenUtil.addInfo(registry, Blocks.SPAWNER, "info.apotheosis.spawner.always_drop");
        } else ScreenUtil.addInfo(registry, Blocks.SPAWNER, new TranslatableComponent("info.apotheosis.spawner", ((MutableComponent) Enchantments.SILK_TOUCH.getFullname(SpawnerModule.spawnerSilkLevel)).withStyle(ChatFormatting.DARK_BLUE).getString()));
        for (Item i : Registry.ITEM) {
            if (i instanceof SpawnEggItem) ScreenUtil.addInfo(registry, i, "info.apotheosis.capturing");
        }
    }

    public static List<ItemStack> stack(ItemLike item) {
        return List.of(new ItemStack(item));
    }
}
