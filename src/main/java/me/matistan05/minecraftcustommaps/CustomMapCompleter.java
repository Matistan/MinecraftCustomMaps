package me.matistan05.minecraftcustommaps;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.util.LinkedList;
import java.util.List;

public class CustomMapCompleter implements TabCompleter {
    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        List<String> list = new LinkedList<>();
        if(args.length == 1) {
            if(startsWith("give", args[0])) {
                list.add("give");
            }
            if(startsWith("fillitemframes", args[0])) {
                list.add("fillitemframes");
            }
            if(startsWith("changeproperties", args[0])) {
                list.add("changeproperties");
            }
            if(startsWith("geturl", args[0])) {
                list.add("geturl");
            }
            if(startsWith("help", args[0])) {
                list.add("help");
            }
        } else if(args.length == 2 && args[0].equals("changeproperties")) {
            if(startsWith("original", args[0])) {
                list.add("original");
            }
            if(startsWith("automatic", args[0])) {
                list.add("automatic");
            }
            if(startsWith("small", args[0])) {
                list.add("small");
            }
            if(startsWith("scale", args[0])) {
                list.add("scale");
            }
            if(startsWith("url", args[0])) {
                list.add("url");
            }
        }
        if(args.length == 3) {
            if(args[0].equals("give")) {
                if(startsWith("automatic", args[2])) {
                    list.add("automatic");
                }
                if(startsWith("small", args[2])) {
                    list.add("small");
                }
                if(startsWith("scale", args[2])) {
                    list.add("scale");
                }
            }
        }
        if(args.length >= 2 && args.length <= 7) {
            if(args[0].equals("fillitemframes")) {
                Block block = ((Player) sender).getTargetBlock(null, 10);
                if(block.getBlockData().getMaterial() != Material.AIR) {
                    if(args.length == 2) {
                        if(startsWith(String.valueOf(block.getX()), args[1])) {
                            list.add(String.valueOf(block.getX()));
                        }
                    }
                    if(args.length == 3) {
                        if(startsWith(String.valueOf(block.getY()), args[2])) {
                            list.add(String.valueOf(block.getY()));
                        }
                    }
                    if(args.length == 4) {
                        if(startsWith(String.valueOf(block.getZ()), args[3])) {
                            list.add(String.valueOf(block.getZ()));
                        }
                    }
                    if(args.length == 5) {
                        if(startsWith(String.valueOf(block.getX()), args[4])) {
                            list.add(String.valueOf(block.getX()));
                        }
                    }
                    if(args.length == 6) {
                        if(startsWith(String.valueOf(block.getY()), args[5])) {
                            list.add(String.valueOf(block.getY()));
                        }
                    }
                    if(args.length == 7) {
                        if(startsWith(String.valueOf(block.getZ()), args[6])) {
                            list.add(String.valueOf(block.getZ()));
                        }
                    }
                }
            }
        }
        if(args.length == 2) {
            if(args[0].equals("changeproperties")) {
                if(startsWith("original", args[1])) {
                    list.add("original");
                }
                if(startsWith("automatic", args[1])) {
                    list.add("automatic");
                }
                if(startsWith("scale", args[1])) {
                    list.add("scale");
                }
                if(startsWith("small", args[1])) {
                    list.add("small");
                }
            }
        }
        if(args.length == 8) {
            if(args[0].equals("fillitemframes")) {
                if(startsWith("north", args[7])) {
                    list.add("north");
                }
                if(startsWith("south", args[7])) {
                    list.add("south");
                }
                if(startsWith("up", args[7])) {
                    list.add("up");
                }
                if(startsWith("down", args[7])) {
                    list.add("down");
                }
                if(startsWith("east", args[7])) {
                    list.add("east");
                }
                if(startsWith("west", args[7])) {
                    list.add("west");
                }
            }
        }
        return list;
    }
    private boolean startsWith(String a, String b) {
        if(b.length() <= a.length()) {
            for(int i = 0; i < b.length(); i++) {
                if(b.toLowerCase().charAt(i) != a.toLowerCase().charAt(i)) {
                    return false;
                }
            }
        } else {
            return false;
        }
        return true;
    }
}
