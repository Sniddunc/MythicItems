package com.github.sniddunc.mythicitems.listeners;

import com.github.sniddunc.mythicitems.objects.CustomItem;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.PrepareAnvilEvent;
import org.bukkit.inventory.ItemStack;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.List;

public class CraftListeners implements Listener {

    @EventHandler
    public void onItemRename(PrepareAnvilEvent event) {
        ItemStack result = event.getResult();

        if (result == null || !result.hasItemMeta() || !result.getItemMeta().hasLore()) {
            return;
        }

        List<String> lore = result.getLore();

        // Extract what might be a tag from the last line of the lore
        String tag = ChatColor.stripColor(lore.get(lore.size() - 1));

        String itemName = null;

        try {
            itemName = new String(Base64.getDecoder().decode(tag.getBytes()), StandardCharsets.UTF_8);
        } catch (IllegalArgumentException e) {
            return;
        }

        CustomItem item = CustomItem.getItem(itemName);

        if (item == null) {
            return;
        }

        // If this special item can't be renamed, show air instead of the result
        if (!item.canBeRenamed()) {
            event.setResult(new ItemStack(Material.AIR));
        }
    }
}
