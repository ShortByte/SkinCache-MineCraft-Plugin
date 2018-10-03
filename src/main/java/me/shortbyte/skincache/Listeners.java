package me.shortbyte.skincache;

import me.shortbyte.skincache.managers.RedisManager;
import me.shortbyte.skincache.misc.ItemBuilder;
import me.shortbyte.skincache.misc.Skin;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.ItemStack;

/**
 *
 * @author LeonEnkn
 *
 * Copyright (c) 2015 - 2017 by ShortByte.de to present. All rights reserved.
 */
public class Listeners implements Listener {

    private final RedisManager manager;

    public Listeners(RedisManager manager) {
        this.manager = manager;
    }
    
    @EventHandler
    public void onPlayerJoinEvent(PlayerJoinEvent event) {
        final Player player = event.getPlayer();
        final String uuid = player.getUniqueId().toString();
        
        Skin skin = manager.getSkin(uuid);
        if (skin == null) {
            skin = Skin.fromMojang(uuid);
        }
        if (skin == null) {
            player.sendMessage("Leider kein Kopf für dich :'(");
            return;
        }
        
        try {
            final ItemStack stack = new ItemBuilder(Material.SKULL_ITEM, 1, (short) 3)
                .setDisplayName("&a&l" + player.getName())
                .setSkullOwner(skin)
                .build();
            
            player.sendMessage("Name: " + skin.getName());
            player.sendMessage("Value: " + skin.getValue());
            player.sendMessage("Signature: " + skin.getSignature());
            player.getInventory().addItem(stack);
        } catch (IllegalStateException e) {
            player.sendMessage("Leider kein Kopf für dich :'(");
        }
    }
}
