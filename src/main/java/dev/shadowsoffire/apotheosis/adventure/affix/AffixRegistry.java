package dev.shadowsoffire.apotheosis.adventure.affix;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;
import dev.shadowsoffire.apotheosis.Apotheosis;
import dev.shadowsoffire.apotheosis.adventure.Adventure.Affixes;
import dev.shadowsoffire.apotheosis.adventure.AdventureModule;
import dev.shadowsoffire.apotheosis.adventure.affix.effect.*;
import dev.shadowsoffire.apotheosis.adventure.client.AdventureModuleClient;
import dev.shadowsoffire.apotheosis.adventure.loot.RarityRegistry;
import dev.shadowsoffire.placebo.reload.DynamicHolder;
import dev.shadowsoffire.placebo.reload.DynamicRegistry;
import net.fabricmc.api.EnvType;
import net.fabricmc.loader.api.FabricLoader;

public class AffixRegistry extends DynamicRegistry<Affix> {

    public static final AffixRegistry INSTANCE = new AffixRegistry();

    private Multimap<AffixType, DynamicHolder<Affix>> byType = ImmutableMultimap.of();

    public AffixRegistry() {
        super(AdventureModule.LOGGER, "affixes", true, true);
    }

    @Override
    protected void beginReload() {
        if (!Apotheosis.enableAdventure) return;
        super.beginReload();
        this.byType = ImmutableMultimap.of();
    }

    @Override
    protected void onReload() {
        if (!Apotheosis.enableAdventure) return;
        super.onReload();
        ImmutableMultimap.Builder<AffixType, DynamicHolder<Affix>> builder = ImmutableMultimap.builder();
        this.registry.values().forEach(a -> builder.put(a.type, this.holder(a)));
        this.byType = builder.build();
        Preconditions.checkArgument(Affixes.DURABLE.get() instanceof DurableAffix, "Durable Affix not registered!");
        if (FabricLoader.getInstance().getEnvironmentType() == EnvType.CLIENT && Apotheosis.enableDebug) {
            AdventureModuleClient.checkAffixLangKeys();
        }
        RarityRegistry.INSTANCE.validateLootRules();
    }

    @Override
    protected void registerBuiltinCodecs() {
        this.registerCodec(Apotheosis.loc("attribute"), AttributeAffix.CODEC);
        this.registerCodec(Apotheosis.loc("mob_effect"), PotionAffix.CODEC);
        this.registerCodec(Apotheosis.loc("damage_reduction"), DamageReductionAffix.CODEC);
        this.registerCodec(Apotheosis.loc("catalyzing"), CatalyzingAffix.CODEC);
        this.registerCodec(Apotheosis.loc("cleaving"), CleavingAffix.CODEC);
        this.registerCodec(Apotheosis.loc("enlightened"), EnlightenedAffix.CODEC);
        this.registerCodec(Apotheosis.loc("executing"), ExecutingAffix.CODEC);
        this.registerCodec(Apotheosis.loc("festive"), FestiveAffix.CODEC);
        this.registerCodec(Apotheosis.loc("magical"), MagicalArrowAffix.CODEC);
        this.registerCodec(Apotheosis.loc("omnetic"), OmneticAffix.CODEC);
        this.registerCodec(Apotheosis.loc("psychic"), PsychicAffix.CODEC);
        this.registerCodec(Apotheosis.loc("radial"), RadialAffix.CODEC);
        this.registerCodec(Apotheosis.loc("retreating"), RetreatingAffix.CODEC);
        this.registerCodec(Apotheosis.loc("spectral"), SpectralShotAffix.CODEC);
        this.registerCodec(Apotheosis.loc("telepathic"), TelepathicAffix.CODEC);
        this.registerCodec(Apotheosis.loc("thunderstruck"), ThunderstruckAffix.CODEC);
        this.registerCodec(Apotheosis.loc("durable"), DurableAffix.CODEC);
    }

    public Multimap<AffixType, DynamicHolder<Affix>> getTypeMap() {
        return this.byType;
    }

}
