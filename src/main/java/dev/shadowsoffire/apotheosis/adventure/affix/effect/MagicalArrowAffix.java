package dev.shadowsoffire.apotheosis.adventure.affix.effect;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.shadowsoffire.apotheosis.adventure.Adventure.Affixes;
import dev.shadowsoffire.apotheosis.adventure.affix.Affix;
import dev.shadowsoffire.apotheosis.adventure.affix.AffixHelper;
import dev.shadowsoffire.apotheosis.adventure.affix.AffixType;
import dev.shadowsoffire.apotheosis.adventure.loot.LootCategory;
import dev.shadowsoffire.apotheosis.adventure.loot.LootRarity;
import io.github.fabricators_of_create.porting_lib.entity.events.living.LivingEntityDamageEvents;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.item.ItemStack;

public class MagicalArrowAffix extends Affix {

    public static final Codec<MagicalArrowAffix> CODEC = RecordCodecBuilder.create(inst -> inst
        .group(
            LootRarity.CODEC.fieldOf("min_rarity").forGetter(a -> a.minRarity))
        .apply(inst, MagicalArrowAffix::new));

    protected LootRarity minRarity;

    public MagicalArrowAffix(LootRarity minRarity) {
        super(AffixType.ABILITY);
        this.minRarity = minRarity;
    }

    @Override
    public boolean canApplyTo(ItemStack stack, LootCategory cat, LootRarity rarity) {
        return cat.isRanged() && rarity.isAtLeast(this.minRarity);
    }

    // EventPriority.HIGH
    public void onHurt(LivingEntityDamageEvents.HurtEvent e) {
        if (e.damageSource.getDirectEntity() instanceof AbstractArrow arrow) {
            if (AffixHelper.getAffixes(arrow).containsKey(Affixes.MAGICAL)) {
                // e.damageSource.setMagic(); TODO: Forge event needs updating with a setDamageSource method.
            }
        }
    }

    @Override
    public Codec<? extends Affix> getCodec() {
        return CODEC;
    }

}
