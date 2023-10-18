package dev.shadowsoffire.apotheosis.adventure.affix.salvaging;

import dev.shadowsoffire.apotheosis.Apoth;
import dev.shadowsoffire.apotheosis.adventure.Adventure;
import dev.shadowsoffire.placebo.cap.InternalItemHandler;
import io.github.fabricators_of_create.porting_lib.transfer.item.ItemStackHandler;
import io.github.fabricators_of_create.porting_lib.util.LazyOptional;
import it.unimi.dsi.fastutil.ints.IntOpenHashSet;
import it.unimi.dsi.fastutil.ints.IntSet;
import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerFactory;
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.fabricmc.fabric.api.transfer.v1.transaction.TransactionContext;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class SalvagingTableTile extends BlockEntity implements ExtendedScreenHandlerFactory {

    public SalvagingTableTile(BlockPos pPos, BlockState pBlockState) {
        super(Adventure.Tiles.SALVAGING_TABLE, pPos, pBlockState);
        this.pos = pPos;
    }

    protected final BlockPos pos;

    /**
     * "Real" output inventory, as reflected in the container menu.
     */
    protected final InternalItemHandler output = new InternalItemHandler(6);

    /**
     * External-facing inventory handler, which automatically salvages input items.
     */
    protected final LazyOptional<SalvagingItemHandler> itemHandler = LazyOptional.of(SalvagingItemHandler::new);

    @Override
    protected void saveAdditional(CompoundTag tag) {
        tag.put("output", this.output.serializeNBT());
        super.saveAdditional(tag);
    }

    @Override
    public void load(CompoundTag tag) {
        if (tag.contains("output")) this.output.deserializeNBT(tag.getCompound("output"));
        super.load(tag);
    }

    @Override
    public void writeScreenOpeningData(ServerPlayer player, FriendlyByteBuf buf) {
        buf.writeBlockPos(pos);
    }

    @Override
    public Component getDisplayName() {
        return Component.translatable("block.zenith.salvaging_table");
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int i, Inventory inventory, Player player) {
        return new SalvagingMenu(i, inventory, pos);
    }

    protected class SalvagingItemHandler extends ItemStackHandler {
        @Override
        public int getSlotCount() {
            return 1 + SalvagingTableTile.this.output.getSlotCount();
        }

        @Override
        public ItemStack getStackInSlot(int slot) {
            if (slot == 0) return ItemStack.EMPTY;
            else return SalvagingTableTile.this.output.getStackInSlot(slot - 1);
        }

        @Override
        public long insertSlot(int slot, ItemVariant resource, long maxAmount, TransactionContext transaction) {
            ItemStack stack = resource.toStack();
            if (slot != 0) return 0;
            List<ItemStack> outputs = SalvagingMenu.getBestPossibleSalvageResults(SalvagingTableTile.this.level, stack);
            if (outputs.isEmpty()) return 0;
            IntSet skipSlots = new IntOpenHashSet();
            // Simulate inserting all outputs.
            for (ItemStack out : outputs) {
                // I've made an assumption with this logic that a Salvaging Recipe won't have two stacks with the same item in the output.
                // Thus, if the size changes, we can assume that part of that stack fit in that slot, and that any further insertions would fail.
                for (int i = 0; i < 6; i++) {
                    if (skipSlots.contains(i)) continue;
                    int size = out.getCount();
            //        out = SalvagingTableTile.this.output.insertItem(i, out, true); // Always simulate during this check.
                    if (size != out.getCount()) skipSlots.add(i);
                    if (out.isEmpty()) break;
                }
                if (!out.isEmpty()) return 0; // If any output fails to insert to the output inventory, we abort.
            }
            // Now, if we passed the checks we aren't simulating, do the actual insertion.
        //    if (!simulate) {
                for (ItemStack out : outputs) {
                    for (int i = 0; i < 6; i++) {
                //        out = SalvagingTableTile.this.output.insert(i, ItemVariant.of(out), 64, transaction);
                        if (out.isEmpty()) break;
                    }
                    if (!out.isEmpty()) return 0; // If any output fails to insert to the output inventory, we abort.
        //        }
            }
            return 1;

        //    return super.insertSlot(slot, resource, maxAmount, transaction);
        }

        @Override
        public long extractSlot(int slot, ItemVariant resource, long amount, TransactionContext transaction) {
            if (slot == 0) return 1;
            return SalvagingTableTile.this.output.extractSlot(slot - 1, resource, amount, transaction);
        }

        @Override
        public int getSlotLimit(int slot) {
            if (slot == 0) return 1;
            return SalvagingTableTile.this.output.getSlotLimit(slot - 1);
        }

        @Override
        public boolean isItemValid(int slot, ItemVariant resource) {
            if (slot == 0) return SalvagingMenu.findMatch(SalvagingTableTile.this.level, resource.toStack()) != null;
            return false;
        }

    }
/*    protected class SalvagingItemHandler implements ItemStackHandler {

        @Override
        public int getSlots() {
            return 1 + SalvagingTableTile.this.output.getSlots();
        }

        @Override
        public ItemStack getStackInSlot(int slot) {
            if (slot == 0) return ItemStack.EMPTY;
            else return SalvagingTableTile.this.output.getStackInSlot(slot - 1);
        }

        @Override
        public ItemStack insertItem(int slot, ItemStack stack, boolean simulate) {
            if (slot != 0) return stack;
            List<ItemStack> outputs = SalvagingMenu.getBestPossibleSalvageResults(SalvagingTableTile.this.level, stack);
            if (outputs.isEmpty()) return stack;
            IntSet skipSlots = new IntOpenHashSet();
            // Simulate inserting all outputs.
            for (ItemStack out : outputs) {
                // I've made an assumption with this logic that a Salvaging Recipe won't have two stacks with the same item in the output.
                // Thus, if the size changes, we can assume that part of that stack fit in that slot, and that any further insertions would fail.
                for (int i = 0; i < 6; i++) {
                    if (skipSlots.contains(i)) continue;
                    int size = out.getCount();
                    out = SalvagingTableTile.this.output.insertItem(i, out, true); // Always simulate during this check.
                    if (size != out.getCount()) skipSlots.add(i);
                    if (out.isEmpty()) break;
                }
                if (!out.isEmpty()) return stack; // If any output fails to insert to the output inventory, we abort.
            }
            // Now, if we passed the checks we aren't simulating, do the actual insertion.
            if (!simulate) {
                for (ItemStack out : outputs) {
                    for (int i = 0; i < 6; i++) {
                        out = SalvagingTableTile.this.output.insertItem(i, out, false);
                        if (out.isEmpty()) break;
                    }
                    if (!out.isEmpty()) return stack; // If any output fails to insert to the output inventory, we abort.
                }
            }
            return ItemStack.EMPTY;
        }

        @Override
        public ItemStack extractItem(int slot, int amount, boolean simulate) {
            if (slot == 0) return ItemStack.EMPTY;
            return SalvagingTableTile.this.output.extractItem(slot - 1, amount, simulate);
        }

        @Override
        public int getSlotLimit(int slot) {
            if (slot == 0) return 1;
            return SalvagingTableTile.this.output.getSlotLimit(slot - 1);
        }

        @Override
        public boolean isItemValid(int slot, ItemStack stack) {
            if (slot == 0) return SalvagingMenu.findMatch(SalvagingTableTile.this.level, stack) != null;
            return false;
        }

    }
*/
}
