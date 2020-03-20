package com.github.sniddunc.mythicitems.listeners;

import com.github.sniddunc.mythicitems.objects.CustomItem;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;

import java.util.Random;

public class BlockListeners implements Listener {

    private Random randy;

    public BlockListeners() {
        randy = new Random();
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        Block block = event.getBlock();
        Material type = event.getBlock().getType();

        for (CustomItem item : CustomItem.getAllItems()) {
            if (!item.isDroppedByMobs()) {
                continue;
            }

            if (!item.isDroppedByBlock(type)) {
                continue;
            }

            int chance = item.getBlockDropChance(type);
            int rand = randy.nextInt(100);

            Location blockLoc = event.getBlock().getLocation();

            if (rand <= chance) {
                // We can't directly modify the drops since it's not mutable, so we have to cancel the event, remove
                // the block and drop the drops ourselves.
                event.setCancelled(true);
                block.breakNaturally();

                for (ItemStack drop : block.getDrops()) {
                    blockLoc.getWorld().dropItemNaturally(blockLoc, drop);
                }

                blockLoc.getWorld().dropItemNaturally(blockLoc, item.build());
            }
        }
    }
}
