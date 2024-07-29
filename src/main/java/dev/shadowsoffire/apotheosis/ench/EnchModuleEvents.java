package dev.shadowsoffire.apotheosis.ench;

import dev.shadowsoffire.apotheosis.ench.anvil.AnvilTile;
import dev.shadowsoffire.apotheosis.ench.objects.ExtractionTomeItem;
import dev.shadowsoffire.apotheosis.ench.objects.ImprovedScrappingTomeItem;
import dev.shadowsoffire.apotheosis.ench.objects.ScrappingTomeItem;
import dev.shadowsoffire.apotheosis.mixin.accessors.ItemCombinerMenuAccessor;
import dev.shadowsoffire.apotheosis.util.Events;
import fuzs.puzzleslib.api.event.v1.FabricPlayerEvents;
import io.github.fabricators_of_create.porting_lib.entity.events.LivingEntityEvents;
import io.github.fabricators_of_create.porting_lib.tags.Tags;
import net.fabricmc.fabric.api.loot.v2.LootTableEvents;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.ThrownTrident;
import net.minecraft.world.inventory.AnvilMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.predicates.LootItemRandomChanceWithLootingCondition;

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
                    e.cost = 1;
                    e.materialCost = 1;
                    e.output = stack;
                    return false;
                }
                else if (e.right.getItem() == dev.shadowsoffire.apotheosis.ench.Ench.Items.PRISMATIC_WEB) {
                    ItemStack stack = e.left.copy();
                    EnchantmentHelper.setEnchantments(EnchantmentHelper.getEnchantments(stack).entrySet().stream().filter(ent -> !ent.getKey().isCurse()).collect(Collectors.toMap(Entry::getKey, Entry::getValue)), stack);
                    e.cost = 30;
                    e.materialCost = 1;
                    e.output = stack;
                }
            }
            if ((e.left.getItem() == Items.CHIPPED_ANVIL || e.left.getItem() == Items.DAMAGED_ANVIL) && e.right.is(Tags.Items.STORAGE_BLOCKS_IRON)) {
                if (e.left.getCount() != 1) return false;
                int dmg = e.left.getItem() == Items.DAMAGED_ANVIL ? 2 : 1;
                ItemStack out = new ItemStack(dmg == 1 ? Items.ANVIL : Items.CHIPPED_ANVIL);
                EnchantmentHelper.setEnchantments(EnchantmentHelper.getEnchantments(e.left), out);
                out.setCount(1);
                e.output = out;
                e.cost = (5 + EnchantmentHelper.getEnchantments(e.left).entrySet().stream().mapToInt(ent -> ent.getValue() * (ent.getKey().getRarity().ordinal() + 1)).sum());
                e.materialCost = 1;
            }
            if (ScrappingTomeItem.updateAnvil(e)) return false;
            if (ImprovedScrappingTomeItem.updateAnvil(e)) return false;
            if (ExtractionTomeItem.updateAnvil(e)) return false;
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

    public static void dropsWarden() {
        LootTableEvents.MODIFY.register((resourceManager, lootManager, id, tableBuilder, source) -> {
            if (EntityType.WARDEN.getDefaultLootTable().equals(id) && source.isBuiltin()) {
                // guaranteed 1 tendril
                LootPool pool = LootPool.lootPool()
                    .add(LootItem.lootTableItem(Ench.Items.WARDEN_TENDRIL))
                    .build();

                tableBuilder.pool(pool);

                // 10% chance + additional 10% chance per looting level for 2nd tendril
                LootPool chancePool = LootPool.lootPool()
                    .add(LootItem.lootTableItem(Ench.Items.WARDEN_TENDRIL))
                    .when(LootItemRandomChanceWithLootingCondition.randomChanceAndLootingBoost(0.1f, 0.1f))
                    .build();

                tableBuilder.pool(chancePool);
            }
       });
    }

    public static void healing() {
        Ench.Enchantments.LIFE_MENDING.lifeMend();
    }

    public static void block() {
        Ench.Enchantments.REFLECTIVE.reflect();
    }

    public static void tridentLooting() {
        LivingEntityEvents.LOOTING_LEVEL.register((src, target, currentLevel, recentlyHit) -> {
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
        if (FabricLoader.getInstance().isModLoaded("puzzleslib")) { //they replace the lambda i mixin into :)))
            FabricPlayerEvents.ANVIL_REPAIR.register((player, itemStack, itemStack1, itemStack2, mutableFloat) -> {
                if (player.containerMenu instanceof AnvilMenu anvMenu) {
                    ((ItemCombinerMenuAccessor) anvMenu).getAccess().execute((level, pos) -> {
                        if (level.getBlockEntity(pos) instanceof AnvilTile anvil) {
                            float old = mutableFloat.getAsFloat();
                            mutableFloat.accept((old / (anvil.getEnchantments().getInt(Enchantments.UNBREAKING) + 1)));
                        }
                    });
                }
            });
        } else {
            Events.AnvilRepair.ANVIL_REPAIR.register((event) -> {
                Player player = event.player;
                if (player.containerMenu instanceof AnvilMenu anvMenu) {
                    ((ItemCombinerMenuAccessor) anvMenu).getAccess().execute((level, pos) -> {
                        if (level.getBlockEntity(pos) instanceof AnvilTile anvil) {
                            event.breakChance = (event.breakChance / (anvil.getEnchantments().getInt(Enchantments.UNBREAKING) + 1));
                        }
                    });
                }
            });
        }
    }


    public static void livingHurt() {
        Ench.Enchantments.BERSERKERS_FURY.livingHurt();
    }

}
