package dev.shadowsoffire.apotheosis.ench.enchantments.masterwork;

import dev.shadowsoffire.apotheosis.Apotheosis;
import dev.shadowsoffire.apotheosis.ench.EnchModule;
import io.github.fabricators_of_create.porting_lib.entity.events.living.LivingEntityLootEvents;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.ExperienceOrb;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentCategory;
import net.minecraft.world.item.enchantment.EnchantmentHelper;

public class KnowledgeEnchant extends Enchantment {

    public KnowledgeEnchant() {
        super(Rarity.RARE, EnchantmentCategory.WEAPON, new EquipmentSlot[] { EquipmentSlot.MAINHAND });
    }

    @Override
    public int getMinCost(int level) {
        return 55 + (level - 1) * 45;
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

    public void drops() {
        LivingEntityLootEvents.DROPS.register((target, source, drops, lootingLevel, recentlyHit) -> {
            if (!(source.getEntity() instanceof Player p)) {
                if (Apotheosis.enableDebug) EnchModule.LOGGER.info("Damage source {} from entity {} is not a player", source, source.getEntity());
                return false;
            }
            int knowledge = EnchantmentHelper.getItemEnchantmentLevel(this, p.getMainHandItem());
            if (knowledge > 0 && !(target instanceof Player)) {
                int items = 0;
                for (ItemEntity i : drops) {
                    items += i.getItem().getCount();
                    if (Apotheosis.enableDebug) EnchModule.LOGGER.info("Item {} is being removed due to KOTA", i.getItem());
                }
                if (items > 0) drops.clear();
                items *= knowledge * 25;
                while (items > 0) {
                    int i = ExperienceOrb.getExperienceValue(items);
                    items -= i;
                    p.level().addFreshEntity(new ExperienceOrb(p.level(), target.getX(), target.getY(), target.getZ(), i));
                    if (Apotheosis.enableDebug) EnchModule.LOGGER.info("Adding new experience orb with xp size {}", i);
                }
            }
            return false;
        });
    }
}
