package id.ac.ui.cs.advprog.perbaikiinaja.model.auth;

import id.ac.ui.cs.advprog.perbaikiinaja.enums.auth.UserRole;

public class AdminBuilder extends UserBuilder<AdminBuilder> {
    @Override
    protected AdminBuilder self() {
        return this;
    }
    
    @Override
    public Admin build() {
        Admin admin = new Admin();
        admin.setEmail(this.email);
        admin.setPhoneNumber(this.phoneNumber);
        admin.setFullName(this.fullName);
        admin.setPassword(this.password);
        admin.setRole(UserRole.ADMIN.getValue());
        return admin;
    }
}
