package safro.zenith.api.placebo.json;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableMap;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import io.github.fabricators_of_create.porting_lib.util.ServerLifecycleHooks;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimpleJsonResourceReloadListener;
import net.minecraft.util.profiling.ProfilerFiller;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.Nullable;
import safro.zenith.network.ReloadListenerPacket;
import safro.zenith.util.ZenithUtil;

import java.util.*;

/**
 * A Placebo JSON Reload Listener is a big pile of boilerplate for registering reload listeners efficiently.<br>
 * To utilize this class, subclass it, and provide the appropriate constructor parameters.<br>
 * You will provide your serializers via {@link #registerBuiltinSerializers()}.<br>
 * 
 * From then on, loading of files, condition checks, network sync, and everything else is automatically handled.
 *
 * @param <V> The base type of objects stored in this reload listener.
 */
public abstract class PlaceboJsonReloadListener<V extends TypeKey<V>> extends SimpleJsonResourceReloadListener {

	/**
	 * The Default key is used when subtypes are not enabled.
	 */
	public static final ResourceLocation DEFAULT = new ResourceLocation("default");

	public static final Map<String, PlaceboJsonReloadListener<?>> SYNC_REGISTRY = new HashMap<>();

	protected final Logger logger;
	protected final String path;
	protected final boolean synced;
	protected final boolean subtypes;
	protected final SerializerMap<V> serializers;
	private final Set<ListenerCallback<V>> callbacks = new HashSet();

	protected Map<ResourceLocation, V> registry = ImmutableMap.of();

	private final Map<ResourceLocation, V> staged = new HashMap<>();
	/**
	 * Constructs a new JSON reload listener.  All parameters will be saved finally in the object.
	 * @param logger The logger used by this listener for all relevant messages.
	 * @param path The datapack path used by this listener for loading files.
	 * @param synced If this listener will be synced over the network.  You must also call {@link PlaceboJsonReloadListener#registerForSync(PlaceboJsonReloadListener)}
	 * @param subtypes If this listener supports subtyped objects (and the "type" key on top-level objects).
	 */
	public PlaceboJsonReloadListener(Logger logger, String path, boolean synced, boolean subtypes) {
		super(new GsonBuilder().setLenient().create(), path);
		this.logger = logger;
		this.path = path;
		this.synced = synced;
		this.subtypes = subtypes;
		this.serializers = new SerializerMap<>(path);
		this.registerBuiltinSerializers();
		if (this.serializers.isEmpty()) throw new RuntimeException("Attempted to create a json reload listener for " + path + " with no built-in serializers!");
	}

	public void init(){
		if (this.synced) {
			registerForSync(this);
		}
	}

	@Override
	@SuppressWarnings("unchecked")
	protected final void apply(Map<ResourceLocation, JsonElement> objects, ResourceManager pResourceManager, ProfilerFiller pProfiler) {
		this.beginReload();
		objects.forEach((key, ele) -> {
			try {
				if (checkAndLogEmpty(ele, key, this.path, this.logger) && checkConditions(ele, key, this.path, this.logger)) {
					JsonObject obj = ele.getAsJsonObject();
					V deserialized;
					if (this.subtypes) {
						deserialized = this.serializers.read(obj);
					} else {
						deserialized = this.serializers.get(DEFAULT).read(obj);
					}
					deserialized.setId(key);
					Preconditions.checkNotNull(deserialized.getId(), "A " + this.path + " with id " + key + " failed to set ID.");
					Preconditions.checkNotNull(deserialized.getSerializer(), "A " + this.path + " with id " + key + " is not declaring a serializer.");
					Preconditions.checkNotNull(this.serializers.get(deserialized.getSerializer()), "A " + this.path + " with id " + key + " is declaring an unregistered serializer.");
					this.register(key, deserialized);
				}
			} catch (Exception e) {
				this.logger.error("Failed parsing {} file {}.", this.path, key);
				this.logger.error("Underlying Exception: ", e);
			}
		});
		this.onReload();
	}

