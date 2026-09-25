package com.moviebooking.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;

import org.junit.jupiter.api.Test;

class CustomerTest {

    @Test
    void customerInheritsPersonIdentityAndRole() {
        Customer customer = new Customer("Asha Rao", "9876543210");

        assertInstanceOf(Person.class, customer);
        assertEquals("Asha Rao", customer.getName());
        assertEquals("9876543210", customer.getPhone());
        assertEquals("CUSTOMER", customer.getRole());
    }
}
