package bg.sofia.uni.fmi.mjt.split.authentication;

import bg.sofia.uni.fmi.mjt.split.dto.User;
import bg.sofia.uni.fmi.mjt.split.encryption.PasswordHashUtil;
import bg.sofia.uni.fmi.mjt.split.repository.UserRepository;

import java.util.Optional;

public class Authenticator {
    private final UserRepository userRepository;
    private User currentUser;

    public Authenticator(UserRepository userRepository) {
        this.userRepository = userRepository;
        currentUser = null;
    }

    public boolean login(String username, String pass) {
        Optional<User> user = userRepository.getUserByUsername(username);

        if (user.isPresent() && user.get().hashedPassword().equals(PasswordHashUtil.hashPassword(pass))) {
            currentUser = user.get();
            return true;
        }

        return false;
    }

    public User getAuthenticatedUser() {
        return currentUser;
    }

    public boolean isAuthenticated() {
        return currentUser != null;
    }

    public void logout() {
        currentUser = null;
    }
}
