package com.github.sniddunc.mythicitems.commands;

import com.github.sniddunc.mythicitems.objects.CustomItem;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.util.StringUtil;

import java.util.ArrayList;
import java.util.List;

public class MythicItemsCommandCompleter implements TabCompleter {

    private static List<String> subCommands;
    private static List<String> customItems;

    public MythicItemsCommandCompleter() {
        subCommands = new ArrayList<>();
        subCommands.add("give");
        subCommands.add("list");

        customItems = new ArrayList<>();
        for (CustomItem customItem : CustomItem.getAllItems()) {
            customItems.add(customItem.getName());
        }
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        List<String> options = new ArrayList<>();

        if (args.length == 1)  {
            StringUtil.copyPartialMatches(args[0], subCommands, options);
        }

        else if (args.length == 2 && args[0].equalsIgnoreCase("give")) {
            // We want to show players, and the default behaviour is to show players, so by returning null we're
            // leveraging that behaviour rather than reinventing the wheel.
            return null;
        }

        else if (args.length == 3 && args[0].equalsIgnoreCase("give")) {
            StringUtil.copyPartialMatches(args[2], customItems, options);
        }

        return options;
    }

    // STATIC
    public static void reloadCustomItemsList() {
        customItems = new ArrayList<>();
        for (CustomItem customItem : CustomItem.getAllItems()) {
            customItems.add(customItem.getName());
        }
    }
}
