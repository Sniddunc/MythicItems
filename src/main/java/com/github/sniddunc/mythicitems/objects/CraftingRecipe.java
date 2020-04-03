package com.github.sniddunc.mythicitems.objects;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapedRecipe;

import java.util.HashMap;

public class CraftingRecipe {
    private ShapedRecipe recipe;
    private HashMap<Character, String> customIngredients;

    public CraftingRecipe(NamespacedKey key, ItemStack result) {
        recipe = new ShapedRecipe(key, result);
        customIngredients = new HashMap<>();
    }

    public void setShape(String[] shape) {
        recipe.shape(shape);
    }

    public void setIngredient(char placeholder, Material ingredient) {
        recipe.setIngredient(placeholder, ingredient);
    }

    public void setCustomIngredient(char placeholder, String customIngredientName, Material material) {
        recipe.setIngredient(placeholder, material);
        customIngredients.put(placeholder, customIngredientName);
    }

    public HashMap<Character, String> getCustomIngredients() {
        return customIngredients;
    }

    public boolean hasCustomIngredients() {
        return customIngredients.size() > 0;
    }

    public String[] getShape() {
        return recipe.getShape();
    }

    public ShapedRecipe getRecipe() {
        return recipe;
    }
}
