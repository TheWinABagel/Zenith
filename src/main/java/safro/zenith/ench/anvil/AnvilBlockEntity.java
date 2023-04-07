package safro.zenith.ench.anvil;

import java.util.HashMap;
import java.util.Map;

import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import safro.zenith.ench.EnchModule;

import static net.minecraft.world.item.enchantment.Enchantments.BLOCK_FORTUNE;

public class AnvilBlockEntity extends BlockEntity {
    protected final Object2IntMap<Enchantment> enchantments = new Object2IntOpenHashMap<>();
protected final Map<Enchantment, Integer> ench = new HashMap<>();

    public AnvilBlockEntity(BlockPos pos, BlockState state) {
        super(EnchModule.ANVIL_TILE, pos, state);
    }


    @Override
    public void saveAdditional(CompoundTag tag) {
        EnchModule.LOGGER.error(ench.size());
        ItemStack stack = new ItemStack(Items.ANVIL);
        EnchModule.LOGGER.error(stack);
        EnchModule.LOGGER.error("get tag: "+ tag);
        EnchantmentHelper.setEnchantments(this.enchantments, stack);
        EnchModule.LOGGER.error("get enchants: "+ enchantments);
        tag.put("enchantments", stack.getEnchantmentTags());

    }


    @Override
    public void load(CompoundTag tag) {
        super.load(tag);
        this.loadFromTag(tag);

    }


    public void loadFromTag(CompoundTag tag) {
        ListTag enchants = tag.getList("enchantments", Tag.TAG_COMPOUND);
        Map<Enchantment, Integer> map = EnchantmentHelper.deserializeEnchantments(enchants);
        EnchModule.LOGGER.warn(enchantments.size());
        EnchModule.LOGGER.warn(map.size());
        this.enchantments.clear();
        this.enchantments.putAll(map);
        }

    public Object2IntMap<Enchantment> getEnchantments() {
        return this.enchantments;
    }

}