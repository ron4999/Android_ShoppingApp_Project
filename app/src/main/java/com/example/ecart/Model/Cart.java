package com.example.ecart.Model;

public class Cart {
    private String p_id, p_name, price, quantity, discount;

    public Cart() {
    }

    public Cart(String p_id, String p_name, String price, String quantity, String discount) {
        this.p_id = p_id;
        this.p_name = p_name;
        this.price = price;
        this.quantity = quantity;
        this.discount = discount;
    }

    public String getP_id() {
        return p_id;
    }

    public void setP_id(String p_id) {
        this.p_id = p_id;
    }

    public String getP_name() {
        return p_name;
    }

    public void setP_name(String p_name) {
        this.p_name = p_name;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getQuantity() {
        return quantity;
    }

    public void setQuantity(String quantity) {
        this.quantity = quantity;
    }

    public String getDiscount() {
        return discount;
    }

    public void setDiscount(String discount) {
        this.discount = discount;
    }
}
