package dev.shadowsoffire.apotheosis.mixin.spawn;

import dev.shadowsoffire.apotheosis.Apotheosis;
import net.minecraft.ChatFormatting;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(BlockItem.class)
public class BlockItemMixin extends Item {

    public BlockItemMixin(Properties properties) {
        super(properties);
    }

    @Override
    public Component getName(ItemStack stack) {
        if (Apotheosis.enableSpawner && stack.hasTag() && stack.getTag().contains("BlockEntityTag")) {
            CompoundTag tag = stack.getTag().getCompound("BlockEntityTag");
            if (tag.contains("SpawnData")) {
                String name = tag.getCompound("SpawnData").getCompound("entity").getString("id");
                String key = "entity." + name.replace(':', '.');
                if (name.length() == 0) key = "Empty";
                ChatFormatting color = ChatFormatting.WHITE;
                try {
                    EntityType<?> t = BuiltInRegistries.ENTITY_TYPE.get(new ResourceLocation(name));
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
                }
                catch (Exception ex) {

                }
                return Component.translatable("item.zenith.spawner", Component.translatable(key)).withStyle(color);
            }
        }
        return super.getName(stack);
    }

}
