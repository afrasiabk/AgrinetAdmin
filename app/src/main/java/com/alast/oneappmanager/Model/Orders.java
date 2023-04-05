package com.alast.oneappmanager.Model;

public class Orders {
    private String address, bill, id, lati, longi, status, user_name, user_phone;
    private String charges = "no";
    private Long delivered_time, placed_time;

    public Orders() {}

    public Orders(String charges, String address, String bill, Long delivered_time, String id, Long placed_time, String user_name, String user_phone, String lati, String longi, String status) {
        this.charges = charges;
        this.address = address;
        this.bill = bill;
        this.delivered_time = delivered_time;
        this.id = id;
        this.placed_time = placed_time;
        this.user_name = user_name;
        this.user_phone = user_phone;
        this.lati = lati;
        this.longi = longi;
        this.status = status;
    }

    public String getCharges() {
        return charges;
    }

    public void setCharges(String charges) {
        this.charges = charges;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getBill() {
        return bill;
    }

    public void setBill(String bill) {
        this.bill = bill;
    }

    public Long getDelivered_time() {
        return delivered_time;
    }

    public void setDelivered_time(Long delivered_time) {
        this.delivered_time = delivered_time;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Long getPlaced_time() {
        return placed_time;
    }

    public void setPlaced_time(Long placed_time) {
        this.placed_time = placed_time;
    }

    public String getUser_name() {
        return user_name;
    }

    public void setUser_name(String user_name) {
        this.user_name = user_name;
    }

    public String getUser_phone() {
        return user_phone;
    }

    public void setUser_phone(String user_phone) {
        this.user_phone = user_phone;
    }

    public String getLati() {
        return lati;
    }

    public void setLati(String lati) {
        this.lati = lati;
    }

    public String getLongi() {
        return longi;
    }

    public void setLongi(String longi) {
        this.longi = longi;
    }
}