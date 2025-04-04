## Custom Maps

---

View on [Spigot](https://www.spigotmc.org/resources/custom-maps.109576/) •
Download [here](https://github.com/Matistan/MinecraftCustomMaps/releases)

---

> **Having issues?** Feel free to report them on the [Issues tab](https://github.com/Matistan/MinecraftCustomMaps/issues). I'll be glad to hear your opinion about the plugin as well as extra features you would like me to add!

## How it works?

Place any image on the map and then place it on item frames!

## Minecraft version

This plugin runs on a Minecraft version 1.16+.

## Features

- Create a wall with item frames using built-in command
- Place your map in the bottom left item frame and your image will be displayed
- Maps show you how big they are and how many item frames they need
- You can copy the URL of already created map
- You can resize an already created map

## Commands
- `/custommap give <URL>` - gives you a map with the picture from the URL with original quality
- `/custommap give <URL> automatic` - gives you a map with the picture from the URL which will be automatically fitting in item frames
- `/custommap give <URL> small` - gives you a map with the picture from the URL resized to 1x1
- `/custommap give <URL> scale <float>` - gives you a map with the picture from the URL with a size multiplied by a given scale
- `/custommap fillitemframes <x1> <y1> <z1> <x2> <y2> <z2>` - fills this terrain with item frames and rotate them automatically
- `/custommap fillitemframes <x1> <y1> <z1> <x2> <y2> <z2> <direction>` - fills this terrain with item frames with a set direction
- `/custommap changeproperties original` - resized a held map to original quality
- `/custommap changeproperties automatic` - changes the property of a help map to have automatically adjusted quality
- `/custommap changeproperties small` - resizes a held map to 1x1
- `/custommap changeproperties scale <float>` - changes the size of a held map to be multiplied by a given scale
- `/custommap geturl` - gives you the URL of the held map
- `/custommap fix` - fixes the preview of the held map (usually it breaks after server restart)
- `/custommap help` - shows a list of commands

## Configuration Options

Edit the `plugins/MinecraftCustomMaps/config.yml` file to change the following options:

| Key            | Description                                                              | Type    | recommended                                             |
|----------------|--------------------------------------------------------------------------|---------|---------------------------------------------------------|
| usePermissions | Set to true to require users to have permission to use certain commands. | boolean | true; false if you trust the people you're playing with |

## Permissions

If `usePermissions` is set to `true` in the `config.yml` file, players without ops will need the following permissions to use the commands:

| Permission                  | Description                                                         |
|-----------------------------|---------------------------------------------------------------------|
| custommaps.custommap        | Allows the player to use all `/custommap` commands.                 |
| custommaps.give             | Allows the player to use the `/custommap give` command.             |
| custommaps.fillitemframes   | Allows the player to use the `/custommap fillitemframes` command.   |
| custommaps.changeproperties | Allows the player to use the `/custommap changeproperties` command. |
| custommaps.geturl           | Allows the player to use the `/custommap geturl` command.           |
| custommaps.fix              | Allows the player to use the `/custommap fix` command.              |
| custommaps.help             | Allows the player to use the `/custommap help` command.             |

### Made by Matistan