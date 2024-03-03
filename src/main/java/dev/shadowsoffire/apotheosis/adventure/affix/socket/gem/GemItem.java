package dev.shadowsoffire.apotheosis.adventure.affix.socket.gem;

import dev.shadowsoffire.apotheosis.adventure.Adventure.Items;
import dev.shadowsoffire.apotheosis.adventure.affix.AffixHelper;
import dev.shadowsoffire.apotheosis.adventure.loot.LootRarity;
import dev.shadowsoffire.apotheosis.adventure.loot.RarityRegistry;
import dev.shadowsoffire.placebo.reload.DynamicHolder;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.stream.Stream;

public class GemItem extends Item  {

    public static final String HAS_REFRESHED = "has_refreshed";
    public static final String UUID_ARRAY = "uuids";
    public static final String GEM = "gem";

    public GemItem(Properties pProperties) {
        super(pProperties);
    }

    @Environment(EnvType.CLIENT)
    @Override
    public void appendHoverText(ItemStack stack, Level pLevel, List<Component> tooltip, TooltipFlag isAdvanced) {
        GemInstance inst = GemInstance.unsocketed(stack);
        if (!inst.isValidUnsocketed()) {
            tooltip.add(Component.literal("Errored gem with no bonus!").withStyle(ChatFormatting.GRAY));
            return;
        }
        inst.gem().get().addInformation(stack, inst.rarity().get(), tooltip::add);
    }

    @Override
    public Component getName(ItemStack pStack) {
        GemInstance inst = GemInstance.unsocketed(pStack);
        if (!inst.isValidUnsocketed()) return super.getName(pStack);
        MutableComponent comp = Component.translatable(this.getDescriptionId(pStack));
        comp = Component.translatable("item.zenith.gem." + inst.rarity().getId(), comp);
        return comp.withStyle(Style.EMPTY.withColor(inst.rarity().get().getColor()));
    }

    @Override
    public String getDescriptionId(ItemStack pStack) {
        DynamicHolder<Gem> gem = getGem(pStack);
        if (!gem.isBound()) return super.getDescriptionId();
        return super.getDescriptionId(pStack) + "." + gem.getId();
    }

    @Override
    public boolean isFoil(ItemStack pStack) {
        GemInstance inst = GemInstance.unsocketed(pStack);
        if (!inst.isValidUnsocketed()) return super.isFoil(pStack);
        return inst.isMaxRarity();
    }

    @Override
    public boolean canBeHurtBy(DamageSource src) {
        return super.canBeHurtBy(src) && !src.is(DamageTypes.FALLING_ANVIL);
    }

    public static void fillItemCategory(CreativeModeTab.Output out) {
        GemRegistry.INSTANCE.getValues().stream().sorted(Comparator.comparing(Gem::getId)).forEach(gem -> {
            ItemStack[] sortedStacks = new ItemStack[RarityRegistry.INSTANCE.getValues().size()];
            for (LootRarity rarity : RarityRegistry.INSTANCE.getValues()) {
                if (gem.clamp(rarity) != rarity) continue;
                ItemStack stack = new ItemStack(Items.GEM);
                setGem(stack, gem);

                AffixHelper.setRarity(stack, rarity);
                sortedStacks[rarity.ordinal()] = (stack);
            }
            out.acceptAll(Stream.of(sortedStacks).filter(Objects::nonNull).toList());
        });
    }

    /**
     * Retrieves cached attribute modifier UUID(s) from a gem itemstack.<br>
     * This method simply invokes {link #getUUIDs(CompoundTag, int)} with the root tag
     * and the {@linkplain Gem#getNumberOfUUIDs() Gem's requested UUID count}.
     *
     * @param gemStack The gem stack
     * @returns The stored UUID(s), creating them if they do not exist.
     */
    public static List<UUID> getUUIDs(ItemStack gemStack) {
        DynamicHolder<Gem> gem = getGem(gemStack);
        if (!gem.isBound()) return Collections.emptyList();
        return getOrCreateUUIDs(gemStack.getOrCreateTag(), gem.get().getNumberOfUUIDs());
    }

    @Override
    @Nullable
    public String getCreatorModId(ItemStack stack) {
        GemInstance inst = GemInstance.unsocketed(stack);
        if (inst.isValidUnsocketed()) {
            return inst.gem().getId().getNamespace();
        }
        return super.getCreatorModId(stack);
    }

    /**
     * Retrieves cached attribute modifier UUID(s) from an itemstack.
     *
     * @param tag The gem's tag
     * @returns The stored UUID(s), creating them if they do not exist.
     */
    public static List<UUID> getOrCreateUUIDs(CompoundTag tag, int numUUIDs) {
        if (numUUIDs == 0) return Collections.emptyList();
        if (tag.contains(UUID_ARRAY)) {
            ListTag list = tag.getList(UUID_ARRAY, Tag.TAG_INT_ARRAY);
            List<UUID> ret = new ArrayList<>(list.size());
            for (Tag t : list) {
                ret.add(NbtUtils.loadUUID(t));
            }
            if (ret.size() < numUUIDs) return generateAndSave(ret, numUUIDs, tag);
            return ret;
        }
        return generateAndSave(new ArrayList<>(numUUIDs), numUUIDs, tag);
    }

    private static List<UUID> generateAndSave(List<UUID> base, int amount, CompoundTag tag) {
        int needed = amount - base.size();
        for (int i = 0; i < needed; i++) {
            base.add(UUID.randomUUID());
        }
        ListTag list = new ListTag();
        for (UUID id : base) {
            list.add(NbtUtils.createUUID(id));
        }
        tag.put(UUID_ARRAY, list);
        return base;
    }

    /**
     * Sets the ID of the gem stored in this gem stack.
     *
     * @param gemStack The gem stack
     * @param gem      The Gem to store
     */
    public static void setGem(ItemStack gemStack, Gem gem) {
        gemStack.getOrCreateTag().putString(GEM, gem.getId().toString());
    }

    /**
     * Retrieves the underlying Gem instance of this gem stack.
     *
     * @param gem The gem stack
     * @return A {@link DynamicHolder} targetting the gem, which may be unbound if the gem is missing or invalid.
     */
    public static DynamicHolder<Gem> getGem(ItemStack gem) {
        if (gem.getItem() != Items.GEM || !gem.hasTag()) return GemRegistry.INSTANCE.emptyHolder();
        var tag = gem.getTag();
        return GemRegistry.INSTANCE.holder(new ResourceLocation(tag.getString(GEM)));
    }
}
