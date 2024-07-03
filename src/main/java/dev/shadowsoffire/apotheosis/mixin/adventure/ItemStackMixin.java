package dev.shadowsoffire.apotheosis.mixin.adventure;

import dev.shadowsoffire.apotheosis.Apotheosis;
import dev.shadowsoffire.apotheosis.adventure.affix.AffixHelper;
import dev.shadowsoffire.apotheosis.adventure.loot.LootRarity;
import dev.shadowsoffire.apotheosis.adventure.socket.SocketHelper;
import dev.shadowsoffire.placebo.reload.DynamicHolder;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.contents.TranslatableContents;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.stream.DoubleStream;

@Mixin(ItemStack.class)
public class ItemStackMixin {

    @Inject(method = "getHoverName", at = @At("RETURN"), cancellable = true)
    public void zenith$affixItemName(CallbackInfoReturnable<Component> cir) {
        if (Apotheosis.enableAdventure) {
            ItemStack ths = (ItemStack) (Object) this;
            CompoundTag afxData = ths.getTagElement(AffixHelper.AFFIX_DATA);
            if (afxData != null && afxData.contains(AffixHelper.NAME, 8)) {
                try {
                    Component component = AffixHelper.getName(ths);
                    if (component.getContents() instanceof TranslatableContents tContents) {
                        int idx = "misc.zenith.affix_name.four".equals(tContents.getKey()) ? 2 : 1;
                        tContents.getArgs()[idx] = cir.getReturnValue();
                        cir.setReturnValue(component);
                    } else afxData.remove(AffixHelper.NAME);
                } catch (Exception exception) {
                    afxData.remove(AffixHelper.NAME);
                }
            }
            DynamicHolder<LootRarity> rarity = AffixHelper.getRarity(afxData);
            if (rarity.isBound()) {
                Component recolored = cir.getReturnValue().copy().withStyle(s -> s.withColor(rarity.get().getColor()));
                cir.setReturnValue(recolored);
            }
        }
    }

    /**
     * Injects before the first call to {@link ItemStack#getDamageValue()} inside of {@link ItemStack#hurt(int, RandomSource, ServerPlayer)} to reduce durability
     * damage.
     * Modifies the pAmount parameter, reducing it by the result of randomly rolling each point of damage against the block chance.
     */
    @ModifyVariable(at = @At(value = "INVOKE", target = "net/minecraft/world/item/ItemStack.getDamageValue()I"), method = "hurt", argsOnly = true, ordinal = 0)
    public int zenith$swapDura(int amount, int amountCopy, RandomSource pRandom, @Nullable ServerPlayer pUser) {
        if (Apotheosis.enableAdventure) {
            int blocked = 0;
            DoubleStream socketBonuses = SocketHelper.getGems((ItemStack) (Object) this).getDurabilityBonusPercentage(pUser);
            DoubleStream afxBonuses = AffixHelper.streamAffixes((ItemStack) (Object) this).mapToDouble(inst -> inst.getDurabilityBonusPercentage(pUser));
            DoubleStream bonuses = DoubleStream.concat(socketBonuses, afxBonuses);
            double chance = bonuses.reduce(0, (res, ele) -> res + (1 - res) * ele);

            int delta = 1;
            if (chance < 0) {
                delta = -1;
                chance = -chance;
            }

            if (chance > 0) {
                for (int i = 0; i < amount; i++) {
                    if (pRandom.nextFloat() <= chance) blocked += delta;
                }
            }
            return amount - blocked;
        }
        return amount;
    }
}
