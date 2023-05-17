package safro.zenith.adventure;

import com.google.common.collect.ImmutableSet;
import io.github.fabricators_of_create.porting_lib.event.common.BlockEvents;
import io.github.fabricators_of_create.porting_lib.event.common.EntityEvents;
import io.github.fabricators_of_create.porting_lib.event.common.LivingEntityEvents;
import io.github.fabricators_of_create.porting_lib.event.common.PlayerEvents;
import net.fabricmc.fabric.api.event.player.UseItemCallback;
import net.fabricmc.fabric.api.item.v1.ModifyItemAttributeModifiersCallback;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.AttributeModifier.Operation;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.entity.projectile.ThrownTrident;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;
import safro.zenith.Zenith;
import safro.zenith.adventure.affix.Affix;
import safro.zenith.adventure.affix.AffixHelper;
import safro.zenith.adventure.affix.AffixInstance;
import safro.zenith.adventure.commands.*;
import safro.zenith.adventure.loot.LootCategory;
import safro.zenith.ench.EnchModuleEvents;
import safro.zenith.util.DamageSourceUtil;

import java.util.Map;
import java.util.Set;
import java.util.UUID;

public class AdventureEvents {

	static void init() {
		affixModifiers();
	//	pierce();
		breakSpd();
		attack();
		onDamage();
		afterDamage();
		LootifyCommand.init();
		RarityCommand.init();
		GemCommand.init();
		SocketCommand.init();
		ModifierCommand.init();
	}

/*
	public void reloads(AddReloadListenerEvent e) {
	}

	public void cmds(ApotheosisCommandEvent e) {
		CategoryCheckCommand.register(e.getRoot());
	}
*/
	private static final UUID HEAVY_WEAPON_AS = UUID.fromString("f8c3de3d-1fea-4d7c-a8b0-29f63c4c3454");



	public static void affixModifiers() {
		ModifyItemAttributeModifiersCallback.EVENT.register((stack, slot, attributeModifiers) -> {
			if (stack.hasTag()) {
				Map<Affix, AffixInstance> affixes = AffixHelper.getAffixes(stack);
				affixes.forEach((afx, inst) -> inst.addModifiers(slot, attributeModifiers::put));
				if (AffixHelper.getRarity(stack) != null && LootCategory.forItem(stack) == LootCategory.HEAVY_WEAPON && slot.getIndex() == EquipmentSlot.MAINHAND.getIndex()) {
					double amt = -0.15 - 0.10 * (AffixHelper.getRarity(stack).ordinal());
					attributeModifiers.put(Attributes.ATTACK_SPEED, new AttributeModifier(HEAVY_WEAPON_AS, "Heavy Weapon AS", amt, Operation.MULTIPLY_TOTAL));
				}
			}
		});
	}

	private static final Set<Float> values = ImmutableSet.of(0.1F, 0.2F, 0.25F, 0.33F, 0.5F, 1.0F, 1.1F, 1.2F, 1.25F, 1.33F, 1.5F, 2.0F, 2.1F, 2.25F, 2.33F, 2.5F, 3F);

	/**
	 * This event handler makes the Draw Speed attribute work as intended.
	 * Modifiers targetting this attribute should use the MULTIPLY_BASE operation.
	 */
	public static int drawSpeed(LivingEntity e, ItemStack item, int currentTicks) {
		if (e instanceof Player player) {
			double t = player.getAttribute(AdventureModule.DRAW_SPEED).getValue() - 1;
			if (t == 0 || !isRanged(item)) return currentTicks;
			float clamped = values.stream().filter(f -> f >= t).min(Float::compareTo).orElse(3F);
			while (clamped > 0) {
				if (e.tickCount % (int) Math.floor(1 / Math.min(1, t)) == 0) currentTicks--;
				clamped--;
			}
		}
		return currentTicks;
	}

	private static boolean isRanged(ItemStack item) {
		if (LootCategory.forItem(item) == null) {
			return false;
		}
		return LootCategory.forItem(item).isRanged();
	}

