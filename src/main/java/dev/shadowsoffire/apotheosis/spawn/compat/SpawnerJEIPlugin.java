package dev.shadowsoffire.apotheosis.spawn.compat;

/*
@JeiPlugin
public class SpawnerJEIPlugin implements IModPlugin {

    @Override
    public void registerRecipes(IRecipeRegistration reg) {
        if (!Apotheosis.enableSpawner) return;
        List<SpawnerModifier> recipes = new ArrayList<>(Minecraft.getInstance().level.getRecipeManager().getAllRecipesFor(RecipeTypes.MODIFIER));
        recipes.sort((r1, r2) -> r1.getOffhandInput() == Ingredient.EMPTY ? r2.getOffhandInput() == Ingredient.EMPTY ? 0 : -1 : 1);

        reg.addRecipes(SpawnerCategory.TYPE, recipes);
        if (SpawnerModule.spawnerSilkLevel == -1) {
            reg.addIngredientInfo(new ItemStack(Blocks.SPAWNER), VanillaTypes.ITEM_STACK, Component.translatable("info.zenith.spawner.no_silk"));
        }
        else if (SpawnerModule.spawnerSilkLevel == 0) {
            reg.addIngredientInfo(new ItemStack(Blocks.SPAWNER), VanillaTypes.ITEM_STACK, Component.translatable("info.zenith.spawner.always_drop"));
        }
        else reg.addIngredientInfo(new ItemStack(Blocks.SPAWNER), VanillaTypes.ITEM_STACK,
            Component.translatable("info.zenith.spawner", ((MutableComponent) Enchantments.SILK_TOUCH.getFullname(SpawnerModule.spawnerSilkLevel)).withStyle(ChatFormatting.DARK_BLUE).getString()));
        for (Item i : ForgeRegistries.ITEMS) {
            if (i instanceof SpawnEggItem) reg.addIngredientInfo(new ItemStack(i), VanillaTypes.ITEM_STACK, Component.translatable("info.zenith.capturing"));
        }
    }

    @Override
    public void registerRecipeCatalysts(IRecipeCatalystRegistration reg) {
        if (!Apotheosis.enableSpawner) return;
        reg.addRecipeCatalyst(new ItemStack(Blocks.SPAWNER), SpawnerCategory.TYPE);
    }

    @Override
    public void registerCategories(IRecipeCategoryRegistration reg) {
        if (!Apotheosis.enableSpawner) return;
        reg.addRecipeCategories(new SpawnerCategory(reg.getJeiHelpers().getGuiHelper()));
    }

    @Override
    public ResourceLocation getPluginUid() {
        return new ResourceLocation(Apotheosis.MODID, "spawner");
    }

}*/
