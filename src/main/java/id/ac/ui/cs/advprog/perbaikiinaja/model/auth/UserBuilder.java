package id.ac.ui.cs.advprog.perbaikiinaja.model.auth;

public abstract class UserBuilder<T extends UserBuilder<T>> {
    protected String fullName;
    protected String email;
    protected String password;
    protected String phoneNumber;

    protected abstract T self();

    public T fullName(String fullName) {
        this.fullName = fullName;
        return self();
    }

    public T email(String email) {
        this.email = email;
        return self();
    }

    public T password(String password) {
        this.password = password;
        return self();
    }

    public T phoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
        return self();
    }
    
    public abstract User build();
}
