package dev.shadowsoffire.apotheosis.ench.enchantments;

import dev.shadowsoffire.apotheosis.ench.Ench;
import dev.shadowsoffire.apotheosis.ench.EnchModule;
import net.fabricmc.fabric.api.event.player.UseBlockCallback;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.BoneMealItem;
import net.minecraft.world.item.HoeItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentCategory;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

public class NaturesBlessingEnchant extends Enchantment {

    public NaturesBlessingEnchant() {
        super(Rarity.RARE, EnchModule.HOE, new EquipmentSlot[0]);
    }

    @Override
    public boolean canEnchant(ItemStack stack) {
        return stack.getItem() instanceof HoeItem;
    }

    @Override
    public int getMaxLevel() {
        return 3;
    }

    @Override
    public int getMinCost(int level) {
        return 25 + level * 10;
    }

    @Override
    public int getMaxCost(int level) {
        return 200;
    }

    public void rightClick() {
        UseBlockCallback.EVENT.register((player, world, hand, hitResult) -> {
            ItemStack s = player.getItemInHand(hand);
            int nbLevel = EnchantmentHelper.getItemEnchantmentLevel(Ench.Enchantments.NATURES_BLESSING, s);
            if (!player.isShiftKeyDown() && nbLevel > 0 && useBonemeal(s.copy(), world, hitResult.getBlockPos(), hitResult.getDirection())) {
                s.hurtAndBreak(Math.max(1, 6 - nbLevel), player, ent -> ent.broadcastBreakEvent(hand));
                //e.setCanceled(true);
                return InteractionResult.SUCCESS;
            }
            return InteractionResult.PASS;
        });

    }

    private boolean useBonemeal (ItemStack stack, Level level, BlockPos blockPos, Direction direction){
        BlockPos blockPos2 = blockPos.relative(direction);
        if (BoneMealItem.growCrop(stack, level, blockPos)) {
            if (!level.isClientSide) {
                level.levelEvent(1505, blockPos, 0);
            }
            return true;
        } else {
            BlockState blockState = level.getBlockState(blockPos);
            boolean bl = blockState.isFaceSturdy(level, blockPos, direction);
            if (bl && BoneMealItem.growWaterPlant(stack, level, blockPos2, direction)) {
                if (!level.isClientSide) {
                    level.levelEvent(1505, blockPos2, 0);
                }
                return true;
            } else {
                return false;
            }
        }
    }
}
