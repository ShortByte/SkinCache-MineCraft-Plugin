package me.shortbyte.skincache.misc;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import java.lang.reflect.Field;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;

/**
 *
 * @author LeonEnkn
 *
 * Copyright (c) 2015 - 2017 by ShortByte.de to present. All rights reserved.
 */
public class ItemBuilder {

    private static Class<?> skullMetaClass;
    
    static {
        final String version = Bukkit.getServer().getClass().getPackage().getName().split("\\.")[3];
        try {
            skullMetaClass = Class.forName("org.bukkit.craftbukkit." + version + ".inventory.CraftMetaSkull");
        } catch (ClassNotFoundException e) {
            Logger.getLogger(ItemBuilder.class.getName()).log(Level.SEVERE, null, e);
            skullMetaClass = null;
        }
    }

    private final ItemStack itemStack;

    public ItemBuilder(Material material) {
        this.itemStack = new ItemStack(material);
    }

    public ItemBuilder(Material material, int amount) {
        this.itemStack = new ItemStack(material, amount);
    }

    public ItemBuilder(Material material, int amount, short damage) {
        this.itemStack = new ItemStack(material, amount, damage);
    }

    public ItemBuilder setDisplayName(String name) {
        ItemMeta meta = itemStack.getItemMeta();
        meta.setDisplayName(ChatColor.translateAlternateColorCodes('&', name));
        itemStack.setItemMeta(meta);
        return this;
    }

    public ItemBuilder setSkullOwner(Skin skin) throws IllegalStateException {
        if (skin.getName() != null && skin.getValue() != null) {
            if (itemStack.getType() == Material.SKULL_ITEM) {
                if (skullMetaClass == null) {
                    throw new IllegalStateException("Can not access Skull Meta Data");
                }
                SkullMeta skullMeta = (SkullMeta) itemStack.getItemMeta();
                GameProfile gameProfile = new GameProfile(UUID.fromString(skin.getUuid()), "skin");
                gameProfile.getProperties().clear();
                gameProfile.getProperties().put(skin.getName(), new Property(skin.getName(), skin.getValue(), skin.getSignature()));

                try {
                    Field field = skullMetaClass.getDeclaredField("profile");
                    field.setAccessible(true);
                    field.set(skullMeta, gameProfile);
                } catch (IllegalArgumentException | IllegalAccessException | NoSuchFieldException | SecurityException ex) {
                    Logger.getLogger(ItemBuilder.class.getName()).log(Level.SEVERE, null, ex);
                    throw new IllegalStateException("Can not access Skull Meta Data");
                }
                itemStack.setItemMeta(skullMeta);
            }
        }
        return this;
    }

    public ItemStack build() {
        return itemStack;
    }

}
