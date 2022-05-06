package safro.apotheosis.mixin.spawner;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.SpawnerBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import safro.apotheosis.Apotheosis;
import safro.apotheosis.advancements.AdvancementTriggers;
import safro.apotheosis.spawn.SpawnerModule;
import safro.apotheosis.spawn.modifiers.SpawnerModifier;
import safro.apotheosis.spawn.modifiers.SpawnerStats;
import safro.apotheosis.spawn.spawner.ApothSpawnerTile;

import java.util.List;

@Mixin(SpawnerBlock.class)
public abstract class SpawnerBlockMixin extends BaseEntityBlock {

    public SpawnerBlockMixin(Properties properties) {
        super(properties);
    }

    @Inject(method = "getCloneItemStack", at = @At("HEAD"), cancellable = true)
    private void apothGetClone(BlockGetter world, BlockPos pos, BlockState blockState, CallbackInfoReturnable<ItemStack> cir) {
        if (Apotheosis.enableSpawner) {
            ItemStack s = new ItemStack(this);
            BlockEntity te = world.getBlockEntity(pos);
            if (te != null) s.getOrCreateTag().put("BlockEntityTag", te.saveWithoutMetadata());
            cir.setReturnValue(s);
        }
    }

    @Override
    public void setPlacedBy(Level world, BlockPos pos, BlockState state, LivingEntity placer, ItemStack stack) {
        BlockEntity te = world.getBlockEntity(pos);
        if (te != null) te.load(stack.getOrCreateTagElement("BlockEntityTag"));
    }

    @Inject(method = "newBlockEntity", at = @At("HEAD"), cancellable = true)
    private void apothNew(BlockPos pPos, BlockState pState, CallbackInfoReturnable<BlockEntity> cir) {
        if (Apotheosis.enableSpawner) {
            cir.setReturnValue(new ApothSpawnerTile(pPos, pState));
        }
    }

    @Override
    public void playerDestroy(Level world, Player player, BlockPos pos, BlockState state, BlockEntity te, ItemStack stack) {
        if (SpawnerModule.spawnerSilkLevel != -1 && EnchantmentHelper.getItemEnchantmentLevel(Enchantments.SILK_TOUCH, stack) >= SpawnerModule.spawnerSilkLevel) {
            ItemStack s = new ItemStack(this);
            if (te != null) s.getOrCreateTag().put("BlockEntityTag", te.saveWithoutMetadata());
            popResource(world, pos, s);
            player.getMainHandItem().hurtAndBreak(SpawnerModule.spawnerSilkDamage, player, pl -> pl.broadcastBreakEvent(EquipmentSlot.MAINHAND));
            player.awardStat(Stats.BLOCK_MINED.get(this));
            player.causeFoodExhaustion(0.035F);
        } else super.playerDestroy(world, player, pos, state, te, stack);
    }

    @Override
    public InteractionResult use(BlockState state, Level world, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
        BlockEntity te = world.getBlockEntity(pos);
        ItemStack stack = player.getItemInHand(hand);
        ItemStack otherStack = player.getItemInHand(hand == InteractionHand.MAIN_HAND ? InteractionHand.OFF_HAND : InteractionHand.MAIN_HAND);
        if (te instanceof ApothSpawnerTile tile) {
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

    @Override
    @Environment(EnvType.CLIENT)
    public void appendHoverText(ItemStack stack, BlockGetter worldIn, List<Component> tooltip, TooltipFlag flagIn) {
        if (stack.hasTag() && stack.getTag().contains("BlockEntityTag", Tag.TAG_COMPOUND)) {
            if (Screen.hasShiftDown()) {
                CompoundTag tag = stack.getTag().getCompound("BlockEntityTag");
                if (tag.contains("SpawnData")) {
                    String name = tag.getCompound("SpawnData").getCompound("entity").getString("id");
                    String key = "entity." + name.replace(':', '.');
                    tooltip.add(concat(new TranslatableComponent("misc.apotheosis.entity"), I18n.exists(key) ? I18n.get(key) : name));
                }
                if (tag.contains("MinSpawnDelay")) tooltip.add(concat(SpawnerStats.MIN_DELAY.name(), tag.getShort("MinSpawnDelay")));
                if (tag.contains("MaxSpawnDelay")) tooltip.add(concat(SpawnerStats.MAX_DELAY.name(), tag.getShort("MaxSpawnDelay")));
                if (tag.contains("SpawnCount")) tooltip.add(concat(SpawnerStats.SPAWN_COUNT.name(), tag.getShort("SpawnCount")));
                if (tag.contains("MaxNearbyEntities")) tooltip.add(concat(SpawnerStats.MAX_NEARBY_ENTITIES.name(), tag.getShort("MaxNearbyEntities")));
                if (tag.contains("RequiredPlayerRange")) tooltip.add(concat(SpawnerStats.REQ_PLAYER_RANGE.name(), tag.getShort("RequiredPlayerRange")));
                if (tag.contains("SpawnRange")) tooltip.add(concat(SpawnerStats.SPAWN_RANGE.name(), tag.getShort("SpawnRange")));
                if (tag.getBoolean("ignore_players")) tooltip.add(SpawnerStats.IGNORE_PLAYERS.name().withStyle(ChatFormatting.DARK_GREEN));
                if (tag.getBoolean("ignore_conditions")) tooltip.add(SpawnerStats.IGNORE_CONDITIONS.name().withStyle(ChatFormatting.DARK_GREEN));
                if (tag.getBoolean("redstone_control")) tooltip.add(SpawnerStats.REDSTONE_CONTROL.name().withStyle(ChatFormatting.DARK_GREEN));
                if (tag.getBoolean("ignore_light")) tooltip.add(SpawnerStats.IGNORE_LIGHT.name().withStyle(ChatFormatting.DARK_GREEN));
                if (tag.getBoolean("no_ai")) tooltip.add(SpawnerStats.NO_AI.name().withStyle(ChatFormatting.DARK_GREEN));
            } else {
                tooltip.add(new TranslatableComponent("misc.apotheosis.shift_stats").withStyle(ChatFormatting.GRAY));
            }
        }
    }

    private static Component concat(Object... args) {
        return new TranslatableComponent("misc.apotheosis.value_concat", args[0], new TextComponent(args[1].toString()).withStyle(ChatFormatting.GRAY)).withStyle(ChatFormatting.GREEN);
    }

    @Override
    public Item asItem() {
        return Items.SPAWNER;
    }
}
