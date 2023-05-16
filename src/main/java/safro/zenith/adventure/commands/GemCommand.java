package safro.zenith.adventure.commands;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.commands.arguments.ResourceLocationArgument;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.ai.attributes.AttributeModifier.Operation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import safro.zenith.adventure.affix.socket.gem.Gem;
import safro.zenith.adventure.affix.socket.gem.GemManager;
import safro.zenith.api.placebo.json.WeightedJsonReloadListener.IDimensional;

import java.util.Arrays;

public class GemCommand {

	public static final SuggestionProvider<CommandSourceStack> SUGGEST_OP = (ctx, builder) -> SharedSuggestionProvider.suggest(Arrays.stream(Operation.values()).map(Operation::name), builder);
	public static final SuggestionProvider<CommandSourceStack> SUGGEST_ATTRIB = (ctx, builder) -> SharedSuggestionProvider.suggest(Registry.ATTRIBUTE.keySet().stream().map(ResourceLocation::toString), builder);
	public static final SuggestionProvider<CommandSourceStack> SUGGEST_GEM = (ctx, builder) -> SharedSuggestionProvider.suggest(GemManager.INSTANCE.getKeys().stream().map(ResourceLocation::toString), builder);

	@SuppressWarnings("removal")
	public static void register(LiteralArgumentBuilder<CommandSourceStack> root) {

	}

	public static void init() {
		CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> {
			dispatcher.register(Commands.literal("zenith").then(Commands.literal("gem").requires(c -> c.hasPermission(2)).then(Commands.literal("fromPreset").then(Commands.argument("gem", ResourceLocationArgument.id()).suggests(SUGGEST_GEM).executes(c -> {
				Gem gem = GemManager.INSTANCE.getValue(ResourceLocationArgument.getId(c, "gem"));
				Player p = c.getSource().getPlayerOrException();
				ItemStack stack = GemManager.createGemStack(gem, p.random, null, p.getLuck());
				p.addItem(stack);
				return 0;
			}))).then(Commands.literal("random").executes(c -> {
				Player p = c.getSource().getPlayerOrException();
				ItemStack gem = GemManager.createRandomGemStack(p.random, c.getSource().getLevel(), p.getLuck(), IDimensional.matches(p.level));
				p.addItem(gem);
				return 0;
			}))));
		});
	}
}