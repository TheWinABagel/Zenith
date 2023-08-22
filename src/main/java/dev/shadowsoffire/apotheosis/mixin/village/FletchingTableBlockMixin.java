package dev.shadowsoffire.apotheosis.mixin.village;

import dev.shadowsoffire.apotheosis.Apotheosis;
import dev.shadowsoffire.apotheosis.village.fletching.FletchingContainer;
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

@Mixin(FletchingTableBlock.class)
public class FletchingTableBlockMixin extends CraftingTableBlock {

    private static final Component NAME = Component.translatable("apotheosis.recipes.fletching");

    public FletchingTableBlockMixin(Properties properties) {
        super(properties);
    }

    @Inject(method = "use",at = @At("RETURN"), cancellable = true)
    public void use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit, CallbackInfoReturnable<InteractionResult> cir) {
        if (!Apotheosis.enableSpawner) return;
        if (level.isClientSide) cir.setReturnValue(InteractionResult.SUCCESS);
        player.openMenu(this.getMenuProvider(state, level, pos));
        cir.setReturnValue(InteractionResult.CONSUME);
    }
    @Override
    public MenuProvider getMenuProvider(BlockState state, Level level, BlockPos pos) {
        return new SimpleMenuProvider((id, inv, player) -> new FletchingContainer(id, inv, level, pos), NAME);
    }
}
