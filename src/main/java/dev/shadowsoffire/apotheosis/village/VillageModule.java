package dev.shadowsoffire.apotheosis.village;

import dev.shadowsoffire.apotheosis.Apoth;
import dev.shadowsoffire.apotheosis.Apotheosis;
import dev.shadowsoffire.apotheosis.village.fletching.FletchingContainer;
import dev.shadowsoffire.apotheosis.village.fletching.FletchingRecipe;
import dev.shadowsoffire.apotheosis.village.fletching.arrows.*;
import dev.shadowsoffire.apotheosis.village.wanderer.WandererReplacements;
import dev.shadowsoffire.placebo.config.Configuration;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricEntityTypeBuilder;
import net.fabricmc.fabric.api.screenhandler.v1.ScreenHandlerRegistry;
import net.minecraft.core.Position;
import net.minecraft.core.Registry;
import net.minecraft.core.dispenser.AbstractProjectileDispenseBehavior;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.Level.ExplosionInteraction;
import net.minecraft.world.level.block.DispenserBlock;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;

public class VillageModule {
    public static final Logger LOGGER = LogManager.getLogger("Zenith : Village");
    public static final RecipeSerializer<FletchingRecipe> FLETCHING_SERIALIZER = new FletchingRecipe.Serializer();
    public static final RecipeType<FletchingRecipe> FLETCHING = RecipeType.register("zenith:fletching");
    public static final MenuType<FletchingContainer> FLETCHING_MENU = ScreenHandlerRegistry.registerSimple(Apotheosis.loc("fletching"), FletchingContainer::new);


    public static Configuration config;

    public static ExplosionInteraction expArrowMode = ExplosionInteraction.BLOCK;

    public static final Item OBSIDIAN_ARROW = new ObsidianArrowItem(new Item.Properties());
    public static final Item BROADHEAD_ARROW = new BroadheadArrowItem(new Item.Properties());
    public static final Item EXPLOSIVE_ARROW = new ExplosiveArrowItem(new Item.Properties());
    public static final Item IRON_MINING_ARROW = new MiningArrowItem(() -> net.minecraft.world.item.Items.IRON_PICKAXE, MiningArrowEntity.Type.IRON);
    public static final Item DIAMOND_MINING_ARROW = new MiningArrowItem(() -> net.minecraft.world.item.Items.DIAMOND_PICKAXE, MiningArrowEntity.Type.DIAMOND);


    public static void init() {
        config = new Configuration(new File(Apotheosis.configDir, "village.cfg"));
        config.setTitle("Zenith Village Module Configuration");
        WandererReplacements.load(config);
        WandererReplacements.setup();
        items();

        boolean blockDmg = config.getBoolean("Explosive Arrow Block Damage", "arrows", true, "If explosive arrows can break blocks.\nServer-authoritative.");
        expArrowMode = blockDmg ? ExplosionInteraction.BLOCK : ExplosionInteraction.NONE;
        if (config.hasChanged()) config.save();
        Registry.register(BuiltInRegistries.RECIPE_SERIALIZER, FletchingRecipe.Serializer.NAME, FLETCHING_SERIALIZER);


        for (Item i : BuiltInRegistries.ITEM) {
            if (i instanceof IApothArrowItem customArrow) {
                DispenserBlock.registerBehavior(i, new AbstractProjectileDispenseBehavior(){
                    @Override
                    protected Projectile getProjectile(Level world, Position pos, ItemStack stack) {
                        return (customArrow.fromDispenser(world, pos.x(), pos.y(), pos.z()));
                    }
                });
            }
        }

    }

    public static void items() {
        Apoth.registerItem(OBSIDIAN_ARROW, "obsidian_arrow");
        Apoth.registerItem(BROADHEAD_ARROW, "broadhead_arrow");
        Apoth.registerItem(EXPLOSIVE_ARROW, "explosive_arrow");
        Apoth.registerItem(IRON_MINING_ARROW, "iron_mining_arrow");
        Apoth.registerItem(DIAMOND_MINING_ARROW,"diamond_mining_arrow");

       // TabFillingRegistry.register(CreativeModeTabs.COMBAT, Apoth.Items.OBSIDIAN_ARROW, Apoth.Items.BROADHEAD_ARROW, Apoth.Items.EXPLOSIVE_ARROW, Apoth.Items.IRON_MINING_ARROW, Apoth.Items.DIAMOND_MINING_ARROW);
    }

    public static final EntityType<ObsidianArrowEntity> OBSIDIAN_ARROW_ENTITY = Registry.register(
            BuiltInRegistries.ENTITY_TYPE,
            new ResourceLocation(Apotheosis.MODID, "obsidian_arrow"),
            FabricEntityTypeBuilder.<ObsidianArrowEntity>create(MobCategory.MISC, ObsidianArrowEntity::new)
                    .dimensions(EntityDimensions.fixed(0.25F, 0.5F))
                    .trackRangeBlocks(4)
                    .trackedUpdateRate(20)
                    .build());
    public static final EntityType<BroadheadArrowEntity> BROADHEAD_ARROW_ENTITY = Registry.register(
            BuiltInRegistries.ENTITY_TYPE,
            new ResourceLocation(Apotheosis.MODID, "broadhead_arrow"),
            FabricEntityTypeBuilder.<BroadheadArrowEntity>create(MobCategory.MISC, BroadheadArrowEntity::new)
                    .dimensions(EntityDimensions.fixed(0.25F, 0.5F))
                    .trackRangeBlocks(4)
                    .trackedUpdateRate(20)
                    .build());
    public static final EntityType<ExplosiveArrowEntity> EXPLOSIVE_ARROW_ENTITY = Registry.register(
            BuiltInRegistries.ENTITY_TYPE,
            new ResourceLocation(Apotheosis.MODID, "explosive_arrow"),
            FabricEntityTypeBuilder.<ExplosiveArrowEntity>create(MobCategory.MISC, ExplosiveArrowEntity::new)
                    .dimensions(EntityDimensions.fixed(0.25F, 0.5F))
                    .trackRangeBlocks(4)
                    .trackedUpdateRate(20)
                    .build());
    public static final EntityType<MiningArrowEntity> MINING_ARROW_ENTITY = Registry.register(
            BuiltInRegistries.ENTITY_TYPE,
            new ResourceLocation(Apotheosis.MODID, "mining_arrow"),
            FabricEntityTypeBuilder.<MiningArrowEntity>create(MobCategory.MISC, MiningArrowEntity::new)
                    .dimensions(EntityDimensions.fixed(0.25F, 0.5F))
                    .trackRangeBlocks(4)
                    .trackedUpdateRate(20)
                    .build());

}
