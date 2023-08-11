package safro.zenith.mixin.anvil;

import net.minecraft.nbt.Tag;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.FallingBlockEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.AnvilBlock;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.FallingBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import safro.zenith.Zenith;
import safro.zenith.adventure.AdventureModule;
import safro.zenith.util.INBTSensitiveFallingBlock;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.EntitySelector;
import net.minecraft.world.item.EnchantedBookItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.EnchantmentInstance;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.block.*;
import net.minecraft.world.phys.AABB;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import safro.zenith.advancements.AdvancementTriggers;
import safro.zenith.ench.EnchModule;
import safro.zenith.ench.anvil.AnvilBlockEntity;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static net.minecraft.world.level.block.AnvilBlock.FACING;

/**
     * Anvils were re-written with mixins instead of replacing the block entirely to improve compat
     */

    @Mixin(value = AnvilBlock.class, priority = 1500)
    public abstract class AnvilBlockMixin extends FallingBlock implements INBTSensitiveFallingBlock, EntityBlock {

        public AnvilBlockMixin(Properties properties) {
            super(properties);
        }

        @Override
        public BlockEntity newBlockEntity(BlockPos pPos, BlockState pState) {
                return new AnvilBlockEntity(pPos, pState);
        }

        @Override
        public void tick(BlockState pState, ServerLevel pLevel, BlockPos pPos, RandomSource pRand) {
            if (Zenith.enableEnch) {
                if (!FallingBlock.isFree(pLevel.getBlockState(pPos.below())) || pPos.getY() < pLevel.getMinBuildHeight()) {
                    return;
                }
                    if (Zenith.DEBUG) Zenith.LOGGER.info("ANVIL IS FREE");
                    BlockEntity be = pLevel.getBlockEntity(pPos);
                    FallingBlockEntity e = FallingBlockEntity.fall(pLevel, pPos, pState);
                    if (be instanceof AnvilBlockEntity anvil) {
                       if (Zenith.DEBUG) Zenith.LOGGER.info("ANVIL HAS TILE");
                       e.blockData = new CompoundTag();
                        anvil.saveAdditional(e.blockData);
                    }
                    this.falling(e);
                }
             else
                super.tick(pState, pLevel, pPos, pRand);
        }

        @Inject(method = "damage", at = @At("HEAD"), cancellable = true)
        private static void zenithDamage(BlockState blockState, CallbackInfoReturnable<BlockState> cir) {

            if (Zenith.enableEnch) {

                if (blockState.is(Blocks.ANVIL)) {
                    cir.setReturnValue(Blocks.CHIPPED_ANVIL.withPropertiesOf(blockState).setValue(FACING, blockState.getValue(FACING)));
                }
                if (blockState.is(Blocks.CHIPPED_ANVIL)) {
                    cir.setReturnValue(Blocks.DAMAGED_ANVIL.withPropertiesOf(blockState).setValue(FACING, blockState.getValue(FACING)));
                }
            }
        }

        @Inject(method = "onLand", at = @At("TAIL"))
        private void zenithOnLand(Level world, BlockPos pos, BlockState fallState, BlockState hitState, FallingBlockEntity anvil, CallbackInfo ci) {
            //if (Zenith.enableAdventure) gemSmashing(world, pos);
            if (Zenith.enableEnch) {
                List<ItemEntity> items = world.getEntitiesOfClass(ItemEntity.class, new AABB(pos, pos.offset(1, 1, 1)));
                if (anvil.blockData != null) {
                    Map<Enchantment, Integer> enchantments = EnchantmentHelper.deserializeEnchantments(anvil.blockData.getList("enchantments", Tag.TAG_COMPOUND));
                    int oblit = enchantments.getOrDefault(EnchModule.OBLITERATION, 0);
                    int split = enchantments.getOrDefault(EnchModule.SPLITTING, 0);
                    int ub = enchantments.getOrDefault(Enchantments.UNBREAKING, 0);
                    if (split > 0 || oblit > 0) for (ItemEntity entity : items) {
                        ItemStack stack = entity.getItem();
                        if (stack.getItem() == Items.ENCHANTED_BOOK) {
                            ListTag enchants = EnchantedBookItem.getEnchantments(stack);
                            boolean handled = false;
                            if (enchants.size() == 1 && oblit > 0) {
                                handled = this.handleObliteration(world, pos, entity, enchants);
                            } else if (enchants.size() > 1 && split > 0) {
                                handled = this.handleSplitting(world, pos, entity, enchants);
                            }
                            if (handled) {
                                if (world.random.nextInt(1 + ub) == 0) {
                                    BlockEntity copy = world.getBlockEntity(pos);
                                    BlockState dmg = AnvilBlock.damage(fallState);
                                    if (copy != null) world.setBlockEntity(copy);
                                    boolean isUnbreakable = fallState.is(EnchModule.UNBREAKABLE_ANVIL);
                                    if (dmg == null && !isUnbreakable) {
                                        world.setBlockAndUpdate(pos, Blocks.AIR.defaultBlockState());
                                        world.levelEvent(LevelEvent.SOUND_ANVIL_BROKEN, pos, 0);
                                    } else world.setBlockAndUpdate(pos, dmg);
                                }
                                break;
                            }
                        }
                    }
                }
            }
        }

        protected boolean handleSplitting(Level world, BlockPos pos, ItemEntity entity, ListTag enchants) {
            entity.remove(Entity.RemovalReason.DISCARDED);
            for (Tag nbt : enchants) {
                CompoundTag tag = (CompoundTag) nbt;
                int level = tag.getInt("lvl");
                Enchantment enchant = Registry.ENCHANTMENT.get(new ResourceLocation(tag.getString("id")));
                if (enchant == null) continue;
                ItemStack book = EnchantedBookItem.createForEnchantment(new EnchantmentInstance(enchant, level));
                Block.popResource(world, pos.above(), book);
            }
            world.getEntitiesOfClass(ServerPlayer.class, new AABB(pos).inflate(5, 5, 5), EntitySelector.NO_SPECTATORS).forEach(p -> {
                AdvancementTriggers.SPLIT_BOOK.trigger(p.getAdvancements());
            });
            return true;
        }

        protected boolean handleObliteration(Level world, BlockPos pos, ItemEntity entity, ListTag enchants) {
            CompoundTag tag = enchants.getCompound(0);
            int level = tag.getInt("lvl") - 1;
            if (level <= 0) return false;
            Enchantment enchant = Registry.ENCHANTMENT.get(new ResourceLocation(tag.getString("id")));
            if (enchant == null) return false;
            ItemStack book = EnchantedBookItem.createForEnchantment(new EnchantmentInstance(enchant, level));
            entity.remove(Entity.RemovalReason.DISCARDED);
            Block.popResource(world, pos.above(), book);
            Block.popResource(world, pos.above(), book.copy());
            return true;
        }

        @Override
        public ItemStack toStack(BlockState state, CompoundTag tag) {
            AnvilBlock a = (AnvilBlock) (Object) this;
            ItemStack anvil = new ItemStack(a);
            Map<Enchantment, Integer> ench = EnchantmentHelper.deserializeEnchantments(tag.getList("enchantments", Tag.TAG_COMPOUND));
            ench = ench.entrySet().stream().filter(e -> e.getValue() > 0).collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
            EnchantmentHelper.setEnchantments(ench, anvil);
            return anvil;
        }

/*
    public void gemSmashing(Level world, BlockPos pos) {
        List<ItemEntity> items = world.getEntitiesOfClass(ItemEntity.class, new AABB(pos, pos.offset(1, 1, 1)));
        for (ItemEntity ent : items) {
            ItemStack stack = ent.getItem();
            if (stack.getItem() == AdventureModule.GEM) {
                ent.setItem(new ItemStack(AdventureModule.GEM_DUST, stack.getCount()));
            }
        }
    }*/
}