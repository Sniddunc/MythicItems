package com.github.sniddunc.mythicitems.listeners;

import com.github.sniddunc.mythicitems.objects.CustomItem;
import org.bukkit.Bukkit;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.inventory.ItemStack;

import java.util.Random;

public class MobListeners implements Listener {

    private Random randy;

    public MobListeners() {
        randy = new Random();
    }

    @EventHandler
    public void onMobKill(EntityDeathEvent event) {
        LivingEntity victim = event.getEntity();

        // Ignore mobs that weren't killed by players and mobs that were spawned from spawners
        if (victim.getKiller() == null || victim.fromMobSpawner()) {
            return;
        }

        EntityType type = victim.getType();

        for (CustomItem item : CustomItem.getAllItems()) {
            if (!item.isDroppedByMobs()) {
                continue;
            }

            if (!item.isDroppedByMob(type)) {
                continue;
            }

            int chance = item.getMobDropChance(type);
            int rand = randy.nextInt(100);

            if (rand <= chance) {
                ItemStack result = item.build();
                result.setAmount(item.getMobDropAmount(type));

                event.getDrops().add(result);
            }
        }
    }
}
