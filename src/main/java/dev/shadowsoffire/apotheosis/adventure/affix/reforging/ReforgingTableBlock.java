package dev.shadowsoffire.apotheosis.adventure.affix.reforging;

import dev.shadowsoffire.apotheosis.adventure.Adventure.Blocks;
import dev.shadowsoffire.apotheosis.adventure.loot.LootRarity;
import dev.shadowsoffire.apotheosis.adventure.loot.RarityRegistry;
import dev.shadowsoffire.placebo.block_entity.TickingEntityBlock;
import dev.shadowsoffire.placebo.menu.SimplerMenuProvider;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerFactory;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

import java.util.List;

public class ReforgingTableBlock extends Block implements TickingEntityBlock {
    public static final Component TITLE = Component.translatable("container.zenith.reforge");
    public static final VoxelShape SHAPE = Block.box(0.0D, 0.0D, 0.0D, 16.0D, 12.0D, 16.0D);

    protected final int maxRarity;

    public ReforgingTableBlock(Properties properties, int maxRarity) {
        super(properties);
        this.maxRarity = maxRarity;
    }

    public LootRarity getMaxRarity() {
        return RarityRegistry.byOrdinal(this.maxRarity).get();
    }

    @Override
    public boolean useShapeForLightOcclusion(BlockState pState) {
        return true;
    }

    @Override
    public VoxelShape getShape(BlockState pState, BlockGetter pLevel, BlockPos pPos, CollisionContext pContext) {
        return SHAPE;
    }

    @Override
    public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
        if (!level.isClientSide) {
            player.openMenu(new ExtendedScreenHandlerFactory() {
                @Override
                public void writeScreenOpeningData(ServerPlayer player, FriendlyByteBuf buf) {
                    buf.writeBlockPos(pos);
                }

                @Override
                public Component getDisplayName() {
                    return Component.translatable("block.zenith.salvaging_table");
                }

                @Override
                public AbstractContainerMenu createMenu(int i, Inventory inventory, Player player) {
                    return new ReforgingMenu(i, inventory, pos);
                }
            });
            return InteractionResult.CONSUME;
        }
        return InteractionResult.PASS;
    }

    @Override
    public MenuProvider getMenuProvider(BlockState state, Level world, BlockPos pos) {
        return new SimplerMenuProvider<>(world, pos, ReforgingMenu::new);
    }

    @Environment(EnvType.CLIENT)
    @Override
    public void appendHoverText(ItemStack pStack, BlockGetter pLevel, List<Component> list, TooltipFlag pFlag) {
        list.add(Component.translatable(Blocks.REFORGING_TABLE.getDescriptionId() + ".desc").withStyle(ChatFormatting.GRAY));
        if (this.maxRarity < RarityRegistry.getMaxRarity().get().ordinal())
            list.add(Component.translatable(Blocks.REFORGING_TABLE.getDescriptionId() + ".desc2", this.getMaxRarity().toComponent()).withStyle(ChatFormatting.GRAY));
    }

    @Override
    public BlockEntity newBlockEntity(BlockPos pPos, BlockState pState) {
        return new ReforgingTableTile(pPos, pState);
    }

    @Override
    @Deprecated
    public void onRemove(BlockState state, Level world, BlockPos pos, BlockState newState, boolean isMoving) {
        if (state.getBlock() == this && newState.getBlock() == this) return;
        BlockEntity te = world.getBlockEntity(pos);
        if (te instanceof ReforgingTableTile ref) {
            for (int i = 0; i < ref.inv.getSlots().size(); i++) {
                popResource(world, pos, ref.inv.getStackInSlot(i));
            }
        }
        super.onRemove(state, world, pos, newState, isMoving);
    }
}
