package com.alast.oneappmanager.Model;

public class Product {

    private String name, description, image, id ,labels;
    private int position, price, quantity;

    public Product() {

    }

    public Product(String name, String description, String image, String id, String labels, int position, int price) {
        this.name = name;
        this.description = description;
        this.image = image;
        this.id = id;
        this.labels = labels;
        this.position = position;
        this.price = price;
    }

    public String getName() {
        if (name == null) return "";
        return name;
    }

    public String getDescription() {
        if (description == null) return "";
        return description;
    }

    public String getImage() {
        if (image == null) return "";
        return image;
    }

    public String getId() {
        if (id == null) return "";
        return id;
    }

    public String getLabels() {
        if (labels == null) return "";
        return labels;
    }

    public int getPosition() {
        return position;
    }

    public int getPrice() {
        return price;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }
}
