package me.matistan05.minecraftcustommaps;

import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.*;
import org.bukkit.block.BlockFace;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.ItemFrame;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.MapMeta;
import org.bukkit.map.*;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.net.URL;
import java.util.LinkedList;
import java.util.List;

public class CustomMapCommand implements CommandExecutor {
    private final Main main;
    public CustomMapCommand(Main main) {
        this.main = main;
    }
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if(!(sender instanceof Player)) {return true;}
        Player p = (Player) sender;
        if(args.length == 0) {
            p.sendMessage(ChatColor.RED + "You must type an argument. For help, type: /custommap help");
            return true;
        }
        if (args[0].equals("help")) {
            if (args.length != 1) {
                p.sendMessage(ChatColor.RED + "Wrong usage of this command. For help, type: /custommap help");
                return true;
            }
            p.sendMessage(ChatColor.GREEN + "------- " + ChatColor.WHITE + " Custom Maps " + ChatColor.GREEN + "----------");
            p.sendMessage(ChatColor.BLUE + "Here is a list of commands:");
            p.sendMessage(ChatColor.YELLOW + "/custommap give <URL> " + ChatColor.AQUA + "- gives you a map with the picture from the URL with original quality");
            p.sendMessage(ChatColor.YELLOW + "/custommap give <URL> automatic" + ChatColor.AQUA + " - gives you a map with the picture from the URL which will be automatically fitting in item frames");
            p.sendMessage(ChatColor.YELLOW + "/custommap give <URL> small" + ChatColor.AQUA + " - gives you a map with the picture from the URL resized to 1x1");
            p.sendMessage(ChatColor.YELLOW + "/custommap give <URL> scale <float>" + ChatColor.AQUA + " - gives you a map with the picture from the URL with a size multiplied by a given scale");
            p.sendMessage(ChatColor.YELLOW + "/custommap fillitemframes <x1> <y1> <z1> <x2> <y2> <z2>" + ChatColor.AQUA + " - fills this terrain with item frames and rotate them automatically");
            p.sendMessage(ChatColor.YELLOW + "/custommap fillitemframes <x1> <y1> <z1> <x2> <y2> <z2> <direction>" + ChatColor.AQUA + " - fills this terrain with item frames with set direction");
            p.sendMessage(ChatColor.YELLOW + "/custommap changeproperties original" + ChatColor.AQUA + " - resized a held map to original quality");
            p.sendMessage(ChatColor.YELLOW + "/custommap changeproperties automatic" + ChatColor.AQUA + " - changes the property of a help map to have automatically adjusted quality");
            p.sendMessage(ChatColor.YELLOW + "/custommap changeproperties small" + ChatColor.AQUA + " - resizes a held map to 1x1");
            p.sendMessage(ChatColor.YELLOW + "/custommap changeproperties scale <float>" + ChatColor.AQUA + " - changes the size of a held map to be multiplied by a given scale");
            p.sendMessage(ChatColor.YELLOW + "/custommap geturl" + ChatColor.AQUA + " - gives you the URL of the held map");
            p.sendMessage(ChatColor.YELLOW + "/custommap help" + ChatColor.AQUA + " - shows a list of commands");
            p.sendMessage(ChatColor.GREEN + "----------------------------------");
            return true;
        }
        if(args[0].equals("give")) {
            if(args.length == 1 || args.length > 4) {
                p.sendMessage(ChatColor.RED + "Wrong usage of this command. For help, type: /custommap help");
                return true;
            }
            ItemStack map = new ItemStack(Material.FILLED_MAP);
            MapMeta mapMeta = (MapMeta) map.getItemMeta();
            MapView mapView = Bukkit.createMap(p.getWorld());
            BufferedImage image, resizedImage;
            try {
                image = ImageIO.read(new URL(args[1]));
                resizedImage = resizeImage(ImageIO.read(new URL(args[1])));
            } catch (Exception e) {
                p.sendMessage(ChatColor.RED + "Invalid URL. For help, type: /custommap help");
                return true;
            }
            if(image == null) {
                p.sendMessage(ChatColor.RED + "This is not an image link. For help, type: /custommap help");
                return true;
            }
            int offsetX = 64 - resizedImage.getWidth() / 2;
            int offsetY = 64 - resizedImage.getHeight() / 2;
            for(MapRenderer mapRenderer : mapView.getRenderers()) {
                mapView.removeRenderer(mapRenderer);
            }
            mapView.addRenderer(new MapRenderer() {
                boolean rendered = false;
                @Override
                public void render(MapView renderMap, MapCanvas canvas, Player player) {
                    if(!rendered) {
                        rendered = true;
                        canvas.drawImage(offsetX, offsetY, resizedImage);
                    }
                }
            });
            mapMeta.setMapView(mapView);
            mapMeta.getPersistentDataContainer().set(new NamespacedKey(main, "path"), PersistentDataType.STRING, args[1]);
            if(args.length > 2) {
                if(!args[2].equals("automatic") && !args[2].equals("small") && !args[2].equals("scale")) {
                    p.sendMessage(ChatColor.RED + "Wrong argument. For help, type: /custommap help");
                    return true;
                }
            }
            if(args.length == 3) {
                mapMeta.getPersistentDataContainer().set(new NamespacedKey(main, "mode"), PersistentDataType.STRING, args[2]);
                if(args[2].equals("scale")) {
                    p.sendMessage(ChatColor.RED + "You must type a float value at the end. For help, type: /custommap help");
                    return true;
                }
            } else if(args.length == 4) {
                if(!args[2].equals("scale")) {
                    p.sendMessage(ChatColor.RED + "Wrong usage of this command. For help, type: /custommap help");
                    return true;
                }
                mapMeta.getPersistentDataContainer().set(new NamespacedKey(main, "mode"), PersistentDataType.STRING, args[2]);
                try {
                    mapMeta.getPersistentDataContainer().set(new NamespacedKey(main, "scalemode"), PersistentDataType.FLOAT, Float.parseFloat(args[3]));
                } catch (NumberFormatException e) {
                    p.sendMessage(ChatColor.RED + "Invalid scale. For help, type: /custommap help");
                    return true;
                }
            } else {
                mapMeta.getPersistentDataContainer().set(new NamespacedKey(main, "mode"), PersistentDataType.STRING, "original");
            }
            int width = (int) Math.ceil(image.getWidth() / 128d);
            int height = (int) Math.ceil(image.getHeight() / 128d);
            if(args.length != 2) {
                switch (args[2]) {
                    case "small":
                        width = 1;
                        height = 1;
                        break;
                    case "scale":
                        float scale = Float.parseFloat(args[3]);
                        width = (int) Math.ceil(image.getWidth() * scale / 128d);
                        height = (int) Math.ceil(image.getHeight() * scale / 128d);
                        if(scale < 0.01 || width > 100 || height > 100) {
                            p.sendMessage(ChatColor.RED + "Scale is too small or too big. For help, type: /custommap help");
                            return true;
                        }
                        break;
                }
            }
            mapMeta.setDisplayName(ChatColor.DARK_PURPLE + "Custom Map (" + (args.length == 3 ? (args[2].equals("automatic") ? "Max " : "") : "") + width + "x" + height + ")");
            mapMeta.addItemFlags(ItemFlag.HIDE_POTION_EFFECTS);
            List<String> lore = new LinkedList<>();
            lore.add(ChatColor.GRAY + (args.length == 3 ? (args[2].equals("automatic") ? "A maximum of " : "") : "") + width * height + " item frame" + (width * height > 1 ? "s" : "") + " required");
            lore.add(ChatColor.BLUE + "How to use it?");
            lore.add(ChatColor.GRAY + "Create a wall of item frames, then place");
            lore.add(ChatColor.GRAY + "this map in a bottom left item frame,");
            lore.add(ChatColor.GRAY + "and your image will be displayed.");
            lore.add(ChatColor.BLUE + "How to remove it?");
            lore.add(ChatColor.GRAY + "Shift-right-click one of the maps to remove");
            lore.add(ChatColor.GRAY + "the whole image off the wall.");
            mapMeta.setLore(lore);
            map.setItemMeta(mapMeta);
            p.getInventory().addItem(map);
            return true;
        }
        if(args[0].equals("fillitemframes")) {
            if(args.length != 7 && args.length != 8) {
                p.sendMessage(ChatColor.RED + "Wrong usage of this command. For help, type: /custommap help");
                return true;
            }
            if(args.length == 8 && !args[7].equals("north") && !args[7].equals("south") && !args[7].equals("east") && !args[7].equals("west") && !args[7].equals("up") && !args[7].equals("down")) {
                p.sendMessage(ChatColor.RED + "Wrong argument. For help, type: /custommap help");
                return true;
            }
            Location loc1, loc2;
            try {
                loc1 = new Location(p.getWorld(), Integer.parseInt(args[1]), Integer.parseInt(args[2]), Integer.parseInt(args[3]));
                loc2 = new Location(p.getWorld(), Integer.parseInt(args[4]), Integer.parseInt(args[5]), Integer.parseInt(args[6]));
            } catch (Exception e) {
                p.sendMessage(ChatColor.RED + "These are not integers. For help, type: /custommap help");
                return true;
            }
            if((loc1.getBlockX() - loc2.getBlockX()) * (loc1.getBlockY() - loc2.getBlockY()) * (loc1.getBlockZ() - loc2.getBlockZ()) > 32768) {
                p.sendMessage(ChatColor.RED + "Too many blocks in the specified area (maximum 32768, specified " +
                        (loc1.getBlockX() - loc2.getBlockX()) * (loc1.getBlockY() - loc2.getBlockY()) * (loc1.getBlockZ() - loc2.getBlockZ()) +
                        "). For help, type: /custommap help");
                return true;
            }
            BlockFace blockFace;
            for(int x = 0; x <= Math.abs(loc2.getBlockX() - loc1.getBlockX()); x++) {
                for(int y = 0; y <= Math.abs(loc2.getBlockY() - loc1.getBlockY()); y++) {
                    for(int z = 0; z <= Math.abs(loc2.getBlockZ() - loc1.getBlockZ()); z++) {
                        new Location(p.getWorld(), loc1.getBlockX() + x * (loc1.getBlockX() > loc2.getBlockX() ? -1 : 1), loc1.getBlockY() + y * (loc1.getBlockY() > loc2.getBlockY() ? -1 : 1), loc1.getBlockZ() + z * (loc1.getBlockZ() > loc2.getBlockZ() ? -1 : 1)).getBlock().setType(Material.AIR);
                    }
                }
            }
            if(args.length == 7) {
                int[] directions = new int[6];
                for(int x = 0; x <= Math.abs(loc2.getBlockX() - loc1.getBlockX()); x++) {
                    for(int y = 0; y <= Math.abs(loc2.getBlockY() - loc1.getBlockY()); y++) {
                        for(int z = 0; z <= Math.abs(loc2.getBlockZ() - loc1.getBlockZ()); z++) {
                            for(int i = 0; i < 6; i++) {
                                Location location = new Location(p.getWorld(), loc1.getBlockX() + x * (loc1.getBlockX() > loc2.getBlockX() ? -1 : 1), loc1.getBlockY() + y * (loc1.getBlockY() > loc2.getBlockY() ? -1 : 1), loc1.getBlockZ() + z * (loc1.getBlockZ() > loc2.getBlockZ() ? -1 : 1));
                                if(isBlockBehind(location, BlockFace.values()[i]) && noItemFrame(location, BlockFace.values()[i])) {
                                    directions[i]++;
                                }
                            }
                        }
                    }
                }
                int maxValue = 0, maxI = 0;
                for(int i = 0; i < 6; i++) {
                    if(directions[i] > maxValue) {
                        maxValue = directions[i];
                        maxI = i;
                    }
                }
                blockFace = BlockFace.values()[maxI];
            } else {
                blockFace = BlockFace.valueOf(args[7].toUpperCase());
            }
            int count = 0;
            for(int x = 0; x <= Math.abs(loc2.getBlockX() - loc1.getBlockX()); x++) {
                for(int y = 0; y <= Math.abs(loc2.getBlockY() - loc1.getBlockY()); y++) {
                    for(int z = 0; z <= Math.abs(loc2.getBlockZ() - loc1.getBlockZ()); z++) {
                        Location location = new Location(p.getWorld(), loc1.getBlockX() + x * (loc1.getBlockX() > loc2.getBlockX() ? -1 : 1), loc1.getBlockY() + y * (loc1.getBlockY() > loc2.getBlockY() ? -1 : 1), loc1.getBlockZ() + z * (loc1.getBlockZ() > loc2.getBlockZ() ? -1 : 1));
                        if(noItemFrame(location, blockFace) && isBlockBehind(location, blockFace)) {
                            ItemFrame itemFrame = (ItemFrame) p.getWorld().spawnEntity(location, EntityType.ITEM_FRAME);
                            itemFrame.setFacingDirection(blockFace);
                            count++;
                        }
                    }
                }
            }
            if(count > 0) {
                p.sendMessage(ChatColor.GREEN + "Successfully filled " + count + " item frame" + (count > 1 ? "s" : ""));
            } else {
                p.sendMessage(ChatColor.RED + "No item frames were filled");
            }
            return true;
        }
        if(args[0].equals("geturl")) {
            if(args.length > 1) {
                p.sendMessage(ChatColor.RED + "Wrong usage of this command. For help, type: /custommap help");
                return true;
            }
            if(!p.getInventory().getItemInMainHand().hasItemMeta()) {
                p.sendMessage(ChatColor.RED + "You're not holding a custom map. For help, type: /custommap help");
                return true;
            }
            PersistentDataContainer container = p.getInventory().getItemInMainHand().getItemMeta().getPersistentDataContainer();
            if(!container.has(new NamespacedKey(main, "path"), PersistentDataType.STRING)) {
                p.sendMessage(ChatColor.RED + "You're not holding a custom map. For help, type: /custommap help");
                return true;
            }
            TextComponent message = new TextComponent("Click here to get the url of this map");
            message.setColor(ChatColor.GREEN.asBungee());
            message.setBold(true);
            message.setClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, container.get(new NamespacedKey(main, "path"), PersistentDataType.STRING)));
            message.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder("Click this to get the URL!").italic(true).color(ChatColor.GRAY.asBungee()).create()));
            p.spigot().sendMessage(message);
            return true;
        }
        if(args[0].equals("changeproperties")) {
            if(args.length == 1 || args.length > 3) {
                p.sendMessage(ChatColor.RED + "Wrong usage of this command. For help, type: /custommap help");
                return true;
            }
            if(((!args[1].equals("automatic") && !args[1].equals("small") && !args[1].equals("original")) && args.length == 2) || ((!args[1].equals("scale")) && args.length == 3)) {
                p.sendMessage(ChatColor.RED + "Wrong usage of this command. For help, type: /custommap help");
                return true;
            }
            if(!p.getInventory().getItemInMainHand().hasItemMeta()) {
                p.sendMessage(ChatColor.RED + "You're not holding a custom map. For help, type: /custommap help");
                return true;
            }
            ItemMeta itemMeta = p.getInventory().getItemInMainHand().getItemMeta();
            if(!itemMeta.getPersistentDataContainer().has(new NamespacedKey(main, "path"), PersistentDataType.STRING)) {
                p.sendMessage(ChatColor.RED + "You're not holding a custom map. For help, type: /custommap help");
                return true;
            }
            String mode = itemMeta.getPersistentDataContainer().get(new NamespacedKey(main, "mode"), PersistentDataType.STRING);
            if((mode.equals(args[1]) && !args[1].equals("scale"))) {
                p.sendMessage(ChatColor.RED + "This property is already assigned to this map. For help, type: /custommap help");
                return true;
            }
            if(mode.equals("scale") && !args[1].equals("scale")) {
                itemMeta.getPersistentDataContainer().remove(new NamespacedKey(main, "scalemode"));
            }
            itemMeta.getPersistentDataContainer().set(new NamespacedKey(main, "mode"), PersistentDataType.STRING, args[1]);
            if(args[1].equals("scale")) {
                try {
                    if(mode.equals("scale") && itemMeta.getPersistentDataContainer().get(new NamespacedKey(main, "scalemode"), PersistentDataType.FLOAT) == Float.parseFloat(args[2])) {
                        p.sendMessage(ChatColor.RED + "This property is already assigned to this map. For help, type: /custommap help");
                        return true;
                    }
                    itemMeta.getPersistentDataContainer().set(new NamespacedKey(main, "scalemode"), PersistentDataType.FLOAT, Float.parseFloat(args[2]));
                } catch (Exception e) {
                    p.sendMessage(ChatColor.RED + "Invalid scale. For help, type: /custommap help");
                    return true;
                }
            }
            BufferedImage image = null;
            try {
                image = ImageIO.read(new URL(itemMeta.getPersistentDataContainer().get(new NamespacedKey(main, "path"), PersistentDataType.STRING)));
            } catch (Exception ignored) {}
            int width = (int) Math.ceil(image.getWidth() / 128d);
            int height = (int) Math.ceil(image.getHeight() / 128d);
            switch (args[1]) {
                case "small":
                    width = 1;
                    height = 1;
                    break;
                case "scale":
                    float scale = Float.parseFloat(args[2]);
                    width = (int) Math.ceil(image.getWidth() * scale / 128d);
                    height = (int) Math.ceil(image.getHeight() * scale / 128d);
                    if(scale < 0.01 || width > 100 || height > 100) {
                        p.sendMessage(ChatColor.RED + "Scale is too small or too big. For help, type: /custommap help");
                        return true;
                    }
                    break;
            }
            itemMeta.setDisplayName(ChatColor.DARK_PURPLE + "Custom Map (" + (!args[1].equals("automatic") ? width + "x" + height : "automatic size") + ")");
            p.getInventory().getItemInMainHand().setItemMeta(itemMeta);
            p.sendMessage(ChatColor.GREEN + "Successfully changed the property of this map!");
            return true;
        }
        p.sendMessage(ChatColor.RED + "Wrong usage of this command. For help, type: /custommap help");
        return true;
    }
    public static BufferedImage resizeImage(BufferedImage image) {
        BufferedImage outputImage;
        if(image.getHeight() >= image.getWidth()) {
            Image resultingImage =  image.getScaledInstance(128 * image.getWidth() / image.getHeight(), 128, Image.SCALE_DEFAULT);
            outputImage = new BufferedImage(128 * image.getWidth() / image.getHeight(), 128, BufferedImage.TYPE_INT_RGB);
            outputImage.getGraphics().drawImage(resultingImage, 0, 0, null);
        } else {
            Image resultingImage =  image.getScaledInstance(128, 128 * image.getHeight() / image.getWidth(), Image.SCALE_DEFAULT);
            outputImage = new BufferedImage(128, 128 * image.getHeight() / image.getWidth(), BufferedImage.TYPE_INT_RGB);
            outputImage.getGraphics().drawImage(resultingImage, 0, 0, null);
        }
        return outputImage;
    }
    public boolean noItemFrame(Location location, BlockFace blockFace) {
        for(Entity entity : location.getWorld().getEntities()) {
            if(entity.getLocation().getBlockX() == location.getX() && entity.getLocation().getBlockY() == location.getY() && entity.getLocation().getBlockZ() == location.getZ() && entity instanceof ItemFrame && entity.getFacing().equals(blockFace)) {
                return false;
            }
        }
        return true;
    }
    public boolean isBlockBehind(Location location, BlockFace blockFace) {
        Location blockLocation = new Location(location.getWorld(), location.getBlockX() - blockFace.getModX(), location.getBlockY() - blockFace.getModY(), location.getBlockZ() - blockFace.getModZ());
        Material material = blockLocation.getBlock().getType();
        if(!material.isSolid() || ((material.name().contains("FENCE") || material.name().contains("WALL")) && blockFace.equals(BlockFace.UP))) {
            return false;
        } else {
            return true;
        }
    }
}