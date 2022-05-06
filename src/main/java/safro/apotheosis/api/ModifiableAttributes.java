package safro.apotheosis.api;

import com.google.common.collect.Multimap;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;

public interface ModifiableAttributes {
    Multimap<Attribute, AttributeModifier> getModifiableMap();
    boolean addModifier(Attribute attribute, AttributeModifier modifier);
}
