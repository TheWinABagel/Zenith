package dev.shadowsoffire.apotheosis.mixin.accessors;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.ChiseledBookShelfBlock;
import net.minecraft.world.level.block.entity.ChiseledBookShelfBlockEntity;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec2;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

import java.util.Optional;

@Mixin(ChiseledBookShelfBlock.class)
public interface ChiseledBookShelfBlockAccessor {
    @Invoker
    static Optional<Vec2> callGetRelativeHitCoordinatesForBlockFace(BlockHitResult hitResult, Direction face) {
        throw new UnsupportedOperationException();
    }

    @Invoker
    static int callGetHitSlot(Vec2 hitPos) {
        throw new UnsupportedOperationException();
    }

    @Invoker
    static void callRemoveBook(Level level, BlockPos pos, Player player, ChiseledBookShelfBlockEntity blockEntity, int slot) {
        throw new UnsupportedOperationException();
    }

    @Invoker
    static void callAddBook(Level level, BlockPos pos, Player player, ChiseledBookShelfBlockEntity blockEntity, ItemStack bookStack, int slot) {
        throw new UnsupportedOperationException();
    }
}
