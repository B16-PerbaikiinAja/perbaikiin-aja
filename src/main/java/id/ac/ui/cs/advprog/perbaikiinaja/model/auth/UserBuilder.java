package id.ac.ui.cs.advprog.perbaikiinaja.model.auth;

public abstract class UserBuilder<T extends UserBuilder<T>> {
    protected String fullName;
    protected String email;
    protected String password;
    protected String phoneNumber;

    protected abstract T self();

    public T fullName(String fullName) {
        if (fullName == null || fullName.isEmpty()) {
            throw new IllegalArgumentException("Full Name cannot be empty");
        }
        this.fullName = fullName;
        return self();
    }

    public T email(String email) {
        if (email == null || email.isEmpty()) {
            throw new IllegalArgumentException("Email cannot be empty");
        }
        if (!email.matches("^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.\\-]+\\.[a-zA-Z]{2,}$")) {
            throw new IllegalArgumentException("Invalid email");
        }

        this.email = email;
        return self();
    }

    public T password(String password) {
        if (password.length() < 8) {
            throw new IllegalArgumentException("Password must be at least 8 characters long");
        }
        this.password = password;
        return self();
    }

    public T phoneNumber(String phoneNumber) {
        if (phoneNumber == null || phoneNumber.isEmpty()) {
            throw new IllegalArgumentException("Phone Number cannot be empty");
        }

        if (!phoneNumber.matches("\\d+")) {
            throw new IllegalArgumentException("Phone Number must be numeric");
        }
        this.phoneNumber = phoneNumber;
        return self();
    }

    protected void validateRequiredFields() {
        StringBuilder missingFields = new StringBuilder();
        
        if (fullName == null) missingFields.append("fullName, ");
        if (email == null) missingFields.append("email, ");
        if (password == null) missingFields.append("password, ");
        if (phoneNumber == null) missingFields.append("phoneNumber, ");
        
        if (missingFields.length() > 0) {
            missingFields.setLength(missingFields.length() - 2); // Remove trailing comma and space
            throw new IllegalStateException("Cannot build User: missing required field(s): " + missingFields);
        }
    }
    
    public abstract User build();
}