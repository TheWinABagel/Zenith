package dev.shadowsoffire.apotheosis.util;

import net.minecraft.world.item.Item;

import java.util.UUID;

public final class ItemAccess extends Item {

    private ItemAccess(Properties pProperties) {
        super(pProperties);
    }

    public static UUID getBaseAD() {
        return Item.BASE_ATTACK_DAMAGE_UUID;
    }

    public static UUID getBaseAS() {
        return Item.BASE_ATTACK_SPEED_UUID;
    }

}
