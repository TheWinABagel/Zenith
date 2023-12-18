package dev.shadowsoffire.apotheosis.spawn.modifiers;

import com.mojang.serialization.Codec;
import dev.shadowsoffire.apotheosis.spawn.spawner.IBaseSpawner;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;

public interface SpawnerStat<T> {

    /**
     * Returns the ID of this spawner stat. Used to build the lang key, and to identify it in json.
     */
    String getId();

    /**
     * Returns a codec that can de/serialize a stat modifier for this stat.
     */
    Codec<StatModifier<T>> getModifierCodec();

    /**
     * Gets the current value of this stat.
     */
    T getValue(IBaseSpawner spawner);

    /**
     * Computes a tooltip to be shown in the item tooltip and Jade/TOP.
     * If the returned component is empty, the tooltip line will not be shown.
     */
    Component getTooltip(IBaseSpawner spawner);

    /**
     * Applies this stat change to the selected spawner.
     *
     * @param value   The change in value being applied.
     * @param min     The minimum acceptable value.
     * @param max     The maximum acceptable value.
     * @param spawner The spawner tile entity.
     * @return If the application was successful (was a spawner stat changed).
     */
    boolean apply(T value, T min, T max, IBaseSpawner spawner);

    default MutableComponent name() {
        return Component.translatable("stat.zenith." + this.getId());
    }

    default MutableComponent desc() {
        return Component.translatable("stat.zenith." + this.getId() + ".desc");
    }
}
