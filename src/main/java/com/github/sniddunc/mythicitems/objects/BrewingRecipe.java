package com.github.sniddunc.mythicitems.objects;

import com.github.sniddunc.mythicitems.ItemAPI;
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
 * Thank you :)
 */
public class BrewingRecipe {
    private static List<BrewingRecipe> recipes = new ArrayList<>();

    private ItemStack ingredient;
    private List<ItemStack> bases;
    private BrewAction action;

    public BrewingRecipe(ItemStack ingredient, List<ItemStack> bases, BrewAction action) {
        this.ingredient = ingredient;
        this.bases = bases;
        this.action = action;

        recipes.add(this);
    }

    public ItemStack getIngredient() {
        return ingredient;
    }

    public BrewAction getBrewAction() {
        return action;
    }

    public List<ItemStack> getBases() {
        return bases;
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
            if (brewTime <= 0) {
                inventory.setIngredient(new ItemStack(Material.AIR));

                for (int i = 0; i < 3; i++) {
                    if (inventory.getItem(i) == null || inventory.getItem(i).getType() == Material.AIR) {
                        continue;
                    }

                    emptyBrewingBases(inventory);

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

    // STATIC
    public static BrewingRecipe getRecipe(BrewerInventory inventory) {
        boolean empty = true;

        for (int i = 0; i < 3; i++) {
            ItemStack item = inventory.getItem(i);

            if (item == null || item.getType() == Material.AIR) {
                continue;
            }

            empty = false;
            break;
        }

        if (empty) {
            return null;
        }

//        int inc = 0;
        for (BrewingRecipe recipe : recipes) {
//            inc++;
            if (inventory.getIngredient().getType() == Material.POTION) {
//                Bukkit.broadcastMessage(String.format("[%s] Comparing potions", inc));
                if (ItemAPI.Utils.potionsAreEqual(inventory.getIngredient(), recipe.getIngredient()) && basesMatch(inventory, recipe.getBases())) {
                    return recipe;
                }

                continue;
            }

            if (inventory.getIngredient().isSimilar(recipe.getIngredient()) && basesMatch(inventory, recipe.getBases())) {
                return recipe;
            }
        }

        return null;
    }

    public static boolean basesMatch(BrewerInventory inventory, List<ItemStack> bases) {
        // Keep track of slots with valid bases so we don't risk treating 1 base value as 3, for example.
        List<Integer> slotsUsed = new ArrayList<>();

        for (ItemStack base : bases) {
            boolean match = false;

            for (int slotId = 0; slotId < 3; slotId++) {
                if (slotsUsed.contains(slotId)) {
                    continue;
                }

                if (base.isSimilar(inventory.getItem(slotId))) {
                    // Mark this slot as used, set match to true and break out of the loop since we found a match
                    // to the current base being checked.
                    slotsUsed.add(slotId);
                    match = true;
                    break;
                }
            }

            if (!match) {
                return false;
            }
        }

        return true;
    }

    public static void emptyBrewingBases(BrewerInventory inventory) {
        for (int slotId = 0; slotId < 3; slotId++) {
            inventory.setItem(slotId, new ItemStack(Material.AIR));
        }
    }
}
