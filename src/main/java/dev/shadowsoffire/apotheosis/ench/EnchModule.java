package dev.shadowsoffire.apotheosis.ench;

import com.chocohead.mm.api.ClassTinkerers;
import dev.shadowsoffire.apotheosis.Apoth;
import dev.shadowsoffire.apotheosis.Apotheosis;
import dev.shadowsoffire.apotheosis.ench.EnchantmentInfo.PowerFunc;
import dev.shadowsoffire.apotheosis.ench.Ench.*;
import dev.shadowsoffire.apotheosis.ench.objects.TypedShelfBlock.SculkShelfBlock;
import dev.shadowsoffire.apotheosis.ench.objects.TomeItem;
import dev.shadowsoffire.apotheosis.ench.objects.TypedShelfBlock;
import dev.shadowsoffire.apotheosis.ench.table.*;
import dev.shadowsoffire.apotheosis.mixin.accessors.BlockEntityTypeAccessor;
import dev.shadowsoffire.placebo.config.Configuration;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import net.minecraft.core.Registry;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.*;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentCategory;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MapColor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.util.*;

public class EnchModule {
    public static final Map<Enchantment, EnchantmentInfo> ENCHANTMENT_INFO = new HashMap<>();
    public static final Object2IntMap<Enchantment> ENCH_HARD_CAPS = new Object2IntOpenHashMap<>();
    public static final String ENCH_HARD_CAP_IMC = "set_ench_hard_cap";
    public static final Logger LOGGER = LogManager.getLogger("Zenith : Enchantment");
    public static final List<TomeItem> TYPED_BOOKS = new ArrayList<>();
    public static final EnchantmentCategory HOE = ClassTinkerers.getEnum(EnchantmentCategory.class, "HOE");
    public static final EnchantmentCategory SHIELD = ClassTinkerers.getEnum(EnchantmentCategory.class, "SHIELD");
    public static final EnchantmentCategory ANVIL = ClassTinkerers.getEnum(EnchantmentCategory.class, "ANVIL");
    public static final EnchantmentCategory SHEARS = ClassTinkerers.getEnum(EnchantmentCategory.class, "SHEARS");
    public static final EnchantmentCategory PICKAXE = ClassTinkerers.getEnum(EnchantmentCategory.class, "PICKAXE");
    public static final EnchantmentCategory AXE = ClassTinkerers.getEnum(EnchantmentCategory.class, "AXE");
    public static final EnchantmentCategory CORE_ARMOR = ClassTinkerers.getEnum(EnchantmentCategory.class, "CORE_ARMOR");

    static Configuration enchInfoConfig;

    public static void init() {
        Ench.bootstrap();
        particles();
        recipeSerializers();

        EnchModuleEvents.registerEvents();


        EnchantingStatRegistry.INSTANCE.register();
        ((BlockEntityTypeAccessor) BlockEntityType.ENCHANTING_TABLE).setFactory(ApothEnchantTile::new);
        reload(false);
    }

    public static void recipeSerializers() {
        Apoth.registerSerializer("enchanting", EnchantingRecipe.SERIALIZER);
        Apoth.registerSerializer("keep_nbt_enchanting", KeepNBTEnchantingRecipe.SERIALIZER);
    }

    public static void particles() {
        Registry.register(BuiltInRegistries.PARTICLE_TYPE, Apotheosis.loc("enchant_fire"), Particles.ENCHANT_FIRE);
        Registry.register(BuiltInRegistries.PARTICLE_TYPE, Apotheosis.loc("enchant_water"), Particles.ENCHANT_WATER);
        Registry.register(BuiltInRegistries.PARTICLE_TYPE, Apotheosis.loc("enchant_sculk"), Particles.ENCHANT_SCULK);
        Registry.register(BuiltInRegistries.PARTICLE_TYPE, Apotheosis.loc("enchant_end"), Particles.ENCHANT_END);
    }

    private static Block shelf(BlockBehaviour.Properties props, float strength) {
        return shelf(props, strength, ParticleTypes.ENCHANT);
    }

