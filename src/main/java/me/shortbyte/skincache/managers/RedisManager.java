package me.shortbyte.skincache.managers;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.logging.Level;
import java.util.logging.Logger;
import me.shortbyte.skincache.misc.Skin;
import redis.clients.jedis.Jedis;
import org.bukkit.configuration.file.FileConfiguration;
import org.json.simple.parser.ParseException;

/**
 *
 * @author LeonEnkn
 *
 * Copyright (c) 2015 - 2017 by ShortByte.de to present. All rights reserved.
 */
public class RedisManager {

    private final FileConfiguration config;

    private Jedis jedis;

    public RedisManager(FileConfiguration config) {
        this.config = config;
        init();
    }

    private void init() {
        final String url = this.config.getString("redis.url", null);

        if (url == null) {
            final String host = this.config.getString("redis.host", "localhost").trim();
            final int port = this.config.getInt("redis.port", 6379);
            final String password = this.config.getString("redis.password", null);
            final int timeout = this.config.getInt("redis.timeout", 10000);
            final int db = this.config.getInt("redis.db", 0);

            jedis = new Jedis(host, port, timeout);
            if (password != null) {
                jedis.auth(password);
            }
            jedis.select(db);
        } else {
            try {
                jedis = new Jedis(new URI(url));
            } catch (URISyntaxException ex) {
                Logger.getLogger(RedisManager.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

        jedis.connect();

        System.out.println("  >> Redis connected!");
    }

    private String getSkin(String uuid, int retrys) {
        if (retrys == 0) {
            return null;
        }
        try {
            return jedis.get("skin:" + uuid);
        } catch (Exception ex) {
            Logger.getLogger(RedisManager.class.getName()).log(Level.SEVERE, null, ex);
            init();
            return getSkin(uuid, retrys - 1);
        }
    }

    public Skin getSkin(String uuid) {
        final String json = getSkin(uuid, 3);
        if (json == null) {
            return null;
        }
        try {
            return Skin.fromJSON(uuid, json);
        } catch (ParseException ex) {
            Logger.getLogger(RedisManager.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        }
    }

    public void setSkin(Skin skin) {
        jedis.set("skin:" + skin.getUuid(), skin.toJSON());
        jedis.expire("skin:" + skin.getUuid(), 43200);
    }
}
