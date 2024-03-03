package me.matistan05.minecraftcustommaps;

import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.ItemFrame;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.MapMeta;
import org.bukkit.map.MapView;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.java.JavaPlugin;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.logging.Level;

import static me.matistan05.minecraftcustommaps.CustomMapCommand.resizeImage;

public final class Main extends JavaPlugin {

    @Override
    public void onEnable() {
        saveDefaultConfig();
        Bukkit.getPluginCommand("custommap").setExecutor(new CustomMapCommand(this));
        Bukkit.getPluginCommand("custommap").setTabCompleter(new CustomMapCompleter());
        Bukkit.getPluginManager().registerEvents(new InteractListener(this), this);
        System.out.println("*********************************************************\n" +
                "Thank you for using this plugin! <3\n" +
                "Author: Matistan\n" +
                "If you enjoy this plugin, please rate it on spigotmc.org:\n" +
                "https://www.spigotmc.org/resources/custom-maps.109576/\n" +
                "*********************************************************");
        HashMap<String, BufferedImage> hashMap = new HashMap<>();
        for(World world : Bukkit.getWorlds()) {
            for(Entity entity : world.getEntities()) {
                if(!(entity instanceof ItemFrame)) {continue;}
                ItemFrame itemFrame = (ItemFrame) entity;
                PersistentDataContainer container = itemFrame.getPersistentDataContainer();
                if(!container.has(new NamespacedKey(this, "uuid"), PersistentDataType.STRING)) {continue;}
                String uuid = container.get(new NamespacedKey(this, "uuid"), PersistentDataType.STRING);
                int imageI = container.get(new NamespacedKey(this, "imagei"), PersistentDataType.INTEGER);
                int imageJ = container.get(new NamespacedKey(this, "imagej"), PersistentDataType.INTEGER);
                ItemStack map = itemFrame.getItem();
                MapMeta mapMeta = (MapMeta) map.getItemMeta();
                MapView mapView = Bukkit.createMap(world);
                mapView.getRenderers().clear();
                CustomMapRenderer customMapRenderer;
                BufferedImage image = null;
                if(imageI == 0 && imageJ == 0 || !hashMap.containsKey(uuid)) {
                    float imageScale = container.get(new NamespacedKey(this, "imagescale"), PersistentDataType.FLOAT);
                    String imagePath = container.get(new NamespacedKey(this, "path"), PersistentDataType.STRING);
                    try {
                        image = ImageIO.read(new URL(imagePath));
                    } catch (IOException ignored) {}
                    if(imageScale == 0) {
                        image = resizeImage(image);
                    } else {
                        Image resultingImage = image.getScaledInstance((int) Math.ceil(image.getWidth() * imageScale), (int) Math.ceil(image.getHeight() * imageScale), Image.SCALE_SMOOTH);
                        BufferedImage outputImage = new BufferedImage((int) Math.ceil(image.getWidth() * imageScale), (int) Math.ceil(image.getHeight() * imageScale), BufferedImage.TYPE_INT_RGB);
                        outputImage.getGraphics().drawImage(resultingImage, 0, 0, null);
                        image = outputImage;
                    }
                    customMapRenderer = new CustomMapRenderer(imageI, imageJ, image);
                    hashMap.put(uuid, image);
                } else {
                    customMapRenderer = new CustomMapRenderer(imageI, imageJ, hashMap.get(uuid));
                }
                mapView.addRenderer(customMapRenderer);
                mapMeta.setMapView(mapView);
                map.setItemMeta(mapMeta);
                itemFrame.setItem(map);
            }
            hashMap.clear();
        }
    }
}