package me.matistan05.minecraftcustommaps;

import org.bukkit.entity.Player;
import org.bukkit.map.MapCanvas;
import org.bukkit.map.MapRenderer;
import org.bukkit.map.MapView;

import java.awt.image.BufferedImage;

public class CustomMapRenderer extends MapRenderer {
    int i, j;
    BufferedImage image;
    public CustomMapRenderer(int i, int j, BufferedImage image) {
        this.i = i;
        this.j = j;
        this.image = image;
    }
    @Override
    public void render(MapView map, MapCanvas canvas, Player player) {
        canvas.drawImage(0, (j == Math.ceil(image.getHeight() / 128d) - 1 ? 128 -
                ((image.getHeight() - 1) % 128) + 1 : 0), image.getSubimage(
                i * 128, (j == Math.ceil(image.getHeight() / 128d) - 1 ? 0 :
                        image.getHeight() - 128 - j * 128), (i == Math.ceil(image.getWidth() / 128d) - 1 ?
                        ((image.getWidth() - 1) % 128) + 1 : 128),
                (j == Math.ceil(image.getHeight() / 128d) - 1 ? ((image.getHeight() - 1) % 128) + 1 : 128)));
    }
}