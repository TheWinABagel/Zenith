package safro.zenith.ench.anvil;

import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.phys.BlockHitResult;

public class EnchantmentBlockSerializer extends BlockPlaceContext {
    public EnchantmentBlockSerializer(Player player, InteractionHand interactionHand, ItemStack itemStack, BlockHitResult blockHitResult) {
        super(player, interactionHand, itemStack, blockHitResult);
    }
}
