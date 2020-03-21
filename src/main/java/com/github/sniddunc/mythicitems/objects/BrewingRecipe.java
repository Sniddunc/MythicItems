package com.github.sniddunc.mythicitems.objects;

import com.github.sniddunc.mythicitems.MythicItems;
import com.github.sniddunc.mythicitems.interfaces.BrewAction;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.BrewingStand;
import org.bukkit.inventory.BrewerInventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Based on the code provided by NacOJerk on
 * https://www.spigotmc.org/threads/how-to-make-custom-potions-and-brewing-recipes.211002/
 */
public class BrewingRecipe {
    private static List<BrewingRecipe> recipes = new ArrayList<>();

    private ItemStack ingredient;
    private BrewAction action;

    public BrewingRecipe(ItemStack ingredient, BrewAction action) {
        this.ingredient = ingredient;
        this.action = action;

        recipes.add(this);
    }

    public BrewingRecipe(Material ingredient, BrewAction action) {
        this(new ItemStack(ingredient), action);
    }

    public ItemStack getIngredient() {
        return ingredient;
    }

    public BrewAction getBrewAction() {
        return action;
    }

    public static BrewingRecipe getRecipe(BrewerInventory inventory) {
        boolean notAllAir = false;

        for (int i = 0 ; i < 3 && !notAllAir ; i++) {
            if (inventory.getItem(i) == null) {
                continue;
            }

            if (inventory.getItem(i).getType() == Material.AIR) {
                continue;
            }

            notAllAir = true;
        }

        if (!notAllAir) {
            return null;
        }

        for (BrewingRecipe recipe : recipes) {
            if (inventory.getIngredient().getType() == recipe.getIngredient().getType()) {
                return recipe;
            }

            if (inventory.getIngredient().isSimilar(recipe.getIngredient())) {
                return recipe;
            }
        }

        return null;
    }

    public void startBrewing(BrewerInventory inventory) {
        new BrewClock(this, inventory);
    }

    private class BrewClock extends BukkitRunnable {
        private BrewerInventory inventory;
        private BrewingRecipe recipe;
        private ItemStack ingredient;
        private BrewingStand stand;
        private int brewTime;
        private ItemStack[] startContents;

        public BrewClock(BrewingRecipe recipe, BrewerInventory inventory) {
            this.recipe = recipe;
            this.inventory = inventory;
            this.ingredient = inventory.getIngredient();
            this.stand = inventory.getHolder();
            this.brewTime = 400;
            startContents = inventory.getContents();

            runTaskTimer(MythicItems.getInstance(), 1L, 1L);
        }

        @Override
        public void run() {
            if (brewTime == 0) {
                inventory.setIngredient(new ItemStack(Material.AIR));

                for (int i = 0; i < 3; i++) {
                    if (inventory.getItem(i) == null || inventory.getItem(i).getType() == Material.AIR) {
                        continue;
                    }

                    recipe.getBrewAction().brew(inventory, inventory.getItem(i), ingredient);
                }

                cancel();
                return;
            }

            if (!Arrays.equals(inventory.getContents(), startContents)) {
                cancel();
                return;
            }

            brewTime--;
            stand.setBrewingTime(brewTime);
            stand.update(true);
        }
    }
}
