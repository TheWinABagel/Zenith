package dev.shadowsoffire.apotheosis.ench.objects;

import com.google.common.collect.Lists;
import dev.shadowsoffire.apotheosis.util.Events;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.ChatFormatting;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.Level;

import java.util.List;
import java.util.Map;
import java.util.Random;

public class ScrappingTomeItem extends BookItem {

    static Random rand = new Random();

    public ScrappingTomeItem() {
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
        tooltip.add(Component.translatable("info.zenith.scrap_tome").withStyle(ChatFormatting.GRAY));
        tooltip.add(Component.translatable("info.zenith.scrap_tome2").withStyle(ChatFormatting.GRAY));
    }

    @Override
    public Rarity getRarity(ItemStack stack) {
        return Rarity.UNCOMMON;
    }

    public static boolean updateAnvil(Events.AnvilUpdate.UpdateAnvilEvent ev) {
        ItemStack weapon = ev.left;
        ItemStack book = ev.right;
        if (!(book.getItem() instanceof ScrappingTomeItem) || book.isEnchanted() || !weapon.isEnchanted()) return false;

        Map<Enchantment, Integer> wepEnch = EnchantmentHelper.getEnchantments(weapon);
        int size = Mth.ceil(wepEnch.size() / 2D);
        List<Enchantment> keys = Lists.newArrayList(wepEnch.keySet());
        long seed = 1831;
        for (Enchantment e : keys) {
            seed ^= BuiltInRegistries.ENCHANTMENT.getKey(e).hashCode();
        }
        seed ^= ev.player.getEnchantmentSeed();
        rand.setSeed(seed);
        while (wepEnch.size() > size) {
            Enchantment lost = keys.get(rand.nextInt(keys.size()));
            wepEnch.remove(lost);
            keys.remove(lost);
        }
        ItemStack out = new ItemStack(Items.ENCHANTED_BOOK);
        EnchantmentHelper.setEnchantments(wepEnch, out);
        ev.setMaterialCost(1);
        ev.setCost(wepEnch.size() * 6);
        ev.setOutput(out);
        return true;
    }
}
