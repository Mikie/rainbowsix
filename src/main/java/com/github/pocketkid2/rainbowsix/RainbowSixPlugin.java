package com.github.pocketkid2.rainbowsix;

import org.bukkit.plugin.java.JavaPlugin;

public class RainbowSixPlugin extends JavaPlugin {

	@Override
	public void onEnable() {
		registerCommands();
		registerListeners();
		registerRecipes();
		getLogger().info("Done!");
	}

	@Override
	public void onDisable() {
		cancelTasks();
		getLogger().info("Done!");
	}

	private void registerRecipes() {
		// TODO Auto-generated method stub

	}

	private void registerListeners() {
		// TODO Auto-generated method stub

	}

	private void registerCommands() {
		// TODO Auto-generated method stub

	}

	private void cancelTasks() {
		// TODO Auto-generated method stub

	}
}
