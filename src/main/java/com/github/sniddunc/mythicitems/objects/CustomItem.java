package com.github.sniddunc.mythicitems.objects;

import com.github.sniddunc.mythicitems.MythicItems;
import com.github.sniddunc.mythicitems.enchantments.GlowEffect;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.EntityType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.*;

public class CustomItem {
    private String name;
    private String displayName;

    private List<String> lore;

    private Material material;

    private Map<Enchantment, Integer> enchantments;

    private ShapedRecipe craftingRecipe;

    private HashMap<EntityType, Integer> mobDropMap;
    private boolean isDroppedByMobs;

    private HashMap<Material, Integer> blockDropMap;
    private boolean isDroppedByBlocks;

    private boolean canBeRenamed;
    private boolean isUnbreakable;
    private boolean isGlowing;

    public CustomItem(String name) {
        this.name = name;
        displayName = "Unnamed";
        lore = new ArrayList<>();
        material = Material.DIRT;
        enchantments = new HashMap<>();
        craftingRecipe = null;
        mobDropMap = new HashMap<>();
        isDroppedByMobs = false;
        blockDropMap = new HashMap<>();
        isDroppedByBlocks = false;
        canBeRenamed = false;
        isUnbreakable = false;
        isGlowing = false;
    }

    public String getName() {
        return name;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public List<String> getLore() {
        return lore;
    }

    /**
     * setLore parses the color codes in each entry in the provided string list, and then sets the lore to the
     * new parsed list.
     * @param lore
     */
    public void setLore(List<String> lore) {
        List<String> temp = new ArrayList<>();

        for (String line : lore) {
            temp.add(ChatColor.RESET + ChatColor.translateAlternateColorCodes('&', line));
        }

        this.lore = temp;
    }

    public Material getMaterial() {
        return material;
    }

    public void setMaterial(Material material) {
        this.material = material;
    }

    public void addEnchantment(Enchantment enchantment, int level) {
        enchantments.put(enchantment, level);
    }

    public ShapedRecipe getCraftingRecipe() {
        return craftingRecipe;
    }

    public void setCraftingRecipe(ShapedRecipe craftingRecipe) {
        this.craftingRecipe = craftingRecipe;
    }

    public void addMobDrop(EntityType type, int chance) {
        mobDropMap.put(type, chance);
        isDroppedByMobs = true;
    }

    public boolean isDroppedByMobs() {
        return isDroppedByMobs;
    }

    public boolean isDroppedByMob(EntityType type) {
        return mobDropMap.containsKey(type);
    }

    public int getMobDropChance(EntityType type) {
        return mobDropMap.get(type);
    }

    public void addBlockDrop(Material type, int chance) {
        blockDropMap.put(type, chance);
        isDroppedByBlocks = true;
    }

    public boolean isDroppedByBlocks() {
        return isDroppedByBlocks;
    }

    public boolean isDroppedByBlock(Material type) {
        return blockDropMap.containsKey(type);
    }

    public int getBlockDropChance(Material type) {
        return blockDropMap.get(type);
    }

    public boolean canBeRenamed() {
        return canBeRenamed;
    }

    public void setCanBeRenamed(boolean canBeRenamed) {
        this.canBeRenamed = canBeRenamed;
    }

    public boolean isUnbreakable() {
        return isUnbreakable;
    }

    public void setUnbreakable(boolean isUnbreakable) {
        this.isUnbreakable = isUnbreakable;
    }

    public boolean isGlowing() {
        return isGlowing;
    }

    public void setGlowing(boolean isGlowing) {
        this.isGlowing = isGlowing;
    }

    public ItemStack build() {
        ItemStack item = new ItemStack(material, 1);

        ItemMeta meta = item.getItemMeta();

        meta.setDisplayName(ChatColor.translateAlternateColorCodes('&', displayName));

        // Lore + base64 tag
        // We use the base64 to uniquely identify the item. It's generated off of it's name which should be unique.
        // We could show it in plain text, but obfuscating it will provide users a better experience instead of showing
        // 2 different versions of the name and making them guess which they should use (name & display name).
        List<String> tempLore = new ArrayList<>(lore);
        tempLore.add(ChatColor.BLACK + Base64.getEncoder().encodeToString(name.getBytes()));
        meta.setLore(tempLore);

        // Apply enchantments
        for (Enchantment enchantment : enchantments.keySet()) {
            meta.addEnchant(enchantment, enchantments.get(enchantment), true);
        }

        // If there are no enchantments and the item should still glow, we add our custom glow effect enchantment
        if (isGlowing && enchantments.size() == 0) {
            NamespacedKey key = new NamespacedKey(MythicItems.getInstance(), MythicItems.getInstance().getDescription().getName());
            GlowEffect glowEffect = new GlowEffect(key);
            meta.addEnchant(glowEffect, 1, true);
        }

        meta.setUnbreakable(isUnbreakable);

        item.setItemMeta(meta);

        return item;
    }


    ///////////////////////////////////
    // STATIC
    private static List<CustomItem> allItems = new ArrayList<>();
    private static HashMap<String, CustomItem> itemMap = new HashMap<>();

    /**
     * registerNewItem adds the passed in item to the allItems list, as well as the itemMap HashMap.
     * @param item
     */
    public static void registerNewItem(CustomItem item) {
        allItems.add(item);
        itemMap.put(item.getName(), item);
    }

    /**
     * itemExists takes in an instance of CustomItem and checks if the allItems list contains it
     * @param item
     * @return boolean
     */
    public static boolean itemExists(CustomItem item) {
        return allItems.contains(item);
    }

    /**
     * itemWithNameExists takes in the name of an item, and checks if it matches a key existing in the itemMap HashMap
     * @param name
     * @return
     */
    public static boolean itemWithNameExists(String name) {
        return itemMap.containsKey(name);
    }

    /**
     * getItem retrieves an instance of CustomItem from the itemMap using the passed in name as the access key
     * @param name
     * @return CustomItem
     */
    public static CustomItem getItem(String name) {
        return itemMap.get(name);
    }

    public static List<CustomItem> getAllItems() {
        return allItems;
    }
}
