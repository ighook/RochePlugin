package org.roche.roche.Command;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.WorldCreator;
import org.bukkit.WorldType;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CreateResourceWorldCommand implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String s, String[] strings) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("This command can only be executed by players.");
            return true;
        }

        Player player = (Player) sender;

        if(player.isOp()) {
            createResourceWorld();
        } else {
            player.sendMessage("You don't have permission to execute this command.");
        }

        return true;
    }

    private void createResourceWorld() {
        if (Bukkit.getWorld("resource") == null) {
            WorldCreator wc = new WorldCreator("resource");
            wc.environment(World.Environment.NORMAL);
            wc.type(WorldType.NORMAL);
            wc.generateStructures(false);
            wc.createWorld();
        }
    }
}
