package com.moviebooking.model;

public final class Customer extends Person {

    public Customer(String name, String phone) {
        super(name, phone);
    }

    @Override
    public String getRole() {
        return "CUSTOMER";
    }
}
