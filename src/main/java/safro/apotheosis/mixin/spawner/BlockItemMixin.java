package safro.apotheosis.mixin.spawner;

import net.minecraft.ChatFormatting;
import net.minecraft.core.Registry;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import org.spongepowered.asm.mixin.Mixin;
import safro.apotheosis.Apotheosis;

@Mixin(BlockItem.class)
public abstract class BlockItemMixin extends Item {

    public BlockItemMixin(Properties properties) {
        super(properties);
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
