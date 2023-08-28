package dev.shadowsoffire.apotheosis.ench.enchantments.masterwork;

import io.github.fabricators_of_create.porting_lib.entity.events.living.LivingEntityLootEvents;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentCategory;
import net.minecraft.world.item.enchantment.EnchantmentHelper;

public class ScavengerEnchant extends Enchantment {

    public ScavengerEnchant() {
        super(Rarity.VERY_RARE, EnchantmentCategory.WEAPON, new EquipmentSlot[] { EquipmentSlot.MAINHAND });
    }

    @Override
    public int getMinCost(int level) {
        return 55 + level * level * 12; // 57 / 103 / 163
    }

    @Override
    public int getMaxCost(int level) {
        return 200;
    }

    @Override
    public int getMaxLevel() {
        return 3;
    }

    @Override
    public Component getFullname(int level) {
        return ((MutableComponent) super.getFullname(level)).withStyle(ChatFormatting.DARK_GREEN);
    }


    public void drops()  {
        LivingEntityLootEvents.DROPS.register((target, source, drops, lootingLevel, recentlyHit) -> {
            if (!(source.getEntity() instanceof Player p)) return recentlyHit;
            if (source.getEntity().level().isClientSide) return recentlyHit;
            int scavenger = EnchantmentHelper.getItemEnchantmentLevel(this, p.getMainHandItem());
            if (scavenger > 0 && p.level().random.nextInt(100) < scavenger * 2.5F) {
                target.startCapturingDrops();
                target.dropFromLootTable(source, true);
                drops.addAll(target.finishCapturingDrops());
            }
            return recentlyHit;
        });

    }

}
