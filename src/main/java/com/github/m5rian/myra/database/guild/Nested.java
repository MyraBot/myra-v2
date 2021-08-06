package com.github.m5rian.myra.database.guild;

import org.bson.Document;

import java.awt.*;
import java.util.List;

public class Nested {
    // Variables
    private final MongoGuild dbGuild;
    private final String nestedKey;
    private final Document nested;

    /**
     * @param dbGuild The current cached {@link MongoGuild}.
     * @param key     The key of the nested object.
     */
    public Nested(MongoGuild dbGuild, String key) {
        this.dbGuild = dbGuild;
        this.nestedKey = key;
        this.nested = dbGuild.get(key, Document.class);
    }

    /**
     * @param key The key to search for.
     * @return Returns whether the nested document contains a key named {@param key}.
     */
    public boolean hasKey(String key) {
        return nested.containsKey(key);
    }

    /**
     * @param key The key to search for.
     * @return Returns a {@link String} given by a key.
     */
    public String getString(String key) {
        return this.nested.getString(key);
    }

    /**
     * Replace a string in the guild document.
     *
     * @param key   The key to search for.
     * @param value The new value.
     */
    public Nested setString(String key, String value) {
        this.nested.replace(key, value); // Replace value
        this.dbGuild.set(this.nestedKey, this.nested); // Update nested object
        return this;
    }

    /**
     * @param key The key to search for.
     * @return Returns a {@link Integer} given by a key.
     */
    public Integer getInteger(String key) {
        return this.nested.getInteger(key);
    }

    /**
     * Replace an integer in the guild document.
     *
     * @param key   The key to search for.
     * @param value The new value.
     * @return Returns the current {@link Nested} for chaining purpose.
     */
    public Nested setInteger(String key, Integer value) {
        this.nested.replace(key, value); // Replace value
        this.dbGuild.set(this.nestedKey, this.nested); // Update nested object
        return this;
    }

    /**
     * @param key The key to search for.
     * @return Returns the value, found by the given key.
     */
    public Long getLong(String key) {
        return this.nested.getLong(key);
    }

    /**
     * Replace a long in the guild document.
     *
     * @param key   The key to search for.
     * @param value The new value.
     */
    public Nested setLong(String key, Long value) {
        this.nested.replace(key, value); // Replace value
        this.dbGuild.set(this.nestedKey, this.nested); // Update nested object
        return this;
    }

    /**
     * @param key The key to search for.
     * @return Returns the value, found by the given key.
     */
    public boolean getBoolean(String key) {
        return this.nested.getBoolean(key);
    }

    /**
     * Replace a boolean in the guild document.
     *
     * @param key   The key to search for.
     * @param value The new value.
     * @return Returns the current {@link Nested} for chaining purpose.
     */
    public Nested setBoolean(String key, boolean value) {
        this.nested.replace(key, value); // Replace value
        this.dbGuild.set(this.nestedKey, this.nested); // Update nested object
        return this;
    }

    /**
     * @param key   The key to search for.
     * @param clazz The Class type to return.
     * @param <T>   The returned class type.
     * @return Returns the value of the given key as the specified class type.
     */
    public <T> T get(String key, Class<T> clazz) {
        return this.nested.get(key, clazz);
    }

    /**
     * @param key   The key to search for.
     * @param clazz The Class type to return.
     * @param <T>   The returned class type.
     * @return Returns a list of the class type T, found by the given key.
     */
    public <T> List<T> getList(String key, Class<T> clazz) {
        return this.nested.getList(key, clazz);
    }

    /**
     * Replace a list of the class type T.
     *
     * @param key   The key to search for.
     * @param value The new list.
     * @param <T>   The class type of which the list will be.
     */
    public <T> void setList(String key, List<T> value) {
        this.nested.replace(key, value); // Replace value
        this.dbGuild.set(this.nestedKey, this.nested); // Update nested object
    }

    /**
     * @param key The key to search for.
     * @return Returns a {@link Color} given by a key.
     */
    public Color getColour(String key) {
        return Color.decode(this.nested.getString(key));
    }

    /**
     * Replace a colour in the guild document.
     *
     * @param key   The key to search for.
     * @param value The new value.
     * @return Returns the current {@link Nested} for chaining purpose.
     */
    public Nested setColour(String key, Color value) {
        this.nested.replace(key, String.format("#%02x%02x%02x", value.getRed(), value.getGreen(), value.getBlue())); // Replace value
        this.dbGuild.set(this.nestedKey, this.nested); // Update nested object
        return this;
    }

    /**
     * Replace a key with a any value.
     *
     * @param key   The key to search for.
     * @param value The new value.
     * @return Returns the current {@link Nested} for chaining purpose.
     */
    public Nested set(String key, Object value) {
        this.nested.replace(key, value); // Replace value
        this.dbGuild.set(this.nestedKey, this.nested); // Update nested object
        return this;
    }

    /**
     * Set a key to null.
     *
     * @param key The key to find.
     * @return Returns the current {@link Nested} for chaining purpose.
     */
    public Nested setNull(String key) {
        this.nested.replace(key, null); // Replace value
        this.dbGuild.set(this.nestedKey, this.nested); // Update nested object
        return this;
    }
}
