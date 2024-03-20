package dev.shadowsoffire.apotheosis.adventure.gen;

import com.mojang.serialization.Codec;
import com.yungnickyoung.minecraft.yungsapi.world.processor.StructureEntityProcessor;
import dev.shadowsoffire.apotheosis.Apotheosis;
import dev.shadowsoffire.apotheosis.adventure.AdventureModule;
import dev.shadowsoffire.apotheosis.adventure.affix.socket.gem.GemRegistry;
import dev.shadowsoffire.placebo.reload.WeightedDynamicRegistry.IDimensional;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructurePlaceSettings;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessorType;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate.StructureEntityInfo;
import org.jetbrains.annotations.Nullable;

@Deprecated
public class ItemFrameGemsProcessorYung extends StructureEntityProcessor {

    public static final Codec<ItemFrameGemsProcessorYung> CODEC = Codec.unit(new ItemFrameGemsProcessorYung(null));

    public static final StructureProcessorType<ItemFrameGemsProcessorYung> ITEM_FRAME_LOOT_YUNG = () -> ItemFrameGemsProcessorYung.CODEC;

    protected final ResourceLocation lootTable;

    public ItemFrameGemsProcessorYung(ResourceLocation lootTable) {
        this.lootTable = lootTable;
    }

    @Override
    protected StructureProcessorType<?> getType() {
        return ITEM_FRAME_LOOT_YUNG;
    }



    protected void writeEntityNBT(ServerLevel world, BlockPos pos, RandomSource rand, CompoundTag nbt, StructurePlaceSettings settings) {
        ItemStack stack = GemRegistry.createRandomGemStack(rand, world, 0, IDimensional.matches(world));
        CompoundTag tag = new CompoundTag();
        stack.save(tag);
        nbt.put("Item", tag);
        nbt.putInt("TileX", pos.getX());
        nbt.putInt("TileY", pos.getY());
        nbt.putInt("TileZ", pos.getZ());
    }

    @Nullable
    @Override
    public StructureEntityInfo processEntity(ServerLevelAccessor sla, BlockPos pos, BlockPos pos1, StructureEntityInfo localEntityInfo, StructureEntityInfo globalEntityInfo, StructurePlaceSettings structurePlaceSettings) {
        CompoundTag entityNBT = globalEntityInfo.nbt;
        if (!Apotheosis.enableAdventure) return globalEntityInfo;

        String id = entityNBT.getString("id"); // entity type ID
        if ("minecraft:item_frame".equals(id)) {
            AdventureModule.LOGGER.info("ENTITY IS ITEM FRAME, global pos {}, raw pos {}", globalEntityInfo.pos, localEntityInfo.pos);
            this.writeEntityNBT(sla.getLevel(), globalEntityInfo.blockPos, structurePlaceSettings.getRandom(globalEntityInfo.blockPos), entityNBT, structurePlaceSettings);
        }

        return globalEntityInfo;
    }
}
