package safro.apotheosis.deadly;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Multimap;
import com.google.common.collect.TreeMultimap;
import net.fabricmc.fabric.api.command.v1.CommandRegistrationCallback;
import net.minecraft.core.Registry;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import safro.apotheosis.api.ModifiableAttributes;
import safro.apotheosis.deadly.commands.CategoryCheckCommand;
import safro.apotheosis.deadly.commands.LootifyCommand;
import safro.apotheosis.deadly.commands.ModifierCommand;
import safro.apotheosis.deadly.commands.RarityCommand;
import safro.apotheosis.deadly.loot.LootCategory;
import safro.apotheosis.deadly.loot.affix.Affix;
import safro.apotheosis.deadly.loot.affix.AffixHelper;

import java.util.Map;
import java.util.Set;

public class DeadlyModuleEvents {

    public static void init() {
        CommandRegistrationCallback.EVENT.register(((dispatcher, dedicated) -> {
            CategoryCheckCommand.register(dispatcher);
            LootifyCommand.register(dispatcher);
            ModifierCommand.register(dispatcher);
            RarityCommand.register(dispatcher);
        }));
    }

    public static Multimap<Attribute, AttributeModifier> sortModifiers(ItemStack stack, EquipmentSlot equipmentSlot, Multimap<Attribute,AttributeModifier> modifiers) {
        if (modifiers == null || modifiers.isEmpty()) return null;
        Multimap<Attribute, AttributeModifier> map = TreeMultimap.create((k1, k2) -> Registry.ATTRIBUTE.getKey(k1).compareTo(Registry.ATTRIBUTE.getKey(k2)), (v1, v2) -> {
            int compOp = Integer.compare(v1.getOperation().ordinal(), v2.getOperation().ordinal());
            int compValue = Double.compare(v2.getAmount(), v1.getAmount());
            return compOp == 0 ? compValue == 0 ? v1.getName().compareTo(v2.getName()) : compValue : compOp;
        });
        for (Map.Entry<Attribute, AttributeModifier> ent : modifiers.entries()) {
            if (ent.getKey() != null && ent.getValue() != null) map.put(ent.getKey(), ent.getValue());
            else DeadlyModule.LOGGER.debug("Detected broken attribute modifier entry on item {}.  Attr={}, Modif={}", stack, ent.getKey(), ent.getValue());
        }
        return map;
    }

    public static void affixModifiers(ItemStack stack, EquipmentSlot equipmentSlot, Multimap<Attribute,AttributeModifier> modifiers) {
        if (stack.hasTag()) {
            Map<Affix, Float> affixes = AffixHelper.getAffixes(stack);
            ModifiableAttributes modif = (ModifiableAttributes) (Object) stack;
            affixes.forEach((afx, lvl) -> afx.addModifiers(stack, lvl, equipmentSlot, modif::addModifier));
        }
    }

    private static final Set<Float> values = ImmutableSet.of(0.1F, 0.2F, 0.25F, 0.33F, 0.5F, 1.0F, 1.1F, 1.2F, 1.25F, 1.33F, 1.5F, 2.0F, 2.1F, 2.25F, 2.33F, 2.5F, 3F);

    /**
     * This event handler makes the Draw Speed attribute work as intended.
     * Modifiers targetting this attribute should use the MULTIPLY_BASE operation.
     */
    public static int drawSpeed(LivingEntity e, ItemStack item, int currentTicks) {
        if (e instanceof Player player) {
            double t = player.getAttribute(DeadlyModule.DRAW_SPEED).getValue() - 1;
            if (t == 0 || !LootCategory.forItem(item).isRanged()) return currentTicks;
            float clamped = values.stream().filter(f -> f >= t).min(Float::compareTo).orElse(3F);
            while (clamped > 0) {
                if (e.tickCount % (int) Math.floor(1 / Math.min(1, t)) == 0) currentTicks--;
                clamped--;
            }
        }
        return currentTicks;
    }

}
