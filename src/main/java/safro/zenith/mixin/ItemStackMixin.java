package safro.zenith.mixin;

import com.google.common.collect.Multimap;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;
import safro.zenith.Zenith;
import safro.zenith.deadly.DeadlyModuleClient;
import safro.zenith.deadly.DeadlyModuleEvents;
import safro.zenith.deadly.loot.affix.AffixHelper;
import safro.zenith.ench.EnchModuleClient;
import safro.zenith.spawn.SpawnerModule;

import java.util.List;

@Mixin(ItemStack.class)
public class ItemStackMixin {

    @Inject(method = "getTooltipLines", at = @At("TAIL"), locals = LocalCapture.CAPTURE_FAILHARD)
    private void apotheosisTooltipEvent(Player player, TooltipFlag tooltipFlag, CallbackInfoReturnable<List<Component>> cir, List<Component> list) {
        ItemStack stack = (ItemStack) (Object) this;
        if (Zenith.enableDeadly) {
            DeadlyModuleClient.tooltips(stack, list, player);
        }
        if (Zenith.enableEnch) {
            EnchModuleClient.tooltips(stack, list, player);
        }
        if (Zenith.enableSpawner) {
            SpawnerModule.handleTooltips(list, stack);
        }
    }

    @Inject(method = "getAttributeModifiers", at = @At("TAIL"), locals = LocalCapture.CAPTURE_FAILHARD, cancellable = true)
    private void apotheosisModifierEvent(EquipmentSlot equipmentSlot, CallbackInfoReturnable<Multimap<Attribute, AttributeModifier>> cir, Multimap<Attribute, AttributeModifier> multimap) {
        ItemStack stack = (ItemStack) (Object) this;
        if (Zenith.enableDeadly) {
            Multimap<Attribute, AttributeModifier> map = DeadlyModuleEvents.sortModifiers(stack, equipmentSlot, multimap);
            DeadlyModuleEvents.affixModifiers(stack, equipmentSlot, multimap);
            if (map != null) cir.setReturnValue(map);
        }
    }

    @Inject(method = "getHoverName", at = @At("RETURN"), cancellable = true)
    public void apoth_getHoverName(CallbackInfoReturnable<Component> ci) {
        ItemStack ths = (ItemStack) (Object) this;
        CompoundTag afxData = ths.getTagElement(AffixHelper.AFFIX_DATA);
        if (afxData != null && afxData.contains("Name", 8)) {
            try {
                Component component = Component.Serializer.fromJson(afxData.getString("Name"));
                if (component instanceof TranslatableComponent tComp) {
                    tComp.getArgs()[0] = ci.getReturnValue();
                    ci.setReturnValue(tComp);
                } else afxData.remove("Name");
            } catch (Exception exception) {
                afxData.remove("Name");
            }
        }
    }

 //   @Inject(method = "getTooltipLines(Lnet/minecraft/world/entity/player/Player;Lnet/minecraft/world/item/TooltipFlag;)Ljava/util/List;", at = @At(shift = At.Shift.BEFORE, value = "JUMP", ordinal = 9, opcode = Opcodes.IFEQ), locals = LocalCapture.CAPTURE_FAILSOFT)
 //   public void getTooltipLines(Player player, TooltipFlag tooltipFlag, CallbackInfoReturnable<List<Component>> cir, List<Component> list) {
 //       if (Apotheosis.enableDeadly) list.add(new TextComponent("APOTH_REMOVE_MARKER"));
 //   }
}
