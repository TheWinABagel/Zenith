package dev.shadowsoffire.apotheosis.ench.compat;


import dev.shadowsoffire.apotheosis.Apotheosis;
import dev.shadowsoffire.apotheosis.ench.Ench;
import dev.shadowsoffire.apotheosis.ench.objects.FilteringShelfBlock;
import dev.shadowsoffire.apotheosis.util.CommonTooltipUtil;
import mcp.mobius.waila.api.*;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.EnchantedBookItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.block.*;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec2;
import net.minecraft.world.phys.Vec3;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class EnchWTHITPlugin implements IWailaPlugin, IBlockComponentProvider {

    @Override
    public void register(IRegistrar reg) {
        if (!Apotheosis.enableEnch) return;
        reg.addComponent(this, TooltipPosition.BODY, Block.class);
    }

    @Override
    public void appendBody(ITooltip tooltip, IBlockAccessor accessor, IPluginConfig config) {
        if (!Apotheosis.enableEnch) return;
        if (accessor.getBlock() instanceof AnvilBlock) {
            CompoundTag tag = accessor.getData().raw();
            Map<Enchantment, Integer> enchants = EnchantmentHelper.deserializeEnchantments(tag.getList("enchantments", Tag.TAG_COMPOUND));
            for (Map.Entry<Enchantment, Integer> e : enchants.entrySet()) {
                tooltip.addLine(e.getKey().getFullname(e.getValue()));
            }
        }
        CommonTooltipUtil.appendBlockStats(accessor.getWorld(), accessor.getBlockState(), accessor.getPosition(), tooltip::addLine);
        if (accessor.getBlock() == Blocks.ENCHANTING_TABLE) {
            CommonTooltipUtil.appendTableStats(accessor.getWorld(), accessor.getPosition(), tooltip::addLine);
        }

    //    if (accessor.getBlock() == Ench.Blocks.FILTERING_SHELF) handleFilteringShelf(tooltip, accessor);
    }
/*
    @Override
    public @Nullable ITooltipComponent getIcon(IBlockAccessor accessor, IPluginConfig config) {
        if (accessor.getBlock() == Ench.Blocks.FILTERING_SHELF) {
            return IElementHelper.get().item(accessor.getStack()); // Need to override the book icon back to the shelf when Jade triggers vanilla integration.
        }
        return accessor.getData();
    }*/

    public void handleFilteringShelf(ITooltip tooltip, IBlockAccessor accessor) {

        if (accessor.getBlockEntity() instanceof FilteringShelfBlock.FilteringShelfTile tile) {
            Optional<Vec2> optional = Optional.empty(); // getRelativeHitCoordinatesForBlockFace(accessor.getBlockState().getValue(HorizontalDirectionalBlock.FACING));
            if (optional.isEmpty()) {
                return;
            }
            int slot = ChiseledBookShelfBlock.getHitSlot(optional.get());
            ItemStack stack = tile.getItem(slot);
            if (stack.isEmpty()) return;
            tooltip.addLine(CommonComponents.EMPTY);

            if (stack.getTag() != null && stack.getTag().contains(EnchantedBookItem.TAG_STORED_ENCHANTMENTS)) {
                List<Component> list = new ArrayList<>();
                ItemStack.appendEnchantmentNames(list, EnchantedBookItem.getEnchantments(stack));
                for (Component c : list)
                    tooltip.addLine(Component.literal(" - ").append(c));
            }
        }
    }
/*
    public static Optional<Vec2> getRelativeHitCoordinatesForBlockFace(IBlockAccessor accessor) {
        Direction direction = accessor.getBlockState().getValue(HorizontalDirectionalBlock.FACING);
        BlockPos blockPos = accessor.getPosition().relative(direction);
        Vec3 vec3 = accessor.getPlayer().subtract(blockPos.getX(), blockPos.getY(), blockPos.getZ());
        double d = vec3.x();
        double e = vec3.y();
        double f = vec3.z();
        return switch (direction) {
            default -> throw new IncompatibleClassChangeError();
            case NORTH -> Optional.of(new Vec2((float)(1.0 - d), (float)e));
            case SOUTH -> Optional.of(new Vec2((float)d, (float)e));
            case WEST -> Optional.of(new Vec2((float)f, (float)e));
            case EAST -> Optional.of(new Vec2((float)(1.0 - f), (float)e));
            case DOWN, UP -> Optional.empty();
        };
    }*/
}
