package safro.apotheosis.mixin.spawner;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.Registry;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.item.*;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import safro.apotheosis.Apotheosis;
import safro.apotheosis.spawn.modifiers.SpawnerStats;

import java.util.List;

@Mixin(BlockItem.class)
public abstract class BlockItemMixin extends Item {

    public BlockItemMixin(Properties properties) {
        super(properties);
    }

    @Inject(method = "appendHoverText", at = @At("TAIL"))
    @Environment(EnvType.CLIENT)
    private void spawnerTooltip(ItemStack stack, Level level, List<Component> tooltip, TooltipFlag tooltipFlag, CallbackInfo ci) {
        if (stack.hasTag() && stack.getTag().contains("BlockEntityTag", Tag.TAG_COMPOUND)) {
            if (Screen.hasShiftDown()) {
                CompoundTag tag = stack.getTag().getCompound("BlockEntityTag");
                if (tag.contains("MinSpawnDelay")) tooltip.add(concat(SpawnerStats.MIN_DELAY.name(), tag.getShort("MinSpawnDelay")));
                if (tag.contains("MaxSpawnDelay")) tooltip.add(concat(SpawnerStats.MAX_DELAY.name(), tag.getShort("MaxSpawnDelay")));
                if (tag.contains("SpawnCount")) tooltip.add(concat(SpawnerStats.SPAWN_COUNT.name(), tag.getShort("SpawnCount")));
                if (tag.contains("MaxNearbyEntities")) tooltip.add(concat(SpawnerStats.MAX_NEARBY_ENTITIES.name(), tag.getShort("MaxNearbyEntities")));
                if (tag.contains("RequiredPlayerRange")) tooltip.add(concat(SpawnerStats.REQ_PLAYER_RANGE.name(), tag.getShort("RequiredPlayerRange")));
                if (tag.contains("SpawnRange")) tooltip.add(concat(SpawnerStats.SPAWN_RANGE.name(), tag.getShort("SpawnRange")));
                if (tag.getBoolean("ignore_players")) tooltip.add(SpawnerStats.IGNORE_PLAYERS.name().withStyle(ChatFormatting.DARK_GREEN));
                if (tag.getBoolean("ignore_conditions")) tooltip.add(SpawnerStats.IGNORE_CONDITIONS.name().withStyle(ChatFormatting.DARK_GREEN));
                if (tag.getBoolean("redstone_control")) tooltip.add(SpawnerStats.REDSTONE_CONTROL.name().withStyle(ChatFormatting.DARK_GREEN));
                if (tag.getBoolean("ignore_light")) tooltip.add(SpawnerStats.IGNORE_LIGHT.name().withStyle(ChatFormatting.DARK_GREEN));
                if (tag.getBoolean("no_ai")) tooltip.add(SpawnerStats.NO_AI.name().withStyle(ChatFormatting.DARK_GREEN));
            } else {
                tooltip.add(new TranslatableComponent("misc.apotheosis.shift_stats").withStyle(ChatFormatting.GRAY));
            }
        }
    }

    private static Component concat(Object... args) {
        return new TranslatableComponent("misc.apotheosis.value_concat", args[0], new TextComponent(args[1].toString()).withStyle(ChatFormatting.GRAY)).withStyle(ChatFormatting.GREEN);
    }

    @Override
    public Component getName(ItemStack stack) {
        if (Apotheosis.enableSpawner && stack.is(Items.SPAWNER)) {
            if (stack.hasTag() && stack.getTag().contains("BlockEntityTag")) {
                CompoundTag tag = stack.getTag().getCompound("BlockEntityTag");
                if (tag.contains("SpawnData")) {
                    String name = tag.getCompound("SpawnData").getCompound("entity").getString("id");
                    String key = "entity." + name.replace(':', '.');
                    ChatFormatting color = ChatFormatting.WHITE;
                    try {
                        EntityType<?> t = Registry.ENTITY_TYPE.get(new ResourceLocation(name));
                        MobCategory cat = t.getCategory();
                        switch (cat) {
                            case CREATURE:
                                color = ChatFormatting.DARK_GREEN;
                                break;
                            case MONSTER:
                                color = ChatFormatting.RED;
                                break;
                            case WATER_AMBIENT:
                            case WATER_CREATURE:
                                color = ChatFormatting.BLUE;
                            default:
                                break;
                        }
                    } catch (Exception ex) {

                    }
                    return new TranslatableComponent("item.apotheosis.spawner", new TranslatableComponent(key)).withStyle(color);
                }
            }
        }
        return super.getName(stack);
    }
}
