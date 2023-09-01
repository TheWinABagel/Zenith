package dev.shadowsoffire.apotheosis.village;

import dev.shadowsoffire.apotheosis.Apoth;
import dev.shadowsoffire.apotheosis.Apotheosis;
import dev.shadowsoffire.apotheosis.village.fletching.FletchingContainer;
import dev.shadowsoffire.apotheosis.village.fletching.FletchingRecipe;
import dev.shadowsoffire.apotheosis.village.fletching.arrows.*;
import dev.shadowsoffire.apotheosis.village.wanderer.WandererReplacements;
import dev.shadowsoffire.placebo.config.Configuration;
import dev.shadowsoffire.placebo.menu.MenuUtil;
import dev.shadowsoffire.placebo.tabs.TabFillingRegistry;
import dev.shadowsoffire.placebo.util.PlaceboUtil;
import io.github.fabricators_of_create.porting_lib.util.RegistryObject;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricEntityTypeBuilder;
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
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.Level.ExplosionInteraction;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.DispenserBlock;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;

public class VillageModule {
    public static final RecipeSerializer<FletchingRecipe> FLETCHING_SERIALIZER = new FletchingRecipe.Serializer();
    public static final Logger LOGGER = LogManager.getLogger("Apotheosis : Village");

    public static Configuration config;

    public static ExplosionInteraction expArrowMode = ExplosionInteraction.BLOCK;

    public static void init() {
        config = new Configuration(new File(Apotheosis.configDir, "village.cfg"));
        config.setTitle("Apotheosis Village Module Configuration");
        WandererReplacements.load(config);
        WandererReplacements.setup();

        boolean blockDmg = config.getBoolean("Explosive Arrow Block Damage", "arrows", true, "If explosive arrows can break blocks.\nServer-authoritative.");
        expArrowMode = blockDmg ? ExplosionInteraction.BLOCK : ExplosionInteraction.NONE;
        if (config.hasChanged()) config.save();
        Registry.register(BuiltInRegistries.RECIPE_SERIALIZER, FletchingRecipe.Serializer.NAME, FLETCHING_SERIALIZER);

        items();
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
/*
    @SubscribeEvent
    public void serializers(Register<RecipeSerializer<?>> e) {
        e.getRegistry().register(FLETCHING_SERIALIZER, FletchingRecipe.Serializer.NAME);
    }

    @SubscribeEvent
    public void blocks(Register<Block> e) {
        PlaceboUtil.registerOverride(Blocks.FLETCHING_TABLE, new ApothFletchingBlock(), Apotheosis.MODID);
    }
*/
    public static void items() {
        registerItem("obsidian_arrow", Apoth.Items.OBSIDIAN_ARROW);
        registerItem("broadhead_arrow", Apoth.Items.BROADHEAD_ARROW);
        registerItem("explosive_arrow",  Apoth.Items.EXPLOSIVE_ARROW);
        registerItem("iron_mining_arrow",  Apoth.Items.IRON_MINING_ARROW);
        registerItem("diamond_mining_arrow", Apoth.Items.DIAMOND_MINING_ARROW);

       // TabFillingRegistry.register(CreativeModeTabs.COMBAT, Apoth.Items.OBSIDIAN_ARROW, Apoth.Items.BROADHEAD_ARROW, Apoth.Items.EXPLOSIVE_ARROW, Apoth.Items.IRON_MINING_ARROW, Apoth.Items.DIAMOND_MINING_ARROW);
    }

    private static void registerItem(String path, Item item){
        Registry.register(BuiltInRegistries.ITEM, new ResourceLocation(Apotheosis.MODID, path), item);
    }

        /*
        e.getRegistry().register(EntityType.Builder
            .<ObsidianArrowEntity>of(ObsidianArrowEntity::new, MobCategory.MISC)
            .setShouldReceiveVelocityUpdates(true)
            .setTrackingRange(4)
            .setUpdateInterval(20)
            .sized(0.5F, 0.5F)
            .setCustomClientFactory((se, w) -> new ObsidianArrowEntity(w))
            .build("obsidian_arrow"), "obsidian_arrow");
        e.getRegistry().register(EntityType.Builder
            .<BroadheadArrowEntity>of(BroadheadArrowEntity::new, MobCategory.MISC)
            .setShouldReceiveVelocityUpdates(true)
            .setTrackingRange(4)
            .setUpdateInterval(20)
            .sized(0.5F, 0.5F)
            .setCustomClientFactory((se, w) -> new BroadheadArrowEntity(w))
            .build("broadhead_arrow"), "broadhead_arrow");
        e.getRegistry().register(EntityType.Builder
            .<ExplosiveArrowEntity>of(ExplosiveArrowEntity::new, MobCategory.MISC)
            .setShouldReceiveVelocityUpdates(true)
            .setTrackingRange(4)
            .setUpdateInterval(20)
            .sized(0.5F, 0.5F)
            .setCustomClientFactory((se, w) -> new ExplosiveArrowEntity(w))
            .build("explosive_arrow"), "explosive_arrow");
        e.getRegistry().register(EntityType.Builder
            .<MiningArrowEntity>of(MiningArrowEntity::new, MobCategory.MISC)
            .setShouldReceiveVelocityUpdates(true)
            .setTrackingRange(4)
            .setUpdateInterval(20)
            .sized(0.5F, 0.5F)
            .setCustomClientFactory((se, w) -> new MiningArrowEntity(w))
            .build("mining_arrow"), "mining_arrow");

    }

    @SubscribeEvent
    public void containers(Register<MenuType<?>> e) {
        e.getRegistry().register(MenuUtil.type(FletchingContainer::new), "fletching");
    }*/

}
