package dev.shadowsoffire.apotheosis.ench.objects;

import dev.shadowsoffire.apotheosis.ench.EnchModule;
import dev.shadowsoffire.apotheosis.ench.table.IEnchantableItem;
import io.github.fabricators_of_create.porting_lib.enchant.CustomEnchantingBehaviorItem;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.ChatFormatting;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentCategory;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.EnchantmentInstance;
import net.minecraft.world.level.Level;

import java.util.List;

public class TomeItem extends BookItem implements IEnchantableItem, CustomEnchantingBehaviorItem {

    final ItemStack rep;
    final EnchantmentCategory type;

    public TomeItem(Item rep, EnchantmentCategory type) {
        super(new Properties());
        this.type = type;
        this.rep = new ItemStack(rep);
        EnchModule.TYPED_BOOKS.add(this);
    }

    @Override
    public boolean canApplyAtEnchantingTable(ItemStack stack, Enchantment enchantment) {
        if (this.type == null) return EnchModule.TYPED_BOOKS.stream().filter(b -> b != this).allMatch(b -> !enchantment.canEnchant(new ItemStack(b)));
        return enchantment.category.canEnchant(this.rep.getItem());
    }

    @Override
    @Environment(EnvType.CLIENT)
    public void appendHoverText(ItemStack stack, Level worldIn, List<Component> tooltip, TooltipFlag flagIn) {
        tooltip.add(Component.translatable("info.zenith." + BuiltInRegistries.ITEM.getKey(this).getPath()).withStyle(ChatFormatting.GRAY));
        if (stack.isEnchanted()) {
            tooltip.add(Component.translatable("info.zenith.tome_error").withStyle(ChatFormatting.RED));
        }
    }

    @Override
    public Rarity getRarity(ItemStack stack) {
        return !stack.isEnchanted() ? super.getRarity(stack) : Rarity.UNCOMMON;
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level world, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        if (stack.isEnchanted()) {
            ItemStack book = new ItemStack(Items.ENCHANTED_BOOK, stack.getCount());
            EnchantmentHelper.setEnchantments(EnchantmentHelper.getEnchantments(stack), book);
            return InteractionResultHolder.consume(book);
        }
        return InteractionResultHolder.pass(stack);
    }

    @Override
    public ItemStack onEnchantment(ItemStack stack, List<EnchantmentInstance> enchantments) {
        stack = new ItemStack(Items.ENCHANTED_BOOK);
        for (EnchantmentInstance inst : enchantments) {
            EnchantedBookItem.addEnchantment(stack, inst);
        }
        return stack;
    }

    @Override
    public boolean forciblyAllowsTableEnchantment(ItemStack stack, Enchantment enchantment) {
        return this.canApplyAtEnchantingTable(stack, enchantment);
    }

}
