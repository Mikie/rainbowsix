package com.github.pocketkid2.rainbowsix.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.github.pocketkid2.rainbowsix.RainbowSixPlugin;
import com.github.pocketkid2.rainbowsix.Rappel;

import net.md_5.bungee.api.ChatColor;

public class RappelCommand implements CommandExecutor {

	private RainbowSixPlugin plugin;

	public RappelCommand(RainbowSixPlugin plugin) {
		this.plugin = plugin;
	}

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if (sender instanceof Player) {
			Player player = (Player) sender;
			player.sendMessage("");

			player.getInventory().addItem(Rappel.createItem());
			player.sendMessage("You were given a " + ChatColor.BLUE + "Rappel Rope");

		} else {
			sender.sendMessage(ChatColor.RED + "You must be a player!");
		}
		return true;
	}

}
