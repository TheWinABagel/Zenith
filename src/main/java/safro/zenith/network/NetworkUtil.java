package safro.zenith.network;

import net.fabricmc.api.EnvType;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.block.entity.BlockEntity;
import safro.zenith.ench.table.ClueMessage;

import java.util.concurrent.Callable;
import java.util.function.Supplier;

public class NetworkUtil {

    public static void initServer() {
        ButtonClickPacket.init();
    }

    public static void initClient() {
        ReloadListenerPacket.Start.init();
        ReloadListenerPacket.Content.init();
        ReloadListenerPacket.End.init();
        ClueMessage.init();
    }

    public static void dispatchTEToNearbyPlayers(BlockEntity tile) {
        ServerLevel world = (ServerLevel) tile.getLevel();
        world.getChunkSource().chunkMap.getPlayers(new ChunkPos(tile.getBlockPos()), false).forEach(player -> {
            player.connection.send(tile.getUpdatePacket());
        });
    }

    public static <T> T callWhenOn(EnvType env, Supplier<Callable<T>> toRun) {
        return unsafeCallWhenOn(env, toRun);
    }

    public static <T> T unsafeCallWhenOn(EnvType env, Supplier<Callable<T>> toRun) {
        if (FabricLoader.getInstance().getEnvironmentType() == env) {
            try {
                return toRun.get().call();
            }
            catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
        return null;
    }

}
