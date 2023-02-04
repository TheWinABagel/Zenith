package safro.zenith.ench;

import com.mojang.datafixers.util.Pair;
import io.github.fabricators_of_create.porting_lib.event.common.LivingEntityEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.minecraft.server.packs.PackType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.ThrownTrident;
import net.minecraft.world.inventory.AnvilMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import safro.zenith.Apoth;
import safro.zenith.Zenith;
import safro.zenith.api.json.ApothJsonReloadListener;
import safro.zenith.ench.enchantments.SpearfishingEnchant;
import safro.zenith.ench.enchantments.masterwork.KnowledgeEnchant;
import safro.zenith.ench.enchantments.masterwork.ScavengerEnchant;
import safro.zenith.ench.objects.ScrappingTomeItem;
import safro.zenith.ench.table.EnchantingStatManager;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class EnchModuleEvents {

    public static void init() {
        ResourceManagerHelper.get(PackType.SERVER_DATA).registerReloadListener(EnchantingStatManager.INSTANCE);

        ServerLifecycleEvents.SYNC_DATA_PACK_CONTENTS.register((player, joined) -> {
            if (!ApothJsonReloadListener.SYNC_REGISTRY.isEmpty()) {
                for (Map.Entry<String, ApothJsonReloadListener<?>> entry : ApothJsonReloadListener.SYNC_REGISTRY.entrySet()) {
                    if (entry.getValue() != null) {
                        entry.getValue().sync(player);
                    }
                }
            }
        });

        LivingEntityEvents.DROPS_WITH_LEVEL.register((target, source, drops, lootingLevel, recentlyHit) -> {
            if (source.getEntity() instanceof Player player) {
                if (Zenith.enableEnch) {
                    ScavengerEnchant.drops(player, target, source);
                    SpearfishingEnchant.addFishes(target, drops, source);
                    KnowledgeEnchant.drops(player, target, drops);
                }
            }
            return false;
        });

        LivingEntityEvents.LOOTING_LEVEL.register(((src, target, currentLevel, recentlyHit) -> {
            if (src != null && src.getDirectEntity() instanceof ThrownTrident trident) {
                ItemStack triStack = ((TridentGetter) trident).getTridentItem();
                return EnchantmentHelper.getItemEnchantmentLevel(Enchantments.MOB_LOOTING, triStack);
            }
            return currentLevel;
        }));
    }

    public static Pair<ItemStack, List<Integer>> anvilEvent(ItemStack left, ItemStack right, Player player, int cost, int materialCost) {
        if (left.isEnchanted()) {
            if (right.getItem() == Items.COBWEB) {
                ItemStack stack = left.copy();
                EnchantmentHelper.setEnchantments(EnchantmentHelper.getEnchantments(stack).entrySet().stream().filter(ent -> ent.getKey().isCurse()).collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue)), stack);
                return new Pair<>(stack, List.of(1, 1));
            } else if (right.getItem() == EnchModule.PRISMATIC_WEB) {
                ItemStack stack = left.copy();
                EnchantmentHelper.setEnchantments(EnchantmentHelper.getEnchantments(stack).entrySet().stream().filter(ent -> !ent.getKey().isCurse()).collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue)), stack);
                return new Pair<>(stack, List.of(30, 1));
            }
        }
        if ((left.getItem() == Items.CHIPPED_ANVIL || left.getItem() == Items.DAMAGED_ANVIL) && right.is(Apoth.IRON_BLOCKS)) {
            if (left.getCount() != 1) return new Pair<>(ItemStack.EMPTY, List.of(cost, materialCost));
            int dmg = left.getItem() == Items.DAMAGED_ANVIL ? 2 : 1;
            ItemStack out = new ItemStack(dmg == 1 ? Items.ANVIL : Items.CHIPPED_ANVIL);
            EnchantmentHelper.setEnchantments(EnchantmentHelper.getEnchantments(left), out);
            out.setCount(1);
            int newCost = 5 + EnchantmentHelper.getItemEnchantmentLevel(Enchantments.UNBREAKING, left) /* + EnchantmentHelper.getItemEnchantmentLevel(Apoth.Enchantments.SPLITTING, left) */ * 2;
            return new Pair<>(out, List.of(newCost, 1));
        }

        Pair<ItemStack, List<Integer>> scrapTome = ScrappingTomeItem.updateAnvil(left, right, player);
        if (scrapTome != null) {
            return scrapTome;
        }
        return new Pair<>(ItemStack.EMPTY, List.of(cost, materialCost));
    }

    public static float anvilRepair(Player player, float prev) {
        if (player.containerMenu instanceof AnvilMenu) {
            AnvilMenu r = (AnvilMenu) player.containerMenu;
            BlockEntity te = r.access.evaluate(Level::getBlockEntity).orElse(null);
        }
        return prev;
    }

    public interface TridentGetter {
        ItemStack getTridentItem();
    }
}
