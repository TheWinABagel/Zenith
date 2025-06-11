package dev.shadowsoffire.apotheosis.ench.compat;


import dev.shadowsoffire.apotheosis.Apotheosis;
import dev.shadowsoffire.apotheosis.ench.Ench;
import dev.shadowsoffire.apotheosis.ench.objects.FilteringShelfBlock;
import dev.shadowsoffire.apotheosis.mixin.accessors.ChiseledBookShelfBlockAccessor;
import dev.shadowsoffire.apotheosis.util.CommonTooltipUtil;
import dev.shadowsoffire.apotheosis.util.ZenithModCompat;
import mcp.mobius.waila.api.*;
import mcp.mobius.waila.api.component.ItemComponent;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.EnchantedBookItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.block.AnvilBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.phys.Vec2;
import org.jetbrains.annotations.Nullable;

import java.text.DecimalFormat;
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
            ZenithModCompat.Ench.easyAnvilsWarn(tooltip::addLine);
            for (Map.Entry<Enchantment, Integer> e : enchants.entrySet()) {
                tooltip.addLine(e.getKey().getFullname(e.getValue()));
            }
        }
        CommonTooltipUtil.appendBlockStats(accessor.getWorld(), accessor.getBlockState(), accessor.getPosition(), tooltip::addLine);
        if (accessor.getBlock() == Blocks.ENCHANTING_TABLE) {
            ZenithModCompat.Ench.easyMagicWarn(tooltip::addLine);
            CommonTooltipUtil.appendTableStats(accessor.getWorld(), accessor.getPosition(), tooltip::addLine);
        }

        if (accessor.getBlock() == Ench.Blocks.FILTERING_SHELF) this.handleFilteringShelf(tooltip, accessor);
    }

    @Override
    public @Nullable ITooltipComponent getIcon(IBlockAccessor accessor, IPluginConfig config) {
        if (!Apotheosis.enableEnch) return IBlockComponentProvider.super.getIcon(accessor, config);;
        if (accessor.getBlock() == Ench.Blocks.FILTERING_SHELF) {
            return new ItemComponent(accessor.getStack()); // Need to override the book icon back to the shelf when WTHIT triggers vanilla integration.
        }
        return IBlockComponentProvider.super.getIcon(accessor, config);
    }

    public void handleFilteringShelf(ITooltip tooltip, IBlockAccessor accessor) {

        if (accessor.getBlockEntity() instanceof FilteringShelfBlock.FilteringShelfTile tile) {
            tooltip.getLine(new ResourceLocation("chiseled_bookshelf"));
            Optional<Vec2> optional = ChiseledBookShelfBlockAccessor.callGetRelativeHitCoordinatesForBlockFace(accessor.getBlockHitResult(), accessor.getBlockState().getValue(HorizontalDirectionalBlock.FACING));
            if (optional.isEmpty()) {
                return;
            }
            int slot = ChiseledBookShelfBlockAccessor.callGetHitSlot(optional.get());
            ItemStack stack = tile.getItem(slot);
            if (stack.isEmpty()) return;
            tooltip.addLine(CommonComponents.EMPTY);
            tooltip.addLine().with(new ItemComponent(stack))
                    .with(Component.literal(" ")
                            .append(Component.literal(humanReadableNumber(stack.getCount(), "", false)).append("× ").append(stack.getHoverName())));

            if (stack.getTag() != null && stack.getTag().contains(EnchantedBookItem.TAG_STORED_ENCHANTMENTS)) {
                List<Component> list = new ArrayList<>();
                ItemStack.appendEnchantmentNames(list, EnchantedBookItem.getEnchantments(stack));
                for (Component c : list)
                    tooltip.addLine(Component.literal(" - ").append(c));
            }
        }
    }

    public static DecimalFormat dfCommas = new DecimalFormat("##.##");

    public String humanReadableNumber(double number, String unit, boolean milli) {
        StringBuilder sb = new StringBuilder();
        boolean n = number < 0.0;
        if (n) {
            number = -number;
            sb.append('-');
        }

        if (milli && number >= 1000.0) {
            number /= 1000.0;
            milli = false;
        }

        if (number < 1000.0) {
            sb.append(dfCommas.format(number));
            if (milli && number != 0.0) {
                sb.append('m');
            }
        } else {
            int exp = (int)(Math.log10(number) / 3.0);
            if (exp > 7) {
                exp = 7;
            }

            char pre = "kMGTPEZ".charAt(exp - 1);
            sb.append(dfCommas.format(number / Math.pow(1000.0, (double)exp)));
            sb.append(pre);
        }

        sb.append(unit);
        return sb.toString();
    }
}