	/**
	 * Add all default serializers to this reload listener.
	 * This should be a series of calls to {registerSerializer}
	 */
	protected abstract void registerBuiltinSerializers();

	/**
	 * Called when this manager begins reloading all items.
	 * Should handle clearing internal data caches.
	 */
	protected void beginReload() {
		this.registry = new HashMap<>();
	}

	/**
	 * Called after this manager has finished reloading all items.
	 * Should handle any info logging, and data immutability.
	 */
	protected void onReload() {
		this.registry = ImmutableMap.copyOf(this.registry);
		this.logger.info("Registered {} {}.", this.registry.size(), this.path);
	}

	/**
	 * Register a serializer to this listener. Does not permit duplicates, and does not permit multiple registration.
	 * @param id The ID of the serializer. If subtypes are not supported, this is ignored, and {@link #DEFAULT} is used.
	 * @param serializer The serializer being registered.
	 */
	public final void registerSerializer(ResourceLocation id, PSerializer<? extends V> serializer) {
		serializer.validate(false, synced);
		if (this.subtypes) {
			if (this.serializers.contains(id)) throw new RuntimeException("Attempted to register a " + this.path + " serializer with id " + id + " but one already exists!");
			this.serializers.register(id, serializer);
		} else {
			if (!this.serializers.isEmpty()) throw new RuntimeException("Attempted to register a " + this.path + " serializer with id " + id + " but subtypes are not supported!");
			this.serializers.register(DEFAULT, serializer);
		}
	}

	public final <T extends V> DynamicRegistryObject<T> makeObj(ResourceLocation id) {
		DynamicRegistryObject<T> obj = new DynamicRegistryObject(id, this);
		this.registerCallback(obj);
		return obj;
	}

	public final boolean registerCallback(ListenerCallback<V> callback) {
		return this.callbacks.add(callback);
	}

	public final boolean removeCallback(ListenerCallback<V> callback) {
		return this.callbacks.remove(callback);
	}

	public final void sync(ServerPlayer player) {
		if (player == null) {
			ReloadListenerPacket.Start.sendToAll(this.path);
			this.registry.forEach((k, v) -> {
				ReloadListenerPacket.Content.sendToAllNew(this.path, k, v);
			});
			ReloadListenerPacket.End.sendToAll(this.path);
		} else {
			ReloadListenerPacket.Start.sendTo(player, this.path);
			this.registry.forEach((k, v) -> {
				ReloadListenerPacket.Content.sendToNew(player, this.path, k, v);
			});
			ReloadListenerPacket.End.sendTo(player, this.path);
		}
	}
	protected <T extends V> void validateItem(T item) {
		Preconditions.checkNotNull(item);
	}

	/**
	 * Registers a single item of this type to the registry during reload.
	 * You can override this method to process things a bit differently.
	 */
	protected <T extends V> void register(ResourceLocation key, T item) {
		if (item.getId() == null) item.setId(key);
		if (!item.getId().equals(key)) throw new UnsupportedOperationException("Attempted to register a " + this.path + " with a mismatched registry ID! Expected: " + item.getId() + " Provided: " + key);
		validateItem(item);
		this.registry.put(key, item);
	}

	/**
	 * @return An immutable view of all keys registered for this type.
	 */
	public Set<ResourceLocation> getKeys() {
		return this.registry.keySet();
	}

	/**
	 * @return An immutable view of all items registered for this type.
	 */
	public Collection<V> getValues() {
		return this.registry.values();
	}

	/**
	 * @return The item associated with this key, or null.
	 */
	@Nullable
	public V getValue(ResourceLocation key) {
		return this.getOrDefault(key, null);
	}

	/**
	 * @return The item associated with this key, or the default value.
	 */
	public V getOrDefault(ResourceLocation key, V defValue) {
		return this.registry.getOrDefault(key, defValue);
	}

