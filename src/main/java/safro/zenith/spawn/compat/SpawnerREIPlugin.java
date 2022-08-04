package safro.zenith.spawn.compat;

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
import safro.zenith.Zenith;
import safro.zenith.spawn.SpawnerModule;
import safro.zenith.spawn.modifiers.SpawnerModifier;
import safro.zenith.util.ClientUtil;

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
            ClientUtil.addInfo(registry, Blocks.SPAWNER, "info.apotheosis.spawner.no_silk");
        } else if (SpawnerModule.spawnerSilkLevel == 0) {
            ClientUtil.addInfo(registry, Blocks.SPAWNER, "info.apotheosis.spawner.always_drop");
        } else ClientUtil.addInfo(registry, Blocks.SPAWNER, new TranslatableComponent("info.apotheosis.spawner", ((MutableComponent) Enchantments.SILK_TOUCH.getFullname(SpawnerModule.spawnerSilkLevel)).withStyle(ChatFormatting.DARK_BLUE).getString()));
        for (Item i : Registry.ITEM) {
            if (i instanceof SpawnEggItem) ClientUtil.addInfo(registry, i, "info.apotheosis.capturing");
        }
    }

    public static List<ItemStack> stack(ItemLike item) {
        return List.of(new ItemStack(item));
    }
}
