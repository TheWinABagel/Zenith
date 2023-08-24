package dev.shadowsoffire.apotheosis.mixin.ench.table;

import dev.shadowsoffire.apotheosis.Apotheosis;
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
import net.minecraft.world.level.block.EnchantmentTableBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;

@Mixin(value = EnchantmentTableBlock.class, priority = 1500)
public class EnchantmentTableBlockMixin {

    @Shadow @Final
    public static List<BlockPos> BOOKSHELF_OFFSETS;

    @Inject(method = "getMenuProvider", at = @At("HEAD"), cancellable = true)
    private void zenithEnchMenu(BlockState state, Level world, BlockPos pos, CallbackInfoReturnable<MenuProvider> cir) {
        if (Apotheosis.enableEnch) {
            BlockEntity tileentity = world.getBlockEntity(pos);
            if (tileentity instanceof ApothEnchantTile) {
                Component itextcomponent = ((Nameable) tileentity).getDisplayName();
                cir.setReturnValue(new SimpleMenuProvider((id, inventory, player) -> new ApothEnchantmentMenu(id, inventory, ContainerLevelAccess.create(world, pos), (ApothEnchantTile) tileentity), itextcomponent));
            } else {
                cir.setReturnValue(null);
            }
        }
    }

    @Inject(method = "newBlockEntity", at = @At("HEAD"), cancellable = true)
    private void zenithNewBlockEntity(BlockPos pos, BlockState state, CallbackInfoReturnable<BlockEntity> cir){
        if (Apotheosis.enableEnch) cir.setReturnValue(new ApothEnchantTile(pos, state));
    }

    @Inject(method = "animateTick", at = @At("HEAD"))
    @Environment(EnvType.CLIENT)
    public void animateTick(BlockState state, Level level, BlockPos pos, RandomSource random, CallbackInfo ci) {
        for (BlockPos offset : BOOKSHELF_OFFSETS) {
            BlockState shelfState = level.getBlockState(pos.offset(offset));
            ((IEnchantingBlock) shelfState.getBlock()).spawnTableParticle(shelfState, level, random, pos, offset);
        }
    }

}
