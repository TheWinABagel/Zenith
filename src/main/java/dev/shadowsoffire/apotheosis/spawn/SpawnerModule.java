package dev.shadowsoffire.apotheosis.spawn;

import com.google.common.collect.ImmutableSet;
import dev.shadowsoffire.apotheosis.Apoth;
import dev.shadowsoffire.apotheosis.Apotheosis;
import dev.shadowsoffire.apotheosis.spawn.enchantment.CapturingEnchant;
import dev.shadowsoffire.apotheosis.spawn.modifiers.SpawnerModifier;
import dev.shadowsoffire.apotheosis.spawn.spawner.ApothSpawnerTile;
import dev.shadowsoffire.placebo.config.Configuration;
import dev.shadowsoffire.placebo.tabs.TabFillingRegistry;
import io.github.fabricators_of_create.porting_lib.entity.events.EntityMoveEvents;
import io.github.fabricators_of_create.porting_lib.entity.events.living.LivingEntityEvents;
import io.github.fabricators_of_create.porting_lib.entity.events.living.LivingEntityLootEvents;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.item.v1.ItemTooltipCallback;
import net.fabricmc.fabric.api.event.player.UseBlockCallback;
import net.minecraft.ChatFormatting;
import net.minecraft.ResourceLocationException;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.item.*;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.phys.Vec3;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.util.HashSet;
import java.util.Set;

import static dev.shadowsoffire.apotheosis.Apotheosis.enableDebug;

public class SpawnerModule {

    public static final Logger LOG = LogManager.getLogger("Apotheosis : Spawner");
    public static int spawnerSilkLevel = 1;
    public static int spawnerSilkDamage = 100;
    public static Set<ResourceLocation> bannedMobs = new HashSet<>();

    public static void init() {
        dropsEvent();
        handleUseItem();
        tickDumbMobs();
        dumbMobsCantTeleport();
        register();

        reload(false);
    }
// Broken: spawner needs to be redone as mixin? probably still, Jade support, advancements, REI
// not all data is saved when block is broken
    public static void register() {
        Registry.register(BuiltInRegistries.RECIPE_SERIALIZER, Apotheosis.loc("spawner_modifier"), SpawnerModifier.SERIALIZER);
        Registry.register(BuiltInRegistries.ENCHANTMENT, Apotheosis.loc("capturing"), Apoth.Enchantments.CAPTURING);
    }

    public static void dropsEvent() {
        LivingEntityLootEvents.DROPS.register((target, source, drops, lootingLevel, recentlyHit) -> {
            CapturingEnchant.handleCapturing(target, source, drops);
            return recentlyHit;
        });

    }

    public static void handleUseItem() {
        UseBlockCallback.EVENT.register((player, world, hand, hitResult) -> {
            if (!player.isSpectator()) {
                if (world.getBlockEntity(hitResult.getBlockPos()) instanceof ApothSpawnerTile) {
                    ItemStack s = player.getItemInHand(hand);
                    if (enableDebug) SpawnerModule.LOG.info("BlockEntity is spawner");
                    if (s.getItem() instanceof SpawnEggItem egg) {
                        if (enableDebug) SpawnerModule.LOG.info("Item is spawn egg");
                        EntityType<?> type = egg.getType(s.getTag());
                        if (bannedMobs.contains(EntityType.getKey(type))) {
                            if (enableDebug) SpawnerModule.LOG.info("Mob {} is banned from being added to spawners", EntityType.getKey(type));
                            return InteractionResult.CONSUME;
                        }
                        if (enableDebug) SpawnerModule.LOG.info("Added mob {} spawner.", EntityType.getKey(type));
                    }
                }
            }
            return InteractionResult.PASS;
        });

    }


    public static void tickDumbMobs() {
        LivingEntityEvents.TICK.register(entity -> {
            if (entity instanceof Mob mob){
                if (!mob.level().isClientSide && mob.isNoAi() && mob.getCustomData().getBoolean("apotheosis:movable")) {
                    mob.setNoAi(false);
                    mob.travel(new Vec3(mob.xxa, mob.zza, mob.yya));
                    mob.setNoAi(true);
                }
            }
        });

    }

    public static void dumbMobsCantTeleport() {
        EntityMoveEvents.TELEPORT.register(e -> {
            if (e.entity.getCustomData().getBoolean("apotheosis:movable")) {
                e.setCanceled(true);
            }
        });
    }

    public static void reload(boolean e) {
        Configuration config = new Configuration(new File(Apotheosis.configDir, "spawner.cfg"));
        config.setTitle("Apotheosis Spawner Module Configuration");
        spawnerSilkLevel = config.getInt("Spawner Silk Level", "general", 1, -1, 127,
            "The level of silk touch needed to harvest a spawner.  Set to -1 to disable, 0 to always drop.  The enchantment module can increase the max level of silk touch.\nFunctionally server-authoritative, but should match on client for information.");
        spawnerSilkDamage = config.getInt("Spawner Silk Damage", "general", 100, 0, 100000, "The durability damage dealt to an item that silk touches a spawner.\nServer-authoritative.");
        bannedMobs.clear();
        String[] bans = config.getStringList("Banned Mobs", "spawn_eggs", new String[0], "A list of entity registry names that cannot be applied to spawners via egg.\nShould match between client and server.");
        for (String s : bans)
            try {
                bannedMobs.add(new ResourceLocation(s));
            }
            catch (ResourceLocationException ex) {
                SpawnerModule.LOG.error("Invalid entry {} detected in the spawner banned mobs list.", s);
                ex.printStackTrace();
            }
        if (!e && config.hasChanged()) config.save();
    }

    public static Component concat(Object... args) {
        return Component.translatable("misc.apotheosis.value_concat", args[0], Component.literal(args[1].toString()).withStyle(ChatFormatting.GRAY)).withStyle(ChatFormatting.GREEN);
    }

}
