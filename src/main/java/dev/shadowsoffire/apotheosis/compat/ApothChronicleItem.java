package dev.shadowsoffire.apotheosis.compat;

import dev.shadowsoffire.apotheosis.Apotheosis;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import vazkii.patchouli.api.PatchouliAPI;

public class ApothChronicleItem extends Item {

    public ApothChronicleItem(Properties settings) {
        super(settings);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level world, Player user, InteractionHand hand) {
        if (!world.isClientSide) {
            if (FabricLoader.getInstance().isModLoaded("patchouli")) {
                PatchouliAPI.get().openBookGUI((ServerPlayer) user, Apotheosis.loc("apoth_chronicle"));
                PatchouliAPI.get().openBookGUI(Apotheosis.loc("apoth_chronicle"));
                user.displayClientMessage(Component.translatable("book.apotheosis.name"), false);
                return InteractionResultHolder.success(user.getItemInHand(hand));
            }
        }
        return InteractionResultHolder.pass(user.getItemInHand(hand));
    }
}
