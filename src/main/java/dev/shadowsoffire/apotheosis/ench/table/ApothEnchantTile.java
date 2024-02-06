package dev.shadowsoffire.apotheosis.ench.table;

import io.github.fabricators_of_create.porting_lib.tags.Tags;
import net.fabricmc.fabric.api.transfer.v1.item.InventoryStorage;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.EnchantmentTableBlockEntity;
import net.minecraft.world.level.block.state.BlockState;


public class ApothEnchantTile extends EnchantmentTableBlockEntity {

    public SimpleContainer inventory = new SimpleContainer(1) {
        @Override
        public void setChanged() {
            super.setChanged();
            ApothEnchantTile.this.setChanged();
        }

        @Override
        public boolean canPlaceItem(int index, ItemStack stack) {
            return stack.is(Tags.Items.ENCHANTING_FUELS);
        }
    };

    public InventoryStorage container = InventoryStorage.of(inventory, null);

    public ApothEnchantTile(BlockPos pos, BlockState state) {
        super(pos, state);
    }



    @Override
    public void saveAdditional(CompoundTag tag) {
        super.saveAdditional(tag);
        ContainerHelper.saveAllItems(tag, inventory.items);
    }


    @Override
    public void load(CompoundTag tag) {
        ContainerHelper.loadAllItems(tag, inventory.items);
        super.load(tag);
    }


}
