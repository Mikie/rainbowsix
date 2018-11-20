package com.github.pocketkid2.rainbowsix.listeners;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;

import com.github.pocketkid2.rainbowsix.Rappel;

public class RappelListener implements Listener {

	@EventHandler
	public void onClickWall(PlayerInteractEvent event) {
		// They must be right-clicking a block
		if (event.getAction() == Action.RIGHT_CLICK_BLOCK) {
			// If there's an item
			if (event.hasItem()) {
				// And it's the rappel rope
				if (Rappel.isItem(event.getItem())) {
					// Check if the player is on a rappel
					if (Rappel.isRappelOn(event.getPlayer())) {

					} else {
						// Check for possibility
						if (Rappel.hasRail(event.getClickedBlock())) {
							Rappel.getRail(event.getClickedBlock());
						}
					}
				}
			}
		}
	}
}
