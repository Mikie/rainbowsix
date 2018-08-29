package com.github.pocketkid2.rainbowsix.listeners;

import org.bukkit.Sound;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.ItemFrame;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.hanging.HangingBreakEvent;
import org.bukkit.event.hanging.HangingPlaceEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;

import com.github.pocketkid2.rainbowsix.Charge;
import com.github.pocketkid2.rainbowsix.RainbowSixPlugin;

import net.md_5.bungee.api.ChatColor;

public class ChargeListener implements Listener {

	private RainbowSixPlugin plugin;

	public ChargeListener(RainbowSixPlugin plugin) {
		this.plugin = plugin;
	}

	/*
	 * Normal placement of charges
	 */
	@EventHandler
	public void onChargePlace(HangingPlaceEvent event) {
		// If hanging is an item frame
		if (event.getEntity() instanceof ItemFrame) {
			ItemFrame frame = (ItemFrame) event.getEntity();
			// If the player is holding a charge
			ItemStack stack = event.getPlayer().getInventory().getItemInMainHand();
			if (Charge.isCharge(stack)) {
				Charge charge = Charge.getCharge(stack);
				// Check that the charge can go on this block
				if (!charge.canBreak(event.getBlock().getType())) {
					event.getPlayer().sendMessage(ChatColor.RED + "Breach Charge cannot be placed there!");
					event.setCancelled(true);
					ItemStack update = stack.clone();
					event.getPlayer().getInventory().setItemInMainHand(update);
				} else if (event.getPlayer().hasMetadata("charge")) {
					event.getPlayer().sendMessage(ChatColor.RED + "You have already placed a Breach Charge!");
					event.setCancelled(true);
					ItemStack update = stack.clone();
					event.getPlayer().getInventory().setItemInMainHand(update);
				} else {
					event.getPlayer().sendMessage(ChatColor.BLUE + "Breach Charge placed!");
					event.setCancelled(false);
					// Set up the item frame
					frame.setItem(Charge.getFrameItem(charge));
					// Set up reference to owner and vice versa
					setOwner(frame, event.getPlayer());
					event.getPlayer().setMetadata("charge", new FixedMetadataValue(plugin, frame));
					event.getPlayer().playSound(frame.getLocation(), Sound.BLOCK_METAL_PLACE, 1.0f, 1.0f);
					// Switch to detonator
					ItemStack det = Charge.createDetonator(charge);
					det.setAmount(stack.getAmount());
					event.getPlayer().getInventory().setItemInMainHand(det);
					frame.setMetadata("det", new FixedMetadataValue(plugin, det));
				}
			}
		}
	}

	private void switchToCharge(ItemFrame frame, Player player, Charge charge, boolean removeOne) {
		ItemStack stack = (ItemStack) frame.getMetadata("det").stream().filter(key -> key.getOwningPlugin().equals(plugin)).findFirst()
				.get().value();
		ItemStack newStack = Charge.createCharge(charge);
		newStack.setAmount(stack.getAmount() - (removeOne ? 1 : 0));
		player.getInventory().remove(stack);
		player.getInventory().addItem(newStack);
	}

	private void setOwner(ItemFrame frame, Player owner) {
		frame.setMetadata("owner", new FixedMetadataValue(plugin, owner));
	}

	private Player getOwner(ItemFrame frame) {
		return (Player) frame.getMetadata("owner").stream().filter(key -> key.getOwningPlugin().equals(plugin)).findFirst().get().value();
	}

	/*
	 * If the charge is removed for an unnatural reason
	 */
	@EventHandler
	public void onChargeBreak(HangingBreakEvent event) {
		// If hanging is an item frame
		if (event.getEntity() instanceof ItemFrame) {
			ItemFrame frame = (ItemFrame) event.getEntity();
			// If the frame is a placed charge
			if (Charge.isFrameItem(frame.getItem())) {
				Player owner = getOwner(frame);
				owner.sendMessage(ChatColor.RED + "Your Breach Charge has been broken!");
				switchToCharge(frame, owner, Charge.getChargeFromFrameItem(frame.getItem()), false);
				owner.removeMetadata("charge", plugin);
				frame.removeMetadata("owner", plugin);
				frame.setItem(null);
				frame.remove();
			}
		}
	}

