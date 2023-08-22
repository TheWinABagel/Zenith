package dev.shadowsoffire.apotheosis.spawn;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.item.v1.ItemTooltipCallback;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.SpawnEggItem;

import static dev.shadowsoffire.apotheosis.spawn.SpawnerModule.bannedMobs;
@Environment(EnvType.CLIENT)
public class SpawnerModuleClient {
    public static void init(){
        handleTooltips();
    }


    public static void handleTooltips() {
        ItemTooltipCallback.EVENT.register((stack, context, lines) -> {
            if (stack.getItem() instanceof SpawnEggItem egg) {
                EntityType<?> type = egg.getType(stack.getTag());
                if (bannedMobs.contains(EntityType.getKey(type))) lines.add(Component.translatable("misc.apotheosis.banned").withStyle(ChatFormatting.GRAY));
            }
        });
    }
}