    private static Block shelf(BlockBehaviour.Properties props, float strength, SimpleParticleType particle) {
        props.strength(strength);
        return new TypedShelfBlock(props, particle);
    }

    private static Block sculkShelf(float strength, SimpleParticleType particle) {
        var props = BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_BLACK).sound(SoundType.STONE).strength(strength).randomTicks().requiresCorrectToolForDrops();
        return new SculkShelfBlock(props, particle);
    }


    @SuppressWarnings("deprecation")
    public static EnchantmentInfo getEnchInfo(Enchantment ench) {
        if (!Apotheosis.enableEnch) return ENCHANTMENT_INFO.computeIfAbsent(ench, EnchantmentInfo::new);

        EnchantmentInfo info = ENCHANTMENT_INFO.get(ench);

        if (enchInfoConfig == null) { // Legitimate occurances can now happen, such as when vanilla calls fillItemGroup
            // LOGGER.error("A mod has attempted to access enchantment information before Zenith init, this should not happen.");
            // Thread.dumpStack();
            return new EnchantmentInfo(ench);
        }

        if (info == null) { // Happens every time the game is loaded... odd
            info = EnchantmentInfo.load(ench, enchInfoConfig);
            ENCHANTMENT_INFO.put(ench, info);
            if (enchInfoConfig.hasChanged()) enchInfoConfig.save();
            LOGGER.error("Had to late load enchantment info for {}, this is a bug in the mod {} as they are registering late!", BuiltInRegistries.ENCHANTMENT.getKey(ench), BuiltInRegistries.ENCHANTMENT.getKey(ench).getNamespace());
        }

        return info;
    }

    /**
     * Tries to find a max level for this enchantment. This is used to scale up default levels to the Apoth cap.
     * Single-Level enchantments are not scaled.
     * Barring that, enchantments are scaled using the {@link EnchantmentInfo#defaultMin(Enchantment)} until outside the default level space.
     */
    public static int getDefaultMax(Enchantment ench) {
        int level = ench.getMaxLevel();
        if (level == 1) return 1;
        PowerFunc minFunc = EnchantmentInfo.defaultMin(ench);
        int max = (int) (EnchantingStatRegistry.getAbsoluteMaxEterna() * 4);
        int minPower = minFunc.getPower(level);
        if (minPower >= max) return level;
        int lastPower = minPower;
        while (minPower < max) {
            minPower = minFunc.getPower(++level);
            if (lastPower == minPower) return level;
            if (minPower > max) return level - 1;
            lastPower = minPower;
        }
        return level;
    }

    public static boolean isVanillaAnvil(ItemStack stack) {
        return stack.is(Items.ANVIL) || stack.is(Items.CHIPPED_ANVIL) || stack.is(Items.DAMAGED_ANVIL);
    }

    public static void reload(boolean e) {
        enchInfoConfig = new Configuration(new File(Apotheosis.configDir, "enchantments.cfg"));
        enchInfoConfig.setTitle("Zenith Enchantment Information");
        enchInfoConfig.setComment("This file contains configurable data for each enchantment.\nThe names of each category correspond to the registry names of every loaded enchantment.");
        ENCHANTMENT_INFO.clear();

        for (Enchantment ench : BuiltInRegistries.ENCHANTMENT) {
            ENCHANTMENT_INFO.put(ench, EnchantmentInfo.load(ench, enchInfoConfig));
        }

        for (Enchantment ench : BuiltInRegistries.ENCHANTMENT) {
            EnchantmentInfo info = ENCHANTMENT_INFO.get(ench);
            for (int i = 1; i <= info.getMaxLevel(); i++)
                if (info.getMinPower(i) > info.getMaxPower(i))
                    LOGGER.warn("Enchantment {} has min/max power {}/{} at level {}, making this level unobtainable.", BuiltInRegistries.ENCHANTMENT.getKey(ench), info.getMinPower(i), info.getMaxPower(i), i);
        }

        if (!e && enchInfoConfig.hasChanged()) enchInfoConfig.save();
    }
}
