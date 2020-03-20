package com.github.sniddunc.mythicitems.enchantments;

import com.github.sniddunc.mythicitems.MythicItems;
import org.bukkit.NamespacedKey;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.enchantments.EnchantmentTarget;
import org.bukkit.inventory.ItemStack;

public class GlowEffect extends Enchantment {
    public GlowEffect(NamespacedKey key) {
        super(key);
    }

    @Override
    @Deprecated
    public String getName() {
        return "gloweffect";
    }

    @Override
    public boolean canEnchantItem(ItemStack itemStack) {
        return false;
    }

    @Override
    public boolean conflictsWith(Enchantment enchantment) {
        return false;
    }

    @Override
    public EnchantmentTarget getItemTarget() {
        return EnchantmentTarget.ALL;
    }

    @Override
    public int getMaxLevel() {
        return 1;
    }

    @Override
    public int getStartLevel() {
        return 0;
    }

    @Override
    public boolean isTreasure() {
        return true;
    }

    @Override
    @Deprecated
    public boolean isCursed() {
        return false;
    }
}
