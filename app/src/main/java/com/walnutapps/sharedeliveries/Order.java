package com.walnutapps.sharedeliveries;

import android.location.Location;

import com.google.android.gms.maps.model.LatLng;

/**
 * Created by Ivan on 2017-07-31.
 */

public class Order {

    private String restaurant;
    private  String description;
    private int numberOfMembers;
    private int membersFilled;
    private float totalPrice;
    private LatLng location;
    private boolean active;

    public Order(String restaurant, String description, int numberOfMembers, float totalPrice, LatLng location){

        this.restaurant = restaurant;
        this.description = description;
        this.numberOfMembers = numberOfMembers;
        this.totalPrice = totalPrice;
        this.location = location;
        this.active = true;
        this.membersFilled = 1;

    }

    public String getRestaurant() {
        return restaurant;
    }

    public void setRestaurant(String restaurant) {
        this.restaurant = restaurant;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getNumberOfMembers() {
        return numberOfMembers;
    }

    public void setNumberOfMembers(int numberOfMembers) {
        this.numberOfMembers = numberOfMembers;
    }

    public int getMembersFilled() {
        return membersFilled;
    }

    public void setMembersFilled(int membersFilled) {
        this.membersFilled = membersFilled;
    }

    public float getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(float totalPrice) {
        this.totalPrice = totalPrice;
    }

    public LatLng getLocation() {
        return location;
    }

    public void setLocation(LatLng location) {
        this.location = location;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }
}
