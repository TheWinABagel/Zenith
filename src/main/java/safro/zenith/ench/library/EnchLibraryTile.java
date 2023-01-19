package safro.zenith.ench.library;

import io.github.fabricators_of_create.porting_lib.block.CustomDataPacketHandlingBlockEntity;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerFactory;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Registry;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.Connection;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;
import safro.zenith.ench.EnchModule;
import safro.zenith.network.NetworkUtil;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public abstract class EnchLibraryTile extends BlockEntity implements CustomDataPacketHandlingBlockEntity, ExtendedScreenHandlerFactory {
	protected final Object2IntMap<Enchantment> points = new Object2IntOpenHashMap<>();
	protected final Object2IntMap<Enchantment> maxLevels = new Object2IntOpenHashMap<>();
	protected final Set<EnchLibraryContainer> activeContainers = new HashSet<>();
	protected final int maxLevel;
	protected final int maxPoints;

	public EnchLibraryTile(BlockEntityType<?> type, BlockPos pos, BlockState state, int maxLevel) {
		super(type, pos, state);
		this.maxLevel = maxLevel;
		this.maxPoints = levelToPoints(maxLevel);
	}

	/**
	 * Inserts a book into this library.
	 * Handles the updating of the points and max levels maps.
	 * Extra enchantment levels that cannot be voided will be destroyed.
	 * @param book An enchanted book
	 */
	public void depositBook(ItemStack book) {
		if (book.getItem() != Items.ENCHANTED_BOOK) return;
		Map<Enchantment, Integer> enchs = EnchantmentHelper.getEnchantments(book);
		for (Map.Entry<Enchantment, Integer> e : enchs.entrySet()) {
			if (e.getKey() == null || e.getValue() == null) continue;
			int newPoints = Math.min(this.maxPoints, this.points.getInt(e.getKey()) + levelToPoints(e.getValue()));
			if (newPoints < 0) newPoints = maxPoints;
			this.points.put(e.getKey(), newPoints);
			this.maxLevels.put(e.getKey(), Math.min(this.maxLevel, Math.max(this.maxLevels.getInt(e.getKey()), e.getValue())));
		}
		if (enchs.size() > 0) NetworkUtil.dispatchTEToNearbyPlayers(this);
		this.setChanged();
	}

	/**
	 * Sets the level on the provided itemstack to the requested level.
	 * Does nothing if the operation is impossible.
	 * Decrements point values equal to the amount of points required to jump between the current level and the requested level.
	 */
	public void extractEnchant(ItemStack stack, Enchantment ench, int level) {
		int curLvl = EnchantmentHelper.getEnchantments(stack).getOrDefault(ench, 0);
		if (stack.isEmpty() || !this.canExtract(ench, level, curLvl) || level == curLvl) return;
		Map<Enchantment, Integer> enchs = EnchantmentHelper.getEnchantments(stack);
		enchs.put(ench, level);
		EnchantmentHelper.setEnchantments(enchs, stack);
		this.points.put(ench, Math.max(0, (this.points.getInt(ench) - levelToPoints(level) + levelToPoints(curLvl)))); //Safety, should never be below zero anyway.
		if (!this.level.isClientSide()) NetworkUtil.dispatchTEToNearbyPlayers(this);
		this.setChanged();
	}

	/**
	 * Checks if this level of an enchantment can be extracted from this library, given the current level of the enchantment on the item.
	 * @param ench The enchantment being extracted
	 * @param level The desired target level
	 * @param currentLevel The current level of this enchantment on the item being applied to.
	 * @return If this level of this enchantment can be extracted.
	 */
	public boolean canExtract(Enchantment ench, int level, int currentLevel) {
		return this.maxLevels.getInt(ench) >= level && this.points.getInt(ench) >= levelToPoints(level) - levelToPoints(currentLevel);
	}

	/**
	 * Converts an enchantment level into the corresponding point value.
	 * @param level The level to convert.
	 * @return 2^(level - 1)
	 */
	public static int levelToPoints(int level) {
		return (int) Math.pow(2, level - 1);
	}

	@Override
	public void saveAdditional(CompoundTag tag) {
		CompoundTag points = new CompoundTag();
		for (Object2IntMap.Entry<Enchantment> e : this.points.object2IntEntrySet()) {
			points.putInt(Registry.ENCHANTMENT.getKey(e.getKey()).toString(), e.getIntValue());
		}
		tag.put("Points", points);
		CompoundTag levels = new CompoundTag();
		for (Object2IntMap.Entry<Enchantment> e : this.maxLevels.object2IntEntrySet()) {
			levels.putInt(Registry.ENCHANTMENT.getKey(e.getKey()).toString(), e.getIntValue());
		}
		tag.put("Levels", levels);
		super.saveAdditional(tag);
	}

	@Override
	public void load(CompoundTag tag) {
		super.load(tag);
		CompoundTag points = tag.getCompound("Points");
		for (String s : points.getAllKeys()) {
			Enchantment ench = Registry.ENCHANTMENT.get(new ResourceLocation(s));
			if (ench == null) continue;
			this.points.put(ench, points.getInt(s));
		}
		CompoundTag levels = tag.getCompound("Levels");
		for (String s : levels.getAllKeys()) {
			Enchantment ench = Registry.ENCHANTMENT.get(new ResourceLocation(s));
			if (ench == null) continue;
			this.maxLevels.put(ench, levels.getInt(s));
		}
	}

	@Override
	public void onDataPacket(Connection net, ClientboundBlockEntityDataPacket pkt) {
		CompoundTag tag = pkt.getTag();
		CompoundTag points = tag.getCompound("Points");
		for (String s : points.getAllKeys()) {
			Enchantment ench = Registry.ENCHANTMENT.get(new ResourceLocation(s));
			if (ench == null) continue;
			this.points.put(ench, points.getInt(s));
		}
		CompoundTag levels = tag.getCompound("Levels");
		for (String s : levels.getAllKeys()) {
			Enchantment ench = Registry.ENCHANTMENT.get(new ResourceLocation(s));
			if (ench == null) continue;
			this.maxLevels.put(ench, levels.getInt(s));
		}
		this.activeContainers.forEach(EnchLibraryContainer::onChanged);
	}

	@Override
	public ClientboundBlockEntityDataPacket getUpdatePacket() {
		return ClientboundBlockEntityDataPacket.create(this);
	}

	@Override
	public CompoundTag getUpdateTag() {
		CompoundTag tag = super.getUpdateTag();
		CompoundTag points = new CompoundTag();
		for (Object2IntMap.Entry<Enchantment> e : this.points.object2IntEntrySet()) {
			points.putInt(Registry.ENCHANTMENT.getKey(e.getKey()).toString(), e.getIntValue());
		}
		tag.put("Points", points);
		CompoundTag levels = new CompoundTag();
		for (Object2IntMap.Entry<Enchantment> e : this.maxLevels.object2IntEntrySet()) {
			levels.putInt(Registry.ENCHANTMENT.getKey(e.getKey()).toString(), e.getIntValue());
		}
		tag.put("Levels", levels);
		return tag;
	}

	public Object2IntMap<Enchantment> getPointsMap() {
		return this.points;
	}

	public Object2IntMap<Enchantment> getLevelsMap() {
		return this.maxLevels;
	}

	public void addListener(EnchLibraryContainer ctr) {
		this.activeContainers.add(ctr);
	}

	public void removeListener(EnchLibraryContainer ctr) {
		this.activeContainers.remove(ctr);
	}

	public int getMax(Enchantment ench) {
		return Math.min(this.maxLevel, this.maxLevels.getInt(ench));
	}

	public void writeScreenOpeningData(ServerPlayer player, FriendlyByteBuf buf) {
		buf.writeBlockPos(getBlockPos());
	}

	public static class BasicLibraryTile extends EnchLibraryTile {

		public BasicLibraryTile(BlockPos pos, BlockState state) {
			super(EnchModule.LIBRARY_TILE, pos, state, 16);
		}

		@Override
		public Component getDisplayName() {
			return Component.translatable("zenith.ench.library");
		}

		@Nullable
		@Override
		public AbstractContainerMenu createMenu(int i, Inventory inventory, Player player) {
			return new EnchLibraryContainer(i, inventory, getBlockPos());
		}
	}

	public static class EnderLibraryTile extends EnchLibraryTile {

		public EnderLibraryTile(BlockPos pos, BlockState state) {
			super(EnchModule.ENDER_LIBRARY_TILE, pos, state, 31);
		}

		@Override
		public Component getDisplayName() {
			return Component.translatable("block.zenith.ender_library");
		}

		@Nullable
		@Override
		public AbstractContainerMenu createMenu(int i, Inventory inventory, Player player) {
			return new EnchLibraryContainer(i, inventory, getBlockPos());
		}
	}

}
