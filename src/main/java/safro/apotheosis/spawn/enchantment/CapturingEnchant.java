package safro.apotheosis.spawn.enchantment;

import net.minecraft.core.Registry;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.SpawnEggItem;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentCategory;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import safro.apotheosis.spawn.SpawnerModule;

import java.util.Collection;

public class CapturingEnchant extends Enchantment {

    public CapturingEnchant() {
        super(Rarity.VERY_RARE, EnchantmentCategory.WEAPON, new EquipmentSlot[] { EquipmentSlot.MAINHAND });
    }

    @Override
    public int getMaxLevel() {
        return 5;
    }

    @Override
    public int getMinCost(int level) {
        return 28 + (level - 1) * 15;
    }

    @Override
    public int getMaxCost(int level) {
        return this.getMinCost(level) + 15;
    }

    public static void handleCapturing(DamageSource source, LivingEntity killed, Collection<ItemEntity> drops) {
        Entity killer = source.getEntity();
        if (killer instanceof LivingEntity) {
            int level = EnchantmentHelper.getItemEnchantmentLevel(SpawnerModule.CAPTURING, ((LivingEntity) killer).getMainHandItem());
            if (SpawnerModule.bannedMobs.contains(Registry.ENTITY_TYPE.getKey(killed.getType()))) return;
            if (killed.level.random.nextFloat() < level / 250F) {
                ItemStack egg = new ItemStack(SpawnEggItem.byId(killed.getType()));
                drops.add(new ItemEntity(killed.level, killed.getX(), killed.getY(), killed.getZ(), egg));
            }
        }
    }

}
