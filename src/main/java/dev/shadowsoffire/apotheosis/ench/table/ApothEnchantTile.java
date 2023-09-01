package dev.shadowsoffire.apotheosis.ench.table;

import io.github.fabricators_of_create.porting_lib.tags.Tags;
import io.github.fabricators_of_create.porting_lib.transfer.item.ItemStackHandler;
import io.github.fabricators_of_create.porting_lib.util.LazyOptional;
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.EnchantmentTableBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;


public class ApothEnchantTile extends EnchantmentTableBlockEntity {

    public ItemStackHandler inv = new ItemStackHandler(1){
        @Override
        public boolean isItemValid(int slot, ItemVariant resource) {

            return resource.toStack().is(Tags.Items.ENCHANTING_FUELS);
        }

        @Override
        protected void onContentsChanged(int slot) {
            ApothEnchantTile.this.setChanged();
        }
    };

    public ApothEnchantTile(BlockPos pos, BlockState state) {
        super(pos, state);
    }



    @Override
    public void saveAdditional(CompoundTag tag) {
        super.saveAdditional(tag);
        tag.put("inventory", this.inv.serializeNBT());
    }


    @Override
    public void load(CompoundTag tag) {
        this.inv.deserializeNBT(tag.getCompound("inventory"));
        super.load(tag);
    }


}
