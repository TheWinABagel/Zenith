package safro.zenith.adventure;

import com.google.common.collect.ImmutableSet;
import net.fabricmc.fabric.api.item.v1.ModifyItemAttributeModifiersCallback;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.AttributeModifier.Operation;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import safro.zenith.adventure.affix.Affix;
import safro.zenith.adventure.affix.AffixHelper;
import safro.zenith.adventure.affix.AffixInstance;
import safro.zenith.adventure.commands.GemCommand;
import safro.zenith.adventure.commands.LootifyCommand;
import safro.zenith.adventure.commands.RarityCommand;
import safro.zenith.adventure.commands.SocketCommand;
import safro.zenith.adventure.loot.LootCategory;

import java.util.Map;
import java.util.Set;
import java.util.UUID;

public class AdventureEvents {

	static void init() {
		affixModifiers();
		LootifyCommand.init();
		RarityCommand.init();
		GemCommand.init();
		SocketCommand.init();
	}

/*
	public void reloads(AddReloadListenerEvent e) {
	}

	public void cmds(ApotheosisCommandEvent e) {
		CategoryCheckCommand.register(e.getRoot());
		ModifierCommand.register(e.getRoot());
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
			if (t == 0 || !LootCategory.forItem(item).isRanged()) return currentTicks;
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

	public void pierce(LivingHurtEvent e) {
		if (e.getSource().getDirectEntity() instanceof LivingEntity attacker) {
			if (!e.getSource().isBypassArmor() && !e.getSource().isMagic()) {
				LivingEntity target = e.getEntity();
				float pierce = (float) (attacker.getAttributeValue(AdventureModule.PIERCING) - 1);
				if (pierce > 0.001) {
					float pierceDmg = e.getAmount() * pierce;
					e.setAmount(e.getAmount() - pierceDmg);
					int time = target.invulnerableTime;
					target.invulnerableTime = 0;
					target.hurt(DamageSourceUtil.copy(e.getSource()).bypassArmor(), pierceDmg);
					target.invulnerableTime = time;
				}
			}
		}
	}

	public void onDamage(LivingHurtEvent e) {
		Apoth.Affixes.MAGICAL.ifPresent(afx -> afx.onHurt(e));
		DamageSource src = e.getSource();
		LivingEntity ent = e.getEntity();
		float amount = e.getAmount();
		for (ItemStack s : ent.getAllSlots()) {
			Map<Affix, AffixInstance> affixes = AffixHelper.getAffixes(s);
			for (AffixInstance inst : affixes.values()) {
				amount = inst.onHurt(src, ent, amount);
			}
		}
		e.setAmount(amount);
	}

	/**
	 * This event handler manages the Life Steal and Overheal attributes.
	 */
/*	public void afterDamage(LivingHurtEvent e) {
		if (e.getSource().getDirectEntity() instanceof LivingEntity attacker && !e.getSource().isMagic()) {
			float lifesteal = (float) attacker.getAttributeValue(AdventureModule.LIFE_STEAL) - 1;
			float dmg = Math.min(e.getAmount(), e.getEntity().getHealth());
			if (lifesteal > 0.001) {
				attacker.heal(dmg * lifesteal);
			}
			float overheal = (float) attacker.getAttributeValue(AdventureModule.OVERHEAL) - 1;
			if (overheal > 0 && attacker.getAbsorptionAmount() < 20) {
				attacker.setAbsorptionAmount(Math.min(20, attacker.getAbsorptionAmount() + dmg * overheal));
			}
		}

		if (e.getSource() == DamageSource.IN_WALL && e.getEntity().getPersistentData().contains("apoth.boss")) {
			e.setCanceled(true);
		}
	}

	private static boolean noRecurse = false;

	LivingEntityEvents.Attack

	public void attack(LivingAttackEvent e) {
		if (e.getEntity().level.isClientSide) return;
		if (noRecurse) return;
		noRecurse = true;
		Entity direct = e.getSource().getDirectEntity();
		direct = direct instanceof AbstractArrow arr ? arr.getOwner() : direct;
		if (direct instanceof LivingEntity attacker && !e.getSource().isMagic()) {
			float hpDmg = (float) attacker.getAttributeValue(AdventureModule.CURRENT_HP_DAMAGE) - 1;
			float fireDmg = (float) attacker.getAttributeValue(AdventureModule.FIRE_DAMAGE);
			float coldDmg = (float) attacker.getAttributeValue(AdventureModule.COLD_DAMAGE);
			LivingEntity target = e.getEntity();
			int time = target.invulnerableTime;
			target.invulnerableTime = 0;
			if (hpDmg > 0.001 && Zenith.localAtkStrength >= 0.85F) {
				target.hurt(src(attacker), Zenith.localAtkStrength * hpDmg * target.getHealth());
			}
			target.invulnerableTime = 0;
			if (fireDmg > 0.001 && Zenith.localAtkStrength >= 0.45F) {
				target.hurt(src(attacker).setMagic().bypassArmor(), Zenith.localAtkStrength * fireDmg);
				target.setRemainingFireTicks(Math.max(target.getRemainingFireTicks(), (int) (15 * fireDmg)));
			}
			target.invulnerableTime = 0;
			if (coldDmg > 0.001 && Zenith.localAtkStrength >= 0.45F) {
				target.hurt(src(attacker).setMagic().bypassArmor(), Zenith.localAtkStrength * coldDmg);
				target.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, (int) (15 * coldDmg), Mth.floor(coldDmg / 5)));
			}
			target.invulnerableTime = time;
		}
		noRecurse = false;
	}

	private static DamageSource src(LivingEntity entity) {
		return entity instanceof Player p ? DamageSource.playerAttack(p) : DamageSource.mobAttack(entity);
	}

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

	public void breakSpd(PlayerEvents.BreakSpeed e) {
		e.setNewSpeed(e.getNewSpeed() * (float) e.getEntity().getAttributeValue(AdventureModule.MINING_SPEED));
	}

	public void onItemUse(ItemUseEvent e) {
		ItemStack s = e.getItemStack();
		Map<Affix, AffixInstance> affixes = AffixHelper.getAffixes(s);
		for (AffixInstance inst : affixes.values()) {
			InteractionResult type = inst.onItemUse(e.getContext());
			if (type != null) {
				e.setCanceled(true);
				e.setCancellationResult(type);
			}
		}
	}

	public void shieldBlock(ShieldBlockEvent e) {
		ItemStack stack = e.getEntity().getUseItem();
		Map<Affix, AffixInstance> affixes = AffixHelper.getAffixes(stack);
		float blocked = e.getBlockedDamage();
		for (AffixInstance inst : affixes.values()) {
			blocked = inst.onShieldBlock(e.getEntity(), e.getDamageSource(), blocked);
		}
		if (blocked != e.getOriginalBlockedDamage()) e.setBlockedDamage(blocked);
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

	public void mobXp(LivingExperienceDropEvent e) {
		Player player = e.getAttackingPlayer();
		if (player == null) return;
		double xpMult = e.getAttackingPlayer().getAttributeValue(AdventureModule.EXPERIENCE_GAINED);
		e.setDroppedExperience((int) (e.getDroppedExperience() * xpMult));
	}

	public void arrow(EntityJoinLevelEvent e) {
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

	public void gemSmashing(AnvilLandEvent e) {
		Level level = e.getLevel();
		BlockPos pos = e.getPos();
		List<ItemEntity> items = level.getEntitiesOfClass(ItemEntity.class, new AABB(pos, pos.offset(1, 1, 1)));
		for (ItemEntity ent : items) {
			ItemStack stack = ent.getItem();
			if (stack.getItem() == Apoth.Items.GEM.get()) {
				ent.setItem(new ItemStack(Apoth.Items.GEM_DUST.get(), stack.getCount()));
			}
		}
	}

	public void enchLevels(GetEnchantmentLevelEvent e) {
		AffixHelper.streamAffixes(e.getStack()).forEach(inst -> inst.getEnchantmentLevels(e.getEnchantments()));
	}
*/
}