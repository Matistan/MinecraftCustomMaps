package me.matistan05.minecraftcustommaps;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.MapMeta;
import org.bukkit.map.*;

import javax.imageio.ImageIO;
import java.io.IOException;
import java.net.URL;
import java.util.LinkedList;
import java.util.List;

public class CustomMapCommand implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if(!(sender instanceof Player)) {return true;}
        Player p = (Player) sender;
        if(args.length != 1) {
            p.sendMessage(ChatColor.RED + "Wrong usage of this command");
            return true;
        }
        ItemStack map = new ItemStack(Material.FILLED_MAP);
        MapMeta mapMeta = (MapMeta) map.getItemMeta();
        MapView mapView = Bukkit.createMap(p.getWorld());
        mapView.getRenderers().clear();
        mapView.addRenderer(new MapRenderer() {
            @Override
            public void render(MapView renderMap, MapCanvas canvas, Player player) {
                try {
                    canvas.drawImage(0, 0, MapPalette.resizeImage(ImageIO.read(new URL(args[0]))));
                } catch (IOException e) {
                    p.sendMessage(ChatColor.RED + "Invalid URL");
                    p.getInventory().remove(map);
                }
            }
        });
        mapMeta.setMapView(mapView);
        List<String> lore = new LinkedList<>();
        lore.add(args[0]);
        mapMeta.setLore(lore);
        map.setItemMeta(mapMeta);
        p.getInventory().addItem(map);
        return true;
    }
}