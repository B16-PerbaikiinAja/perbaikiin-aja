package id.ac.ui.cs.advprog.perbaikiinaja.model.auth;

import id.ac.ui.cs.advprog.perbaikiinaja.enums.auth.UserRole;

public class CustomerBuilder extends UserBuilder<CustomerBuilder> {
    private String address;

    @Override
    protected CustomerBuilder self() {
        return this;
    }

    public CustomerBuilder address(String address) {
        this.address = address;
        return this;
    }
    
    @Override
    public Customer build() {
        Customer customer = new Customer();
        customer.setEmail(this.email);
        customer.setPhoneNumber(this.phoneNumber);
        customer.setFullName(this.fullName);
        customer.setPassword(this.password);
        customer.setAddress(this.address);
        customer.setRole(UserRole.CUSTOMER.getValue());
        return customer;
    }
}
