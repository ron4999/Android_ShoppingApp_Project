package com.example.ecart.Model;

public class Products {
    private String p_name, description, price, image, category, p_id, date, time;

    public Products() {
    }

    public Products(String p_name, String description, String price, String image, String category, String p_id, String date, String time) {
        this.p_name = p_name;
        this.description = description;
        this.price = price;
        this.image = image;
        this.category = category;
        this.p_id = p_id;
        this.date = date;
        this.time = time;
    }

    public String getP_name() {
        return p_name;
    }

    public void setP_name(String p_name) {
        this.p_name = p_name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getP_id() {
        return p_id;
    }

    public void setP_id(String p_id) {
        this.p_id = p_id;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }
}
