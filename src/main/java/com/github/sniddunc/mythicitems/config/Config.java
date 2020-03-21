package com.github.sniddunc.mythicitems.config;

import com.github.sniddunc.mythicitems.MythicItems;
import com.github.sniddunc.mythicitems.objects.CustomItem;
import com.github.sniddunc.mythicitems.objects.ItemValuePair;
import org.bukkit.*;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.enchantments.EnchantmentWrapper;
import org.bukkit.entity.EntityType;
import org.bukkit.inventory.FurnaceRecipe;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.RecipeChoice;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
            // POTION EFFECT PARSING
            ConfigurationSection effectSection = config.getConfigurationSection(path + "effects");

            if (effectSection != null) {
                for (String effectName : effectSection.getKeys(false)) {
                    PotionEffectType effectType = PotionEffectType.getByName(effectName);

                    if (effectType == null) {
                        plugin.getConsole().sendMessage(ChatColor.RED + String.format(
                                "An invalid potion effect '%s' was provided for item '%s'",
                                effectName,
                                itemName
                        ));
                        continue;
                    }

                    int level = effectSection.getInt(effectName + ".level", 0);
                    int duration = effectSection.getInt(effectName + ".duration", 0);

                    PotionEffect effect = new PotionEffect(effectType, duration * 20, level);

                    item.addPotionEffect(effect);
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

                    int chance = mobDropSection.getInt(entityString + ".chance", 0);
                    int amount = mobDropSection.getInt(entityString + ".amount", 0);

                    item.addMobDrop(type, chance, amount);
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

                    int chance = blockDropSection.getInt(blockMatString + ".chance", 0);
                    int amount = blockDropSection.getInt(blockMatString + ".amount", 0);

                    item.addBlockDrop(blockMaterial, chance, amount);
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

                // Potion color property
                String potionColorString = propertiesSection.getString("potionColor", "100,100,100");
                assert potionColorString != null;
                String[] split = potionColorString.split(",");

                int r, g, b;
                r = Integer.parseInt(split[0]);
                g = Integer.parseInt(split[1]);
                b = Integer.parseInt(split[2]);

                item.setPotionColor(Color.fromRGB(r, g, b));

                plugin.getConsole().sendMessage("Potion color: " + r + "," + b + "," + g);
            }

            ///////////////////////////////
            // CRAFTING RECIPE PARSING
            // (should be done last)
            ConfigurationSection craftingSection = config.getConfigurationSection(path + "recipe.crafting");

            if (craftingSection != null) {
                List<String> pattern = craftingSection.getStringList("pattern");

                if (pattern.size() == 3) {
                    NamespacedKey recipeKey = new NamespacedKey(plugin, itemName + "-crafting");
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

                            // Check if this matString is actually a custom item
                            String[] split = placeholderMatString.split("/");
                            if (split.length == 2 && split[0].equals("custom")) {
                                CustomItem customIngredient = CustomItem.getItem(split[1]);

                                // Check if custom item exists
                                if (customIngredient == null) {
                                    plugin.getConsole().sendMessage(ChatColor.RED + String.format(
                                            "Custom item '%s' provided as a material in the crafting recipe for item '%s' is invalid",
                                            split[1],
                                            itemName
                                    ));

                                    plugin.getConsole().sendMessage(ChatColor.RED + String.format(
                                            "Make sure '%s' is loaded before item '%s'",
                                            split[1],
                                            itemName
                                    ));

                                    continue;
                                }

                                item.addCustomCraftingIngredient(placeholder.charAt(0), customIngredient.getItemTag());

                                plugin.getConsole().sendMessage(customIngredient.getItemTag() + " is the tag for " + itemName);

                                plugin.getConsole().sendMessage(ChatColor.GRAY + String.format(
                                        "> depends on '%s'",
                                        split[1]
                                ));

                                Material placeholderMaterial = customIngredient.getMaterial();
                                recipe.setIngredient(placeholder.charAt(0), placeholderMaterial);
                            }

                            else {
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
                        }

                        // Everything's valid! Register recipe on server
                        item.setCraftingPattern(pattern);
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

            ///////////////////////////////
            // FURNACE RECIPE PARSING
            // (should be done last)
            ConfigurationSection furnaceSection = config.getConfigurationSection(path + "recipe.furnace");

            if (furnaceSection != null) {
                String inputString = furnaceSection.getString("input", null);

                Material inputMaterial = Material.getMaterial(inputString);

                if (inputMaterial != null) {
                    int exp = furnaceSection.getInt("exp", 0);
                    int cookTime = furnaceSection.getInt("cookTime", 20);

                    NamespacedKey recipeKey = new NamespacedKey(plugin, itemName + "-smelting");
                    FurnaceRecipe recipe = new FurnaceRecipe(recipeKey, item.build(), new RecipeChoice.MaterialChoice(inputMaterial), exp, cookTime);

                    item.setFurnaceRecipe(recipe);
                    plugin.getServer().addRecipe(recipe);
                }

                else {
                    plugin.getConsole().sendMessage(ChatColor.RED + String.format(
                            "Material provided '%s' as the furnace input for item '%s' is invalid",
                            inputString,
                            itemName
                    ));
                }
            }

            // Register item to list
            CustomItem.registerNewItem(item);
            plugin.getConsole().sendMessage(ChatColor.GREEN + String.format("Registered item '%s'", itemName));
        }
    }

    public static void reload() {
        MythicItems.getInstance().reloadConfig();
        init();
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

    public static void saveCustomItem(CustomItem item) {
        FileConfiguration config = MythicItems.getInstance().getConfig();

//        this.name = name;
//        displayName = "Unnamed";
//        lore = new ArrayList<>();
//        material = Material.DIRT;
//        enchantments = new HashMap<>();
//        craftingRecipe = null;
//        craftingPattern = new ArrayList<>();
//        customIngredients = new HashMap<>();
//        hasCustomIngredients = false;
//        mobDropMap = new HashMap<>();
//        isDroppedByMobs = false;
//        blockDropMap = new HashMap<>();
//        isDroppedByBlocks = false;
//        canBeRenamed = false;
//        isUnbreakable = false;
//        isGlowing = false;

        String path = "items." + item.getName();

        // Save displayName material and lore
        config.set(path + ".displayName", item.getDisplayName());
        config.set(path + ".material", item.getMaterial().toString());
        config.set(path + ".lore", item.getLore());

        // Save enchantments
        Map<Enchantment, Integer> enchantments = item.getEnchantments();

        for (Enchantment enchant : enchantments.keySet()) {
            config.set(path + ".enchantments." + enchant.getKey().getKey(), enchantments.get(enchant));
        }

        // Save potion effect
        for (PotionEffect effect : item.getPotionEffects()) {
            String effectName = effect.getType().getName();

            config.set(path + ".effects." + effectName + ".level", effect.getAmplifier());
            config.set(path + ".effects." + effectName + ".duration", effect.getDuration());
        }

        // Save crafting recipe
        config.set(path + ".recipe.crafting.pattern", item.getCraftingPattern());

        // Write basic materials first, and then update them to any custom values
        Map<Character, ItemStack> ingredientMap =  item.getCraftingRecipe().getIngredientMap();

        for (char placeholder : ingredientMap.keySet()) {
            config.set(path + ".recipe.crafting.materials." + placeholder, ingredientMap.get(placeholder).getType().toString());
        }

        if (item.hasCustomIngredients()) {
            // Update to any custom values
            Map<Character, String> customIngredientMap = item.getCustomIngredients();

            for (char placeholder : customIngredientMap.keySet()) {
                config.set(path + ".recipe.crafting.materials." + placeholder, "custom/" + ingredientMap.get(placeholder).getType().toString());
            }
        }

        // Save furnace recipe
        config.set(path + ".recipe.furnace.input", item.getFurnaceRecipe().getInput().getType().toString());
        config.set(path + ".recipe.furnace.exp", item.getFurnaceRecipe().getExperience());
        config.set(path + ".recipe.furnace.cookTime", item.getFurnaceRecipe().getCookingTime());

        // Save mob drops
        HashMap<EntityType, ItemValuePair> mobDrops = item.getMobDrops();

        for (EntityType entity : mobDrops.keySet()) {
            config.set(path + ".drops.mobs." + entity.toString() + ".chance", mobDrops.get(entity).getChance());
            config.set(path + ".drops.mobs." + entity.toString() + ".amount", mobDrops.get(entity).getAmount());
        }

        // Save block drops
        HashMap<Material, ItemValuePair> blockDrops = item.getBlockDrops();

        for (Material material : blockDrops.keySet()) {
            config.set(path + ".drops.blocks." + material.toString() + ".chance", blockDrops.get(material).getChance());
            config.set(path + ".drops.blocks." + material.toString() + ".amount", blockDrops.get(material).getAmount());
        }

        // Save misc. properties
        config.set(path + ".properties.canBeRenamed", item.canBeRenamed());
        config.set(path + ".properties.isGlowing", item.isGlowing());
        config.set(path + ".properties.isUnbreakable", item.isUnbreakable());

        // Save potion color property
        Color potionColor = item.getPotionColor();

        if (potionColor != null) {
            config.set(path + ".properties.potionColor", String.format(
                    "%s,%s,%s",
                    potionColor.getRed(),
                    potionColor.getGreen(),
                    potionColor.getBlue()
            ));
        }

        // Save to file
        saveConfig(config);
    }
}
