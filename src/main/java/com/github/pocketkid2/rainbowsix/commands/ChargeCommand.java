package com.github.pocketkid2.rainbowsix.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import com.github.pocketkid2.rainbowsix.Charge;
import com.github.pocketkid2.rainbowsix.RainbowSixPlugin;

import net.md_5.bungee.api.ChatColor;

public class ChargeCommand implements CommandExecutor {

	private RainbowSixPlugin plugin;

	public ChargeCommand(RainbowSixPlugin plugin) {
		this.plugin = plugin;
	}

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if (sender instanceof Player) {
			Player player = (Player) sender;
			player.sendMessage("");
			if (args.length >= 1) {
				int amount = 1;
				if (args.length >= 2) {
					try {
						amount = Integer.parseInt(args[1]);
					} catch (NumberFormatException e) {
						player.sendMessage(ChatColor.RED + "'" + args[1] + "' is not a number");
						return false;
					}
				}
				if (args[0].equalsIgnoreCase("clear")) {
					player.removeMetadata("charge", plugin);
				} else if (args[0].equalsIgnoreCase("breach")) {
					// Give a breach charge
					ItemStack stack = Charge.createCharge(Charge.BREACH);
					stack.setAmount(amount);
					player.getInventory().addItem(stack);
					player.sendMessage(ChatColor.AQUA + "You were given a " + Charge.BREACH.getName());
				} else if (args[0].equalsIgnoreCase("exothermic")) {
					// Give an exothermic charge
					ItemStack stack = Charge.createCharge(Charge.EXOTHERMIC);
					stack.setAmount(amount);
					player.getInventory().addItem(stack);
					player.sendMessage(ChatColor.AQUA + "You were given an " + Charge.EXOTHERMIC.getName());
				} else {
					player.sendMessage(ChatColor.RED + "Wrong charge name '" + args[0] + "'");
					return false;
				}
			} else {
				player.sendMessage(ChatColor.RED + "Not enough arguments!");
				return false;
			}
		} else {
			sender.sendMessage(ChatColor.RED + "You must be a player!");
		}
		return true;
	}

}
