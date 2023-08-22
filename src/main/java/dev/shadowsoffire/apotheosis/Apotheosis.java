package dev.shadowsoffire.apotheosis;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import dev.shadowsoffire.apotheosis.advancements.AdvancementTriggers;
import dev.shadowsoffire.apotheosis.compat.PatchouliCompat;
import dev.shadowsoffire.apotheosis.garden.GardenModule;
import dev.shadowsoffire.apotheosis.spawn.SpawnerModule;
import dev.shadowsoffire.apotheosis.util.Events;
import dev.shadowsoffire.apotheosis.util.ModuleCondition;
import dev.shadowsoffire.apotheosis.village.VillageModule;
import dev.shadowsoffire.placebo.config.Configuration;

import dev.shadowsoffire.placebo.recipe.RecipeHelper;
import dev.shadowsoffire.placebo.registry.DeferredHelper;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.resource.conditions.v1.ResourceConditions;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.resources.ResourceLocation;

import java.io.File;
import java.util.function.BooleanSupplier;

public class Apotheosis implements ModInitializer {

    public static final String MODID = "apotheosis";
   // public static final RecipeHelper HELPER = new RecipeHelper(Apotheosis.MODID);

    public static File configDir;
    public static Configuration config;
    public static boolean enableEnch = true;
    public static boolean enableAdventure = false;
    public static boolean enableSpawner = true;
    public static boolean enablePotion = true;
    public static boolean enableVillage = true;
    public static boolean enableGarden = true;
    public static boolean enableDebug = false;
    public static boolean giveBook = true;

    public static float localAtkStrength = 1;

    @Override
    public void onInitialize() {
    //    if (enableEnch) EnchModule.init();
        if (enableSpawner) SpawnerModule.init();
        if (enableGarden) GardenModule.init();
    //    if (enableAdventure) AdventureModule.init(); this might be a while :P
   //     if (enablePotion) PotionModule.init();
        if (enableVillage) VillageModule.init();

        if (config.hasChanged()) config.save();

        AdvancementTriggers.init();
        Events.init();
        ResourceConditions.register(ModuleCondition.ID, ModuleCondition::test);
        //MinecraftForge.EVENT_BUS.addListener(this::reloads);
        //MinecraftForge.EVENT_BUS.addListener(this::trackCooldown);
        //MinecraftForge.EVENT_BUS.addListener(this::cmds);
        Apoth.RecipeTypes.FLETCHING.getClass(); // Static init wew
        if (FabricLoader.getInstance().isModLoaded("patchouli")) PatchouliCompat.registerPatchouli(); //This wasnt working when in its own class? wut

    }

    static {
        configDir = new File(FabricLoader.getInstance().getConfigDir().toFile(), MODID);
        config = new Configuration(new File(configDir, MODID + ".cfg"));
        enableEnch = config.getBoolean("Enable Enchantment Module", "general", true, "If the enchantment module is enabled.");
        enableAdventure = config.getBoolean("Enable Adventure Module", "general", true, "If the adventure module is loaded.");
        enableSpawner = config.getBoolean("Enable Spawner Module", "general", true, "If the spawner module is enabled.");
        enablePotion = config.getBoolean("Enable Potion Module", "general", true, "If the potion module is loaded.");
        enableVillage = config.getBoolean("Enable Village Module", "general", true, "If the village module is loaded.");
        enableGarden = config.getBoolean("Enable Garden Module", "general", true, "If the garden module is loaded.");
        enableDebug = config.getBoolean("Enable Debug mode", "general", false, "If a lot of random debug info is added to the console. Not recommended for normal play.");
        giveBook = config.getBoolean("Give Book on First Join", "general", true, "If the Chronicle of Shadows is given to new players.");
        config.setTitle("Apotheosis Module Control");
        config.setComment("This file allows individual modules of Apotheosis to be enabled or disabled.\nChanges will have no effect until the next game restart.\nThis file must match on client and server.");
        if (config.hasChanged()) config.save();
    }
/*
    @SubscribeEvent
    public void init(FMLCommonSetupEvent e) {
        MessageHelper.registerMessage(CHANNEL, 0, new ParticleMessage.Provider());
        MessageHelper.registerMessage(CHANNEL, 1, new BossSpawnMessage.Provider());
        MessageHelper.registerMessage(CHANNEL, 2, new ClueMessage.Provider());
        e.enqueueWork(() -> {
            CraftingHelper.register(new ModuleCondition.Serializer());
        });
    }

    @SubscribeEvent(priority = EventPriority.LOW)
    public void reloads(AddReloadListenerEvent e) {
        e.addListener(RunnableReloader.of(() -> MinecraftForge.EVENT_BUS.post(new ApotheosisReloadEvent())));
    }

    @SubscribeEvent
    public void trackCooldown(AttackEntityEvent e) {
        Player p = e.getEntity();
        localAtkStrength = p.getAttackStrengthScale(0.5F);
    }

    @SubscribeEvent
    public void cmds(RegisterCommandsEvent e) {
        var builder = Commands.literal("apoth");
        MinecraftForge.EVENT_BUS.post(new ApotheosisCommandEvent(builder));
        e.getDispatcher().register(builder);
    }

    public static Ingredient potionIngredient(Potion type) {
        return new NBTIngredient(PotionUtils.setPotion(new ItemStack(Items.POTION), type));
    }
*/
    public static ResourceLocation loc(String s) {
        return new ResourceLocation(MODID, s);
    }
/*


    /**
     * The apotheosis construction event is fired from {@link Apotheosis}'s constructor.
     */
 //   public static class ApotheosisConstruction extends Event implements IModBusEvent {}

    /**
     * The apotheosis reload event is fired from resource reload.
     */
  //  public static class ApotheosisReloadEvent extends Event {}

    /**
     * The apotheosis command event is fired when commands are to be registered.
     * Register subcommands at this time.
     */

    public static class ApotheosisCommandEvent  {

        private final LiteralArgumentBuilder<CommandSourceStack> root;

        public ApotheosisCommandEvent(LiteralArgumentBuilder<CommandSourceStack> root) {
            this.root = root;
        }

        public LiteralArgumentBuilder<CommandSourceStack> getRoot() {
            return this.root;
        }
    }

    public static class ModularDeferredHelper extends DeferredHelper {

        protected final BooleanSupplier flag;

        public static ModularDeferredHelper create(BooleanSupplier flag) {
            ModularDeferredHelper helper = new ModularDeferredHelper(flag);
        //    FMLJavaModLoadingContext.get().getModEventBus().register(helper);
            return helper;
        }

        protected ModularDeferredHelper(BooleanSupplier flag) {
            super(Apotheosis.MODID);
            this.flag = flag;
        }



    //    public void register(RegisterEvent e) {
    //        if (flag.getAsBoolean()) super.register(e);
    //    }

    }


}
