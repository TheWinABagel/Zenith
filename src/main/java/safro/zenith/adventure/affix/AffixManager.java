package safro.zenith.adventure.affix;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;
import safro.zenith.Zenith;
import safro.zenith.adventure.AdventureModule;
import safro.zenith.adventure.affix.socket.SocketAffix;
import safro.zenith.api.placebo.json.PlaceboJsonReloadListener;

public class AffixManager extends PlaceboJsonReloadListener<Affix> {

	public static final AffixManager INSTANCE = new AffixManager();

	private Multimap<AffixType, Affix> byType = ImmutableMultimap.of();

	public AffixManager() {
		super(AdventureModule.LOGGER, "affixes", true, true);
	}

//	public static void init() {
/*		LivingEntityEvents.DROPS_WITH_LEVEL.register(((target, source, drops, lootingLevel, recentlyHit) -> {
			FestiveAffix.drops(source, target, drops);
			return false;
		}));
 */
//	}

	@Override
	protected void beginReload() {
		super.beginReload();
	}

	@Override
	protected void onReload() {
		super.onReload();
		ImmutableMultimap.Builder<AffixType, Affix> builder = ImmutableMultimap.builder();
		this.registry.values().forEach(a -> builder.put(a.type, a));
		byType = builder.build();
		Preconditions.checkArgument(AdventureModule.SOCKET.get() instanceof SocketAffix, "Socket Affix not registered!");
	//	Preconditions.checkArgument(Affixes.DURABLE.get() instanceof DurableAffix, "Durable Affix not registered!");
	}

	@Override
	protected void registerBuiltinSerializers() { /*
		this.registerSerializer(Zenith.loc("attribute"), AttributeAffix.SERIALIZER);
		this.registerSerializer(Zenith.loc("mob_effect"), PotionAffix.SERIALIZER);
		this.registerSerializer(Zenith.loc("damage_reduction"), DamageReductionAffix.SERIALIZER);
		this.registerSerializer(Zenith.loc("catalyzing"), CatalyzingAffix.SERIALIZER);
		this.registerSerializer(Zenith.loc("cleaving"), CleavingAffix.SERIALIZER);
		this.registerSerializer(Zenith.loc("enlightened"), EnlightenedAffix.SERIALIZER);
		this.registerSerializer(Zenith.loc("executing"), ExecutingAffix.SERIALIZER);
		this.registerSerializer(Zenith.loc("festive"), FestiveAffix.SERIALIZER);
		this.registerSerializer(Zenith.loc("magical"), MagicalArrowAffix.SERIALIZER);
		this.registerSerializer(Zenith.loc("omnetic"), OmneticAffix.SERIALIZER);
		this.registerSerializer(Zenith.loc("psychic"), PsychicAffix.SERIALIZER);
		this.registerSerializer(Zenith.loc("radial"), RadialAffix.SERIALIZER);
		this.registerSerializer(Zenith.loc("retreating"), RetreatingAffix.SERIALIZER);
		this.registerSerializer(Zenith.loc("spectral"), SpectralShotAffix.SERIALIZER);
		this.registerSerializer(Zenith.loc("telepathic"), TelepathicAffix.SERIALIZER);
		this.registerSerializer(Zenith.loc("thunderstruck"), ThunderstruckAffix.SERIALIZER);
		this.registerSerializer(Zenith.loc("durable"), DurableAffix.SERIALIZER);*/
		this.registerSerializer(Zenith.loc("socket"), SocketAffix.SERIALIZER);

	}

	public Multimap<AffixType, Affix> getTypeMap() {
		return byType;
	}

}
