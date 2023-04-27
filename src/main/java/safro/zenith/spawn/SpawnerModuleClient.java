package safro.zenith.spawn;

import net.fabricmc.fabric.api.client.item.v1.ItemTooltipCallback;
import net.minecraft.ChatFormatting;
import net.minecraft.core.Registry;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.SpawnEggItem;

public class SpawnerModuleClient {
    public static void init() {
        ItemTooltipCallback.EVENT.register((stack, context, lines) -> {
            if (stack.getItem() instanceof SpawnEggItem egg) {
                EntityType<?> type = egg.getType(stack.getTag());
                if (SpawnerModule.invertBannedMobs) {
                    if (!SpawnerModule.bannedMobs.contains(Registry.ENTITY_TYPE.getKey(type))) lines.add(Component.translatable("misc.zenith.banned").withStyle(ChatFormatting.GRAY));
                } else {
                    if (SpawnerModule.bannedMobs.contains(Registry.ENTITY_TYPE.getKey(type))) lines.add(Component.translatable("misc.zenith.banned").withStyle(ChatFormatting.GRAY));
                }
            }
        });
    }
}
