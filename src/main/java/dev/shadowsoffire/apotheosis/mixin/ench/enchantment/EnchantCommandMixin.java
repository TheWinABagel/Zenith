package dev.shadowsoffire.apotheosis.mixin.ench.enchantment;

import dev.shadowsoffire.apotheosis.Apotheosis;
import dev.shadowsoffire.apotheosis.ench.asm.EnchHooks;
import net.minecraft.server.commands.EnchantCommand;
import net.minecraft.world.item.enchantment.Enchantment;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(EnchantCommand.class)
public abstract class EnchantCommandMixin {

    @Redirect(method = "enchant", at = @At(value = "INVOKE", target = "net/minecraft/world/item/enchantment/Enchantment.getMaxLevel ()I"))
    private static int zenithMax(Enchantment instance){
        if (!Apotheosis.enableEnch) return instance.getMaxLevel();
        return EnchHooks.getMaxLevel(instance);
    }
}