	/**
	 * Checks if an item is empty, and if it is, returns false and logs the key.
	 */
	public static boolean checkAndLogEmpty(JsonElement e, ResourceLocation id, String type, Logger logger) {
		String s = e.toString();
		if (s.isEmpty() || s.equals("{}")) {
			logger.error("Ignoring {} item with id {} as it is empty.  Please switch to a condition-false json instead of an empty one.", type, id);
			return false;
		}
		return true;
	}

	/**
	 * Checks the conditions on a Json, and returns true if they are met.
	 * @param e The Json being checked.
	 * @param id The ID of that json.
	 * @param type The type of the json, for logging.
	 * @param logger The logger to log to.
	 * @return True if the item's conditions are met, false otherwise.
	 */
	public static boolean checkConditions(JsonElement e, ResourceLocation id, String type, Logger logger) {
		if (e.isJsonObject() && !ZenithUtil.processConditions(e.getAsJsonObject(), "conditions")) {
			logger.debug("Skipping loading {} item with id {} as it's conditions were not met", type, id);
			return false;
		}
		return true;
	}



	private final void registerForSync(PlaceboJsonReloadListener<?> listener) {
		if (!listener.synced) throw new RuntimeException("Attempted to register the non-synced JSON Reload Listener " + listener.path + " as a synced listener!");
		synchronized (SYNC_REGISTRY) {
			if (SYNC_REGISTRY.containsKey(listener.path)) throw new RuntimeException("Attempted to register the JSON Reload Listener for syncing " + listener.path + " but one already exists!");
			SYNC_REGISTRY.put(listener.path, listener);
		}
	}


	/**
	 * Begins the sync for a specific listener.
	 * @param path The path of the listener being synced.
	 */
	public static void initSync(String path) {
		SYNC_REGISTRY.computeIfPresent(path, (k, v) -> {
			v.staged.clear();
			return v;
		});
	}

	/**
	 * Write an item (with the same type as the listener) to the network.
	 * @param <V> The type of item being written.
	 * @param path The path of the listener.
	 * @param value The value being written.
	 * @param buf The buffer being written to.
	 */
	@SuppressWarnings("unchecked")
	public static <V extends TypeKey<V>> void writeItem(String path, V value, FriendlyByteBuf buf) {
		SYNC_REGISTRY.computeIfPresent(path, (k, v) -> {
			((SerializerMap<V>) v.serializers).write(value, buf);
			return v;
		});
	}

	/**
	 * Reads an item from the network, via the listener's serializers.
	 * @param <V> The type of item being read.
	 * @param path The path of the listener.
	 * @param key The key of the item being read (not the serializer key).
	 * @param buf The buffer being read from.
	 * @return An object of type V as deserialized from the network.
	 */
	@SuppressWarnings({ "unchecked" })
	public static <V extends TypeKey<V>> V readItem(String path, ResourceLocation key, FriendlyByteBuf buf) {
		var listener = SYNC_REGISTRY.get(path);
		if (listener == null) throw new RuntimeException("Received sync packet for unknown registry!");
		V v = (V) listener.serializers.read(buf);
		v.setId(key);
		return v;
	}

	/**
	 * Stages an item to a listener.
	 * @param <V> The type of the item being staged.
	 * @param path The path of the listener.
	 * @param value The object being staged.
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static <V extends TypeKey<V>> void acceptItem(String path, V value) {
		SYNC_REGISTRY.computeIfPresent(path, (k, v) -> {
			((Map) v.staged).put(value.getId(), value);
			return v;
		});
	}

	/**
	 * Ends the sync for a specific listener.
	 * This will delete current data, push staged data to live, and call the appropriate methods for reloading.
	 * @param path The path of the listener.
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static void endSync(String path) {
		if (ServerLifecycleHooks.getCurrentServer() != null) return; // Do not propgate received changed on the host of a singleplayer world, as they may not be the full data.
		SYNC_REGISTRY.computeIfPresent(path, (k, v) -> {
			v.beginReload();
			v.staged.forEach(((PlaceboJsonReloadListener) v)::register);
			v.onReload();
			return v;
		});
	}

}
