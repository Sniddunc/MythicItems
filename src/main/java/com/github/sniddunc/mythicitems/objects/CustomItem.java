package com.github.sniddunc.mythicitems.objects;

import com.github.sniddunc.mythicitems.MythicItems;
import com.github.sniddunc.mythicitems.enchantments.GlowEffect;
import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.EntityType;
import org.bukkit.inventory.FurnaceRecipe;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.potion.PotionEffect;

import java.nio.charset.StandardCharsets;
import java.util.*;

public class CustomItem {
    private String name;
    private String displayName;

    private List<String> lore;

    private Material material;

    private Map<Enchantment, Integer> enchantments;

    private List<PotionEffect> potionEffects;

    private ShapedRecipe craftingRecipe;
    private List<String> craftingPattern;
    private HashMap<Character, String> customIngredients;
    private boolean hasCustomCraftingIngredients;

    private FurnaceRecipe furnaceRecipe;

    private ItemStack brewingIngredient;
    private List<ItemStack> brewingBases;

    private HashMap<EntityType, ItemValuePair> mobDropMap;
    private boolean isDroppedByMobs;

    private HashMap<Material, ItemValuePair> blockDropMap;
    private boolean isDroppedByBlocks;

    private boolean canBeRenamed;
    private boolean isUnbreakable;
    private boolean isGlowing;

    private Color potionColor;

