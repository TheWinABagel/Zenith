package dev.shadowsoffire.apotheosis.adventure.socket.gem.bonus.special;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableSet;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.shadowsoffire.apotheosis.Apotheosis;
import dev.shadowsoffire.apotheosis.adventure.AdventureModule;
import dev.shadowsoffire.apotheosis.adventure.affix.Affix;
import dev.shadowsoffire.apotheosis.adventure.loot.LootCategory;
import dev.shadowsoffire.apotheosis.adventure.loot.LootRarity;
import dev.shadowsoffire.apotheosis.adventure.socket.gem.GemClass;
import dev.shadowsoffire.apotheosis.adventure.socket.gem.bonus.GemBonus;
import dev.shadowsoffire.placebo.util.StepFunction;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;

import java.util.Map;

/**
 * Reduces magic damage by a certain percentage.
 */
public class MageSlayerBonus extends GemBonus {

    public static Codec<MageSlayerBonus> CODEC = RecordCodecBuilder.create(inst -> inst
        .group(
            VALUES_CODEC.fieldOf("values").forGetter(a -> a.values))
        .apply(inst, MageSlayerBonus::new));

    protected final Map<LootRarity, StepFunction> values;

    public MageSlayerBonus(Map<LootRarity, StepFunction> values) {
        super(Apotheosis.loc("mageslayer"), new GemClass("helmet", ImmutableSet.of(LootCategory.HELMET)));
        this.values = values;
    }

    @Override
    public float onHurt(ItemStack gem, LootRarity rarity, DamageSource src, LivingEntity user, float amount) {
        float value = this.values.get(rarity).min();
        if (src.is(DamageTypeTags.BYPASSES_ARMOR)) { //No MAGIC tag
            user.heal(amount * (1 - value));
            if (Apotheosis.enableDebug) AdventureModule.LOGGER.info("Mage slayer triggered, healing for {}, value is {}, total taken is {}", (amount * (1 - value)), amount, amount - (amount * (1 - value)));
            return amount;
        }
        return super.onHurt(gem, rarity, src, user, amount);
    }

    @Override
    public Codec<? extends GemBonus> getCodec() {
        return CODEC;
    }

    @Override
    public Component getSocketBonusTooltip(ItemStack gem, LootRarity rarity) {
        float value = this.values.get(rarity).min();
        return Component.translatable("bonus." + this.getId() + ".desc", Affix.fmt(value * 100)).withStyle(ChatFormatting.YELLOW);
    }

    @Override
    public MageSlayerBonus validate() {
        Preconditions.checkNotNull(this.values);
        this.values.forEach((k, v) -> {
            Preconditions.checkNotNull(k);
            Preconditions.checkNotNull(v);
        });
        return this;
    }

    @Override
    public boolean supports(LootRarity rarity) {
        return this.values.containsKey(rarity);
    }

    @Override
    public int getNumberOfUUIDs() {
        return 0;
    }
}
