package id.ac.ui.cs.advprog.perbaikiinaja.model.auth;

public class UserBuilder {
    private final User user;

    public UserBuilder() {
        user = new User();
    }

    public UserBuilder fullName(String fullName) {
        user.setFullName(fullName);
        return this;
    }

    public UserBuilder email(String email) {
        user.setEmail(email);
        return this;
    }

    public UserBuilder password(String password) {
        try {
            java.lang.reflect.Field passwordField = User.class.getDeclaredField("password");
            passwordField.setAccessible(true);
            passwordField.set(user, password);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            throw new RuntimeException("Unable to set password", e);
        }
        return this;
    }

    public UserBuilder phoneNumber(String phoneNumber) {
        user.setPhoneNumber(phoneNumber);
        return this;
    }

    public User build() {
        return user;
    }
}
