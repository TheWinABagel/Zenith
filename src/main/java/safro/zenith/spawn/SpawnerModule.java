package safro.zenith.spawn;

import io.github.fabricators_of_create.porting_lib.event.common.LivingEntityEvents;
import net.fabricmc.fabric.api.event.player.UseBlockCallback;
import net.minecraft.ChatFormatting;
import net.minecraft.ResourceLocationException;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Registry;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.SpawnEggItem;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import safro.zenith.Zenith;
import safro.zenith.api.config.Configuration;
import safro.zenith.spawn.enchantment.CapturingEnchant;
import safro.zenith.spawn.modifiers.SpawnerModifier;
import safro.zenith.spawn.spawner.ApothSpawnerTile;
import safro.zenith.util.ApotheosisUtil;

import java.io.File;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class SpawnerModule {
    public static final Logger LOG = LogManager.getLogger("Zenith : Spawner");
    public static int spawnerSilkLevel = 1;
    public static int spawnerSilkDamage = 100;
    public static boolean invertBannedMobs;
    public static Set<ResourceLocation> bannedMobs = new HashSet<>();

    public static final Enchantment CAPTURING = register("capturing", new CapturingEnchant());

    public static final RecipeType<SpawnerModifier> MODIFIER = ApotheosisUtil.makeRecipeType("zenith:spawner_modifier");
    public static final RecipeSerializer<SpawnerModifier> SPAWNER_MODIFIER = register("spawner_modifier", SpawnerModifier.SERIALIZER);


    public static void init() {
        reload(false);

        LivingEntityEvents.DROPS.register(((target, source, drops) -> {
            CapturingEnchant.handleCapturing(source, target, drops);
            return false;
        }));

        LivingEntityEvents.TICK.register(SpawnerModule::tickDumbMobs);

        UseBlockCallback.EVENT.register(((player, world, hand, hitResult) -> handleUseItem(world, hitResult.getBlockPos(), player.getItemInHand(hand))));
    }

    public static InteractionResult handleUseItem(Level world, BlockPos pos, ItemStack s) {
        if (world.getBlockEntity(pos) instanceof ApothSpawnerTile) {
            if (s.getItem() instanceof SpawnEggItem egg) {
                EntityType<?> type = egg.getType(s.getTag());
                if(invertBannedMobs) {
                    if (!bannedMobs.contains(Registry.ENTITY_TYPE.getKey(type))) return InteractionResult.FAIL;
                } else {
                    if (bannedMobs.contains(Registry.ENTITY_TYPE.getKey(type))) return InteractionResult.FAIL;
                }

            }
        }
        return InteractionResult.PASS;
    }

    public static void handleTooltips(List<Component> tooltip, ItemStack s) {
        if (s.getItem() instanceof SpawnEggItem egg) {
            EntityType<?> type = egg.getType(s.getTag());
            if (invertBannedMobs) {
                if (!bannedMobs.contains(Registry.ENTITY_TYPE.getKey(type))) tooltip.add(Component.translatable("misc.zenith.banned").withStyle(ChatFormatting.GRAY));
            } else {
                if (bannedMobs.contains(Registry.ENTITY_TYPE.getKey(type))) tooltip.add(Component.translatable("misc.zenith.banned").withStyle(ChatFormatting.GRAY));
            }

        }
    }

    public static void tickDumbMobs(LivingEntity entity) {
        if (entity instanceof Mob mob) {
            if (!mob.level.isClientSide && mob.isNoAi() && entity.getTags().contains("zenith:movable")) {
                mob.setNoAi(false);
                mob.travel(new Vec3(mob.xxa, mob.zza, mob.yya));
                mob.setNoAi(true);
            }
        }
    }

    public static void reload(boolean e) {
        Configuration config = new Configuration(new File(Zenith.configDir, "spawner.cfg"));
        config.setTitle("Zenith Spawner Module Configuration");
        spawnerSilkLevel = config.getInt("Spawner Silk Level", "general", 1, -1, 127, "The level of silk touch needed to harvest a spawner.  Set to -1 to disable, 0 to always drop.  The enchantment module can increase the max level of silk touch.");
        spawnerSilkDamage = config.getInt("Spawner Silk Damage", "general", 100, 0, 100000, "The durability damage dealt to an item that silk touches a spawner.");
        invertBannedMobs = config.getBoolean("Enable Whitelist", "spawn_eggs", false, "Turns the blacklist into a whitelist.");
        bannedMobs.clear();
        String[] bans = config.getStringList("Banned Mobs", "spawn_eggs", new String[0], "A list of entity registry names that cannot be applied to spawners via egg.");
        for (String s : bans)
            try {
                bannedMobs.add(new ResourceLocation(s));
            } catch (ResourceLocationException ex) {
                SpawnerModule.LOG.error("Invalid entry {} detected in the spawner banned mobs list.", s);
                ex.printStackTrace();
            }
        if (!e && config.hasChanged()) config.save();
    }

    private static <S extends RecipeSerializer<T>, T extends Recipe<?>> S register(String id, S serializer) {
        return Registry.register(Registry.RECIPE_SERIALIZER, new ResourceLocation(Zenith.MODID, id), serializer);
    }

    private static Enchantment register(String name, Enchantment ench) {
        return Registry.register(Registry.ENCHANTMENT, new ResourceLocation(Zenith.MODID, name), ench);
    }
}
