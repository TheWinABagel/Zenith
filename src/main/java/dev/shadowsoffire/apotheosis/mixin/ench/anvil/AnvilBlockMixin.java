package dev.shadowsoffire.apotheosis.mixin.ench.anvil;

import dev.shadowsoffire.apotheosis.Apotheosis;
import dev.shadowsoffire.apotheosis.advancements.AdvancementTriggers;
import dev.shadowsoffire.apotheosis.ench.EnchModule;
import dev.shadowsoffire.apotheosis.ench.anvil.AnvilTile;
import dev.shadowsoffire.apotheosis.util.INBTSensitiveFallingBlock;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntitySelector;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.FallingBlockEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.EnchantedBookItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.EnchantmentInstance;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.phys.AABB;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Mixin(value = AnvilBlock.class, priority = 1500)
public abstract class AnvilBlockMixin extends FallingBlock implements INBTSensitiveFallingBlock, EntityBlock {

    public AnvilBlockMixin(Properties properties) {
        super(properties);
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new AnvilTile(pos, state);
    }

    @Environment(EnvType.CLIENT)
    @Override
    public void appendHoverText(ItemStack itemStack, @Nullable BlockGetter blockGetter, List<Component> list, TooltipFlag tooltipFlag) {
        if (Apotheosis.enableEnch && EnchModule.isVanillaAnvil(itemStack)) {
            if (!itemStack.hasFoil()) {
                list.add(Component.translatable("info.zenith.anvil").withStyle(ChatFormatting.GRAY));
            }
        }
    }

    @Override
    public ItemStack getCloneItemStack(BlockGetter level, BlockPos pos, BlockState state) {
        ItemStack anvil = new ItemStack(this);
        BlockEntity te = level.getBlockEntity(pos);
        if (te instanceof AnvilTile anvilTile && Apotheosis.enableEnch) {
            Map<Enchantment, Integer> ench = anvilTile.getEnchantments();
            ench = ench.entrySet().stream().filter(e -> e.getValue() > 0).collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
            EnchantmentHelper.setEnchantments(ench, anvil);
        }
        return anvil;
    }

    @Inject(method = "onLand", at = @At("HEAD"))
    public void onLand(Level world, BlockPos pos, BlockState fallState, BlockState hitState, FallingBlockEntity anvil, CallbackInfo ci) {
        if (Apotheosis.enableEnch) {
            super.onLand(world, pos, fallState, hitState, anvil);
            List<ItemEntity> items = world.getEntitiesOfClass(ItemEntity.class, new AABB(pos, pos.offset(1, 1, 1)));
            if (anvil.blockData == null) return;
            Map<Enchantment, Integer> enchantments = EnchantmentHelper.deserializeEnchantments(anvil.blockData.getList("enchantments", Tag.TAG_COMPOUND));
            int oblit = enchantments.getOrDefault(dev.shadowsoffire.apotheosis.ench.Ench.Enchantments.OBLITERATION, 0);
            int split = enchantments.getOrDefault(dev.shadowsoffire.apotheosis.ench.Ench.Enchantments.SPLITTING, 0);
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
                            BlockState dmg = AnvilBlock.damage(fallState);
                            if (dmg == null) {
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

    @Override
    public void tick(BlockState pState, ServerLevel pLevel, BlockPos pPos, RandomSource pRand) {
        if ((isFree(pLevel.getBlockState(pPos.below())) && pPos.getY() >= pLevel.getMinBuildHeight()) && Apotheosis.enableEnch) {
            BlockEntity be = pLevel.getBlockEntity(pPos);
            FallingBlockEntity e = FallingBlockEntity.fall(pLevel, pPos, pState);
            if (be instanceof AnvilTile anvil) {
                e.blockData = new CompoundTag();
                anvil.saveAdditional(e.blockData);
            }
            this.falling(e);
        }
        else super.tick(pState, pLevel, pPos, pRand);
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

    @Override
    public List<ItemStack> getDrops(BlockState state, LootParams.Builder builder) {
        ItemStack anvil = new ItemStack((AnvilBlock) (Object) this);
        if (builder.getParameter(LootContextParams.BLOCK_ENTITY) instanceof AnvilTile te) {
            Map<Enchantment, Integer> ench = te.getEnchantments();
            ench = ench.entrySet().stream().filter(e -> e.getValue() > 0).collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
            EnchantmentHelper.setEnchantments(ench, anvil);
        }
        return List.of(anvil);
    }

    @Override
    public void setPlacedBy(Level level, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack stack) {
        if (Apotheosis.enableEnch) {
            BlockEntity te = level.getBlockEntity(pos);
            if (te instanceof AnvilTile anvil) {
                anvil.getEnchantments().putAll(EnchantmentHelper.getEnchantments(stack));
            }
        }
        else super.setPlacedBy(level, pos, state, placer, stack);
    }

    @Unique
    protected boolean handleSplitting(Level world, BlockPos pos, ItemEntity entity, ListTag enchants) {
        entity.remove(Entity.RemovalReason.DISCARDED);
        Boolean isCursed = entity.getItem().getTag().contains("BMCursed");
        for (Tag nbt : enchants) {
            CompoundTag tag = (CompoundTag) nbt;
            int level = tag.getInt("lvl");
            Enchantment enchant = BuiltInRegistries.ENCHANTMENT.get(new ResourceLocation(tag.getString("id")));
            if (enchant == null) continue;
            ItemStack book = EnchantedBookItem.createForEnchantment(new EnchantmentInstance(enchant, level));
            if (isCursed) book.getOrCreateTag().putBoolean("BMCursed", true);
            Block.popResource(world, pos.above(), book);
        }
        world.getEntitiesOfClass(ServerPlayer.class, new AABB(pos).inflate(5, 5, 5), EntitySelector.NO_SPECTATORS).forEach(p -> {
            AdvancementTriggers.SPLIT_BOOK.trigger(p.getAdvancements());
        });
        return true;
    }

    @Unique
    protected boolean handleObliteration(Level world, BlockPos pos, ItemEntity entity, ListTag enchants) {
        CompoundTag nbt = enchants.getCompound(0);
        int level = nbt.getInt("lvl") - 1;
        if (level <= 0) return false;
        Enchantment enchant = BuiltInRegistries.ENCHANTMENT.get(new ResourceLocation(nbt.getString("id")));
        if (enchant == null) return false;
        ItemStack book = EnchantedBookItem.createForEnchantment(new EnchantmentInstance(enchant, level));
        entity.remove(Entity.RemovalReason.DISCARDED);
        Block.popResource(world, pos.above(), book);
        Block.popResource(world, pos.above(), book.copy());
        return true;
    }

    @Override
    public void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean isMoving) {
        if (!Apotheosis.enableEnch) {
            super.onRemove(state, level, pos, newState, isMoving);
            return;
        }
        if (state.hasBlockEntity() && !newState.is(BlockTags.ANVIL)) {
            level.removeBlockEntity(pos);
        }
    }
}
