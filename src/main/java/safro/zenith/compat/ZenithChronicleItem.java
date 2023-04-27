package safro.zenith.compat;

import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import safro.zenith.Zenith;
import vazkii.patchouli.api.PatchouliAPI;

public class ZenithChronicleItem extends Item {

    public ZenithChronicleItem(Properties settings) {
        super(settings);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level world, Player user, InteractionHand hand) {
        if (!world.isClientSide) {
            if (FabricLoader.getInstance().isModLoaded("patchouli")) {
                PatchouliAPI.get().openBookGUI((ServerPlayer) user, new ResourceLocation(Zenith.MODID, "apoth_chronicle"));
                return InteractionResultHolder.success(user.getItemInHand(hand));
            }
        }
        return InteractionResultHolder.pass(user.getItemInHand(hand));
    }
}
