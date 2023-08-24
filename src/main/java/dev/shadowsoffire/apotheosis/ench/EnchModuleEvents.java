package dev.shadowsoffire.apotheosis.ench;

import dev.shadowsoffire.apotheosis.ench.anvil.AnvilTile;
import dev.shadowsoffire.apotheosis.ench.objects.ExtractionTomeItem;
import dev.shadowsoffire.apotheosis.ench.objects.ImprovedScrappingTomeItem;
import dev.shadowsoffire.apotheosis.ench.objects.ScrappingTomeItem;
import dev.shadowsoffire.apotheosis.util.Events;
import io.github.fabricators_of_create.porting_lib.entity.events.living.LivingEntityLootEvents;
import io.github.fabricators_of_create.porting_lib.tags.Tags;
import net.minecraft.world.entity.projectile.ThrownTrident;
import net.minecraft.world.inventory.AnvilMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;


import java.util.Map.Entry;
import java.util.stream.Collectors;

public class EnchModuleEvents {

    public static float breakChance = .12f;

    public static void registerEvents() {
        anvilEvent();



        livingHurt();
    }

    public static void anvilEvent() {
        Events.AnvilUpdateEvent.EVENT.register((left, right, name, baseCost, player) -> {
            if (left.isEnchanted()) {
                if (right.getItem() == Items.COBWEB) {
                    ItemStack stack = left.copy();
                    EnchantmentHelper.setEnchantments(EnchantmentHelper.getEnchantments(stack).entrySet().stream().filter(ent -> ent.getKey().isCurse()).collect(Collectors.toMap(Entry::getKey, Entry::getValue)), stack);
                    //e.setCost(1);
                    //e.setMaterialCost(1);
                    //e.setOutput(stack);
                }
                else if (right.getItem() == Ench.Items.PRISMATIC_WEB) {
                    ItemStack stack = left.copy();
                    EnchantmentHelper.setEnchantments(EnchantmentHelper.getEnchantments(stack).entrySet().stream().filter(ent -> !ent.getKey().isCurse()).collect(Collectors.toMap(Entry::getKey, Entry::getValue)), stack);
                    //e.setCost(30);
                    //e.setMaterialCost(1);
                    //e.setOutput(stack);
                    return true;
                }
            }
            if ((left.getItem() == Items.CHIPPED_ANVIL || left.getItem() == Items.DAMAGED_ANVIL) && right.is(Tags.Items.STORAGE_BLOCKS_IRON)) {
                if (left.getCount() != 1) return true;
                int dmg = left.getItem() == Items.DAMAGED_ANVIL ? 2 : 1;
                ItemStack out = new ItemStack(dmg == 1 ? Items.ANVIL : Items.CHIPPED_ANVIL);
                EnchantmentHelper.setEnchantments(EnchantmentHelper.getEnchantments(left), out);
                out.setCount(1);
                //e.setOutput(out);
                //e.setCost(5 + EnchantmentHelper.getEnchantments(left).entrySet().stream().mapToInt(ent -> ent.getValue() * (ent.getKey().getRarity().ordinal() + 1)).sum());
                //e.setMaterialCost(1);
                return true;
            }
            if (ScrappingTomeItem.updateAnvil(left, right, name, baseCost, player)) return true;
            if (ImprovedScrappingTomeItem.updateAnvil(left, right, name, baseCost, player)) return true;
            if (ExtractionTomeItem.updateAnvil(left, right, name, baseCost, player)) return true;
            return false;
        });

    }

    public static void repairEvent() {
        ExtractionTomeItem.updateRepair();
    }

    /**
     * Event handler for the Scavenger and Spearfishing enchantments.
     */
    public void drops() throws Throwable {
            Ench.Enchantments.SCAVENGER.drops();
            Ench.Enchantments.SPEARFISHING.addFishes();
    }

    public static void dropsLowest() {
            Ench.Enchantments.KNOWLEDGE.drops();
    }

    public void healing() {
        Ench.Enchantments.LIFE_MENDING.lifeMend();
    }

    public void block() {

        Ench.Enchantments.REFLECTIVE.reflect();
    }

    public void tridentLooting() {
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
    public void modifyBreakSpeed() {
        Ench.Enchantments.STABLE_FOOTING.breakSpeed();
        Ench.Enchantments.MINERS_FERVOR.breakSpeed();
    }

    public void breakSpeedLow() {

    }

    /**
     * Event handler for the Boon of the Earth enchant.
     */
    public void breakBreak() { //Might have to make these before block break? not sure
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
    private static float breakchance = .12f;
    public void applyUnbreaking() {
        Events.AnvilRepairEvent.EVENT.register((player, output, left, right) -> {
            if (player.containerMenu instanceof AnvilMenu anvMenu) {
                anvMenu.access.execute((level, pos) -> {
                    if (level.getBlockEntity(pos) instanceof AnvilTile anvil) {
                        breakchance = (EnchModuleEvents.breakChance / (anvil.getEnchantments().getInt(Enchantments.UNBREAKING) + 1));
                    }
                });
            }
            return breakchance;
        });

    }


    public static void livingHurt() {
        Ench.Enchantments.BERSERKERS_FURY.livingHurt();
    }

}
