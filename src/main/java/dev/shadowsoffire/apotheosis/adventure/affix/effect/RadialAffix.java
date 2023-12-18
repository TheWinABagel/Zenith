package dev.shadowsoffire.apotheosis.adventure.affix.effect;

import com.jamieswhiteshirt.reachentityattributes.ReachEntityAttributes;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.shadowsoffire.apotheosis.Apotheosis;
import dev.shadowsoffire.apotheosis.adventure.Adventure.Affixes;
import dev.shadowsoffire.apotheosis.adventure.AdventureModule;
import dev.shadowsoffire.apotheosis.adventure.affix.Affix;
import dev.shadowsoffire.apotheosis.adventure.affix.AffixHelper;
import dev.shadowsoffire.apotheosis.adventure.affix.AffixInstance;
import dev.shadowsoffire.apotheosis.adventure.affix.AffixType;
import dev.shadowsoffire.apotheosis.adventure.loot.LootCategory;
import dev.shadowsoffire.apotheosis.adventure.loot.LootRarity;
import dev.shadowsoffire.placebo.util.PlaceboUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Direction.Axis;
import net.minecraft.core.Vec3i;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.HitResult.Type;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.function.Consumer;

/**
 * Enables AOE mining.
 */
public class RadialAffix extends Affix {

    public static final Codec<RadialAffix> CODEC = RecordCodecBuilder.create(inst -> inst
        .group(
            LootRarity.mapCodec(Codec.list(RadialData.CODEC)).fieldOf("values").forGetter(a -> a.values))
        .apply(inst, RadialAffix::new));

    private static Set<UUID> breakers = new HashSet<>();

    protected final Map<LootRarity, List<RadialData>> values;

    public RadialAffix(Map<LootRarity, List<RadialData>> values) {
        super(AffixType.ABILITY);
        this.values = values;
    }

    @Override
    public boolean canApplyTo(ItemStack stack, LootCategory cat, LootRarity rarity) {
        return cat.isBreaker() && this.values.containsKey(rarity);
    }

    @Override
    public void addInformation(ItemStack stack, LootRarity rarity, float level, Consumer<Component> list) {
        RadialData data = this.getTrueLevel(rarity, level);
        list.accept(Component.translatable("affix." + this.getId() + ".desc", data.x, data.y));
    }

    // EventPriority.LOW
    public void onBreak(Level world, Player player, BlockPos pos, BlockState state, @Nullable BlockEntity blockEntity) {
        ItemStack tool = player.getMainHandItem();
        //if (Apotheosis.enableDebug) AdventureModule.LOGGER.info("Radial affix breaking");
        if (!world.isClientSide && tool.hasTag()) {
        //    if (Apotheosis.enableDebug) AdventureModule.LOGGER.info("World is not client side, Tool has tag");
            AffixInstance inst = AffixHelper.getAffixes(tool).get(Affixes.RADIAL);
            if (inst != null && inst.isValid()) {
                if (Apotheosis.enableDebug) AdventureModule.LOGGER.info("Affix instance is valid");
                float hardness = state.getDestroySpeed(world, pos);
                breakExtraBlocks((ServerPlayer) player, pos, tool, this.getTrueLevel(inst.rarity().get(), inst.level()), hardness);
            }
        }
    }

    private RadialData getTrueLevel(LootRarity rarity, float level) {
        var list = this.values.get(rarity);
        return list.get(Math.min(list.size() - 1, (int) Mth.lerp(level, 0, list.size())));
    }

    @Override
    public Codec<? extends Affix> getCodec() {
        return CODEC;
    }

    static record RadialData(int x, int y, int xOff, int yOff) {

        public static Codec<RadialData> CODEC = RecordCodecBuilder.create(inst -> inst
            .group(
                Codec.INT.fieldOf("x").forGetter(RadialData::x),
                Codec.INT.fieldOf("y").forGetter(RadialData::y),
                Codec.INT.fieldOf("xOff").forGetter(RadialData::xOff),
                Codec.INT.fieldOf("yOff").forGetter(RadialData::yOff))
            .apply(inst, RadialData::new));

    }

