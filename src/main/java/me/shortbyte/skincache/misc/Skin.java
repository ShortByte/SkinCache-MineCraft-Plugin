package me.shortbyte.skincache.misc;

import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

/**
 *
 * @author LeonEnkn
 *
 * Copyright (c) 2015 - 2017 by ShortByte.de to present. All rights reserved.
 */
public class Skin {

    public static Skin fromJSON(String uuid, JSONObject json) {
        final String name = (String) json.get("name");
        final String value = (String) json.get("value");
        final String signature = (String) json.get("signature");
        return new Skin(uuid, name, value, signature);
    }

    public static Skin fromJSON(String uuid, String jsonString) throws ParseException {
        final JSONObject json = (JSONObject) new JSONParser().parse(jsonString);
        return Skin.fromJSON(uuid, json);
    }

    public static Skin fromMojang(String uuid) {
        try {
            final URL url = new URL(String.format("https://sessionserver.mojang.com/session/minecraft/profile/%s?unsigned=false", uuid.replaceAll("-", "")));
            final URLConnection connection = url.openConnection();
            connection.setUseCaches(false);
            connection.setDefaultUseCaches(false);
            connection.setRequestProperty("User-Agent", "curl/7.26.0");
            connection.setRequestProperty("Host", "sessionserver.mojang.com");
            connection.setRequestProperty("Accept", "*/*");

            final String json = new Scanner(connection.getInputStream(), "UTF-8").useDelimiter("\\A").next();
            final JSONArray array = (JSONArray) ((JSONObject) new JSONParser().parse(json)).get("properties");
            final JSONObject properties = (JSONObject) array.get(0);
            return Skin.fromJSON(uuid, properties);
        } catch(IOException | ParseException ex) {
            Logger.getLogger(Skin.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        }
    }

    private final String uuid;
    private final String name;
    private final String value;
    private final String signature;

    public Skin(String uuid, String name, String value, String signature) {
        this.uuid = uuid;
        this.name = name;
        this.value = value;
        this.signature = signature;
    }

    public String toJSON() {
        final JSONObject json = new JSONObject();
        json.put("name", this.name);
        json.put("value", this.value);
        json.put("signature", this.signature);
        return json.toString();
    }

    public String getUuid() {
        return uuid;
    }

    public String getName() {
        return name;
    }

    public String getValue() {
        return value;
    }

    public String getSignature() {
        return signature;
    }

}