    /**
     * Only use this constructor from within MythicItems itself.
     * For API use, use the ItemAPI functionality to create items.
     * @param name
     */
    public CustomItem(String name) {
        this.name = name;
        displayName = "Unnamed";
        lore = new ArrayList<>();
        material = Material.DIRT;
        enchantments = new HashMap<>();
        potionEffects = new ArrayList<>();
        craftingRecipe = null;
        craftingPattern = new ArrayList<>();
        customIngredients = new HashMap<>();
        hasCustomCraftingIngredients = false;
        furnaceRecipe = null;
        brewingIngredient = null;
        brewingBases = new ArrayList<>();
        mobDropMap = new HashMap<>();
        isDroppedByMobs = false;
        blockDropMap = new HashMap<>();
        isDroppedByBlocks = false;
        canBeRenamed = false;
        isUnbreakable = false;
        isGlowing = false;
        potionColor = null;
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

    public Map<Enchantment, Integer> getEnchantments() {
        return enchantments;
    }

    public void addPotionEffect(PotionEffect effect) {
        potionEffects.add(effect);
    }

    public List<PotionEffect> getPotionEffects() {
        return potionEffects;
    }

    public ShapedRecipe getCraftingRecipe() {
        return craftingRecipe;
    }

    public void setCraftingRecipe(ShapedRecipe craftingRecipe) {
        this.craftingRecipe = craftingRecipe;
    }

    public List<String> getCraftingPattern() {
        return craftingPattern;
    }

    public void setCraftingPattern(List<String> pattern) {
        this.craftingPattern = pattern;
    }

    public void addCustomCraftingIngredient(char placeholder, String itemTag) {
        customIngredients.put(placeholder, itemTag);
        hasCustomCraftingIngredients = true;
    }

    public HashMap<Character, String> getCustomIngredients() {
        return customIngredients;
    }

    public boolean hasCustomIngredients() {
        return hasCustomCraftingIngredients;
    }

    public FurnaceRecipe getFurnaceRecipe() {
        return furnaceRecipe;
    }

    public void setFurnaceRecipe(FurnaceRecipe furnaceRecipe) {
        this.furnaceRecipe = furnaceRecipe;
    }

    public ItemStack getBrewingIngredient() {
        return brewingIngredient;
    }

    public List<ItemStack> getBrewingBases() {
        return brewingBases;
    }

    public void addBrewingBase(ItemStack base) {
        brewingBases.add(base);
    }

    public void setBrewingIngredient(ItemStack brewingIngredient) {
        this.brewingIngredient = brewingIngredient;
    }

    public void addMobDrop(EntityType type, int chance, int amount) {
        mobDropMap.put(type, new ItemValuePair(chance, amount));
        isDroppedByMobs = true;
    }

    public boolean isDroppedByMobs() {
        return isDroppedByMobs;
    }

    public boolean isDroppedByMob(EntityType type) {
        return mobDropMap.containsKey(type);
    }

    public int getMobDropChance(EntityType type) {
        return mobDropMap.get(type).getChance();
    }

    public int getMobDropAmount(EntityType type) {
        return mobDropMap.get(type).getAmount();
    }

    public HashMap<EntityType, ItemValuePair> getMobDrops() {
        return mobDropMap;
    }

    public void addBlockDrop(Material type, int chance, int amount) {
        blockDropMap.put(type, new ItemValuePair(chance, amount));
        isDroppedByBlocks = true;
    }

    public boolean isDroppedByBlocks() {
        return isDroppedByBlocks;
    }

    public boolean isDroppedByBlock(Material type) {
        return blockDropMap.containsKey(type);
    }

    public int getBlockDropChance(Material type) {
        return blockDropMap.get(type).getChance();
    }

    public int getBlockDropAmount(Material type) {
        return blockDropMap.get(type).getAmount();
    }

    public HashMap<Material, ItemValuePair> getBlockDrops() {
        return blockDropMap;
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

    public Color getPotionColor() {
        return potionColor;
    }

    public void setPotionColor(Color potionColor) {
        this.potionColor = potionColor;
    }

    public String getItemTag() {
        return Base64.getEncoder().encodeToString(name.getBytes());
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
        tempLore.add(ChatColor.BLACK + getItemTag());
        meta.setLore(tempLore);

        // Apply enchantments
        for (Enchantment enchantment : enchantments.keySet()) {
            meta.addEnchant(enchantment, enchantments.get(enchantment), true);
        }

        meta.setUnbreakable(isUnbreakable);

        item.setItemMeta(meta);

        // Apply potion effects if the item is a potion
        if (material.equals(Material.POTION)) {
            PotionMeta potionMeta = (PotionMeta) meta;

            for (PotionEffect effect : potionEffects) {
                potionMeta.addCustomEffect(effect, true);
            }

            // Also, apply the potionColor property if it's set
            potionMeta.setColor(potionColor);

            item.setItemMeta(potionMeta);
        }

        // If there are no enchantments and the item should still glow, we add our custom glow effect enchantment
        if (isGlowing && enchantments.size() == 0) {
            NamespacedKey key = new NamespacedKey(MythicItems.getInstance(), MythicItems.getInstance().getDescription().getName());
            GlowEffect glowEffect = new GlowEffect(key);
            meta.addEnchant(glowEffect, 1, true);
        }

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

    public static boolean itemExists(String name) {
        return itemMap.containsKey(name);
    }

    public static List<CustomItem> getAllItems() {
        return allItems;
    }

    /**
     * getItemByTag reads an item's lore and searches for a tag. If it finds one, it decodes it and returns
     * the CustomItem instance it's part of. Null is returned in case of error.
     * @param item The item to check
     * @return CustomItem
     */
    public static CustomItem getItemByTag(ItemStack item) {
        String tag = getTag(item);

        if (tag == null) {
            return null;
        }

        String itemName = null;

        try {
            itemName = new String(Base64.getDecoder().decode(tag.getBytes()), StandardCharsets.UTF_8);
        } catch (IllegalArgumentException e) {
            return null;
        }

        return CustomItem.getItem(itemName);
    }

    public static String getTag(ItemStack item) {
        if (item == null || !item.hasItemMeta() || !item.getItemMeta().hasLore()) {
            return null;
        }

        List<String> lore = item.getItemMeta().getLore();

        if (lore == null || lore.size() == 0) {
            return null;
        }

        // Extract what might be a tag from the last line of the lore
        String tag = ChatColor.stripColor(lore.get(lore.size() - 1));
//
//        // Ensure there is no padding ==
//        if (tag.substring(tag.length() - 2).equals("==")) {
//            tag = tag.substring(0, tag.length() - 2);
//        }

        return tag;
    }
}