	@EventHandler
	public void onPlayerHitCharge(EntityDamageByEntityEvent event) {
		if (event.getEntityType() == EntityType.ITEM_FRAME) {
			if (event.getDamager().getType() == EntityType.PLAYER) {
				ItemFrame frame = (ItemFrame) event.getEntity();
				if (Charge.isFrameItem(frame.getItem())) {
					Player damager = (Player) event.getDamager();
					Player owner = getOwner(frame);
					if (damager.equals(owner)) {
						// Success
						event.setCancelled(true);
						switchToCharge(frame, owner, Charge.getChargeFromFrameItem(frame.getItem()), false);
						frame.setItem(null);
						frame.remove();
						frame.removeMetadata("owner", plugin);
						owner.removeMetadata("charge", plugin);
						owner.sendMessage(ChatColor.BLUE + "Your Breach Charge has been removed!");
						owner.playSound(frame.getLocation(), Sound.BLOCK_METAL_BREAK, 1.0f, 1.0f);
					} else {
						// Failure
						event.setCancelled(true);
						damager.sendMessage(ChatColor.RED + "You do not own that Breach Charge!");
					}
				}
			}
		}
	}

	@EventHandler
	public void onPlayerClickCharge(PlayerInteractEntityEvent event) {
		if (event.getRightClicked() instanceof ItemFrame) {
			ItemFrame frame = (ItemFrame) event.getRightClicked();
			if (Charge.isFrameItem(frame.getItem())) {
				event.getPlayer().sendMessage(ChatColor.RED + "Breach Charges cannot be modified!");
				event.setCancelled(true);
			}
		}
	}

	@EventHandler
	public void onDetonateClick(PlayerInteractEvent event) {
		if ((event.getItem() != null) && Charge.isDetonator(event.getItem())) {
			if (event.getAction() == Action.RIGHT_CLICK_AIR) {
				ItemFrame frame = (ItemFrame) event.getPlayer().getMetadata("charge").stream()
						.filter(key -> key.getOwningPlugin().equals(plugin)).findFirst().get().value();
				switchToCharge(frame, event.getPlayer(), Charge.getChargeFromFrameItem(frame.getItem()), true);
				Charge.detonate(frame);
				event.getPlayer().removeMetadata("charge", plugin);
				event.getPlayer().sendMessage(ChatColor.YELLOW + "Charge Detonated!");
			} else if (event.getAction() == Action.RIGHT_CLICK_BLOCK) {
				event.setCancelled(true);
				event.getPlayer().sendMessage(ChatColor.RED + "You cannot place the detonator!");
			}
		}
	}

	@EventHandler
	public void onPlayerQuit(PlayerQuitEvent event) {
		if (event.getPlayer().hasMetadata("charge")) {
			ItemFrame frame = (ItemFrame) event.getPlayer().getMetadata("charge").stream()
					.filter(key -> key.getOwningPlugin().equals(plugin)).findFirst().get().value();
			switchToCharge(frame, event.getPlayer(), Charge.getChargeFromFrameItem(frame.getItem()), false);
			frame.setItem(null);
			frame.remove();
			frame.removeMetadata("owner", plugin);
			event.getPlayer().removeMetadata("charge", plugin);
		}
	}

	@EventHandler
	public void onPlayerDropDetonator(PlayerDropItemEvent event) {
		if (Charge.isDetonator(event.getPlayer().getInventory().getItemInMainHand())) {
			event.setCancelled(true);
			event.getPlayer().sendMessage(ChatColor.RED + "Do not drop the detonator!");
		}
	}

	@EventHandler
	public void onPlayerDeath(EntityDamageEvent event) {
		if (event.getEntityType() == EntityType.PLAYER) {
			Player player = (Player) event.getEntity();
			if (event.getFinalDamage() >= player.getHealth()) {
				// The player died
				ItemFrame frame = (ItemFrame) player.getMetadata("charge").stream().filter(key -> key.getOwningPlugin().equals(plugin))
						.findFirst().get().value();
				switchToCharge(frame, player, Charge.getChargeFromFrameItem(frame.getItem()), false);
				frame.setItem(null);
				frame.remove();
				frame.removeMetadata("owner", plugin);
				player.removeMetadata("charge", plugin);
			}
		}
	}

}
