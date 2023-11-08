package dev.shadowsoffire.apotheosis.adventure.affix.reforging;

import dev.shadowsoffire.apotheosis.Apoth;
import dev.shadowsoffire.apotheosis.adventure.Adventure;
import dev.shadowsoffire.apotheosis.adventure.Adventure.Items;
import dev.shadowsoffire.apotheosis.adventure.loot.LootRarity;
import dev.shadowsoffire.apotheosis.adventure.loot.RarityRegistry;
import dev.shadowsoffire.placebo.block_entity.TickingBlockEntity;
import dev.shadowsoffire.placebo.reload.DynamicHolder;
import io.github.fabricators_of_create.porting_lib.transfer.item.ItemStackHandler;
import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerFactory;
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

public class ReforgingTableTile extends BlockEntity implements ExtendedScreenHandlerFactory, TickingBlockEntity {

    public int time = 0;
    public boolean step1 = true;
    protected final BlockPos pos;

    protected ItemStackHandler inv = new ItemStackHandler(2){
        @Override
        public boolean isItemValid(int slot, ItemVariant resource) {
            if (slot == 0) return ReforgingTableTile.this.isValidRarityMat(resource.toStack());
            return resource.toStack().is(Items.GEM_DUST);
        }

        @Override
        protected void onContentsChanged(int slot) {
            ReforgingTableTile.this.setChanged();
        };
    };

    public ReforgingTableTile(BlockPos pPos, BlockState pBlockState) {
        super(Adventure.Tiles.REFORGING_TABLE, pPos, pBlockState);
        this.pos = pPos;
    }

    public LootRarity getMaxRarity() {
        return ((ReforgingTableBlock) this.getBlockState().getBlock()).getMaxRarity();
    }

    public boolean isValidRarityMat(ItemStack stack) {
        DynamicHolder<LootRarity> rarity = RarityRegistry.getMaterialRarity(stack.getItem());
        return rarity.isBound() && this.getMaxRarity().isAtLeast(rarity.get()) && getRecipeFor(rarity.get()) != null;
    }

    @Nullable
    public ReforgingRecipe getRecipeFor(LootRarity rarity) {
        return this.level.getRecipeManager().getAllRecipesFor(Adventure.RecipeTypes.REFORGING).stream().filter(r -> r.rarity().get() == rarity).findFirst().orElse(null);
    }

    @Override
    public void clientTick(Level pLevel, BlockPos pPos, BlockState pState) {
        Player player = pLevel.getNearestPlayer(pPos.getX() + 0.5D, pPos.getY() + 0.5D, pPos.getZ() + 0.5D, 4, false);

        if (player != null) {
            this.time++;
        }
        else {
            if (this.time == 0 && this.step1) return;
            else this.time++;
        }

        if (this.step1 && this.time == 59) {
            this.step1 = false;
            this.time = 0;
        }
        else if (this.time == 4 && !this.step1) {
            RandomSource rand = pLevel.random;
            for (int i = 0; i < 6; i++) {
                pLevel.addParticle(ParticleTypes.CRIT, pPos.getX() + 0.5 - 0.1 * rand.nextDouble(), pPos.getY() + 13 / 16D, pPos.getZ() + 0.5 + 0.1 * rand.nextDouble(), 0, 0, 0);
            }
            pLevel.playLocalSound(pPos.getX(), pPos.getY(), pPos.getZ(), SoundEvents.ANVIL_PLACE, SoundSource.BLOCKS, 0.03F, 1.7F + rand.nextFloat() * 0.2F, true);
            this.step1 = true;
            this.time = 0;
        }
    }

    @Override
    public void saveAdditional(CompoundTag tag) {
        super.saveAdditional(tag);
        tag.put("inventory", this.inv.serializeNBT());
    }

    @Override
    public void load(CompoundTag tag) {
        super.load(tag);
        this.inv.deserializeNBT(tag.getCompound("inventory"));
    }

    public void writeScreenOpeningData(ServerPlayer player, FriendlyByteBuf buf) {
        buf.writeBlockPos(pos);
    }

    @Override
    public Component getDisplayName() {
        return Component.translatable("block.zenith.reforging_table");
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int i, Inventory inventory, Player player) {
        return null;
    }
}
