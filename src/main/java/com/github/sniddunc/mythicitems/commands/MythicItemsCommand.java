package com.github.sniddunc.mythicitems.commands;

import com.github.sniddunc.mythicitems.objects.CustomItem;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

public class MythicItemsCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        // Give command handler
        if (sender.isOp() && args.length == 3 && args[0].equalsIgnoreCase("give")) {
            if (!(sender instanceof Player)) {
                sender.sendMessage(ChatColor.RED + "Sorry! Only players may use this command");
                return true;
            }

            Player target = Bukkit.getPlayerExact(args[1]);

            if (target == null || !target.isOnline()) {
                sender.sendMessage(ChatColor.RED + args[1] + " is not online");
                return true;
            }

            String itemName = args[2];

            if (!CustomItem.itemWithNameExists(itemName)) {
                sender.sendMessage(ChatColor.RED + args[2] + " is not a valid item");
                return true;
            }

            CustomItem item = CustomItem.getItem(args[2]);

            ((Player) sender).getInventory().addItem(item.build());

            sender.sendMessage(ChatColor.GREEN + target.getName() + " has been given " + item.getName());
        }

        // List command handler
        else if (args.length == 1 && args[0].equalsIgnoreCase("list")) {
            for (CustomItem item : CustomItem.getAllItems()) {
                sender.sendMessage(ChatColor.DARK_GRAY + "> " + ChatColor.GREEN + item.getName());
            }
        }

        return false;
    }
}
