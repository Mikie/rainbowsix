package com.github.pocketkid2.rainbowsix.listeners;

import org.bukkit.entity.ItemFrame;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.hanging.HangingBreakEvent;
import org.bukkit.event.hanging.HangingPlaceEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

import com.github.pocketkid2.rainbowsix.Charge;
import com.github.pocketkid2.rainbowsix.RainbowSixPlugin;

import net.md_5.bungee.api.ChatColor;

public class ChargeListener implements Listener {

	private RainbowSixPlugin plugin;

	public ChargeListener(RainbowSixPlugin plugin) {
		this.plugin = plugin;
	}

	@EventHandler
	public void onChargePlace(HangingPlaceEvent event) {
		// If hanging is an item frame
		System.out.println(1);
		if (event.getEntity() instanceof ItemFrame) {
			System.out.println(2);
			ItemFrame frame = (ItemFrame) event.getEntity();
			// If the player is holding a charge
			ItemStack stack = event.getPlayer().getInventory().getItemInMainHand();
			if (Charge.isCharge(stack)) {
				System.out.println(3);
				Charge charge = Charge.getCharge(stack);
				// Check that the charge can go on this block
				if (charge.canBreak(event.getBlock().getType())) {
					event.getPlayer().sendMessage(ChatColor.BLUE + "Breach Charge placed!");
					event.setCancelled(false);
					// Set up the item frame
				} else {
					event.getPlayer().sendMessage(ChatColor.RED + "Breach Charge cannot be placed there!");
					event.setCancelled(true);
				}
			}
		}
	}

	@EventHandler
	public void onChargeBreak(HangingBreakEvent event) {

	}

	@EventHandler
	public void onDetonateClick(PlayerInteractEvent event) {

	}

}
