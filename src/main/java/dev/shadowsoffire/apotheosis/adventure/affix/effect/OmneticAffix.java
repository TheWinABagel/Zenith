package dev.shadowsoffire.apotheosis.adventure.affix.effect;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.shadowsoffire.apotheosis.adventure.Adventure.Affixes;
import dev.shadowsoffire.apotheosis.adventure.affix.Affix;
import dev.shadowsoffire.apotheosis.adventure.affix.AffixHelper;
import dev.shadowsoffire.apotheosis.adventure.affix.AffixInstance;
import dev.shadowsoffire.apotheosis.adventure.affix.AffixType;
import dev.shadowsoffire.apotheosis.adventure.loot.LootCategory;
import dev.shadowsoffire.apotheosis.adventure.loot.LootRarity;
import dev.shadowsoffire.placebo.json.ItemAdapter;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.effect.MobEffectUtil;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluids;

import java.util.Arrays;
import java.util.Map;
import java.util.function.Consumer;

/**
 * Increases mining level of tools to a level set by the affix.
 */
public class OmneticAffix extends Affix {

    public static final Codec<OmneticAffix> CODEC = RecordCodecBuilder.create(inst -> inst
        .group(
            LootRarity.mapCodec(OmneticData.CODEC).fieldOf("values").forGetter(a -> a.values))
        .apply(inst, OmneticAffix::new));

    protected final Map<LootRarity, OmneticData> values;

    public OmneticAffix(Map<LootRarity, OmneticData> values) {
        super(AffixType.ABILITY);
        this.values = values;
    }

    @Override
    public boolean canApplyTo(ItemStack stack, LootCategory cat, LootRarity rarity) {
        return cat.isBreaker() && this.values.containsKey(rarity);
    }

    @Override
    public void addInformation(ItemStack stack, LootRarity rarity, float level, Consumer<Component> list) {
        list.accept(Component.translatable("affix." + this.getId() + ".desc", Component.translatable("misc.zenith." + this.values.get(rarity).name)));
    }

    public boolean harvest(Player player, BlockState state) {
        ItemStack stack = player.getMainHandItem();
        if (!stack.isEmpty()) {
            AffixInstance inst = AffixHelper.getAffixes(stack).get(Affixes.OMNETIC);
            if (inst != null && inst.isValid()) {
                OmneticData data = this.values.get(inst.rarity().get());
                for (ItemStack item : data.items()) {
                    if (item.isCorrectToolForDrops(state)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    public float speed(Player player, BlockState state, BlockPos pos, float speed) {

        ItemStack stack = player.getMainHandItem();
        if (!stack.isEmpty()) {
            AffixInstance inst = AffixHelper.getAffixes(stack).get(Affixes.OMNETIC);
            if (inst != null && inst.isValid()) {
                OmneticData data = this.values.get(inst.rarity().get());
                for (ItemStack item : data.items()) {
                    speed = Math.max(getBaseSpeed(player, item, state, pos), speed);
                }
            }
        }
        return speed;
    }

    @Override
    public Codec<? extends Affix> getCodec() {
        return CODEC;
    }

    static record OmneticData(String name, ItemStack[] items) {

        public static Codec<OmneticData> CODEC = RecordCodecBuilder.create(inst -> inst
            .group(
                Codec.STRING.fieldOf("name").forGetter(OmneticData::name),
                Codec.list(ItemAdapter.CODEC).xmap(l -> l.toArray(new ItemStack[0]), Arrays::asList).fieldOf("items").forGetter(OmneticData::items))
            .apply(inst, OmneticData::new));

    }

    static float getBaseSpeed(Player player, ItemStack tool, BlockState state, BlockPos pos) {
        float f = tool.getDestroySpeed(state);
        if (f > 1.0F) {
            int i = EnchantmentHelper.getBlockEfficiency(player);
            ItemStack itemstack = player.getMainHandItem();
            if (i > 0 && !itemstack.isEmpty()) {
                f += i * i + 1;
            }
        }

        if (MobEffectUtil.hasDigSpeed(player)) {
            f *= 1.0F + (MobEffectUtil.getDigSpeedAmplification(player) + 1) * 0.2F;
        }

        if (player.hasEffect(MobEffects.DIG_SLOWDOWN)) {
            float f1 = switch (player.getEffect(MobEffects.DIG_SLOWDOWN).getAmplifier()) {
                case 0 -> 0.3F;
                case 1 -> 0.09F;
                case 2 -> 0.0027F;
                case 3 -> 8.1E-4F;
                default -> 8.1E-4F;
            };
            f *= f1;
        }

        if (player.isEyeInFluid(FluidTags.WATER) && !EnchantmentHelper.hasAquaAffinity(player)) {
            f /= 5.0F;
        }

        if (!player.onGround()) {
            f /= 5.0F;
        }
        return f;
    }

}
