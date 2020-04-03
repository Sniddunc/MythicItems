package com.github.sniddunc.mythicitems.superclasses;

import com.github.sniddunc.mythicitems.ItemAPI;
import com.github.sniddunc.mythicitems.interfaces.ItemSetup;
import com.github.sniddunc.mythicitems.objects.BrewingRecipe;
import com.github.sniddunc.mythicitems.objects.CraftingRecipe;
import com.github.sniddunc.mythicitems.objects.CustomItem;
import com.github.sniddunc.mythicitems.objects.SourceChancePair;
import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.inventory.FurnaceRecipe;
import org.bukkit.plugin.Plugin;
import org.bukkit.potion.PotionEffect;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public abstract class ItemCreator implements ItemSetup {
    private ItemAPI itemAPI;

    private CustomItem result;

    public ItemCreator(ItemAPI itemAPI) {
        this.itemAPI = itemAPI;
    }

    public final CustomItem setup() {
        if (getName() == null) {
            notifyNull("name");
            return null;
        }

        CustomItem item = new CustomItem(itemAPI.getNamespace() + "/" + getName());

        if (getDisplayName() == null) {
            notifyNull("display name");
            return null;
        }

        if (getMaterial() == null) {
            notifyNull("material");
            return null;
        }

        item.setDisplayName(getDisplayName());
        item.setMaterial(getMaterial());

        if (getLore() != null) {
            item.setLore(getLore());
        }

        item.setGlowing(doesGlow());

        if (getPotionColor() != null) {
            item.setPotionColor(getPotionColor());
        }

        if (getMobDrops() != null) {
            item.setMobDrops(getMobDrops());
        }

        if (getBlockDrops() != null) {
            item.setBlockDrops(getBlockDrops());
        }

        // Add potion effects
        if (getPotionEffects() != null) {
            for (PotionEffect effect : getPotionEffects()) {
                item.addPotionEffect(effect.getType(), effect.getDuration(), effect.getAmplifier());
            }
        }

        // Store result
        result = item;

        /////////////////////////
        // SET UP RECIPES LAST
        // Crafting
        CraftingRecipe craftingRecipe = getCraftingRecipe();

        if (craftingRecipe != null) {
            item.setCraftingRecipe(craftingRecipe);
            itemAPI.getPlugin().getServer().getConsoleSender().sendMessage(ChatColor.RED + "Registering crafting recipe for " + getName());
            itemAPI.getPlugin().getServer().addRecipe(craftingRecipe.getRecipe());
        }

        // Furnace
        FurnaceRecipe furnaceRecipe = getFurnaceRecipe();
        if (furnaceRecipe != null) {
            itemAPI.getPlugin().getServer().addRecipe(furnaceRecipe);
        }

        // Brewing Recipe
        if (getBrewingRecipe() != null) {
            getBrewingRecipe().registerRecipe();
        }

        // Update result once recipes are setup
        result = item;

        // Register custom item
        itemAPI.registerItem(item);

        return item;
    }

    private void notifyNull(String value) {
        itemAPI.getPlugin().getServer().getConsoleSender().sendMessage(ChatColor.RED + itemAPI.getPlugin().getDescription().getName() +
                String.format(" tried to register a CustomItem with a null '%s'", value));
    }

    public final CustomItem getResult() {
        return result;
    }

//    @Override
//    public String getName() {
//        return null;
//    }
//
//    @Override
//    public Material getMaterial() {
//        return null;
//    }
//
//    @Override
//    public String getDisplayName() {
//        return null;
//    }
//
//    @Override
//    public List<String> getLore() {
//        return null;
//    }
//
//    @Override
//    public boolean doesGlow() {
//        return false;
//    }
//
//    @Override
//    public Color getPotionColor() {
//        return null;
//    }
//
//    @Override
//    public List<PotionEffect> getPotionEffects() {
//        return null;
//    }
//
//    @Override
//    public CraftingRecipe getCraftingRecipe() {
//        return null;
//    }
//
//    @Override
//    public FurnaceRecipe getFurnaceRecipe() {
//        return null;
//    }
//
//    @Override
//    public BrewingRecipe getBrewingRecipe() {
//        return null;
//    }
//
//    @Override
//    public HashMap<EntityType, SourceChancePair> getMobDrops() {
//        return new HashMap<>();
//    }
//
//    @Override
//    public HashMap<Material, SourceChancePair> getBlockDrops() {
//        return new HashMap<>();
//    }
}
