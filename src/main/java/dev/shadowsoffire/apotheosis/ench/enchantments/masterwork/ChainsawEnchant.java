package dev.shadowsoffire.apotheosis.ench.enchantments.masterwork;

import dev.shadowsoffire.apotheosis.ench.EnchModule;
import dev.shadowsoffire.apotheosis.util.BlockUtil;
import dev.shadowsoffire.placebo.util.PlaceboTaskQueue;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import net.fabricmc.fabric.api.event.player.PlayerBlockBreakEvents;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentCategory;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

import java.util.ArrayDeque;
import java.util.Queue;
import java.util.UUID;
import java.util.function.BooleanSupplier;

public class ChainsawEnchant extends Enchantment {

    public ChainsawEnchant() {
        super(Rarity.VERY_RARE, EnchModule.AXE, new EquipmentSlot[] { EquipmentSlot.MAINHAND });
    }

    @Override
    public int getMaxLevel() {
        return 1;
    }

    @Override
    public int getMinCost(int level) {
        return 55;
    }

    @Override
    public int getMaxCost(int enchantmentLevel) {
        return 200;
    }

    @Override
    public Component getFullname(int level) {
        return ((MutableComponent) super.getFullname(level)).withStyle(ChatFormatting.DARK_GREEN);
    }

    public void chainsaw() {
        PlayerBlockBreakEvents.AFTER.register((world, player, pos, state, blockEntity) -> {
            Level level = player.level();
            ItemStack stack = player.getMainHandItem();
            int enchLevel = EnchantmentHelper.getItemEnchantmentLevel(this, stack);
            if (player.getClass() == ServerPlayer.class && enchLevel > 0 && !level.isClientSide && this.isTree(level, pos, state)) {
                if (!player.getAbilities().instabuild) PlaceboTaskQueue.submitTask("zenith:chainsaw_task", new ChainsawTask(player.getUUID(), stack, level, pos));
            }
        });

    }

    private boolean isTree(Level level, BlockPos pos, BlockState state) {
        if (!state.is(BlockTags.LOGS)) return false;
        while (state.is(BlockTags.LOGS)) {
            state = level.getBlockState(pos = pos.above());
        }
        for (BlockPos p : BlockPos.betweenClosed(pos.offset(-2, -2, -2), pos.offset(2, 2, 2))) {
            if (level.getBlockState(p).is(BlockTags.LEAVES) || level.getBlockState(p).is(BlockTags.WART_BLOCKS)) return true;
        }
        return false;
    }

    private static class ChainsawTask implements BooleanSupplier {

        UUID owner;
        ItemStack axe;
        ServerLevel level;
        Int2ObjectMap<Queue<BlockPos>> hits = new Int2ObjectOpenHashMap<>();
        int ticks = 0;

        public ChainsawTask(UUID owner, ItemStack axe, Level level, BlockPos pos) {
            this.owner = owner;
            this.axe = axe;
            this.level = (ServerLevel) level;
            this.hits.computeIfAbsent(pos.getY(), i -> new ArrayDeque<>()).add(pos);
        }

        @Override
        public boolean getAsBoolean() {
            if (++this.ticks % 2 != 0) return false;
            if (this.axe.isEmpty()) return true;
            int minY = this.hits.keySet().intStream().min().getAsInt();
            Queue<BlockPos> queue = this.hits.get(minY);
            int breaks = 0;
            while (!queue.isEmpty()) {
                BlockPos pos = queue.poll();
                for (BlockPos p : BlockPos.betweenClosed(pos.offset(-1, 0, -1), pos.offset(1, 1, 1))) {
                    if (p.equals(pos)) continue;
                    BlockState state = this.level.getBlockState(p);
                    if (state.is(BlockTags.LOGS)) {
                        BlockUtil.breakExtraBlock(this.level, p, this.axe, this.owner);
                        if (!this.level.getBlockState(p).is(BlockTags.LOGS)) { // Ensure a change happened
                            this.hits.computeIfAbsent(p.getY(), i -> new ArrayDeque<>()).add(p.immutable());
                            breaks++;
                        }
                    }
                }
                if (breaks > 5) break;
            }
            if (queue.isEmpty()) this.hits.remove(minY);
            return this.hits.isEmpty();
        }

    }
}
