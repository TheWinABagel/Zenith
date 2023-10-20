package dev.shadowsoffire.apotheosis.adventure.affix.socket.gem.bonus.special;

import com.google.common.base.Preconditions;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.shadowsoffire.apotheosis.Apotheosis;
import dev.shadowsoffire.apotheosis.adventure.AdventureModule;
import dev.shadowsoffire.apotheosis.adventure.affix.Affix;
import dev.shadowsoffire.apotheosis.adventure.affix.socket.gem.GemClass;
import dev.shadowsoffire.apotheosis.adventure.affix.socket.gem.GemRegistry;
import dev.shadowsoffire.apotheosis.adventure.affix.socket.gem.bonus.GemBonus;
import dev.shadowsoffire.apotheosis.adventure.loot.LootRarity;
import dev.shadowsoffire.placebo.codec.IngredientCodec;
import dev.shadowsoffire.placebo.json.ItemAdapter;
import dev.shadowsoffire.placebo.util.StepFunction;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.ChatFormatting;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;

import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * Modifies drops of a block.
 */
public class DropTransformBonus extends GemBonus {

    public static Codec<DropTransformBonus> CODEC = RecordCodecBuilder.create(inst -> inst
        .group(
            gemClass(),
            TagKey.codec(Registries.BLOCK).fieldOf("blocks").forGetter(a -> a.tag),
            IngredientCodec.INSTANCE.fieldOf("inputs").forGetter(a -> a.inputs),
            ItemAdapter.CODEC.fieldOf("output").forGetter(a -> a.output),
            VALUES_CODEC.fieldOf("values").forGetter(a -> a.values),
            Codec.STRING.fieldOf("desc").forGetter(a -> a.descKey))
        .apply(inst, DropTransformBonus::new));

    /**
     * Input blocks this transformation triggers on.<br>
     * If the tag is empty, this works on all blocks, as long as a block was broken.<br>
     * If none of the builtin tags are sufficient, you will have to make a new tag.
     */
    protected final TagKey<Block> tag;
    /**
     * List of input items merged as an ingredient.
     */
    protected final Ingredient inputs;
    /**
     * Output item. Each replaced stack will be cloned with this stack, with the same size as the original.
     */
    protected final ItemStack output;
    /**
     * Rarity -> Chance map.
     */
    protected final Map<LootRarity, StepFunction> values;
    protected final String descKey;

    protected final transient List<Block> blocks;

    public DropTransformBonus(GemClass gemClass, TagKey<Block> tag, Ingredient inputs, ItemStack output, Map<LootRarity, StepFunction> values, String descKey) {
        super(Apotheosis.loc("drop_transform"), gemClass);
        this.tag = tag;
        this.inputs = inputs;
        this.output = output;
        this.values = values;
        this.descKey = descKey;
    //    if (EffectiveSide.get().isServer()) {
    //        this.blocks = GemRegistry.INSTANCE._getContext().getTag(tag).stream().map(Holder::get).toList();
    //    }
    //    else
            this.blocks = Collections.emptyList();
    }

    @Override
    public Codec<? extends GemBonus> getCodec() {
        return CODEC;
    }

    @Override
    public Component getSocketBonusTooltip(ItemStack gem, LootRarity rarity) {
        float chance = this.values.get(rarity).min();
        return Component.translatable(this.descKey, Affix.fmt(chance * 100)).withStyle(ChatFormatting.YELLOW);
    }

    @Override
    public void modifyLoot(ItemStack gem, LootRarity rarity, ObjectArrayList<ItemStack> loot, LootContext ctx) {
        if (Apotheosis.enableDebug) AdventureModule.LOGGER.info("Drop transform init");
        if (ctx.hasParam(LootContextParams.BLOCK_STATE)) {
            if (Apotheosis.enableDebug) AdventureModule.LOGGER.info("LootContext is a block");
            Block block = ctx.getParam(LootContextParams.BLOCK_STATE).getBlock();
            if (!this.blocks.isEmpty() && !this.blocks.contains(block)){
                if (Apotheosis.enableDebug) AdventureModule.LOGGER.error("List of blocks is empty or list doesn't contain broken block");
                return;
            }
            if (Apotheosis.enableDebug) AdventureModule.LOGGER.info("List of blocks contains this block");
            if (ctx.getRandom().nextFloat() <= this.values.get(rarity).min()) {
                if (Apotheosis.enableDebug) AdventureModule.LOGGER.info("Changing loot, size of loot list: {}", loot.size());
                for (int i = 0; i < loot.size(); i++) {
                    ItemStack stack = loot.get(i);
                    if (Apotheosis.enableDebug) AdventureModule.LOGGER.info("Passes test? {}, input ingredient: {}, stack: {}", this.inputs.test(stack), this.inputs, stack);
                    if (this.inputs.test(stack)) {
                        ItemStack outCopy = this.output.copy();
                        if (Apotheosis.enableDebug) AdventureModule.LOGGER.info("Changing {} to {}", stack, outCopy);
                        outCopy.setCount(stack.getCount());
                        loot.set(i, outCopy);
                    }
                }
            }
        }
    }

    @Override
    public DropTransformBonus validate() {
        Preconditions.checkNotNull(this.values);
        this.values.forEach((k, v) -> {
            Preconditions.checkNotNull(k);
            Preconditions.checkNotNull(v);
        });
        return this;
    }

    @Override
    public boolean supports(LootRarity rarity) {
        return this.values.containsKey(rarity);
    }

    @Override
    public int getNumberOfUUIDs() {
        return 0;
    }
}
