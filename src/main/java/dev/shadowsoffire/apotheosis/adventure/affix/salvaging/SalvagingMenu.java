package dev.shadowsoffire.apotheosis.adventure.affix.salvaging;

import dev.shadowsoffire.apotheosis.adventure.Adventure;
import dev.shadowsoffire.apotheosis.adventure.Adventure.Blocks;
import dev.shadowsoffire.apotheosis.adventure.Adventure.Menus;
import dev.shadowsoffire.apotheosis.adventure.affix.salvaging.SalvagingRecipe.OutputData;
import dev.shadowsoffire.placebo.menu.PlaceboContainerMenu;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class SalvagingMenu extends PlaceboContainerMenu {

    protected final Player player;
    protected final BlockPos pos;
    protected final SalvagingTableTile tile;

    protected final SimpleContainer inputInventory = new SimpleContainer(15) {
        @Override
        public int getMaxStackSize() {
            return 1;
        }

        @Override
        public void setChanged() {
            SalvagingMenu.this.tile.setChanged();
        }
    };

    public SalvagingMenu(int id, Inventory inv, FriendlyByteBuf buf) {
        this(id, inv, buf.readBlockPos());
    }

    public SalvagingMenu(int id, Inventory inv, BlockPos pos) {
        super(Menus.SALVAGE, id, inv);
        this.player = inv.player;
        this.pos = pos;
        this.tile = (SalvagingTableTile) this.level.getBlockEntity(pos);
        for (int i = 0; i < 15; i++) {
            this.addSlot(new Slot(this.inputInventory, i, 8 + i % 5 * 18, 17 + i / 5 * 18) {
                @Override
                public boolean mayPlace(ItemStack stack) {
                    return findMatch(SalvagingMenu.this.level, stack) != null;
                }

                @Override
                public void setChanged() {
                    SalvagingMenu.this.slotsChanged(SalvagingMenu.this.inputInventory);
                    super.setChanged();
                }
            });
        }

        for (int i = 0; i < 6; i++) {
            this.addSlot(new Slot(this.tile.container, i, 134 + i % 2 * 18, 17 + i / 2 * 18) {
                @Override
                public boolean mayPlace(ItemStack stack) {
                    return false;
                }
            });
        }

        this.addPlayerSlots(inv, 8, 84);
        this.mover.registerRule((stack, slot) -> slot >= this.playerInvStart && findMatch(this.level, stack) != null, 0, 15);
        this.mover.registerRule((stack, slot) -> slot < this.playerInvStart, this.playerInvStart, this.hotbarStart + 9);
        this.registerInvShuffleRules();
    }

    @Override
    public boolean stillValid(Player player) {
        if (this.level.isClientSide) return true;
        return this.level.getBlockState(this.pos).getBlock() == Blocks.SALVAGING_TABLE;
    }

    @Override
    public void removed(Player player) {
        super.removed(player);
        if (!this.level.isClientSide) {
            this.clearContainer(player, this.inputInventory);
        }
    }

    @Override
    public boolean clickMenuButton(Player player, int id) {
        if (id == 0) {
            this.salvageAll();
            this.level.playSound(null, player.blockPosition(), SoundEvents.EVOKER_CAST_SPELL, SoundSource.BLOCKS, 0.99F, this.level.random.nextFloat() * 0.25F + 1F);
            this.level.playSound(null, player.blockPosition(), SoundEvents.AMETHYST_CLUSTER_STEP, SoundSource.BLOCKS, 0.34F, this.level.random.nextFloat() * 0.2F + 0.8F);
            this.level.playSound(null, player.blockPosition(), SoundEvents.SMITHING_TABLE_USE, SoundSource.BLOCKS, 0.45F, this.level.random.nextFloat() * 0.5F + 0.75F);
            return true;
        }
        return super.clickMenuButton(player, id);
    }

    protected void giveItem(Player player, ItemStack stack) {
        if (!player.isAlive() || player instanceof ServerPlayer && ((ServerPlayer) player).hasDisconnected()) {
            player.drop(stack, false);
        }
        else {
            Inventory inventory = player.getInventory();
            if (inventory.player instanceof ServerPlayer) {
                inventory.placeItemBackInInventory(stack);
            }
        }
    }

    protected void salvageAll() {
        for (int inSlot = 0; inSlot < 15; inSlot++) {
            Slot s = this.getSlot(inSlot);
            ItemStack stack = s.getItem();
            List<ItemStack> outputs = salvageItem(this.level, stack);
            s.set(ItemStack.EMPTY);
            for (ItemStack out : outputs) {
                for (int outSlot = 0; outSlot < 6; outSlot++) {
                    if (out.isEmpty()) break;
                    out = this.tile.container.addItem(out);
                }
                if (!out.isEmpty()) this.giveItem(this.player, out);
            }
        }
    }

    public static int getSalvageCount(OutputData output, ItemStack stack, RandomSource rand) {
        int[] counts = getSalvageCounts(output, stack);
        return rand.nextInt(counts[0], counts[1] + 1);
    }

    public static int[] getSalvageCounts(OutputData output, ItemStack stack) {
        int[] out = { output.min, output.max };
        if (stack.isDamageableItem()) {
            out[1] = Math.max(out[0], Math.round((float) (out[1] * (stack.getMaxDamage() - stack.getDamageValue())) / stack.getMaxDamage()));
        }
        return out;
    }

    public static List<ItemStack> salvageItem(Level level, ItemStack stack) {
        var recipe = findMatch(level, stack);
        if (recipe == null) return Collections.emptyList();
        List<ItemStack> outputs = new ArrayList<>();
        for (OutputData d : recipe.getOutputs()) {
            ItemStack out = d.stack.copy();
            out.setCount(getSalvageCount(d, stack, level.random));
            outputs.add(out);
        }
        return outputs;
    }

    public static List<ItemStack> getBestPossibleSalvageResults(Level level, ItemStack stack) {
        var recipe = findMatch(level, stack);
        if (recipe == null) return Collections.emptyList();
        List<ItemStack> outputs = new ArrayList<>();
        for (OutputData d : recipe.getOutputs()) {
            ItemStack out = d.stack.copy();
            out.setCount(getSalvageCounts(d, stack)[1]);
            outputs.add(out);
        }
        return outputs;
    }

    @Nullable
    public static SalvagingRecipe findMatch(Level level, ItemStack stack) {
        for (var recipe : level.getRecipeManager().getAllRecipesFor(Adventure.RecipeTypes.SALVAGING)) {
            if (recipe.matches(stack)) return recipe;
        }
        return null;
    }
}
