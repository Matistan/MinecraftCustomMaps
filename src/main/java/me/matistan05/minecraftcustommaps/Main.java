package me.matistan05.minecraftcustommaps;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

public final class Main extends JavaPlugin {

    @Override
    public void onEnable() {
        Bukkit.getPluginCommand("custommap").setExecutor(new CustomMapCommand(this));
        Bukkit.getPluginCommand("custommap").setTabCompleter(new CustomMapCompleter());
        Bukkit.getPluginManager().registerEvents(new InteractListener(this), this);
        System.out.println("*********************************************************\n" +
                "Thank you for using this plugin! <3\n" +
                "Author: Matistan\n" +
                "If you enjoy this plugin, please rate it on spigotmc.org:\n" +
                "https://www.spigotmc.org/resources/custom-maps.109576/\n" +
                "*********************************************************");
    }
}