	/**
	 * This event handler allows affixes to react to arrows being fired to trigger additional actions.
	 * Arrows marked as "apoth.generated" will not trigger the affix hook, so affixes can fire arrows without recursion.
	 */
/*	public void fireArrow(EntityJoinLevelEvent e) {
		if (e.getEntity() instanceof AbstractArrow arrow && !arrow.getPersistentData().getBoolean("apoth.generated")) {
			Entity shooter = arrow.getOwner();
			if (shooter instanceof LivingEntity living) {
				ItemStack bow = living.getMainHandItem();
				Map<Affix, AffixInstance> affixes = AffixHelper.getAffixes(bow);
				affixes.values().forEach(a -> {
					a.onArrowFired(living, arrow);
				});
				AffixHelper.copyFrom(bow, arrow);
			}
		}
	}

	/**
	 * This event handler allows affixes to react to arrows hitting something.
	 */
/*	public void impact(ProjectileImpactEvent e) {
		if (e.getProjectile() instanceof AbstractArrow arrow) {
			Map<Affix, AffixInstance> affixes = AffixHelper.getAffixes(arrow);
			affixes.values().forEach(inst -> inst.onArrowImpact(arrow, e.getRayTraceResult(), e.getRayTraceResult().getType()));
		}
	}
*/

	public static void pierce() {
		LivingEntityEvents.ACTUALLY_HURT.register(((source, damaged, amount) -> {
			if (source.getDirectEntity() instanceof LivingEntity attacker) {
				if (!source.isBypassArmor() && !source.isMagic()) {
					LivingEntity target = damaged;
					float pierce = (float) (attacker.getAttributeValue(AdventureModule.PIERCING) - 1);
					if (pierce > 0.001) {
						float pierceDmg = amount * pierce;
						float newAmount = (amount - pierceDmg);
						int time = damaged.invulnerableTime;
						damaged.invulnerableTime = 0;
						damaged.hurt(DamageSourceUtil.copy(source).bypassArmor(), pierceDmg);
						damaged.invulnerableTime = time;
						return newAmount;
					}
				}
			}
			return amount;
		}));

	}

	public static void onDamage() {
		LivingEntityEvents.ACTUALLY_HURT.register(((source, damaged, amount) -> {
			float finalAmount = amount;
			AdventureModule.MAGICAL.ifPresent(afx -> afx.onHurt(source, finalAmount));
			LivingEntity ent = (LivingEntity) source.getEntity();
			if (ent==null)
				return amount;
		for (ItemStack s : ent.getAllSlots()) {
			Map<Affix, AffixInstance> affixes = AffixHelper.getAffixes(s);
			for (AffixInstance inst : affixes.values()) {
				amount = inst.onHurt(source, ent, amount);
			}
		}
		return amount;
		}));
	}

	/**
	 * This event handler manages the Life Steal and Overheal attributes.
	 */
	public static void afterDamage() {
		LivingEntityEvents.ACTUALLY_HURT.register(((source, damaged, amount) -> {
		if (source.getDirectEntity() instanceof LivingEntity attacker && !source.isMagic()) {
			float lifesteal = (float) attacker.getAttributeValue(AdventureModule.LIFE_STEAL) - 1;
			AdventureModule.LOGGER.error("lifesteal level: " + lifesteal);
			float dmg = Math.min(amount, damaged.getHealth());
			if (lifesteal > 0.001) {
				attacker.heal(dmg * lifesteal);
			}
			float overheal = (float) attacker.getAttributeValue(AdventureModule.OVERHEAL) - 1;
			AdventureModule.LOGGER.error("overheal level: " + overheal);
			if (overheal > 0 && attacker.getAbsorptionAmount() < 20) {
				attacker.setAbsorptionAmount(Math.min(20, attacker.getAbsorptionAmount() + dmg * overheal));
			}
		}

		if (source == DamageSource.IN_WALL && damaged.getExtraCustomData().contains("apoth.boss")) {
			return 0;
		}

			return amount;
		}));
	}

	private static boolean noRecurse = false;


