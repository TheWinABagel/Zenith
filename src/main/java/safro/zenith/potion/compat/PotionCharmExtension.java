package safro.zenith.potion.compat;

/*
@SuppressWarnings("UnstableApiUsage")
public class PotionCharmExtension implements CategoryExtensionProvider<DefaultCraftingDisplay<?>> {

    @Override
    public DisplayCategoryView<DefaultCraftingDisplay<?>> provide(DefaultCraftingDisplay<?> display, DisplayCategory<DefaultCraftingDisplay<?>> category, DisplayCategoryView<DefaultCraftingDisplay<?>> lastView) {
        if (display.getOptionalRecipe().isPresent()) {
            Recipe<?> recipe = display.getOptionalRecipe().get();
            Potion potion = PotionUtils.getPotion(focuses.getFocuses(VanillaTypes.ITEM).findFirst().map(IFocus::getTypedValue).map(ITypedIngredient::getIngredient).orElse(ItemStack.EMPTY));
            List<List<ItemStack>> recipeInputs = recipe.getIngredients().stream().map(i -> Arrays.asList(i.getItems())).collect(Collectors.toCollection(ArrayList::new));
            if (potion != Potions.EMPTY) {
                for (int i : recipe.getPotionSlots()) {
                    recipeInputs.set(i, Arrays.asList(PotionUtils.setPotion(new ItemStack(Items.POTION), potion)));
                }
            }
            ItemStack output = new ItemStack(PotionModule.POTION_CHARM);
            PotionUtils.setPotion(output, potion);
            craftingGridHelper.setInputs(builder, VanillaTypes.ITEM, recipeInputs, size.width, size.height);
            if (potion != Potions.EMPTY) {
                craftingGridHelper.setOutputs(builder, VanillaTypes.ITEM, Arrays.asList(output));
            } else {
                List<ItemStack> potionStacks = new ArrayList<>();
                for (Potion p : Registry.POTION) {
                    if (p.getEffects().size() != 1 || p.getEffects().get(0).getEffect().isInstantenous()) continue;
                    ItemStack charm = new ItemStack(PotionModule.POTION_CHARM);
                    PotionUtils.setPotion(charm, p);
                    potionStacks.add(charm);
                }
                craftingGridHelper.setOutputs(builder, VanillaTypes.ITEM, potionStacks);
            }
        }
        return lastView;
    }
}
*/