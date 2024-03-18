package dev.shadowsoffire.apotheosis.mixin.spawn;

import dev.shadowsoffire.apotheosis.Apotheosis;
import dev.shadowsoffire.apotheosis.advancements.AdvancementTriggers;
import dev.shadowsoffire.apotheosis.spawn.SpawnerModule;
import dev.shadowsoffire.apotheosis.spawn.modifiers.SpawnerModifier;
import dev.shadowsoffire.apotheosis.spawn.modifiers.SpawnerStats;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SpawnerBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.SpawnerBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.phys.BlockHitResult;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;


@Mixin(SpawnerBlock.class)
public abstract class SpawnerBlockMixin extends BaseEntityBlock {

    protected SpawnerBlockMixin(Properties properties) {
        super(properties);
    }

    @Override
    public void playerDestroy(Level world, Player player, BlockPos pos, BlockState state, BlockEntity te, ItemStack stack) {
        if (Apotheosis.enableSpawner && SpawnerModule.spawnerSilkLevel != -1 && EnchantmentHelper.getItemEnchantmentLevel(Enchantments.SILK_TOUCH, stack) >= SpawnerModule.spawnerSilkLevel) {
            if (SpawnerModule.spawnerSilkDamage > 1) {
                player.getMainHandItem().hurtAndBreak(SpawnerModule.spawnerSilkDamage - 1, player, pl -> pl.broadcastBreakEvent(EquipmentSlot.MAINHAND));
            }
            player.awardStat(Stats.BLOCK_MINED.get(this));
            player.causeFoodExhaustion(0.035F);
        }
        else super.playerDestroy(world, player, pos, state, te, stack);
    }

    @Override
    @Deprecated
    public List<ItemStack> getDrops(BlockState state, LootParams.Builder params) {
        if (!Apotheosis.enableSpawner) return super.getDrops(state, params);
        ItemStack tool = params.getParameter(LootContextParams.TOOL);
        if (SpawnerModule.spawnerSilkLevel != -1 && EnchantmentHelper.getItemEnchantmentLevel(Enchantments.SILK_TOUCH, tool) >= SpawnerModule.spawnerSilkLevel) {
            ItemStack s = new ItemStack(this);
            BlockEntity te = params.getOptionalParameter(LootContextParams.BLOCK_ENTITY);
            if (te != null) s.getOrCreateTag().put("BlockEntityTag", te.saveWithoutMetadata());
            return List.of(s);
        }

        return super.getDrops(state, params);
    }

    @Override
    public void setPlacedBy(Level world, BlockPos pos, BlockState state, LivingEntity placer, ItemStack stack) {
        BlockEntity te = world.getBlockEntity(pos);
        if (te != null && stack.hasTag()) te.load(stack.getOrCreateTagElement("BlockEntityTag"));
    }

    @Override
    public InteractionResult use(BlockState state, Level world, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
        if (!Apotheosis.enableSpawner) return InteractionResult.PASS;
        BlockEntity te = world.getBlockEntity(pos);
        ItemStack stack = player.getItemInHand(hand);
        ItemStack otherStack = player.getItemInHand(hand == InteractionHand.MAIN_HAND ? InteractionHand.OFF_HAND : InteractionHand.MAIN_HAND);
        if (te instanceof SpawnerBlockEntity tile) {
            SpawnerModifier match = SpawnerModifier.findMatch(tile, stack, otherStack);
            if (match != null && match.apply(tile)) {
                if (world.isClientSide) return InteractionResult.SUCCESS;
                if (!player.isCreative()) {
                    stack.shrink(1);
                    if (match.consumesOffhand()) otherStack.shrink(1);
                }
                AdvancementTriggers.SPAWNER_MODIFIER.trigger((ServerPlayer) player, tile, match);
                world.sendBlockUpdated(pos, state, state, 3);
                return InteractionResult.SUCCESS;
            }
        }
        return InteractionResult.PASS;
    }

    @Environment(EnvType.CLIENT)
    @Inject(method = "appendHoverText", at = @At("HEAD"), cancellable = true)
    public void zenith$spawnerHoverText(ItemStack stack, @Nullable BlockGetter level, List<Component> tooltip, TooltipFlag flag, CallbackInfo ci) {
        if (Apotheosis.enableSpawner && stack.hasTag() && stack.getTag().contains("BlockEntityTag", Tag.TAG_COMPOUND)) {
            if (Screen.hasShiftDown()) {
                CompoundTag tag = stack.getTag().getCompound("BlockEntityTag");
                SpawnerBlockEntity tooltipTile = new SpawnerBlockEntity(BlockPos.ZERO, Blocks.AIR.defaultBlockState());
                tooltipTile.load(tag);
                SpawnerStats.generateTooltip(tooltipTile, tooltip::add);
            }
            else {
                tooltip.add(Component.translatable("misc.zenith.shift_stats").withStyle(ChatFormatting.GRAY));
            }
            ci.cancel();
        }
    }
}
