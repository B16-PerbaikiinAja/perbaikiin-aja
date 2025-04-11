package id.ac.ui.cs.advprog.perbaikiinaja.model.auth;

import id.ac.ui.cs.advprog.perbaikiinaja.enums.auth.UserRole;

public class TechnicianBuilder extends UserBuilder<TechnicianBuilder> {
    private String address;
    private int completedJobs = 0;
    private double totalEarnings = 0;

    @Override
    protected TechnicianBuilder self() {
        return this;
    }

    public TechnicianBuilder address(String address) {
        this.address = address;
        return self();
    }
    
    public TechnicianBuilder completedJobs(int completedJobs) {
        this.completedJobs = completedJobs;
        return self();
    }
    
    public TechnicianBuilder totalEarnings(double totalEarnings) {
        this.totalEarnings = totalEarnings;
        return self();
    }
    
    @Override
    public Technician build() {
        Technician technician = new Technician();
        technician.setEmail(this.email);
        technician.setPhoneNumber(this.phoneNumber);
        technician.setFullName(this.fullName);
        technician.setPassword(this.password);
        technician.setAddress(this.address);
        technician.setCompletedJobs(this.completedJobs);
        technician.setTotalEarnings(this.totalEarnings);
        technician.setRole(UserRole.TECHNICIAN.getValue());
        return technician;
    }
}
