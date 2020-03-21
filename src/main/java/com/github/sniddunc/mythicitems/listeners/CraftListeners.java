package com.github.sniddunc.mythicitems.listeners;

import com.github.sniddunc.mythicitems.objects.CustomItem;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Furnace;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.*;
import org.bukkit.inventory.ItemStack;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;

public class CraftListeners implements Listener {

    /**
     * onItemRename is used to stop players from renaming custom items in anvils if it's disallowed in the config
     * @param event
     */
    @EventHandler
    public void onItemRename(PrepareAnvilEvent event) {
        ItemStack result = event.getResult();

        CustomItem item = CustomItem.getItemByTag(result);

        if (item == null) {
            return;
        }

        // If this special item can't be renamed, show air instead of the result
        if (!item.canBeRenamed()) {
            event.setResult(new ItemStack(Material.AIR));
        }
    }

    /**
     * onCraftPrepare is used to support crafting recipes with custom items. Spigot and Minecraft base the crafting
     * inputs on Material, so if we want to use a custom special ItemStack as the input, we need to verify that the
     * crafting table contains the right resources when the recipe is matched. It's a bit hacky, but it's the best
     * way to do it. Hopefully ItemStack inputs are added into the API so this solution isn't necessary.
     * @param event
     */
    @EventHandler
    public void onCraftPrepare(PrepareItemCraftEvent event) {
        if (event.getRecipe() == null) {
            return;
        }

        ItemStack[] contents = event.getInventory().getContents();

        // Crafting table slot map
        // 1 2 3
        // 4 5 6   0
        // 7 8 9

        ItemStack resultItem = event.getRecipe().getResult();

        CustomItem result = CustomItem.getItemByTag(resultItem);

        if (result == null || !result.hasCustomIngredients()) {
            return;
        }

        List<String> pattern = result.getCraftingPattern();
        HashMap<Character, String> customIngredients = result.getCustomIngredients();

        boolean isValid = true;

        int slotOffset = 0;
        for (String row : pattern) {
            for (int i = 0; i < row.length(); i++) {
                char placeholder = row.charAt(i);

                if (!customIngredients.containsKey(placeholder)) {
                    continue;
                }

                int slot = slotOffset + i + 1;

                String currentItemTag = CustomItem.getTag(contents[slot]);

                // If a slot where a custom item is required doesn't contain a custom item
                if (currentItemTag == null) {
                    isValid = false;
                    break;
                }

                // If the tags don't match
                if (!currentItemTag.equals(customIngredients.get(placeholder))) {
                    isValid = false;
                    break;
                }
            }

            slotOffset += 3;
        }

        if (!isValid) {
            event.getInventory().setResult(null);
        }
    }
}
