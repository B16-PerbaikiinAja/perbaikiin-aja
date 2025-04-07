package id.ac.ui.cs.advprog.perbaikiinaja.model.auth;

import id.ac.ui.cs.advprog.perbaikiinaja.enums.auth.UserRole;

public class TechnicianBuilder extends UserBuilder<TechnicianBuilder> {
    private String address;

    @Override
    protected TechnicianBuilder self() {
        return this;
    }

    public TechnicianBuilder address(String address) {
        this.address = address;
        return this;
    }
    
    @Override
    public Technician build() {
        Technician technician = new Technician();
        technician.setEmail(this.email);
        technician.setPhoneNumber(this.phoneNumber);
        technician.setFullName(this.fullName);
        technician.setPassword(this.password);
        technician.setAddress(this.address);
        technician.setRole(UserRole.TECHNICIAN.getValue());
        technician.setCompletedJobs(0);
        technician.setTotalEarnings(0);
        return technician;
    }
}
