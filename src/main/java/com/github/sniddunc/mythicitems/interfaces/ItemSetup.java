package com.github.sniddunc.mythicitems.interfaces;

import com.github.sniddunc.mythicitems.objects.BrewingRecipe;
import com.github.sniddunc.mythicitems.objects.CraftingRecipe;
import com.github.sniddunc.mythicitems.objects.SourceChancePair;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.inventory.FurnaceRecipe;
import org.bukkit.potion.PotionEffect;

import java.util.HashMap;
import java.util.List;

public interface ItemSetup {
    String getName();
    Material getMaterial();
    String getDisplayName();
    List<String> getLore();
    boolean doesGlow();
    Color getPotionColor();
    List<PotionEffect> getPotionEffects();
    CraftingRecipe getCraftingRecipe();
    FurnaceRecipe getFurnaceRecipe();
    BrewingRecipe getBrewingRecipe();
    HashMap<EntityType, SourceChancePair> getMobDrops();
    HashMap<Material, SourceChancePair> getBlockDrops();
}
