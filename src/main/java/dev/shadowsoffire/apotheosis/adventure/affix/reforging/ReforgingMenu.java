package dev.shadowsoffire.apotheosis.adventure.affix.reforging;

import blue.endless.jankson.annotation.Nullable;
import dev.shadowsoffire.apotheosis.adventure.Adventure;
import dev.shadowsoffire.apotheosis.adventure.Adventure.Items;
import dev.shadowsoffire.apotheosis.adventure.Adventure.Menus;
import dev.shadowsoffire.apotheosis.adventure.loot.LootCategory;
import dev.shadowsoffire.apotheosis.adventure.loot.LootController;
import dev.shadowsoffire.apotheosis.adventure.loot.LootRarity;
import dev.shadowsoffire.apotheosis.adventure.loot.RarityRegistry;
import dev.shadowsoffire.apotheosis.cca.ZenithComponents;
import dev.shadowsoffire.apotheosis.util.ApothMiscUtil;
import dev.shadowsoffire.placebo.menu.PlaceboContainerMenu;
import dev.shadowsoffire.placebo.util.EnchantmentUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.RandomSource;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.DataSlot;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.levelgen.XoroshiroRandomSource;
import org.jetbrains.annotations.NotNull;


public class ReforgingMenu extends PlaceboContainerMenu {

    public static final String REFORGE_SEED = "apoth_reforge_seed";
    protected final BlockPos pos;
    protected final ReforgingTableTile tile;
    protected final Player player;
    protected SimpleContainer itemInventory = new SimpleContainer(1);
    protected SimpleContainer choicesInv = new SimpleContainer(3);

    protected final RandomSource random = new XoroshiroRandomSource(0);
    protected final int[] costs = new int[3];
    protected int seed = -1;

    public ReforgingMenu(int id, Inventory inv, FriendlyByteBuf buf) {
        this(id, inv, buf.readBlockPos());
    }

    public ReforgingMenu(int id, Inventory inv, BlockPos pos) {
        super(Menus.REFORGING, id, inv);
        this.player = inv.player;
        this.pos = pos;
        this.tile = (ReforgingTableTile) this.level.getBlockEntity(pos);
        this.addSlot(new UpdatingSlot(this.itemInventory, 0, 81, 62, stack -> !LootCategory.forItem(stack).isNone()){
            @Override
            public int getMaxStackSize() {
                return 1;
            }

            @Override
            public int getMaxStackSize(ItemStack stack) {
                return 1;
            }
        });
        this.addSlot(new UpdatingSlot(this.tile.inventory, 0, 39, 40, this.tile::isValidRarityMat));
        this.addSlot(new UpdatingSlot(this.tile.inventory, 1, 123, 86, stack -> stack.getItem() == Items.SIGIL_OF_REBIRTH));
        this.addSlot(new ReforgingResultSlot(this.choicesInv, 0, 27, 135));
        this.addSlot(new ReforgingResultSlot(this.choicesInv, 1, 81, 135));
        this.addSlot(new ReforgingResultSlot(this.choicesInv, 2, 135, 135));
        this.addPlayerSlots(inv, 8, 184);

        this.mover.registerRule((stack, slot) -> slot >= this.playerInvStart && !LootCategory.forItem(stack).isNone(), 0, 1);
        this.mover.registerRule((stack, slot) -> slot >= this.playerInvStart && this.tile.isValidRarityMat(stack), 1, 2);
        this.mover.registerRule((stack, slot) -> slot >= this.playerInvStart && stack.getItem() == Items.SIGIL_OF_REBIRTH, 2, 3);
        this.mover.registerRule((stack, slot) -> slot < this.playerInvStart, this.playerInvStart, this.hotbarStart + 9);
        this.registerInvShuffleRules();

        this.updateSeed();
        this.addDataSlot(DataSlot.shared(this.costs, 0));
        this.addDataSlot(DataSlot.shared(this.costs, 1));
        this.addDataSlot(DataSlot.shared(this.costs, 2));
    }

    @Override
    public void removed(Player player) {
        super.removed(player);
        this.clearContainer(player, this.itemInventory);
    }

    protected void updateSeed() {
        if (this.player.getCustomData().contains(REFORGE_SEED)) {
            this.player.getCustomData().remove(REFORGE_SEED);
        }
        int seed = ZenithComponents.REFORGING_SEED.get(this.player).getValue();
        if (seed == 0) {
            seed = this.player.getRandom().nextInt();
            ZenithComponents.REFORGING_SEED.get(this.player).setValue(seed);
        }
        this.seed = seed;
    }

    public int getMatCount() {
        return this.getSlot(1).getItem().getCount();
    }

    public int getSigilCount() {
        return this.getSlot(2).getItem().getCount();
    }

