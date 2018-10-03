package me.shortbyte.skincache;

import me.shortbyte.skincache.managers.RedisManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.configuration.file.FileConfiguration;

/**
 *
 * @author LeonEnkn
 *
 * Copyright (c) 2015 - 2017 by ShortByte.de to present. All rights reserved.
 */
public class SkinCache extends JavaPlugin {

    
    @Override
    public void onEnable() {
        this.saveDefaultConfig();
        final FileConfiguration conf = this.getConfig();
        final RedisManager manager = new RedisManager(conf);
        final Listeners listeners = new Listeners(manager);
        
        getServer().getPluginManager().registerEvents(listeners, this);
    }
}
