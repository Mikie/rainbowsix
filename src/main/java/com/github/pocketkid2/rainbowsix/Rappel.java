package com.github.pocketkid2.rainbowsix;

import java.util.Arrays;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.material.Rails;
import org.bukkit.metadata.FixedMetadataValue;

import net.md_5.bungee.api.ChatColor;

public class Rappel {

	class Session {
		Direction wall;
		int pos;

		Session(Direction wall, int pos) {
			this.wall = wall;
			this.pos = pos;
		}

		public boolean valid(Block block) {
			switch (wall) {
			case EAST_WEST:
				return block.getX() == pos;
			case NORTH_SOUTH:
				return block.getZ() == pos;
			default:
				return false;
			}
		}
	}

	enum Direction {
		// +/- Z
		NORTH_SOUTH,

		// +/- X
		EAST_WEST
	}

	private static final Material ITEM_TYPE = Material.LEASH;

	private static final String NAME_ON = "Rappel Rope: " + ChatColor.GREEN;
	private static final String NAME_OFF = "Rappel Rope: " + ChatColor.RED;
	private static final String LORE_ON = "Right click a valid wall to start rappel";
	private static final String LORE_OFF = "Right click the wall to end rappel";

	public static ItemStack createItem() {
		return createItem(false);
	}

	public static boolean isItem(ItemStack stack) {
		if (stack.getType() == ITEM_TYPE) {
			if (stack.getItemMeta().getDisplayName().equalsIgnoreCase(NAME_ON)) {
				return true;
			} else if (stack.getItemMeta().getDisplayName().equalsIgnoreCase(NAME_OFF)) {
				return true;
			}
		}
		return false;
	}

	public static ItemStack createItem(boolean on) {
		ItemStack stack = new ItemStack(ITEM_TYPE);
		ItemMeta meta = stack.getItemMeta();
		meta.setDisplayName(on ? NAME_ON : NAME_OFF);
		meta.setLore(Arrays.asList(on ? LORE_ON : LORE_OFF));
		stack.setItemMeta(meta);
		return stack;
	}

	public static Direction getRappelDirection(Player player) {
		if (isRappelOn(player)) {
			return (Direction) player.getMetadata("rappel").stream()
					.filter(key -> key.getOwningPlugin().equals(RainbowSixPlugin.getInstance())).findFirst().get().value();
		} else {
			return null;
		}
	}

	public static boolean isRappelOn(Player player) {
		return player.hasMetadata("rappel");
	}

	public static void turnRappelOn(Player player, Direction dir) {
		player.setMetadata("rappel", new FixedMetadataValue(RainbowSixPlugin.getInstance(), dir));
	}

	public static void turnRappelOff(Player player) {
		player.removeMetadata("rappel", RainbowSixPlugin.getInstance());
	}

	public Session create(Block block) {
		if (Rappel.hasRail(block)) {
			Direction dir = Rappel.getRail(block);
			int pos = 0;
			if (dir == Direction.EAST_WEST) {
				pos = block.getX();
			} else if (dir == Direction.NORTH_SOUTH) {
				pos = block.getZ();
			} else {
				return null;
			}
			return new Session(dir, pos);
		} else {
			return null;
		}
	}

	public static boolean hasRail(Block block) {
		if (block.getType() == Material.RAILS) {
			return true;
		} else {
			if (block.getY() < block.getWorld().getMaxHeight()) {
				return hasRail(block.getRelative(0, 1, 0));
			} else {
				return false;
			}
		}
	}

	public static Direction getRail(Block block) {
		if (block.getType() == Material.RAILS) {
			Rails rails = (Rails) block.getState().getData();
			System.out.println(rails.getDirection());
			switch (rails.getDirection()) {
			case EAST:
			case WEST:
				return Direction.EAST_WEST;
			case NORTH:
			case SOUTH:
				return Direction.NORTH_SOUTH;
			default:
				return null;
			}
		} else {
			if (block.getY() >= block.getWorld().getMaxHeight()) {
				return null;
			} else {
				return getRail(block.getRelative(0, 1, 0));
			}
		}
	}
}
