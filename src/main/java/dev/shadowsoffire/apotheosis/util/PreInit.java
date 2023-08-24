package dev.shadowsoffire.apotheosis.util;

import com.chocohead.mm.api.ClassTinkerers;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.MappingResolver;

public class PreInit implements Runnable {
    @Override
    public void run() { // fabric really needs an api for new enchantment categories, this sucks
        MappingResolver remapper = FabricLoader.getInstance().getMappingResolver();
        String enchantmentTarget = remapper.mapClassName("intermediary", "net.minecraft.class_1886");
        ClassTinkerers.enumBuilder(enchantmentTarget).addEnumSubclass("ANVIL", "dev.shadowsoffire.apotheosis.ench.asm.categories.AnvilCategory").build();
        ClassTinkerers.enumBuilder(enchantmentTarget).addEnumSubclass("AXE", "dev.shadowsoffire.apotheosis.ench.asm.categories.AxeCategory").build();
        ClassTinkerers.enumBuilder(enchantmentTarget).addEnumSubclass("CORE_ARMOR", "dev.shadowsoffire.apotheosis.ench.asm.categories.CoreArmorCategory").build();
        ClassTinkerers.enumBuilder(enchantmentTarget).addEnumSubclass("HOE", "dev.shadowsoffire.apotheosis.ench.asm.categories.HoeCategory").build();
        ClassTinkerers.enumBuilder(enchantmentTarget).addEnumSubclass("PICKAXE", "dev.shadowsoffire.apotheosis.ench.asm.categories.PickaxeCategory").build();
        ClassTinkerers.enumBuilder(enchantmentTarget).addEnumSubclass("SHEARS", "dev.shadowsoffire.apotheosis.ench.asm.categories.ShearsCategory").build();
        ClassTinkerers.enumBuilder(enchantmentTarget).addEnumSubclass("SHIELD", "dev.shadowsoffire.apotheosis.ench.asm.categories.ShieldCategory").build();
        ClassTinkerers.enumBuilder(enchantmentTarget).addEnumSubclass("NULL", "dev.shadowsoffire.apotheosis.ench.asm.categories.NullCategory").build();
    }
}
