package safro.zenith.mixin.spawner;

import net.minecraft.core.BlockPos;
import net.minecraft.stats.Stats;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SpawnerBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import safro.zenith.Zenith;
import safro.zenith.spawn.SpawnerModule;

@Mixin(SpawnerBlock.class)
public class BlockMixin extends BaseEntityBlock {

    public BlockMixin(Properties properties) {
        super(properties);
    }
/*
    @Inject(method = "setPlacedBy", at = @At("HEAD"))
    public void zenithSetPlacedBy(Level world, BlockPos pos, BlockState state, LivingEntity placer, ItemStack stack, CallbackInfo ci) {
        if (Zenith.enableSpawner && state.is(Blocks.SPAWNER)) {
            BlockEntity te = world.getBlockEntity(pos);
            if (te != null && stack.hasTag()) te.load(stack.getOrCreateTagElement("BlockEntityTag"));
        }
    }

    @Inject(method = "playerDestroy", at = @At("HEAD"), cancellable = true)
    private void zenithPlayerDestroy(Level world, Player player, BlockPos pos, BlockState state, BlockEntity te, ItemStack stack, CallbackInfo ci) {
        if (Zenith.enableSpawner && state.getBlock() instanceof SpawnerBlock block) {
            if (SpawnerModule.spawnerSilkLevel != -1 && EnchantmentHelper.getItemEnchantmentLevel(Enchantments.SILK_TOUCH, stack) >= SpawnerModule.spawnerSilkLevel) {
                ItemStack s = new ItemStack(block);
                if (te != null) s.getOrCreateTag().put("BlockEntityTag", te.saveWithoutMetadata());
                Block.popResource(world, pos, s);
                player.getMainHandItem().hurtAndBreak(SpawnerModule.spawnerSilkDamage, player, pl -> pl.broadcastBreakEvent(EquipmentSlot.MAINHAND));
                player.awardStat(Stats.BLOCK_MINED.get(block));
                player.causeFoodExhaustion(0.035F);
                ci.cancel();
            }
        }
    }
*/
    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos blockPos, BlockState blockState) {
        return null;
    }
}
