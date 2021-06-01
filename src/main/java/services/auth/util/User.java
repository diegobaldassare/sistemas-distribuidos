package services.auth.util;

public class User {

    private final String username;
    private final int password;

    public User(String username, String password) {
        this.username = username;
        this.password = password.hashCode();
    }

    public String getUsername() {
        return username;
    }

    public int getPassword() {
        return password;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return password == user.password && username.equals(user.username);
    }

}
