package id.ac.ui.cs.advprog.perbaikiinaja.model.auth;

/**
 * A concrete implementation of UserBuilder for testing purposes.
 */
public class TestUserBuilder extends UserBuilder<TestUserBuilder> {
    
    @Override
    protected TestUserBuilder self() {
        return this;
    }
    
    @Override
    public TestUser build() {
        validateRequiredFields();
        TestUser user = new TestUser();
        user.setFullName(this.fullName);
        user.setEmail(this.email);
        user.setPassword(this.password);
        user.setPhoneNumber(this.phoneNumber);
        return user;
    }
}
