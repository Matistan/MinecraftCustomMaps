package me.matistan05.minecraftcustommaps;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Rotation;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.ItemFrame;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.MapMeta;
import org.bukkit.map.MapView;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.net.URL;

public class InteractListener implements Listener {
    BufferedImage image;
    @EventHandler
    public void InteractEvent(PlayerInteractAtEntityEvent e) {
        ItemStack item = e.getPlayer().getInventory().getItemInMainHand();
        if(item.getType().equals(Material.FILLED_MAP)) {
            if(e.getRightClicked().getType().equals(EntityType.ITEM_FRAME)) {
                if(item.hasItemMeta()) {
                    if(item.getItemMeta().hasLore()) {
                        if(item.getItemMeta().getLore().size() == 1) {
                            Location location = e.getRightClicked().getLocation();
                            location.setX((int)(location.getX()));
                            location.setY((int)(location.getY()));
                            location.setZ((int)(location.getZ()) - 1);
                            ItemFrame itemFrame = (ItemFrame) e.getRightClicked();
                            if(itemFrame.getItem().getType().equals(Material.AIR)) {
                                try{
                                    image = ImageIO.read(new URL(item.getItemMeta().getLore().get(0)));
                                } catch (Exception ignored) {}
                                for(int i = 0; i < (int) Math.ceil(image.getWidth() / 128d); i++) {
                                    for(int j = 0; j < (int) Math.ceil(image.getHeight() / 128d); j++) {
                                        ItemStack map = new ItemStack(Material.FILLED_MAP);
                                        MapMeta mapMeta = (MapMeta) map.getItemMeta();
                                        MapView mapView = Bukkit.createMap(e.getPlayer().getWorld());
                                        mapView.getRenderers().clear();
                                        CustomMapRenderer customMapRenderer = new CustomMapRenderer(i, j, image);
                                        mapView.addRenderer(customMapRenderer);
                                        mapMeta.setMapView(mapView);
                                        map.setItemMeta(mapMeta);
                                        placeMap(map, new Location(e.getPlayer().getWorld(), location.getX() + i, location.getY() + j, location.getZ()));
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
    public void placeMap(ItemStack map, Location location) {
        for(Entity entity : location.getWorld().getEntities()) {
            Location entityLocation = new Location(entity.getWorld(),(int)(entity.getLocation().getX()), (int)(entity.getLocation().getY()), (int)(entity.getLocation().getZ() - 1));
            if(entityLocation.getX() == location.getX() && entityLocation.getY() == location.getY() && entityLocation.getZ() == location.getZ() && entity instanceof ItemFrame) {
                ItemFrame itemFrame = (ItemFrame) entity;
                itemFrame.setRotation(Rotation.NONE);
                itemFrame.setItem(map);
                return;
            }
        }
    }
}