package dev.shadowsoffire.apotheosis.adventure.commands;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import dev.shadowsoffire.apotheosis.adventure.affix.socket.SocketHelper;
import dev.shadowsoffire.apotheosis.adventure.affix.socket.gem.Gem;
import dev.shadowsoffire.apotheosis.adventure.affix.socket.gem.GemRegistry;
import dev.shadowsoffire.apotheosis.adventure.compat.GameStagesCompat;
import dev.shadowsoffire.apotheosis.adventure.loot.LootRarity;
import dev.shadowsoffire.placebo.reload.WeightedDynamicRegistry;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.commands.arguments.ResourceLocationArgument;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class AddGemCommand {

    public static final SuggestionProvider<CommandSourceStack> SUGGEST_GEM = (ctx, builder) -> SharedSuggestionProvider.suggest(GemRegistry.INSTANCE.getKeys().stream().map(ResourceLocation::toString), builder);

    @SuppressWarnings("removal")
    public static void register(LiteralArgumentBuilder<CommandSourceStack> root) {
        root.then(Commands.literal("socketGem").requires(c -> c.hasPermission(2)).then(Commands.literal("fromPreset").then(Commands.argument("gem", ResourceLocationArgument.id()).suggests(SUGGEST_GEM).executes(c -> {
            Gem gem = GemRegistry.INSTANCE.getValue(ResourceLocationArgument.getId(c, "gem"));
            Player p = c.getSource().getPlayerOrException();
            ItemStack gemStack = GemRegistry.createGemStack(gem, LootRarity.random(p.getRandom(), p.getLuck()));

            ItemStack result = p.getMainHandItem();
            result.setCount(1);
            int socket = SocketHelper.getFirstEmptySocket(result);
            List<ItemStack> gems = new ArrayList<>(SocketHelper.getGems(result));
            ItemStack gemToInsert = gemStack.copy();
            gemToInsert.setCount(1);
            gems.set(socket, gemStack.copy());
            SocketHelper.setGems(result, gems);

            return 0;
        }))));
    }
}
