package safro.zenith.mixin.spawner;

import io.github.fabricators_of_create.porting_lib.block.CustomDataPacketHandlingBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.Connection;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.level.BaseSpawner;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.entity.SpawnerBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(SpawnerBlockEntity.class)
public class SpawnerBlockEntityMixin extends BlockEntity implements CustomDataPacketHandlingBlockEntity {

    @Shadow
    public BaseSpawner spawner;
    public boolean ignoresPlayers = false;
    public boolean ignoresConditions = false;
    public boolean redstoneControl = false;
    public boolean ignoresLight = false;
    public boolean hasNoAI = false;
    public boolean silent = false;

    public BaseSpawner getSpawner() {
        return spawner;
    }

    public boolean doesIgnorePlayers() {
        return ignoresPlayers;
    }

    public boolean doesIgnoreConditions() {
        return ignoresConditions;
    }

    public boolean doesRequireRedstoneControl() {
        return redstoneControl;
    }

    public boolean doesIgnoreLight() {
        return ignoresLight;
    }

    public boolean doesHaveNoAi() {
        return hasNoAI;
    }

    public void setIgnorePlayers(boolean flag) {
        ignoresPlayers = flag;
    }

    public void setIgnoreConditions(boolean flag) {
        ignoresConditions = flag;
    }

    public void setRequireRedstoneControl(boolean flag) {
        redstoneControl = flag;
    }

    public void setIgnoreLight(boolean flag) {
        ignoresLight = flag;
    }

    public void setHaveNoAi(boolean flag) {
        hasNoAI = flag;
    }

    public SpawnerBlockEntityMixin(BlockEntityType<?> blockEntityType, BlockPos blockPos, BlockState blockState) {
        super(blockEntityType, blockPos, blockState);
    }

    @Inject(method = "saveAdditional", at = @At("HEAD"))
    public void zenithSaveAdditional(CompoundTag tag, CallbackInfo ci) {
        tag.putBoolean("ignore_players", this.ignoresPlayers);
        tag.putBoolean("ignore_conditions", this.ignoresConditions);
        tag.putBoolean("redstone_control", this.redstoneControl);
        tag.putBoolean("ignore_light", this.ignoresLight);
        tag.putBoolean("no_ai", this.hasNoAI);
        tag.putBoolean("silent", this.silent);
    }

    @Inject(method = "load", at = @At("HEAD"))
    public void zenithLoad(CompoundTag tag, CallbackInfo ci) {
        this.ignoresPlayers = tag.getBoolean("ignore_players");
        this.ignoresConditions = tag.getBoolean("ignore_conditions");
        this.redstoneControl = tag.getBoolean("redstone_control");
        this.ignoresLight = tag.getBoolean("ignore_light");
        this.hasNoAI = tag.getBoolean("no_ai");
        this.silent = tag.getBoolean("silent");
    }


    @Override
    public void onDataPacket(Connection net, ClientboundBlockEntityDataPacket pkt) {
        this.zenithLoad(pkt.getTag(), null);
    }



}
