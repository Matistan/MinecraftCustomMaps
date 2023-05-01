package me.matistan05.minecraftcustommaps;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

public final class Main extends JavaPlugin {

    @Override
    public void onEnable() {
        Bukkit.getPluginCommand("custommap").setExecutor(new CustomMapCommand());
        Bukkit.getPluginManager().registerEvents(new InteractListener(), this);
    }
}