package org.roche.roche.Command;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class MoveWorldCommand implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String s, String[] strings) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("This command can only be executed by players.");
            return true;
        }

        Player player = (Player) sender;
        player.sendMessage("test");

        player.sendMessage(strings[0]);

        if (player.isOp()) {
            // 월드 이동
            World world;

            if (strings[0].equals("1")) {
                world = Bukkit.getWorld("resource");
            } else {
                world = Bukkit.getWorld("world");
            }

            if (world == null) {
                player.sendMessage("Resource world does not exist.");
                return true;
            } else {
                player.teleport(world.getSpawnLocation());
                player.sendMessage("You have been teleported to the resource world.");
            }
        } else {
            player.sendMessage("You don't have permission to execute this command.");
        }

        return true;
    }
}