	public static void attack() {
		LivingEntityEvents.ACTUALLY_HURT.register(((source, damaged, amount) -> {
		if (source.getEntity()==null) return amount;
		if (source.getEntity().level.isClientSide) return amount;
		if (noRecurse) return amount;
		noRecurse = true;
		Entity direct = source.getDirectEntity();
		direct = direct instanceof AbstractArrow arr ? arr.getOwner() : direct;
		if (direct instanceof LivingEntity attacker && !source.isMagic()) {
			float hpDmg = (float) attacker.getAttributeValue(AdventureModule.CURRENT_HP_DAMAGE) - 1;
			float fireDmg = (float) attacker.getAttributeValue(AdventureModule.FIRE_DAMAGE);
			float coldDmg = (float) attacker.getAttributeValue(AdventureModule.COLD_DAMAGE);
			int time = damaged.invulnerableTime;
			damaged.invulnerableTime = 0;
			if (hpDmg > 0.001 && Zenith.localAtkStrength >= 0.85F) {
				damaged.hurt(src(attacker), Zenith.localAtkStrength * hpDmg * damaged.getHealth());
			}
			damaged.invulnerableTime = 0;
			if (fireDmg > 0.001 && Zenith.localAtkStrength >= 0.45F) {
				damaged.hurt(src(attacker).setMagic().bypassArmor(), Zenith.localAtkStrength * fireDmg);
				damaged.setRemainingFireTicks(Math.max(damaged.getRemainingFireTicks(), (int) (15 * fireDmg)));
			}
			damaged.invulnerableTime = 0;
			if (coldDmg > 0.001 && Zenith.localAtkStrength >= 0.45F) {
				damaged.hurt(src(attacker).setMagic().bypassArmor(), Zenith.localAtkStrength * coldDmg);
				damaged.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, (int) (15 * coldDmg), Mth.floor(coldDmg / 5)));
			}
			damaged.invulnerableTime = time;
		}
		noRecurse = false;
			return amount;
		}));
	}

	private static DamageSource src(LivingEntity entity) {
		return entity instanceof Player p ? DamageSource.playerAttack(p) : DamageSource.mobAttack(entity);
	}
/*
	public void crit(CriticalHitEvent e) {
		double critChance = e.getEntity().getAttributeValue(AdventureModule.CRIT_CHANCE) - 1;
		float critDmg = (float) e.getEntity().getAttributeValue(AdventureModule.CRIT_DAMAGE);
		float overcritMult = Math.max(1.5F, critDmg - 1.5F);
		RandomSource rand = e.getEntity().random;
		if (e.isVanillaCritical() && critChance >= 0.5F) {
			critChance -= 0.5F;
			critDmg *= 1.5F;
		}

		// Roll once to determine if the attack should become a crit.
		if (rand.nextFloat() <= critChance || critChance >= 1) {
			e.setResult(Result.ALLOW);
		}
		// Reduce the chance since this roll "consumes" 1 point.
		critChance--;

		// Roll for overcrit
		while (rand.nextFloat() <= critChance) {
			e.setResult(Result.ALLOW);
			critChance--;
			critDmg *= overcritMult;
		}

		e.setDamageModifier(critDmg);
	}
*/
	public static void breakSpd() {

		PlayerEvents.BREAK_SPEED.register((player) -> {
			player.setNewSpeed(player.getNewSpeed() * (float) ((LivingEntity) (player.getEntity())).getAttributeValue(AdventureModule.MINING_SPEED));
		});
	}
//TODO figure out how to get this to work
	public void onItemUse(){
	UseItemCallback.EVENT.register((player, world, hand) -> {
			ItemStack s = player.getItemInHand(hand);
			Map<Affix, AffixInstance> affixes = AffixHelper.getAffixes(s);
			for (AffixInstance inst : affixes.values()) {
			//	InteractionResult type = inst.onItemUse(player.getContext());
			//	if (type != null) {
				//	return;
			//	}
			}
		return InteractionResultHolder.success(player.getItemInHand(hand));
	});
	}



	public void blockBreak(BlockEvents.BreakEvent e) {
			double xpMult = e.getPlayer().getAttributeValue(AdventureModule.EXPERIENCE_GAINED);
			e.setExpToDrop((int) (e.getExpToDrop() * xpMult));
			ItemStack stack = e.getPlayer().getMainHandItem();
			Map<Affix, AffixInstance> affixes = AffixHelper.getAffixes(stack);
			for (AffixInstance inst : affixes.values()) {
				inst.onBlockBreak(e.getPlayer(), e.getWorld(), e.getPos(), e.getState());
			}
		}

