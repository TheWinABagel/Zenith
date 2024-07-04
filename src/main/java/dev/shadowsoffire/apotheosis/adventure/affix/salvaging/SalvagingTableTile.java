package dev.shadowsoffire.apotheosis.adventure.affix.salvaging;

import dev.shadowsoffire.apotheosis.adventure.Adventure;
import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerFactory;
import net.fabricmc.fabric.api.transfer.v1.item.InventoryStorage;
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.fabricmc.fabric.api.transfer.v1.item.base.SingleStackStorage;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.fabricmc.fabric.api.transfer.v1.storage.base.CombinedStorage;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.SimpleContainer;
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

    @Override
    protected void saveAdditional(CompoundTag tag) {
        ContainerHelper.saveAllItems(tag, this.output.items);
        super.saveAdditional(tag);
    }

    @Override
    public void load(CompoundTag tag) {
        ContainerHelper.loadAllItems(tag, this.output.items);
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

    protected SimpleContainer output = new SimpleContainer(6) {
        @Override
        public void setChanged() {
            SalvagingTableTile.this.setChanged();
        }

        @Override
        public boolean canPlaceItem(int index, ItemStack stack) {
            return false;
        }

    };
    /**
     * Output inventory, as reflected in the container menu.
     */
    public final InventoryStorage outputStorage = InventoryStorage.of(output, null);

    public SingleStackStorage inputStorage = new SingleStackStorage() {

        @Override
        protected ItemStack getStack() {
            return ItemStack.EMPTY;
        }

        @Override
        protected void setStack(ItemStack stack) {
            List<ItemStack> outputs = SalvagingMenu.getBestPossibleSalvageResults(SalvagingTableTile.this.level, stack);
            if (outputs.isEmpty()) return;
            for (ItemStack stack2 : outputs) {
                if (canAddItemWithStackSize(stack2)) {
                    SalvagingTableTile.this.output.addItem(stack2);
                }
            }
        }

        @Override
        protected boolean canInsert(ItemVariant itemVariant) {
            List<ItemStack> outputs = SalvagingMenu.getBestPossibleSalvageResults(SalvagingTableTile.this.level, itemVariant.toStack());
            int i = 0;
            for (ItemStack outputStack : outputs) {
                if (!canAddItemWithStackSize(outputStack)) i++;
            }
            return !outputs.isEmpty() && i == 0;
        }

        @Override
        public boolean supportsExtraction() {
            return false;
        }

        @Override
        protected void onFinalCommit() {
            SalvagingTableTile.this.setChanged();
            super.onFinalCommit();
        }
        private boolean canAddItemWithStackSize(ItemStack stack) {
            boolean canAdd = false;
            for (ItemStack itemStack : SalvagingTableTile.this.output.items) {
                if (itemStack.isEmpty() || ItemStack.isSameItemSameTags(itemStack, stack) && !(itemStack.getCount() + stack.getCount() > itemStack.getMaxStackSize())) {
                    canAdd = true;
                    break;
                }
            }
            return canAdd;
        }
    };
    public Storage<ItemVariant> combinedStorage = new CombinedStorage<>(List.of(outputStorage, inputStorage));
}
