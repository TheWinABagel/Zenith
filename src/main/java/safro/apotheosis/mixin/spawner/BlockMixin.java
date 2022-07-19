package safro.apotheosis.mixin.spawner;

import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.stats.Stats;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SpawnerBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import safro.apotheosis.Apotheosis;
import safro.apotheosis.spawn.SpawnerModule;
import safro.apotheosis.spawn.modifiers.SpawnerStats;

import java.util.List;

@Mixin(Block.class)
public class BlockMixin {

    @Inject(method = "setPlacedBy", at = @At("HEAD"))
    public void apothSetPlacedBy(Level world, BlockPos pos, BlockState state, LivingEntity placer, ItemStack stack, CallbackInfo ci) {
        if (Apotheosis.enableSpawner && state.is(Blocks.SPAWNER)) {
            BlockEntity te = world.getBlockEntity(pos);
            if (te != null && stack.hasTag()) te.load(stack.getOrCreateTagElement("BlockEntityTag"));
        }
    }

    @Inject(method = "playerDestroy", at = @At("HEAD"), cancellable = true)
    private void apothPlayerDestroy(Level world, Player player, BlockPos pos, BlockState state, BlockEntity te, ItemStack stack, CallbackInfo ci) {
        if (Apotheosis.enableSpawner && state.getBlock() instanceof SpawnerBlock block) {
            if (SpawnerModule.spawnerSilkLevel != -1 && EnchantmentHelper.getItemEnchantmentLevel(Enchantments.SILK_TOUCH, stack) >= SpawnerModule.spawnerSilkLevel) {
                ItemStack s = new ItemStack(block);
                if (te != null) s.getOrCreateTag().put("BlockEntityTag", te.saveWithoutMetadata());
                Block.popResource(world, pos, s);
                player.getMainHandItem().hurtAndBreak(SpawnerModule.spawnerSilkDamage, player, pl -> pl.broadcastBreakEvent(EquipmentSlot.MAINHAND));
                player.awardStat(Stats.BLOCK_MINED.get(block));
                player.causeFoodExhaustion(0.035F);
                ci.cancel();
            }
        }
    }

    @Inject(method = "appendHoverText", at = @At("HEAD"))
    private void apothAppendHoverText(ItemStack stack, BlockGetter blockGetter, List<Component> tooltip, TooltipFlag tooltipFlag, CallbackInfo ci) {
        if (Apotheosis.enableSpawner && stack.is(Items.SPAWNER)) {
            if (stack.hasTag() && stack.getTag().contains("BlockEntityTag", Tag.TAG_COMPOUND)) {
                if (Screen.hasShiftDown()) {
                    CompoundTag tag = stack.getTag().getCompound("BlockEntityTag");
                    if (tag.contains("MinSpawnDelay"))
                        tooltip.add(concat(SpawnerStats.MIN_DELAY.name(), tag.getShort("MinSpawnDelay")));
                    if (tag.contains("MaxSpawnDelay"))
                        tooltip.add(concat(SpawnerStats.MAX_DELAY.name(), tag.getShort("MaxSpawnDelay")));
                    if (tag.contains("SpawnCount"))
                        tooltip.add(concat(SpawnerStats.SPAWN_COUNT.name(), tag.getShort("SpawnCount")));
                    if (tag.contains("MaxNearbyEntities"))
                        tooltip.add(concat(SpawnerStats.MAX_NEARBY_ENTITIES.name(), tag.getShort("MaxNearbyEntities")));
                    if (tag.contains("RequiredPlayerRange"))
                        tooltip.add(concat(SpawnerStats.REQ_PLAYER_RANGE.name(), tag.getShort("RequiredPlayerRange")));
                    if (tag.contains("SpawnRange"))
                        tooltip.add(concat(SpawnerStats.SPAWN_RANGE.name(), tag.getShort("SpawnRange")));
                    if (tag.getBoolean("ignore_players"))
                        tooltip.add(SpawnerStats.IGNORE_PLAYERS.name().withStyle(ChatFormatting.DARK_GREEN));
                    if (tag.getBoolean("ignore_conditions"))
                        tooltip.add(SpawnerStats.IGNORE_CONDITIONS.name().withStyle(ChatFormatting.DARK_GREEN));
                    if (tag.getBoolean("redstone_control"))
                        tooltip.add(SpawnerStats.REDSTONE_CONTROL.name().withStyle(ChatFormatting.DARK_GREEN));
                    if (tag.getBoolean("ignore_light"))
                        tooltip.add(SpawnerStats.IGNORE_LIGHT.name().withStyle(ChatFormatting.DARK_GREEN));
                    if (tag.getBoolean("no_ai"))
                        tooltip.add(SpawnerStats.NO_AI.name().withStyle(ChatFormatting.DARK_GREEN));
                } else {
                    tooltip.add(new TranslatableComponent("misc.apotheosis.shift_stats").withStyle(ChatFormatting.GRAY));
                }
            }
        }
    }

    private static Component concat(Object... args) {
        return new TranslatableComponent("misc.apotheosis.value_concat", args[0], new TextComponent(args[1].toString()).withStyle(ChatFormatting.GRAY)).withStyle(ChatFormatting.GREEN);
    }
}
