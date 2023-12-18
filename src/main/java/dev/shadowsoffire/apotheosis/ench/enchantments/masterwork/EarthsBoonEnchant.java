package dev.shadowsoffire.apotheosis.ench.enchantments.masterwork;

import dev.shadowsoffire.apotheosis.Apoth;
import dev.shadowsoffire.apotheosis.ench.EnchModule;
import io.github.fabricators_of_create.porting_lib.tags.TagHelper;
import io.github.fabricators_of_create.porting_lib.tags.Tags;
import net.fabricmc.fabric.api.event.player.PlayerBlockBreakEvents;
import net.minecraft.ChatFormatting;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.block.Block;

public class EarthsBoonEnchant extends Enchantment {

    public EarthsBoonEnchant() {
        super(Rarity.VERY_RARE, EnchModule.PICKAXE, new EquipmentSlot[] { EquipmentSlot.MAINHAND });
    }

    @Override
    public int getMaxLevel() {
        return 3;
    }

    @Override
    public int getMinCost(int level) {
        return 60 + (level - 1) * 20;
    }

    @Override
    public int getMaxCost(int enchantmentLevel) {
        return 200;
    }

    @Override
    public Component getFullname(int level) {
        return ((MutableComponent) super.getFullname(level)).withStyle(ChatFormatting.DARK_GREEN);
    }

    public void provideBenefits() {
        PlayerBlockBreakEvents.AFTER.register((world, player, pos, state, blockEntity) -> {
            ItemStack stack = player.getMainHandItem();
            int level = EnchantmentHelper.getItemEnchantmentLevel(this, stack);
            if (player.level().isClientSide) return;
            if (state.is(Tags.Blocks.STONE) && level > 0 && player.getRandom().nextFloat() <= 0.01F * level) {
                ItemStack newDrop = new ItemStack(TagHelper.getRandomElement(BuiltInRegistries.ITEM, Apoth.Tags.BOON_DROPS, player.getRandom()).orElse(Items.AIR));
                Block.popResource(player.level(), pos, newDrop);
            }
        });

    }
}
