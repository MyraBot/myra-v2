package com.myra.dev.marian.database.documents;

import org.bson.Document;

public class ShopRolesDocument {
    // Variables
    private String id;
    private int price;

    // Constructor
    public ShopRolesDocument(Document role) {
        this.id = role.getString("id");
        this.price = role.getInteger("price");
    }

    public String getId() {
        return id;
    }

    public Integer getPrice() {
        return price;
    }
}
