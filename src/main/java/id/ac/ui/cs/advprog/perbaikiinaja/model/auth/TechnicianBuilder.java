package id.ac.ui.cs.advprog.perbaikiinaja.model.auth;

import id.ac.ui.cs.advprog.perbaikiinaja.enums.auth.UserRole;

public class TechnicianBuilder extends UserBuilder<TechnicianBuilder> {
    private String address;
    private int completedJobs = 0;
    private double totalEarnings = 0;

    @Override
    protected TechnicianBuilder self() {
        return null;
    }

    public TechnicianBuilder address(String address) {
        return null;
    }
    
    public TechnicianBuilder completedJobs(int completedJobs) {
        return null;
    }
    
    public TechnicianBuilder totalEarnings(double totalEarnings) {
        return null;
    }
    
    @Override
    public Technician build() {
        return null;
    }
}
