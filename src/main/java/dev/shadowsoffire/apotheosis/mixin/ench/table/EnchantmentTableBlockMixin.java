package dev.shadowsoffire.apotheosis.mixin.ench.table;

import dev.shadowsoffire.apotheosis.Apotheosis;
import dev.shadowsoffire.apotheosis.ench.EnchModule;
import dev.shadowsoffire.apotheosis.ench.api.IEnchantingBlock;
import dev.shadowsoffire.apotheosis.ench.table.ApothEnchantTile;
import dev.shadowsoffire.apotheosis.ench.table.ApothEnchantmentMenu;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.util.RandomSource;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.Nameable;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EnchantmentTableBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.EnchantmentTableBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;

@Mixin(value = EnchantmentTableBlock.class, priority = 1500)
abstract class EnchantmentTableBlockMixin extends BaseEntityBlock {

    @Shadow @Final
    public static List<BlockPos> BOOKSHELF_OFFSETS;

    protected EnchantmentTableBlockMixin(Properties properties) {
        super(properties);
    }

    @Inject(method = "newBlockEntity", at = @At("HEAD"), cancellable = true)
    public void newBlockEntity(BlockPos pos, BlockState state, CallbackInfoReturnable<BlockEntity> cir) {
        if (Apotheosis.enableEnch) cir.setReturnValue(new ApothEnchantTile(pos, state));
    }


    @Inject(method = "getMenuProvider", at = @At("HEAD"), cancellable = true)
    private void zenithEnchMenu(BlockState state, Level world, BlockPos pos, CallbackInfoReturnable<MenuProvider> cir) {
        if (Apotheosis.enableEnch) {
            BlockEntity tileentity = world.getBlockEntity(pos);
            if (tileentity instanceof ApothEnchantTile enchTile) {
                Component itextcomponent = ((Nameable) tileentity).getDisplayName();
                cir.setReturnValue(new SimpleMenuProvider((id, inventory, player) -> new ApothEnchantmentMenu(id, inventory, ContainerLevelAccess.create(world, pos), enchTile), itextcomponent));
            }
        }
    }

    @Override
    public void onRemove(BlockState state, Level world, BlockPos pos, BlockState newState, boolean isMoving) {
        if (state.getBlock() != newState.getBlock()) {
            BlockEntity tileentity = world.getBlockEntity(pos);
            if (tileentity instanceof ApothEnchantTile blockEntity) {
                Block.popResource(world, pos, blockEntity.inv.getStackInSlot(0));
                blockEntity.setChanged();
                world.removeBlockEntity(pos);
            }
        }
    }

    @Inject(method = "animateTick", at = @At("HEAD"))
    @Environment(EnvType.CLIENT)
    public void animateTick(BlockState state, Level level, BlockPos pos, RandomSource random, CallbackInfo ci) {
        if (Apotheosis.enableEnch){
            for (BlockPos offset : BOOKSHELF_OFFSETS) {
                BlockState shelfState = level.getBlockState(pos.offset(offset));
                ((IEnchantingBlock) shelfState.getBlock()).spawnTableParticle(shelfState, level, random, pos, offset);
            }
        }
    }


}
