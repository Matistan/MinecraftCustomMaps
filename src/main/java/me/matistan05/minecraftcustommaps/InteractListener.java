package me.matistan05.minecraftcustommaps;

import org.bukkit.*;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Entity;
import org.bukkit.entity.ItemFrame;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.MapMeta;
import org.bukkit.map.MapView;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.net.URL;
import java.util.UUID;

import static me.matistan05.minecraftcustommaps.CustomMapCommand.resizeImage;

public class InteractListener implements Listener {
    private final Main main;
    public InteractListener(Main main) {
        this.main = main;
    }
    BufferedImage image;
    @EventHandler
    public void InteractEvent(PlayerInteractAtEntityEvent e) {
        if(!(e.getRightClicked() instanceof ItemFrame)) {return;}
        ItemFrame itemFrame = (ItemFrame) e.getRightClicked();
        if(!itemFrame.getItem().getType().equals(Material.AIR)) {return;}
        ItemStack item = e.getPlayer().getInventory().getItemInMainHand();
        if(!item.getType().equals(Material.FILLED_MAP)) {return;}
        if(!item.hasItemMeta()) {return;}
        PersistentDataContainer container = item.getItemMeta().getPersistentDataContainer();
        if(!container.has(new NamespacedKey(main, "path"), PersistentDataType.STRING)) {return;}
        String path = container.get(new NamespacedKey(main, "path"), PersistentDataType.STRING);
        String mode = container.get(new NamespacedKey(main, "mode"), PersistentDataType.STRING);
        float scaleMode = 0;
        if(container.has(new NamespacedKey(main, "scalemode"), PersistentDataType.FLOAT)) {
            scaleMode = container.get(new NamespacedKey(main, "scalemode"), PersistentDataType.FLOAT);
        }
        Location location = e.getRightClicked().getLocation();
        try{
            image = ImageIO.read(new URL(path));
            switch (mode) {
                case "automatic":
                    for(int width = 128;; width += 128) {
                        if(width > (image.getWidth() / 128) * 128 + 128) {
                            scaleMode = 1;
                            break;
                        }
                        int height = (int) Math.ceil((float) (width * image.getHeight()) / image.getWidth());
                        if(fitsImage(location, width, height, itemFrame.getFacing(), e.getPlayer().getFacing())) {
                            scaleMode = (float) width / image.getWidth();
                        } else {
                            break;
                        }
                    }
                    float secondScale = 0;
                    for(int height = 128;; height += 128) {
                        if(height > (image.getHeight() / 128) * 128 + 128) {
                            secondScale = 1;
                            break;
                        }
                        int width = (int) Math.ceil((float) (height * image.getWidth()) / image.getHeight());
                        if(fitsImage(location, width, height, itemFrame.getFacing(), e.getPlayer().getFacing())) {
                            secondScale = (float) height / image.getHeight();
                        } else {
                            break;
                        }
                    }
                    if(secondScale > scaleMode) {
                        scaleMode = secondScale;
                    }
                case "scale":
                    Image resultingImage = image.getScaledInstance((int) Math.ceil(image.getWidth() * scaleMode), (int) Math.ceil(image.getHeight() * scaleMode), Image.SCALE_SMOOTH);
                    BufferedImage outputImage = new BufferedImage((int) Math.ceil(image.getWidth() * scaleMode), (int) Math.ceil(image.getHeight() * scaleMode), BufferedImage.TYPE_INT_RGB);
                    outputImage.getGraphics().drawImage(resultingImage, 0, 0, null);
                    image = outputImage;
                    break;
                case "small":
                    image = resizeImage(image);
                    break;
            }
        } catch (Exception ignored) {}
        UUID uuid = UUID.randomUUID();
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
                switch (itemFrame.getFacing()) {
                    case SOUTH:
                        placeMap(map, new Location(e.getPlayer().getWorld(), location.getBlockX() + i, location.getBlockY() + j, location.getBlockZ()), Rotation.NONE, itemFrame.getFacing(), uuid);
                        break;
                    case NORTH:
                        placeMap(map, new Location(e.getPlayer().getWorld(), location.getBlockX() - i, location.getBlockY() + j, location.getBlockZ()), Rotation.NONE, itemFrame.getFacing(), uuid);
                        break;
                    case EAST:
                        placeMap(map, new Location(e.getPlayer().getWorld(), location.getBlockX(), location.getBlockY() + j, location.getBlockZ() - i), Rotation.NONE, itemFrame.getFacing(), uuid);
                        break;
                    case WEST:
                        placeMap(map, new Location(e.getPlayer().getWorld(), location.getBlockX(), location.getBlockY() + j, location.getBlockZ() + i), Rotation.NONE, itemFrame.getFacing(), uuid);
                        break;
                    case UP:
                        switch (e.getPlayer().getFacing()) {
                            case NORTH:
                                placeMap(map, new Location(e.getPlayer().getWorld(), location.getBlockX() + i, location.getBlockY(), location.getBlockZ() - j), Rotation.NONE, itemFrame.getFacing(), uuid);
                                break;
                            case SOUTH:
                                placeMap(map, new Location(e.getPlayer().getWorld(), location.getBlockX() - i, location.getBlockY(), location.getBlockZ() + j), Rotation.CLOCKWISE, itemFrame.getFacing(), uuid);
                                break;
                            case EAST:
                                placeMap(map, new Location(e.getPlayer().getWorld(), location.getBlockX() + j, location.getBlockY(), location.getBlockZ() + i), Rotation.CLOCKWISE_45, itemFrame.getFacing(), uuid);
                                break;
                            case WEST:
                                placeMap(map, new Location(e.getPlayer().getWorld(), location.getBlockX() - j, location.getBlockY(), location.getBlockZ() - i), Rotation.CLOCKWISE_135, itemFrame.getFacing(), uuid);
                                break;
                        }
                        break;
                    case DOWN:
                        switch (e.getPlayer().getFacing()) {
                            case NORTH:
                                placeMap(map, new Location(e.getPlayer().getWorld(), location.getBlockX() + i, location.getBlockY(), location.getBlockZ() + j), Rotation.NONE, itemFrame.getFacing(), uuid);
                                break;
                            case SOUTH:
                                placeMap(map, new Location(e.getPlayer().getWorld(), location.getBlockX() - i, location.getBlockY(), location.getBlockZ() - j), Rotation.CLOCKWISE, itemFrame.getFacing(), uuid);
                                break;
                            case EAST:
                                placeMap(map, new Location(e.getPlayer().getWorld(), location.getBlockX() - j, location.getBlockY(), location.getBlockZ() + i), Rotation.CLOCKWISE_135, itemFrame.getFacing(), uuid);
                                break;
                            case WEST:
                                placeMap(map, new Location(e.getPlayer().getWorld(), location.getBlockX() + j, location.getBlockY(), location.getBlockZ() - i), Rotation.CLOCKWISE_45, itemFrame.getFacing(), uuid);
                                break;
                        }
                        break;
                }
            }
        }
        itemFrame.setRotation(Rotation.values()[(itemFrame.getRotation().ordinal() + 7) % 8]);
    }
    @EventHandler
    public void interactEvent(EntityDamageByEntityEvent e) {
        if(!(e.getDamager() instanceof Player) || !(e.getEntity() instanceof ItemFrame)) {return;}
        ItemFrame firstItemFrame = (ItemFrame) e.getEntity();
        PersistentDataContainer container = firstItemFrame.getPersistentDataContainer();
        if(!container.has(new NamespacedKey(main, "uuid"), PersistentDataType.STRING)) {return;}
        if(!((Player)e.getDamager()).isSneaking()) {
            firstItemFrame.getPersistentDataContainer().remove(new NamespacedKey(main, "uuid"));
            return;
        }
        UUID firstUuid = UUID.fromString(container.get(new NamespacedKey(main, "uuid"), PersistentDataType.STRING));
        for(Entity entity : firstItemFrame.getWorld().getEntities()) {
            if(!(entity instanceof ItemFrame)) {continue;}
            ItemFrame itemFrame = (ItemFrame) entity;
            if(!itemFrame.getPersistentDataContainer().has(new NamespacedKey(main, "uuid"), PersistentDataType.STRING)) {continue;}
            UUID uuid = UUID.fromString(itemFrame.getPersistentDataContainer().get(new NamespacedKey(main, "uuid"), PersistentDataType.STRING));
            if(uuid.equals(firstUuid)) {
                itemFrame.setItem(new ItemStack(Material.AIR));
                itemFrame.getPersistentDataContainer().remove(new NamespacedKey(main, "uuid"));
            }
        }
    }
    public void placeMap(ItemStack map, Location location, Rotation rotation, BlockFace blockFace, UUID uuid) {
        for(Entity entity : location.getWorld().getEntities()) {
            if(entity.getLocation().getBlockX() == location.getX() && entity.getLocation().getBlockY() == location.getY() &&
                    entity.getLocation().getBlockZ() == location.getZ() && entity instanceof ItemFrame && entity.getFacing().equals(blockFace)) {
                ItemFrame itemFrame = (ItemFrame) entity;
                itemFrame.getPersistentDataContainer().set(new NamespacedKey(main, "uuid"), PersistentDataType.STRING, uuid.toString());
                itemFrame.setItem(map);
                itemFrame.setRotation(rotation);
                return;
            }
        }
    }
    private boolean fitsImage(Location location, int width, int height, BlockFace itemFrameBlockFace, BlockFace playerBlockFace) {
        for(int i = 0; i < (int) Math.ceil(width / 128d); i++) {
            for(int j = 0; j < (int) Math.ceil(height / 128d); j++) {
                switch (itemFrameBlockFace) {
                    case SOUTH:
                        if(!isMap(new Location(location.getWorld(), location.getBlockX() + i, location.getBlockY() + j, location.getBlockZ()), itemFrameBlockFace)) {return false;}
                        break;
                    case NORTH:
                        if(!isMap(new Location(location.getWorld(), location.getBlockX() - i, location.getBlockY() + j, location.getBlockZ()), itemFrameBlockFace)) {return false;}
                        break;
                    case EAST:
                        if(!isMap(new Location(location.getWorld(), location.getBlockX(), location.getBlockY() + j, location.getBlockZ() - i), itemFrameBlockFace)) {return false;}
                        break;
                    case WEST:
                        if(!isMap(new Location(location.getWorld(), location.getBlockX(), location.getBlockY() + j, location.getBlockZ() + i), itemFrameBlockFace)) {return false;}
                        break;
                    case UP:
                        switch (playerBlockFace) {
                            case NORTH:
                                if(!isMap(new Location(location.getWorld(), location.getBlockX() + i, location.getBlockY(), location.getBlockZ() - j), itemFrameBlockFace)) {return false;}
                                break;
                            case SOUTH:
                                if(!isMap(new Location(location.getWorld(), location.getBlockX() - i, location.getBlockY(), location.getBlockZ() + j), itemFrameBlockFace)) {return false;}
                                break;
                            case EAST:
                                if(!isMap(new Location(location.getWorld(), location.getBlockX() + j, location.getBlockY(), location.getBlockZ() + i), itemFrameBlockFace)) {return false;}
                                break;
                            case WEST:
                                if(!isMap(new Location(location.getWorld(), location.getBlockX() - j, location.getBlockY(), location.getBlockZ() - i), itemFrameBlockFace)) {return false;}
                                break;
                        }
                        break;
                    case DOWN:
                        switch (playerBlockFace) {
                            case NORTH:
                                if(!isMap(new Location(location.getWorld(), location.getBlockX() + i, location.getBlockY(), location.getBlockZ() + j), itemFrameBlockFace)) {return false;}
                                break;
                            case SOUTH:
                                if(!isMap(new Location(location.getWorld(), location.getBlockX() - i, location.getBlockY(), location.getBlockZ() - j), itemFrameBlockFace)) {return false;}
                                break;
                            case EAST:
                                if(!isMap(new Location(location.getWorld(), location.getBlockX() - j, location.getBlockY(), location.getBlockZ() + i), itemFrameBlockFace)) {return false;}
                                break;
                            case WEST:
                                if(!isMap(new Location(location.getWorld(), location.getBlockX() + j, location.getBlockY(), location.getBlockZ() - i), itemFrameBlockFace)) {return false;}
                                break;
                        }
                        break;
                }
            }
        }
        return true;
    }
    public boolean isMap(Location location, BlockFace blockFace) {
        for(Entity entity : location.getWorld().getEntities()) {
            if(entity.getLocation().getBlockX() == location.getX() && entity.getLocation().getBlockY() == location.getY() &&
                    entity.getLocation().getBlockZ() == location.getZ() && entity instanceof ItemFrame && entity.getFacing().equals(blockFace)) {
                return true;
            }
        }
        return false;
    }
}