package safro.zenith.mixin.fletching;

import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.CraftingTableBlock;
import net.minecraft.world.level.block.FletchingTableBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import safro.zenith.Zenith;
import safro.zenith.village.fletching.FletchingContainer;

@Mixin(FletchingTableBlock.class)
public abstract class FletchingTableBlockMixin extends CraftingTableBlock {
    private static final Component NAME = Component.translatable("zenith.recipes.fletching");

    public FletchingTableBlockMixin(Properties properties) {
        super(properties);
    }

    @Inject(method = "use", at = @At("HEAD"), cancellable = true)
    private void apothUse(BlockState state, Level worldIn, BlockPos pos, Player player, InteractionHand handIn, BlockHitResult hit, CallbackInfoReturnable<InteractionResult> cir) {
        if (Zenith.enableVillage) {
            if (worldIn.isClientSide) cir.setReturnValue(InteractionResult.SUCCESS);
            player.openMenu(this.getMenuProvider(state, worldIn, pos));
            cir.setReturnValue(InteractionResult.CONSUME);
        }
    }

    @Override
    public MenuProvider getMenuProvider(BlockState state, Level world, BlockPos pos) {
        return new SimpleMenuProvider((id, inv, player) -> new FletchingContainer(id, inv, world, pos), NAME);
    }
}
