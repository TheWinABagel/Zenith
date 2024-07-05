package dev.shadowsoffire.apotheosis.adventure.affix.augmenting;

import dev.shadowsoffire.apotheosis.adventure.Adventure;
import dev.shadowsoffire.apotheosis.adventure.Adventure.Items;
import dev.shadowsoffire.placebo.block_entity.TickingBlockEntity;
import net.fabricmc.fabric.api.transfer.v1.item.InventoryStorage;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

public class AugmentingTableTile extends BlockEntity implements TickingBlockEntity {

    public static int RISE_TIME = 30;
    public static int SPIN_CYCLE_TIME = 90;

    public int time = 0;
    public AnimationStage stage = AnimationStage.HIDING;

    protected SimpleContainer inv = new SimpleContainer(1){
        @Override
        public boolean canAddItem(ItemStack stack) {
            return stack.is(Items.SIGIL_OF_ENHANCEMENT);
        }

        @Override
        public void setChanged() {
            super.setChanged();
            AugmentingTableTile.this.setChanged();
        }
    };

    public InventoryStorage storage = InventoryStorage.of(inv, null);

    public AugmentingTableTile(BlockPos pPos, BlockState pBlockState) {
        super(Adventure.Tiles.AUGMENTING_TABLE, pPos, pBlockState);
    }

    @Override
    public void clientTick(Level pLevel, BlockPos pPos, BlockState pState) {
        Player player = pLevel.getNearestPlayer(pPos.getX() + 0.5D, pPos.getY() + 0.5D, pPos.getZ() + 0.5D, 4, false);
        switch (this.stage) {
            case HIDING -> {
                if (player != null) {
                    this.stage = AnimationStage.RISING;
                    this.time = 0;
                }
            }
            case RISING -> {
                if (player != null) {
                    this.time++;
                    if (this.time >= RISE_TIME) {
                        this.stage = AnimationStage.SPINNING;
                        this.time = 0;
                    }
                }
                else {
                    this.stage = AnimationStage.FALLING;
                    // Keep the same time counter, falling operates on a backwards scale
                }
            }
            case FALLING -> {
                if (player != null) {
                    this.stage = AnimationStage.RISING;
                }
                else {
                    this.time--;
                    if (this.time <= 0) {
                        this.stage = AnimationStage.HIDING;
                        this.time = 0;
                    }
                }
            }
            case SPINNING -> {
                this.time++;
                if (player == null) {
                    if (this.time % SPIN_CYCLE_TIME == 0) {
                        this.stage = AnimationStage.FALLING;
                        this.time = RISE_TIME;
                    }
                }
            }
        }
    }

    @Override
    public void saveAdditional(CompoundTag tag) {
        super.saveAdditional(tag);
        ContainerHelper.saveAllItems(tag, this.inv.items);
    }

    @Override
    public void load(CompoundTag tag) {
        super.load(tag);
        ContainerHelper.loadAllItems(tag, this.inv.items);
    }


    public static enum AnimationStage {
        HIDING,
        RISING,
        FALLING,
        SPINNING;
    }

}