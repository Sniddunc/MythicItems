package com.github.sniddunc.mythicitems.config;

import com.github.sniddunc.mythicitems.MythicItems;
import com.github.sniddunc.mythicitems.objects.CustomItem;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.enchantments.EnchantmentWrapper;
import org.bukkit.entity.EntityType;
import org.bukkit.inventory.ShapedRecipe;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Config {
    public static void init() {
        MythicItems plugin = MythicItems.getInstance();
        FileConfiguration config = MythicItems.getInstance().getConfig();

        ConfigurationSection itemSection = config.getConfigurationSection("items");

        if (itemSection == null) {
            plugin.getConsole().sendMessage(ChatColor.RED + "Items section could not found in config");
            return;
        }

        String path = "items";

        for (String itemName : itemSection.getKeys(false)) {
            path = "items." + itemName + ".";

            plugin.getConsole().sendMessage(ChatColor.GRAY + String.format("Loading item '%s'...", itemName));

            ///////////////////////////////
            // MATERIAL PARSING
            String materialString = config.getString(path + "material", null);

            // If no material value is set for the item, we consider it invalid and skip it
            if (materialString == null) {
                plugin.getConsole().sendMessage(ChatColor.RED + String.format("Item '%s' does not have a valid material set. Skipping...", itemName));
                continue;
            }

            Material material = Material.getMaterial(materialString);

            if (material == null) {
                plugin.getConsole().sendMessage(ChatColor.RED + String.format("Item '%s' does not have a valid material set. Skipping...", itemName));
                continue;
            }

            ///////////////////////////////
            // DISPLAY NAME PARSING
            String displayName = config.getString(path + "displayName", "Unnamed Item");

            ///////////////////////////////
            // LORE PARSING
            List<String> lore = config.getStringList(path + "lore");

            // Start building the item
            CustomItem item = new CustomItem(itemName);
            item.setMaterial(material);
            item.setDisplayName(displayName);
            item.setLore(lore);

            ///////////////////////////////
            // ENCHANTMENT PARSING
            ConfigurationSection enchantSection = config.getConfigurationSection(path + "enchantments");

            if (enchantSection != null) {
                for (String enchantName : enchantSection.getKeys(false)) {
                    Enchantment enchant = EnchantmentWrapper.getByKey(NamespacedKey.minecraft(enchantName));

                    if (enchant == null) {
                        plugin.getConsole().sendMessage(ChatColor.RED + String.format(
                                "An invalid enchantment '%s' was provided for item '%s'",
                                enchantName,
                                itemName
                        ));
                        continue;
                    }

                    int level = config.getInt(path + "enchantments." + enchantName, 0);

                    item.addEnchantment(enchant, level);
                }
            }

            ///////////////////////////////
            // MOB DROP PARSING
            // (must be done after the item itself is setup)
            ConfigurationSection mobDropSection = config.getConfigurationSection(path + "drops.mobs");

            if (mobDropSection != null) {
                for (String entityString : mobDropSection.getKeys(false)) {
                    EntityType type;

                    try {
                        type = EntityType.valueOf(entityString);
                    } catch (Exception e) {
                        type = null;
                    }

                    if (type == null) {
                        plugin.getConsole().sendMessage(ChatColor.RED + String.format(
                                "Entity '%s' provided for mob drops of item '%s' is invalid",
                                entityString,
                                itemName
                        ));

                        continue;
                    }

                    int chance = mobDropSection.getInt(entityString, 0);

                    item.addMobDrop(type, chance);
                }
            }

            ///////////////////////////////
            // BLOCK DROP PARSING
            // (must be done after the item itself is setup)
            ConfigurationSection blockDropSection = config.getConfigurationSection(path + "drops.blocks");

            if (blockDropSection != null) {
                for (String blockMatString : blockDropSection.getKeys(false)) {
                    Material blockMaterial = Material.getMaterial(blockMatString);

                    if (blockMaterial == null) {
                        plugin.getConsole().sendMessage(ChatColor.RED + String.format(
                                "Material '%s' provided for block drops of item '%s' is invalid",
                                blockMatString,
                                itemName
                        ));

                        continue;
                    }

                    int chance = blockDropSection.getInt(blockMatString, 0);

                    item.addBlockDrop(blockMaterial, chance);
                }
            }

            ///////////////////////////////
            // PROPERTIES PARSING
            // (must be done after the item itself is setup)
            ConfigurationSection propertiesSection = config.getConfigurationSection(path + "properties");

            if (propertiesSection != null) {
                item.setCanBeRenamed(propertiesSection.getBoolean("canBeRenamed", false));
                item.setUnbreakable(propertiesSection.getBoolean("isUnbreakable", false));
                item.setGlowing(propertiesSection.getBoolean("isGlowing", false));
            }

            ///////////////////////////////
            // CRAFTING RECIPE PARSING
            // (should be done last)
            ConfigurationSection craftingSection = config.getConfigurationSection(path + "recipe.crafting");

            if (craftingSection != null) {
                List<String> pattern = craftingSection.getStringList("pattern");

                if (pattern.size() == 3) {
                    NamespacedKey recipeKey = new NamespacedKey(plugin, itemName);
                    ShapedRecipe recipe = new ShapedRecipe(recipeKey, item.build());

                    // Set crafting pattern
                    recipe.shape(pattern.toArray(new String[0]));

                    ConfigurationSection materialSection = config.getConfigurationSection(path + "recipe.crafting.materials");

                    if (materialSection != null) {
                        for (String placeholder : materialSection.getKeys(false)) {
                            String placeholderMatString = materialSection.getString(placeholder, "");
                            // We can assert this to get rid of editor warnings since placeholderMatString
                            // will never be null due to it having a default value set.
                            assert placeholderMatString != null;
                            Material placeholderMaterial = Material.getMaterial(placeholderMatString);

                            if (placeholderMaterial == null) {
                                plugin.getConsole().sendMessage(ChatColor.RED + String.format(
                                        "Material provided '%s' in the crafting pattern for item '%s' is invalid",
                                        placeholder,
                                        itemName
                                ));

                                continue;
                            }

                            recipe.setIngredient(placeholder.charAt(0), placeholderMaterial);
                        }

                        // Everything's valid! Register recipe on server
                        item.setCraftingRecipe(recipe);
                        plugin.getServer().addRecipe(recipe);
                    }

                    else {
                        plugin.getConsole().sendMessage(ChatColor.RED + String.format(
                                "A crafting pattern for item '%s' was provided, but the crafting materials weren't",
                                itemName
                        ));
                    }
                }

                else {
                    plugin.getConsole().sendMessage(ChatColor.RED + String.format(
                            "Crafting pattern for '%s' is invalid",
                            itemName
                    ));
                }
            }

            // Register item to list
            CustomItem.registerNewItem(item);
            plugin.getConsole().sendMessage(ChatColor.GREEN + String.format("> Registered item '%s'", itemName));
        }
    }

    public static void reload() {
        MythicItems.getInstance().reloadConfig();
    }

    public static File getConfigFile() {
        return new File(MythicItems.getInstance().getDataFolder(), "config.yml");
    }

    public static void saveConfig(FileConfiguration config) {
        try {
            config.save(getConfigFile());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
