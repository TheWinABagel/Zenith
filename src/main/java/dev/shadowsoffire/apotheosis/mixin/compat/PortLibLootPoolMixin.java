package dev.shadowsoffire.apotheosis.mixin.compat;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import io.github.fabricators_of_create.porting_lib.loot.extensions.LootPoolBuilderExtension;
import net.minecraft.world.level.storage.loot.LootPool;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(LootPool.Builder.class)
public class PortLibLootPoolMixin implements LootPoolBuilderExtension {
    @Unique
    private String name;

    @Override
    public LootPool.Builder name(String name) {
        this.name = name;
        //noinspection DataFlowIssue
        return (LootPool.Builder) (Object) this;
    }
/*
    @ModifyReturnValue(method = "build", at = @At("RETURN"))
    public void setName(LootPool pool) {
        pool.setName(name);
    }*/

}
