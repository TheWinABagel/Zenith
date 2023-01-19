package safro.zenith.mixin.anvil;

import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.AnvilBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import safro.zenith.Zenith;
import safro.zenith.ench.anvil.AnvilTile;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Mixin(Block.class)
public abstract class BlockMixin {

    @Inject(method = "playerDestroy", at = @At("HEAD"))
    private void apothPlayerDestroy(Level world, Player player, BlockPos pos, BlockState state, BlockEntity te, ItemStack stack, CallbackInfo ci) {
        Block block = (Block) (Object) this;
        if (isValid()) {
            ItemStack anvil = new ItemStack(block);
            if (te instanceof AnvilTile) {
                Map<Enchantment, Integer> ench = ((AnvilTile) te).getEnchantments();
                ench = ench.entrySet().stream().filter(e -> e.getValue() > 0).collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
                EnchantmentHelper.setEnchantments(ench, anvil);
            }
            Block.popResource(world, pos, anvil);
        }
    }

    @Inject(method = "setPlacedBy", at = @At("HEAD"))
    private void apothPlaced(Level world, BlockPos pos, BlockState state, LivingEntity placer, ItemStack stack, CallbackInfo ci) {
        if (isValid()) {
            BlockEntity te = world.getBlockEntity(pos);
            if (te instanceof AnvilTile) {
                ((AnvilTile) te).getEnchantments().putAll(EnchantmentHelper.getEnchantments(stack));
            }
        }
    }

    @Inject(method = "getCloneItemStack", at = @At("HEAD"), cancellable = true)
    private void apothClone(BlockGetter world, BlockPos pos, BlockState blockState, CallbackInfoReturnable<ItemStack> cir) {
        Block block = (Block) (Object) this;
        if (isValid()) {
            ItemStack anvil = new ItemStack(block);
            BlockEntity te = world.getBlockEntity(pos);
            if (te instanceof AnvilTile) {
                Map<Enchantment, Integer> ench = ((AnvilTile) te).getEnchantments();
                ench = ench.entrySet().stream().filter(e -> e.getValue() > 0).collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
                EnchantmentHelper.setEnchantments(ench, anvil);
            }
            cir.setReturnValue(anvil);
        }
    }

    @Inject(method = "appendHoverText", at = @At("HEAD"))
    private void apothAppend(ItemStack stack, BlockGetter blockGetter, List<Component> tooltip, TooltipFlag tooltipFlag, CallbackInfo ci) {
        if (isValid()) {
            if (!stack.hasFoil()) tooltip.add(Component.translatable("info.zenith.anvil").withStyle(ChatFormatting.GRAY));
        }
    }

    private boolean isValid() {
        Block block = (Block) (Object) this;
        return block instanceof AnvilBlock && Zenith.enableEnch;
    }
}