    @Nullable
    public LootRarity getRarity() {
        ItemStack s = this.getSlot(1).getItem();
        if (s.isEmpty()) return null;
        return RarityRegistry.getMaterialRarity(s.getItem()).getOptional().orElse(null);
    }

    public int getSigilCost(int slot) {
        return this.costs[0] * ++slot;
    }

    public int getMatCost(int slot) {
        return this.costs[1] * ++slot;
    }

    public int getLevelCost(int slot) {
        return this.costs[2] * ++slot;
    }

    @Override
    public void slotsChanged(Container pContainer) {
        LootRarity rarity = this.getRarity();
        if (rarity != null) {
            ReforgingRecipe recipe = this.tile.getRecipeFor(rarity);
            if (recipe != null) {
                this.costs[0] = recipe.sigilCost();
                this.costs[1] = recipe.matCost();
                this.costs[2] = recipe.levelCost();
            }
        }

        ItemStack input = this.getSlot(0).getItem();
        for (int slot = 0; slot < 3; slot++) {
            if (!input.isEmpty() && rarity != null) {
                RandomSource rand = this.random;
                rand.setSeed(this.seed ^ BuiltInRegistries.ITEM.getKey(input.getItem()).hashCode() + slot);
                ItemStack output = LootController.createLootItem(input.copy(), rarity, rand);
                this.choicesInv.setItem(slot, output);
            }
            else {
                this.choicesInv.setItem(slot, ItemStack.EMPTY);
            }
        }
        super.slotsChanged(pContainer);
        this.tile.setChanged();
    }

    @Override
    public boolean stillValid(Player player) {
        if (this.level.isClientSide) return true;
        return this.level.getBlockState(this.pos).getBlock() == Adventure.Blocks.REFORGING_TABLE || this.level.getBlockState(this.pos).getBlock() == Adventure.Blocks.SIMPLE_REFORGING_TABLE;
    }
    public class ReforgingResultSlot extends Slot {

        public ReforgingResultSlot(Container itemHandler, int index, int xPosition, int yPosition) {
            super(itemHandler, index, xPosition, yPosition);
        }

        @Override
        public boolean mayPlace(@NotNull ItemStack stack) {
            return false;
        }

        @Override
        public boolean mayPickup(Player playerIn) {
            ItemStack input = ReforgingMenu.this.getSlot(0).getItem();
            LootRarity rarity = ReforgingMenu.this.getRarity();
            ReforgingRecipe recipe = ReforgingMenu.this.tile.getRecipeFor(rarity);
            if (recipe == null || input.isEmpty()) return false;

            int sigils = ReforgingMenu.this.getSigilCount();
            int sigilCost = ReforgingMenu.this.getSigilCost(this.getSlotIndex());
            int mats = ReforgingMenu.this.getMatCount();
            int matCost = ReforgingMenu.this.getMatCost(this.getSlotIndex());
            int levels = ReforgingMenu.this.player.experienceLevel;
            int levelCost = ReforgingMenu.this.getLevelCost(this.getSlotIndex());

            if ((sigils < sigilCost || mats < matCost || levels < levelCost) && !ReforgingMenu.this.player.isCreative()) return false;

            return super.mayPickup(playerIn);
        }

        @Override
        public void onTake(Player player, ItemStack stack) {
            if (!player.level().isClientSide) {
                ReforgingMenu.this.getSlot(0).set(ItemStack.EMPTY);
                if (!player.isCreative()) {
                    int sigilCost = ReforgingMenu.this.getSigilCost(this.getSlotIndex());
                    int matCost = ReforgingMenu.this.getMatCost(this.getSlotIndex());
                    int levelCost = ReforgingMenu.this.getLevelCost(this.getSlotIndex());
                    ReforgingMenu.this.getSlot(1).getItem().shrink(matCost);
                    ReforgingMenu.this.getSlot(2).getItem().shrink(sigilCost);
                    EnchantmentUtils.chargeExperience(player, ApothMiscUtil.getExpCostForSlot(levelCost, this.getSlotIndex()));
                }
                ZenithComponents.REFORGING_SEED.get(player).setValue(player.getRandom().nextInt());
                ReforgingMenu.this.updateSeed();
            }

            player.playSound(SoundEvents.EVOKER_CAST_SPELL, 0.99F, player.level().random.nextFloat() * 0.25F + 1F);
            player.playSound(SoundEvents.AMETHYST_CLUSTER_STEP, 0.34F, player.level().random.nextFloat() * 0.2F + 0.8F);
            player.playSound(SoundEvents.SMITHING_TABLE_USE, 0.45F, player.level().random.nextFloat() * 0.5F + 0.75F);
        }
    }


}
