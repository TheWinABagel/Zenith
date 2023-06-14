package safro.zenith.village;

import net.fabricmc.fabric.api.object.builder.v1.entity.FabricEntityTypeBuilder;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.fabricmc.fabric.api.screenhandler.v1.ScreenHandlerRegistry;
import net.minecraft.core.Position;
import net.minecraft.core.Registry;
import net.minecraft.core.dispenser.AbstractProjectileDispenseBehavior;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.PackType;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.DispenserBlock;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import safro.zenith.Zenith;
import safro.zenith.api.config.Configuration;
import safro.zenith.util.ZenithUtil;
import safro.zenith.village.fletching.FletchingContainer;
import safro.zenith.village.fletching.FletchingRecipe;
import safro.zenith.village.fletching.arrows.*;
import safro.zenith.village.fletching.effects.BleedingEffect;
import safro.zenith.village.wanderer.WandererReplacements;
import safro.zenith.village.wanderer.WandererTradeManager;

import java.io.File;

public class VillageModule {
    public static final RecipeType<FletchingRecipe> FLETCHING = ZenithUtil.makeRecipeType(Zenith.MODID + ":fletching");
    public static final RecipeSerializer<FletchingRecipe> FLETCHING_SERIALIZER = new FletchingRecipe.Serializer();
    public static final Logger LOGGER = LogManager.getLogger("Zenith : Village");

    public static Configuration config;

    // Effects
    public static final MobEffect BLEEDING = register("bleeding", new BleedingEffect());

    // Menus
    public static final MenuType<FletchingContainer> FLETCHING_MENU = ScreenHandlerRegistry.registerSimple(new ResourceLocation(Zenith.MODID, "fletching"), FletchingContainer::new);

    // Entities
    public static final EntityType<ObsidianArrowEntity> OBSIDIAN = register("obsidian_arrow", FabricEntityTypeBuilder.<ObsidianArrowEntity>create(MobCategory.MISC, ObsidianArrowEntity::new)
            .forceTrackedVelocityUpdates(true)
            .trackRangeBlocks(4)
            .trackedUpdateRate(20)
            .dimensions(EntityDimensions.fixed(0.5F, 0.5F))
            .build());
    public static final EntityType<BroadheadArrowEntity> BROADHEAD = register("broadhead_arrow", FabricEntityTypeBuilder.<BroadheadArrowEntity>create(MobCategory.MISC, BroadheadArrowEntity::new)
            .forceTrackedVelocityUpdates(true)
            .trackRangeBlocks(4)
            .trackedUpdateRate(20)
            .dimensions(EntityDimensions.fixed(0.5F, 0.5F))
            .build());
    public static final EntityType<ExplosiveArrowEntity> EXPLOSIVE = register("explosive_arrow", FabricEntityTypeBuilder.<ExplosiveArrowEntity>create(MobCategory.MISC, ExplosiveArrowEntity::new)
            .forceTrackedVelocityUpdates(true)
            .trackRangeBlocks(4)
            .trackedUpdateRate(20)
            .dimensions(EntityDimensions.fixed(0.5F, 0.5F))
            .build());
    public static final EntityType<MiningArrowEntity> MINING = register("mining_arrow", FabricEntityTypeBuilder.<MiningArrowEntity>create(MobCategory.MISC, MiningArrowEntity::new)
            .forceTrackedVelocityUpdates(true)
            .trackRangeBlocks(4)
            .trackedUpdateRate(20)
            .dimensions(EntityDimensions.fixed(0.5F, 0.5F))
            .build());

    // Items
    public static final Item OBSIDIAN_ARROW = register("obsidian_arrow", new ObsidianArrowItem());
    public static final Item BROADHEAD_ARROW = register("broadhead_arrow", new BroadheadArrowItem());
    public static final Item EXPLOSIVE_ARROW = register("explosive_arrow", new ExplosiveArrowItem());
    public static final Item IRON_MINING_ARROW = register("iron_mining_arrow", new MiningArrowItem(() -> Items.IRON_PICKAXE, MiningArrowEntity.Type.IRON));
    public static final Item DIAMOND_MINING_ARROW = register("diamond_mining_arrow", new MiningArrowItem(() -> Items.DIAMOND_PICKAXE, MiningArrowEntity.Type.DIAMOND));

    public static Explosion.BlockInteraction expArrowMode = Explosion.BlockInteraction.DESTROY;

    public static void init() {
        config = new Configuration(new File(Zenith.configDir, "village.cfg"));
        WandererReplacements.load(config);

        boolean blockDmg = config.getBoolean("Explosive Arrow Block Damage", "arrows", true, "If explosive arrows can break blocks.\nServer-authoritative.");
        expArrowMode = blockDmg ? Explosion.BlockInteraction.DESTROY : Explosion.BlockInteraction.NONE;
        if (config.hasChanged()) config.save();

        ResourceManagerHelper.get(PackType.SERVER_DATA).registerReloadListener(WandererTradeManager.INSTANCE);

        register(FletchingRecipe.Serializer.NAME, FLETCHING_SERIALIZER);



        for (Item i : Registry.ITEM) {
            if (i instanceof IZenithArrowItem) {
                DispenserBlock.registerBehavior(i, new AbstractProjectileDispenseBehavior() {
                    @Override
                    protected Projectile getProjectile(Level world, Position pos, ItemStack stack) {
                        return ((IZenithArrowItem) i).fromDispenser(world, pos.x(), pos.y(), pos.z());
                    }
                });
            }
        }
    }

    private static MobEffect register(String name, MobEffect effect) {
        return Registry.register(Registry.MOB_EFFECT, new ResourceLocation(Zenith.MODID, name), effect);
    }

    private static <S extends RecipeSerializer<T>, T extends Recipe<?>> S register(ResourceLocation id, S serializer) {
        return Registry.register(Registry.RECIPE_SERIALIZER, id, serializer);
    }

    private static <T extends Entity> EntityType<T> register(String name, EntityType<T> type) {
        return Registry.register(Registry.ENTITY_TYPE, new ResourceLocation(Zenith.MODID, name), type);
    }

    private static Item register(String name, Item item) {
        return Registry.register(Registry.ITEM, new ResourceLocation(Zenith.MODID, name), item);
    }
}
