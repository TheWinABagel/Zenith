package safro.zenith.adventure.commands;

import com.mojang.brigadier.arguments.IntegerArgumentType;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import safro.zenith.Zenith;
import safro.zenith.adventure.affix.socket.SocketHelper;
import safro.zenith.adventure.loot.LootCategory;

public class SocketCommand {


	public static void init() {
		CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> {
		dispatcher.register(Commands.literal("zenith").then(Commands.literal("set_sockets").requires(c -> c.hasPermission(2)).then(Commands.argument("sockets", IntegerArgumentType.integer()).executes(c -> {
			Zenith.LOGGER.warn(c.getSource().getTextName());
			Player p = c.getSource().getPlayerOrException();
			ItemStack stack = p.getMainHandItem();

			LootCategory cat = LootCategory.forItem(stack);
			if (cat.isNone()) {
				c.getSource().sendFailure(Component.literal("The target item cannot receive sockets!"));
				return 1;
			}

			int sockets =  IntegerArgumentType.getInteger(c, "sockets");
			SocketHelper.setSockets(stack, sockets);
			return 0;
		}))));
		});
	}
}
