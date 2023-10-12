package dev.shadowsoffire.apotheosis.ench.objects;

import dev.shadowsoffire.apotheosis.util.Events;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.Level;

import java.util.List;
import java.util.Map;
import java.util.Random;

public class ImprovedScrappingTomeItem extends BookItem {

    static Random rand = new Random();

    public ImprovedScrappingTomeItem() {
        super(new Properties());
    }

    @Override
    public boolean isEnchantable(ItemStack stack) {
        return false;
    }

    @Override
    @Environment(EnvType.CLIENT)
    public void appendHoverText(ItemStack stack, Level world, List<Component> tooltip, TooltipFlag flagIn) {
        if (stack.isEnchanted()) return;
        tooltip.add(Component.translatable("info.zenith.improved_scrap_tome").withStyle(ChatFormatting.GRAY));
        tooltip.add(Component.translatable("info.zenith.improved_scrap_tome2").withStyle(ChatFormatting.GRAY));
    }

    @Override
    public Rarity getRarity(ItemStack stack) {
        return Rarity.RARE;
    }

    public static boolean updateAnvil(Events.AnvilUpdate.UpdateAnvilEvent ev) {
        ItemStack weapon = ev.left;
        ItemStack book = ev.right;
        if (!(book.getItem() instanceof ImprovedScrappingTomeItem) || book.isEnchanted() || !weapon.isEnchanted()) return false;

        Map<Enchantment, Integer> wepEnch = EnchantmentHelper.getEnchantments(weapon);
        ItemStack out = new ItemStack(Items.ENCHANTED_BOOK);
        EnchantmentHelper.setEnchantments(wepEnch, out);
        ev.setMaterialCost(1);
        ev.setCost(wepEnch.size() * 10);
        ev.setOutput(out);
        return true;
    }
}