    /**
     * Performs the actual extra breaking of blocks
     *
     * @param player The player breaking the block
     * @param pos    The position of the originally broken block
     * @param tool   The tool being used (which has this affix on it)
     * @param level  The level of this affix, in this case, the mode of operation.
     */
    public static void breakExtraBlocks(ServerPlayer player, BlockPos pos, ItemStack tool, RadialData level, float hardness) {
        if (Apotheosis.enableDebug) AdventureModule.LOGGER.info("BreakExtraBlocks initialised");
        if (!breakers.add(player.getUUID())) {
            if (Apotheosis.enableDebug) AdventureModule.LOGGER.warn("Multiple break attempts by the same player");
            return; // Prevent multiple break operations from cascading, and don't execute when sneaking.
        }
        if (!player.isShiftKeyDown()) try {
            if (Apotheosis.enableDebug) AdventureModule.LOGGER.info("Shift key is not down, attempting to break in a radius");
            breakBlockRadius(player, pos, level.x, level.y, level.xOff, level.yOff, hardness);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        if (Apotheosis.enableDebug) AdventureModule.LOGGER.info("Removing player {} from break list", player);
        breakers.remove(player.getUUID());
    }

    @SuppressWarnings("deprecation")
    public static void breakBlockRadius(ServerPlayer player, BlockPos pos, int x, int y, int xOff, int yOff, float hardness) {
        Level world = player.level();
        if (x < 2 && y < 2) {
            if (Apotheosis.enableDebug) AdventureModule.LOGGER.warn("Area is not large enough, aborting");
            return;
        }
        int lowerY = (int) Math.ceil(-y / 2D), upperY = (int) Math.round(y / 2D);
        int lowerX = (int) Math.ceil(-x / 2D), upperX = (int) Math.round(x / 2D);

        Vec3 base = player.getEyePosition(0);
        Vec3 look = player.getLookAngle();
        double reach = player.getAttributeValue(ReachEntityAttributes.REACH);

        reach = player.isCreative() ? reach + 5D : reach + 4.5D;
        if (Apotheosis.enableDebug) AdventureModule.LOGGER.info("Reach distance: {}", reach);
        Vec3 target = base.add(look.x * reach, look.y * reach, look.z * reach);
        HitResult trace = world.clip(new ClipContext(base, target, ClipContext.Block.OUTLINE, ClipContext.Fluid.NONE, player));

        if (trace == null || trace.getType() != Type.BLOCK){
            if (Apotheosis.enableDebug) AdventureModule.LOGGER.warn("Trace is null, or trace type isnt block!");
            return;
        }
        BlockHitResult res = (BlockHitResult) trace;

        Direction face = res.getDirection(); // Face of the block currently being looked at by the player.

        for (int iy = lowerY; iy < upperY; iy++) {
            for (int ix = lowerX; ix < upperX; ix++) {
                BlockPos genPos = new BlockPos(pos.getX() + ix + xOff, pos.getY() + iy + yOff, pos.getZ());

                if (player.getDirection().getAxis() == Axis.X) {
                    genPos = new BlockPos(genPos.getX() - (ix + xOff), genPos.getY(), genPos.getZ() + ix + xOff);
                }

                if (face.getAxis().isVertical()) {
                    genPos = rotateDown(genPos, iy + yOff, player.getDirection());
                }

                if (genPos.equals(pos)) continue;
                BlockState state = world.getBlockState(genPos);
                float stateHardness = state.getDestroySpeed(world, genPos);
                if (!state.isAir() && stateHardness != -1 && stateHardness <= hardness * 3F && isEffective(state, player)) PlaceboUtil.tryHarvestBlock(player, genPos);
            }
        }

    }

    static BlockPos rotateDown(BlockPos pos, int y, Direction horizontal) {
        Vec3i vec = horizontal.getNormal();
        return new BlockPos(pos.getX() + vec.getX() * y, pos.getY() - y, pos.getZ() + vec.getZ() * y);
    }

    static boolean isEffective(BlockState state, Player player) {
        return player.hasCorrectToolForDrops(state);
    }

}
