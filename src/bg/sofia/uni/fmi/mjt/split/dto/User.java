package bg.sofia.uni.fmi.mjt.split.dto;

import java.util.Objects;

public class User {
    private final String username;
    private final String hashedPassword;
    private final String firstName;
    private final String lastName;

    public User(String username, String hashedPassword, String firstName, String lastName) {
        this.username = username;
        this.hashedPassword = hashedPassword;
        this.firstName = firstName;
        this.lastName = lastName;
    }

    public String username() {
        return username;
    }

    public String hashedPassword() {
        return hashedPassword;
    }

    public String firstName() {
        return firstName;
    }

    public String lastName() {
        return lastName;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return Objects.equals(username, user.username);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(username);
    }
}