/*
	public void mobXp(LivingExperienceDropEvent e) {
		Player player = e.getAttackingPlayer();
		if (player == null) return;
		double xpMult = e.getAttackingPlayer().getAttributeValue(AdventureModule.EXPERIENCE_GAINED);
		e.setDroppedExperience((int) (e.getDroppedExperience() * xpMult));
	}

	public void arrow(EntityJoinLevelEvent e) {
		EntityEvents.ON_JOIN_WORLD.register();
		if (e.getEntity() instanceof AbstractArrow arrow) {
			if (arrow.level.isClientSide || arrow.getExtraCustomData().getBoolean("apoth.attrib.done")) return; //TODO add custom data
			if (arrow.getOwner() instanceof LivingEntity le) {
				arrow.setBaseDamage(arrow.getBaseDamage() * le.getAttributeValue(AdventureModule.ARROW_DAMAGE));
				arrow.setDeltaMovement(arrow.getDeltaMovement().scale(le.getAttributeValue(AdventureModule.ARROW_VELOCITY)));
				if (!arrow.isCritArrow()) arrow.setCritArrow(arrow.random.nextFloat() <= le.getAttributeValue(AdventureModule.CRIT_CHANCE) - 1);
			}
			arrow.getExtraCustomData().putBoolean("apoth.attrib.done", true);
		}
	}

	public void dropsHigh(LivingDropsEvent e) {
		if (e.getSource().getEntity() instanceof ServerPlayer p && e.getEntity() instanceof Monster) {
			if (p instanceof FakePlayer) return;
			float chance = AdventureConfig.gemDropChance + (e.getEntity().getPersistentData().contains("apoth.boss") ? AdventureConfig.gemBossBonus : 0);
			if (p.random.nextFloat() <= chance) {
				Entity ent = e.getEntity();
				e.getDrops().add(new ItemEntity(ent.level, ent.getX(), ent.getY(), ent.getZ(), GemManager.createRandomGemStack(p.random, (ServerLevel) p.level, p.getLuck(), WeightedJsonReloadListener.IDimensional.matches(p.getLevel()), IStaged.matches(p)), 0, 0, 0));
			}
		}
	}

	public void drops(LivingDropsEvent e) {
		Apoth.Affixes.FESTIVE.ifPresent(afx -> afx.drops(e));
	}

	public void deathMark(LivingDeathEvent e) {
		AdventureModule.FESTIVE.ifPresent(afx -> afx.markEquipment(e));
	}

	public void dropsLowest(LivingDropsEvent e) {
		TelepathicAffix.drops(e);
	}

	public void harvest(HarvestCheck e) {
		Apoth.Affixes.OMNETIC.ifPresent(afx -> afx.harvest(e));
	}

	public void speed(PlayerEvents.BreakSpeed e) {
		AdventureModule.OMNETIC.ifPresent(afx -> afx.speed(e));
	}

	public void onBreak(BlockEvents.BreakEvent e) {
		Apoth.Affixes.RADIAL.ifPresent(afx -> afx.onBreak(e));
	}

	public void special(SpecialSpawn e) {
		if (e.getSpawnReason() == MobSpawnType.NATURAL && e.getLevel().getRandom().nextFloat() <= AdventureConfig.randomAffixItem && e.getEntity() instanceof Monster) {
			e.setCanceled(true);
			Player player = e.getLevel().getNearestPlayer(e.getX(), e.getY(), e.getZ(), -1, false);
			if (player == null) return;
			ItemStack affixItem = LootController.createRandomLootItem(e.getLevel().getRandom(), null, player, (ServerLevel) e.getEntity().level);
			if (affixItem.isEmpty()) return;
			affixItem.getOrCreateTag().putBoolean("apoth_rspawn", true);
			LootCategory cat = LootCategory.forItem(affixItem);
			EquipmentSlot slot = cat.getSlots(affixItem)[0];
			e.getEntity().setItemSlot(slot, affixItem);
			e.getEntity().setGuaranteedDrop(slot);
		}
	}



	public void enchLevels(GetEnchantmentLevelEvent e) {
		AffixHelper.streamAffixes(e.getStack()).forEach(inst -> inst.getEnchantmentLevels(e.getEnchantments()));
	}
*/
}
