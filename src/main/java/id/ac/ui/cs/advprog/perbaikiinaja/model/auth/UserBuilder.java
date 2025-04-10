package id.ac.ui.cs.advprog.perbaikiinaja.model.auth;

public abstract class UserBuilder<T extends UserBuilder<T>> {
    protected String fullName;
    protected String email;
    protected String password;
    protected String phoneNumber;

    protected abstract T self();

    public T fullName(String fullName) {
        return null;
    }

    public T email(String email) {
        return null;
    }

    public T password(String password) {
        return null;
    }

    public T phoneNumber(String phoneNumber) {
        return null;
    }
    
    public abstract User build();
}