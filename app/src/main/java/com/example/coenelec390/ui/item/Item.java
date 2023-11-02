package com.example.coenelec390.ui.item;

public class Item {
    private String id;
    private String imageURL;
    private String description;
    private int stock;

    public Item(String id, String imageURL, String description, int stock){
        this.id = id;
        this.imageURL = imageURL;
        this.description = description;
        this.stock = stock;
    }

    public String getId(){
        return id;
    }

    public String getImageURL() {
        return imageURL;
    }

    public String getDescription() {
        return description;
    }

    public int getStock() {
        return stock;
    }
}
