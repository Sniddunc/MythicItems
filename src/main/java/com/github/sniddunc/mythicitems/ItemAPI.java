package com.github.sniddunc.mythicitems;

import com.github.sniddunc.mythicitems.commands.MythicItemsCommandCompleter;
import com.github.sniddunc.mythicitems.config.Config;
import com.github.sniddunc.mythicitems.exceptions.InvalidNamespaceException;
import com.github.sniddunc.mythicitems.objects.CustomItem;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.plugin.Plugin;

import java.util.List;

public class ItemAPI {

    private String namespace;

    private Plugin plugin;

    public ItemAPI(final String namespace, Plugin plugin) throws InvalidNamespaceException {
        if (!namespace.matches("^[a-z0-9_\"]+$")) {
            throw new InvalidNamespaceException("Namespace must only contain lowercase characters and underscores");
        }

        this.namespace = namespace;
        this.plugin = plugin;
    }

    public String getNamespace() {
        return namespace;
    }

    public Plugin getPlugin() {
        return plugin;
    }

    /**
     * Creates an empty item and returns it. If the item already exists, null is returned.
     * @param name e.g test_item
     */
    public CustomItem createNewItem(String name) {
        String fullName = namespace + "/" + name;

        return CustomItem.itemExists(fullName) ? null : new CustomItem(fullName);
    }

    /**
     * Takes in a populated item and writes it to the items config and loads it into the item lists
     * @param item
     */
    public void registerItem(CustomItem item) {
        Config.saveCustomItem(item);
        CustomItem.registerNewItem(item);

        MythicItemsCommandCompleter.reloadCustomItemsList();
    }

    /**
     * Tries to fetch an item matching the name provided in the current namespace
     * @param name
     * @return CustomItem
     */
    public CustomItem getItemByName(String name) {
        return CustomItem.getItem(namespace + "/" + name);
    }

    public static class Utils {
        public static boolean itemsAreEqual(ItemStack item1, CustomItem item2) {
            String item1Tag = CustomItem.getTag(item1);

            if (item1Tag == null) {
                return false;
            }

            return item1Tag.equals(item2.getItemTag());
        }

        public static CustomItem getItemByName(String namespace, String name) {
            return CustomItem.getItem(namespace + "/" + name);
        }

        public static boolean potionsAreEqual(ItemStack potion1, ItemStack potion2) {
            if (potion1.getType() != Material.POTION || potion2.getType() != Material.POTION) {
                return false;
            }

            PotionMeta potion1Meta = (PotionMeta) potion1.getItemMeta();
            PotionMeta potion2Meta = (PotionMeta) potion2.getItemMeta();

            if (!ChatColor.stripColor(potion1Meta.getDisplayName()).equals(ChatColor.stripColor(potion2Meta.getDisplayName()))) {
//                Bukkit.broadcastMessage("Display named aren't equal");
                return false;
            }

            List<String> potion1Lore = potion1Meta.getLore();
            List<String> potion2Lore = potion2Meta.getLore();

            if (potion1Lore != null && potion2Lore != null) {
                int potion1LoreSize = potion1Lore.size();
                int potion2LoreSize = potion2Lore.size();

                int max = Math.max(potion1LoreSize, potion2LoreSize);

                for (int i = 0; i < max; i++) {
                    if (!ChatColor.stripColor(potion1Lore.get(i)).equals(ChatColor.stripColor(potion2Lore.get(i)))) {
//                        Bukkit.broadcastMessage("Lores aren't equal");
                        return false;
                    }
                }
            }

            if (!potion1Meta.getBasePotionData().equals(potion2Meta.getBasePotionData())) {
//                Bukkit.broadcastMessage("Potion data not equal");
                return false;
            }

            return true;
        }
    }
}
