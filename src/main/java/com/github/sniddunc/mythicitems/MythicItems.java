package com.github.sniddunc.mythicitems;

import com.github.sniddunc.mythicitems.commands.MythicItemsCommand;
import com.github.sniddunc.mythicitems.config.Config;
import com.github.sniddunc.mythicitems.enchantments.GlowEffect;
import com.github.sniddunc.mythicitems.listeners.BlockListeners;
import com.github.sniddunc.mythicitems.listeners.CraftListeners;
import com.github.sniddunc.mythicitems.listeners.MobListeners;
import org.bukkit.NamespacedKey;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.java.JavaPlugin;

import java.lang.reflect.Field;

public final class MythicItems extends JavaPlugin {

    private static MythicItems instance;

    private PluginDescriptionFile pdf;
    private ConsoleCommandSender console;

    @Override
    public void onEnable() {
        instance = this;

        pdf = this.getDescription();
        console = this.getServer().getConsoleSender();

        if  (!this.getDataFolder().exists()) {
            this.getDataFolder().mkdirs();
        }

        // Config setup
        getConfig().options().copyDefaults(true);
        saveConfig();
        Config.init();

        // Register custom glow effect
        registerGlowEffect();

        // Command executor setup
        getCommand("mythicitems").setExecutor(new MythicItemsCommand());

        // Listener setup
        getServer().getPluginManager().registerEvents(new MobListeners(), this);
        getServer().getPluginManager().registerEvents(new BlockListeners(), this);
        getServer().getPluginManager().registerEvents(new CraftListeners(), this);
    }

    public void registerGlowEffect() {
        try {
            Field f = Enchantment.class.getDeclaredField("acceptingNew");
            f.setAccessible(true);
            f.set(null, true);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        try {
            NamespacedKey key = new NamespacedKey(MythicItems.getInstance(), MythicItems.getInstance().getDescription().getName());

            Enchantment.registerEnchantment(new GlowEffect(key));
        }
        catch (IllegalArgumentException e){
        }
        catch(Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }

    public ConsoleCommandSender getConsole() {
        return console;
    }

    public static MythicItems getInstance() {
        return instance;
    }
}
