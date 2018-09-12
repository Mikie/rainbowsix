package com.github.pocketkid2.rainbowsix;

import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.ItemFrame;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public enum Charge {

	BREACH(Material.ENDER_PEARL, ChatColor.GREEN + "Breach Charge", new HashSet<Material>(Arrays.asList(Material.WOOD))),
	EXOTHERMIC(
			Material.EYE_OF_ENDER,
			ChatColor.GOLD + "Exothermic Charge",
			new HashSet<Material>(Arrays.asList(Material.WOOD, Material.IRON_BLOCK)));

	private static final Material CHARGE_ITEM = Material.ITEM_FRAME;
	private static final Material DETONATOR_ITEM = Material.TRIPWIRE_HOOK;

	private static final String CHARGE_LORE = "Right-click to place";
	private static final String DETONATOR_LORE = "Right-click to detonate";

	private Material item;
	private String name;
	private Set<Material> breakable;

	private Charge(Material item, String name, Set<Material> breakable) {
		this.item = item;
		this.name = name;
		this.breakable = breakable;
	}

	public static ItemStack createCharge(Charge type) {
		ItemStack stack = new ItemStack(CHARGE_ITEM);
		ItemMeta meta = stack.getItemMeta();
		meta.setDisplayName(type.name);
		meta.setLore(Arrays.asList(CHARGE_LORE));
		stack.setItemMeta(meta);
		return stack;
	}

	public static ItemStack createDetonator(Charge type) {
		ItemStack stack = new ItemStack(DETONATOR_ITEM);
		ItemMeta meta = stack.getItemMeta();
		meta.setDisplayName(type.name);
		meta.setLore(Arrays.asList(DETONATOR_LORE));
		stack.setItemMeta(meta);
		return stack;
	}

	public Material getItem() {
		return item;
	}

	public String getName() {
		return name;
	}

	public static boolean isCharge(ItemStack stack) {
		if (stack.getType() == CHARGE_ITEM) {
			for (Charge charge : Charge.values()) {
				if (stack.hasItemMeta() && stack.getItemMeta().getDisplayName().equals(charge.name)) {
					return true;
				}
			}
		}
		return false;
	}

	public static boolean isDetonator(ItemStack stack) {
		if (stack.getType() == DETONATOR_ITEM) {
			for (Charge charge : Charge.values()) {
				if (stack.hasItemMeta() && stack.getItemMeta().getDisplayName().equals(charge.name)) {
					return true;
				}
			}
		}
		return false;
	}

	public static Charge getCharge(ItemStack stack) {
		if (stack.getType() == CHARGE_ITEM) {
			for (Charge charge : Charge.values()) {
				if (stack.getItemMeta().getDisplayName().equals(charge.name)) {
					return charge;
				}
			}
		}
		return null;
	}

	public boolean canBreak(Material type) {
		return breakable.contains(type);
	}

	public static ItemStack getFrameItem(Charge charge) {
		ItemStack stack = new ItemStack(charge.item);
		ItemMeta meta = stack.getItemMeta();
		meta.setDisplayName(charge.name);
		stack.setItemMeta(meta);
		return stack;
	}

	public static boolean isFrameItem(ItemStack stack) {
		for (Charge charge : Charge.values()) {
			if ((stack.getType() == charge.item) && stack.getItemMeta().getDisplayName().equalsIgnoreCase(charge.name)) {
				return true;
			}
		}
		return false;
	}

	public static Charge getChargeFromFrameItem(ItemStack stack) {
		for (Charge charge : Charge.values()) {
			if ((stack.getType() == charge.item) && stack.getItemMeta().getDisplayName().equalsIgnoreCase(charge.name)) {
				return charge;
			}
		}
		return null;
	}

	public static void detonate(ItemFrame frame) {
		Block start = frame.getLocation().getBlock().getRelative(frame.getAttachedFace());
		List<Block> destroy = new LinkedList<Block>();

		Charge charge = Charge.getChargeFromFrameItem(frame.getItem());

		destroy.add(start);
		Block under = start.getRelative(0, -1, 0);
		if (charge.canBreak(under.getType())) {
			destroy.add(under);
		}

		frame.setItem(null);
		frame.remove();

		for (Block block : destroy) {
			block.setType(Material.AIR);
			block.getWorld().createExplosion(block.getX() + 0.5, block.getY() + 0.5, block.getZ() + 0.5, 1.0f, false, false);
		}
	}

}
