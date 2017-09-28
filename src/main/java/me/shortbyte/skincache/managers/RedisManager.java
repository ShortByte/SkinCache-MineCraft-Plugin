package me.shortbyte.skincache.managers;

import me.shortbyte.skincache.SkinCache;
import me.shortbyte.skincache.misc.Skin;
import org.json.simple.JSONObject;
import redis.clients.jedis.Jedis;

/**
 *
 * @author LeonEnkn
 *
 * Copyright (c) 2015 - 2017 by ShortByte.de to present. All rights reserved.
 */
public class RedisManager {

    private final SkinCache plugin;

    private Jedis jedis;

    public RedisManager(SkinCache plugin) {
        this.plugin = plugin;
        init();
    }

    private void init() {
        jedis = new Jedis("HOSTNAME", 6379, 10000);
        jedis.auth("PASSWORD"); // BLABLABLA
        jedis.select(0);
        jedis.connect();

        System.out.println("  >> Redis connected!");
    }

    public String getSkin(String uuid) {
        try {
            return jedis.get("skin:" + uuid);
        } catch (Exception ex) {
            init();
            return getSkin(uuid);
        }
    }

    public void setSkin(String uuid, Skin skin) {
        JSONObject json = new JSONObject();
        json.put("name", skin.getName());
        json.put("value", skin.getValue());
        json.put("signature", skin.getSignature());
        jedis.set("skin:" + uuid, json.toString());
        jedis.expire("skin:" + uuid, 43200);
    }
}
