package dev.shadowsoffire.apotheosis.mixin.spawn;

import io.github.fabricators_of_create.porting_lib.block.CustomDataPacketHandlingBlockEntity;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.Connection;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.level.block.entity.SpawnerBlockEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(SpawnerBlockEntity.class)
public abstract class SpawnerBlockEntityMixin implements CustomDataPacketHandlingBlockEntity {


    @Shadow public abstract void load(CompoundTag tag);

    @Override
    public void onDataPacket(Connection net, ClientboundBlockEntityDataPacket pkt) {
        this.load(pkt.getTag());
    }
}
