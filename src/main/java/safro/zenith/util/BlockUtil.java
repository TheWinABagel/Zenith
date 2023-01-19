package safro.zenith.util;

import com.mojang.authlib.GameProfile;
import io.github.fabricators_of_create.porting_lib.fake_players.FakePlayer;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;
import safro.zenith.Zenith;

import javax.annotation.Nullable;
import java.util.UUID;

public class BlockUtil {
    /**
     * Vanilla Copy: {@link } <br>
     * Attempts to harvest a block as if the player with the given uuid
     * harvested it while holding the passed item.
     * @param world The world the block is in.
     * @param pos The position of the block.
     * @param mainhand The main hand item that the player is supposibly holding.
     * @param source The UUID of the breaking player.
     * @return If the block was successfully broken.
     */
    public static boolean breakExtraBlock(ServerLevel world, BlockPos pos, ItemStack mainhand, @Nullable UUID source) {
        BlockState blockstate = world.getBlockState(pos);
        FakePlayer player = new FakePlayer(world, new GameProfile(source, world.getPlayerByUUID(source).getDisplayName().getString()));
        if (player.connection == null) player.connection = new DeadPacketListenerImpl(player);
        player.getInventory().items.set(player.getInventory().selected, mainhand);
        player.setPos(pos.getX(), pos.getY(), pos.getZ());

        if (blockstate.getDestroySpeed(world, pos) < 0) return false;

        GameType type = player.getAbilities().instabuild ? GameType.CREATIVE : GameType.SURVIVAL;
        int exp = getXpForBlock(blockstate, world);
        if (exp == -1) {
            return false;
        } else {
            BlockEntity tileentity = world.getBlockEntity(pos);
            Block block = blockstate.getBlock();
            if ((block instanceof CommandBlock || block instanceof StructureBlock || block instanceof JigsawBlock) && !player.canUseGameMasterBlocks()) {
                world.sendBlockUpdated(pos, blockstate, blockstate, 3);
                return false;
            } else if (player.blockActionRestricted(world, pos, type)) {
                return false;
            } else {
                if (player.getAbilities().instabuild) {
                    removeBlock(world, player, pos, false);
                    return true;
                } else {
                    ItemStack itemstack = player.getMainHandItem();
                    ItemStack itemstack1 = itemstack.copy();
                    boolean canHarvest = canHarvestBlock(blockstate); // previously player.hasCorrectToolForDrops(blockstate)
                    itemstack.mineBlock(world, blockstate, pos, player);
                    boolean removed = removeBlock(world, player, pos, canHarvest);

                    if (removed && canHarvest) {
                        block.playerDestroy(world, player, pos, blockstate, tileentity, itemstack1);
                    }

                    if (removed && exp > 0) blockstate.getBlock().popExperience(world, pos, exp);

                    return true;
                }
            }
        }
    }

    /**
     * Vanilla Copy: {@link }
     * @param world The world
     * @param player The removing player
     * @param pos The block location
     * @param canHarvest If the player can actually harvest this block.
     * @return If the block was actually removed.
     */
    public static boolean removeBlock(ServerLevel world, ServerPlayer player, BlockPos pos, boolean canHarvest) {
        BlockState state = world.getBlockState(pos);
        boolean removed = onDestroyedByPlayer(state, world, pos, player, canHarvest, world.getFluidState(pos));
        if (removed) state.getBlock().destroy(world, pos, state);
        return removed;
    }

    private static boolean canHarvestBlock(BlockState state) {
        if (!state.requiresCorrectToolForDrops()) {
            return true;
        }
        return !state.requiresCorrectToolForDrops();
    }

    private static int getXpForBlock(BlockState state, ServerLevel level) {
        if (state.getBlock() instanceof DropExperienceBlock ore) {
            return ore.xpRange.sample(level.random);
        } else if (state.getBlock() instanceof RedStoneOreBlock) {
            return 1 + level.random.nextInt(5);
        } else if (state.getBlock() instanceof SpawnerBlock) {
            return 15 + level.random.nextInt(15) + level.random.nextInt(15);
        }
        return 0;
    }

    private static boolean onDestroyedByPlayer(BlockState state, Level level, BlockPos pos, Player player, boolean willHarvest, FluidState fluid) {
        state.getBlock().playerWillDestroy(level, pos, state, player);
        return level.setBlock(pos, fluid.createLegacyBlock(), level.isClientSide ? 11 : 3);
    }
}
