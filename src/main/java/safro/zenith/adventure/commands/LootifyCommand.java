package safro.zenith.adventure.commands;

import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import safro.zenith.adventure.affix.AffixHelper;
import safro.zenith.adventure.loot.LootController;
import safro.zenith.adventure.loot.LootRarity;

import java.util.Collections;

public class LootifyCommand {

	public static final SuggestionProvider<CommandSourceStack> SUGGEST_RARITY = RarityCommand.SUGGEST_RARITY;

public static void init() {
	CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> {
		dispatcher.register(Commands.literal("zenith").then(Commands.literal("lootify").requires(c -> c.hasPermission(2)).then(Commands.argument("rarity", StringArgumentType.word()).suggests(SUGGEST_RARITY).executes(c -> {
			Player p = c.getSource().getPlayerOrException();
			String type = c.getArgument("rarity", String.class);
			LootRarity rarity = LootRarity.byId(type);
			ItemStack stack = p.getMainHandItem();
			AffixHelper.setAffixes(stack, Collections.emptyMap());
			LootController.createLootItem(stack, rarity, p.level.random);
			return 0;
		}))));
	});
}

}
