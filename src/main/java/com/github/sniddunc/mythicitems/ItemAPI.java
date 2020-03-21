package com.github.sniddunc.mythicitems;

import com.github.sniddunc.mythicitems.config.Config;
import com.github.sniddunc.mythicitems.exceptions.InvalidNamespaceException;
import com.github.sniddunc.mythicitems.objects.CustomItem;
import org.bukkit.inventory.ItemStack;

public class ItemAPI {

    private String namespace;

    public ItemAPI(final String namespace) throws InvalidNamespaceException {
        if (!namespace.matches("^[a-z0-9_\"]+$")) {
            throw new InvalidNamespaceException("Namespace must only contain lowercase characters and underscores");
        }

        this.namespace = namespace;
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

        public CustomItem getItemByName(String namespace, String name) {
            return CustomItem.getItem(namespace + "/" + name);
        }
    }
}
