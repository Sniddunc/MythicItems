package com.github.sniddunc.mythicitems.listeners;

import com.github.sniddunc.mythicitems.objects.CustomItem;
import org.bukkit.Bukkit;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;

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

        for (CustomItem item : CustomItem.getAllItems()) {
            if (!item.isDroppedByMobs()) {
                continue;
            }

            if (!item.isDroppedByMob(victim.getType())) {
                continue;
            }

            int chance = item.getMobDropChance(victim.getType());
            int rand = randy.nextInt(100);

            if (rand <= chance) {
                event.getDrops().add(item.build());
            }
        }
    }
}
