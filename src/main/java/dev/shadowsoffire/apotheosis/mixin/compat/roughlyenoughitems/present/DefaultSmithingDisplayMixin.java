package dev.shadowsoffire.apotheosis.mixin.compat.roughlyenoughitems.present;

import dev.shadowsoffire.apotheosis.adventure.AdventureModule;
import dev.shadowsoffire.apotheosis.util.IGetRecipe;
import me.shedaniel.rei.plugin.common.displays.DefaultSmithingDisplay;
import net.minecraft.world.item.crafting.SmithingRecipe;
import net.minecraft.world.item.crafting.SmithingTransformRecipe;
import net.minecraft.world.item.crafting.SmithingTrimRecipe;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;
import java.util.Optional;

@Mixin(DefaultSmithingDisplay.class)
public class DefaultSmithingDisplayMixin implements IGetRecipe { // why not pass this info by default :pain:

    @Unique SmithingTransformRecipe recipe;

    @Override
    public SmithingTransformRecipe getRecipe() {
        return recipe;
    }

    @Inject(method = "<init>(Lnet/minecraft/world/item/crafting/SmithingTransformRecipe;)V", at = @At("TAIL"))
    private void setRecipe2(SmithingTransformRecipe recipe, CallbackInfo ci){
        this.recipe = recipe;
    }
}
