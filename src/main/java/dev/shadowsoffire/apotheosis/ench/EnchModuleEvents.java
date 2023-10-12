package dev.shadowsoffire.apotheosis.ench;

import dev.shadowsoffire.apotheosis.Apotheosis;
import dev.shadowsoffire.apotheosis.ench.anvil.AnvilTile;
import dev.shadowsoffire.apotheosis.ench.objects.ExtractionTomeItem;
import dev.shadowsoffire.apotheosis.ench.objects.ImprovedScrappingTomeItem;
import dev.shadowsoffire.apotheosis.ench.objects.ScrappingTomeItem;
import dev.shadowsoffire.apotheosis.util.Events;
import io.github.fabricators_of_create.porting_lib.entity.events.living.LivingEntityLootEvents;
import io.github.fabricators_of_create.porting_lib.tags.Tags;
import net.minecraft.world.entity.ExperienceOrb;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.monster.warden.Warden;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.ThrownTrident;
import net.minecraft.world.inventory.AnvilMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;


import java.util.Map.Entry;
import java.util.stream.Collectors;

public class EnchModuleEvents {

    public static void registerEvents() {
        anvilEvent();
        repairEvent();
        drops();
        dropsWarden();
        healing();
        block();
        livingHurt();
        tridentLooting();
        modifyBreakSpeed();
        breakBlock();
        rightClick();
        applyUnbreaking();
    }


    public static void anvilEvent() {
        Events.AnvilUpdate.UPDATE_ANVIL.register((e) -> {
            if (e.left.isEnchanted()) {
                if (e.right.getItem() == Items.COBWEB) {
                    ItemStack stack = e.left.copy();
                    EnchantmentHelper.setEnchantments(EnchantmentHelper.getEnchantments(stack).entrySet().stream().filter(ent -> ent.getKey().isCurse()).collect(Collectors.toMap(Entry::getKey, Entry::getValue)), stack);
                    e.setCost(1);
                    e.setMaterialCost(1);
                    e.setOutput(stack);
                }
                else if (e.right.getItem() == dev.shadowsoffire.apotheosis.ench.Ench.Items.PRISMATIC_WEB) {
                    ItemStack stack = e.left.copy();
                    EnchantmentHelper.setEnchantments(EnchantmentHelper.getEnchantments(stack).entrySet().stream().filter(ent -> !ent.getKey().isCurse()).collect(Collectors.toMap(Entry::getKey, Entry::getValue)), stack);
                    e.setCost(30);
                    e.setMaterialCost(1);
                    e.setOutput(stack);
                    return false;
                }
            }
            if ((e.left.getItem() == Items.CHIPPED_ANVIL || e.left.getItem() == Items.DAMAGED_ANVIL) && e.right.is(Tags.Items.STORAGE_BLOCKS_IRON)) {
                if (e.left.getCount() != 1) return false;
                int dmg = e.left.getItem() == Items.DAMAGED_ANVIL ? 2 : 1;
                ItemStack out = new ItemStack(dmg == 1 ? Items.ANVIL : Items.CHIPPED_ANVIL);
                EnchantmentHelper.setEnchantments(EnchantmentHelper.getEnchantments(e.left), out);
                out.setCount(1);
                e.setOutput(out);
                e.setCost(5 + EnchantmentHelper.getEnchantments(e.left).entrySet().stream().mapToInt(ent -> ent.getValue() * (ent.getKey().getRarity().ordinal() + 1)).sum());
                e.setMaterialCost(1);
                return false;
            }
            if (ScrappingTomeItem.updateAnvil(e)) return true;
            if (ImprovedScrappingTomeItem.updateAnvil(e)) return true;
            if (ExtractionTomeItem.updateAnvil(e)) return true;
            return false;
        });

    }

    public static void repairEvent() {
        ExtractionTomeItem.updateRepair();
    }

    /**
     * Event handler for the Scavenger and Spearfishing enchantments.
     */
    public static void drops()  {
        Ench.Enchantments.SCAVENGER.drops();
        Ench.Enchantments.SPEARFISHING.addFishes();
        Ench.Enchantments.KNOWLEDGE.drops();
    }

    public static void dropsWarden() { //TODO modify warden loot table
        LivingEntityLootEvents.DROPS.register((target, source, drops, lootingLevel, recentlyHit) -> {
            if (Apotheosis.enableEnch && target instanceof Warden warden) {
                int amount = 1;
                if (warden.random.nextFloat() <= 0.10F + lootingLevel * 0.10F) {
                    amount++;
                }
                drops.add(new ItemEntity(warden.level(), warden.getX(), warden.getY(), warden.getZ(),
                        new ItemStack(dev.shadowsoffire.apotheosis.ench.Ench.Items.WARDEN_TENDRIL, amount)));
            }
            return false;
        });
    }

    public static void healing() {
        Ench.Enchantments.LIFE_MENDING.lifeMend();
    }

    public static void block() {
        Ench.Enchantments.REFLECTIVE.reflect();
    }

    public static void tridentLooting() {
        LivingEntityLootEvents.LOOTING_LEVEL.register((src, target, currentLevel, recentlyHit) -> {
            if (src != null && src.getDirectEntity() instanceof ThrownTrident trident) {
                ItemStack triStack = ((TridentGetter) trident).getTridentItem();
                return (EnchantmentHelper.getItemEnchantmentLevel(Enchantments.MOB_LOOTING, triStack));
            }
            return currentLevel;
        });
    }

    public static interface TridentGetter {
        ItemStack getTridentItem();
    }

    /**
     * Event handler for the Stable Footing and Miner's Fervor enchants.
     */
    public static void modifyBreakSpeed() {
        Ench.Enchantments.STABLE_FOOTING.breakSpeed();
        Ench.Enchantments.MINERS_FERVOR.breakSpeed();
    }

    /**
     * Event handler for the Boon of the Earth enchant.
     */
    public static void breakBlock() { //Might have to make these before block break? not sure
        Ench.Enchantments.EARTHS_BOON.provideBenefits();
        Ench.Enchantments.CHAINSAW.chainsaw();
    }

    /**
     * Event handler for the Nature's Blessing enchantment.
     */
    public static void rightClick() {
        Ench.Enchantments.NATURES_BLESSING.rightClick();
    }

    /**
     * Event handler for Anvil Unbreaking.
     */
    public static void applyUnbreaking() {
        Events.ANVIL_REPAIR.register((event) -> {
            Player player = event.player;
            if (player.containerMenu instanceof AnvilMenu anvMenu) {
                anvMenu.access.execute((level, pos) -> {
                    if (level.getBlockEntity(pos) instanceof AnvilTile anvil) {
                        event.setBreakChance(event.breakChance / (anvil.getEnchantments().getInt(Enchantments.UNBREAKING) + 1));
                    }
                });
            }
            return false;
        });

    }


    public static void livingHurt() {
        Ench.Enchantments.BERSERKERS_FURY.livingHurt();
    }

}
