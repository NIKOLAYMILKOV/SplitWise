package bg.sofia.uni.fmi.mjt.split.encryption;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

public class TestPasswordHashUtil {
    private static final String password1 = "password";
    private static final String password2 = "differentPassword";

    @Test
    void testHashWithEqualPasswords() {
        assertEquals(PasswordHashUtil.hashPassword(password1), PasswordHashUtil.hashPassword(password1),
            "Equal passwords should return equal hashed passwords");
    }

    @Test
    void testHashWithDifferentPasswords() {
        assertNotEquals(PasswordHashUtil.hashPassword(password1), PasswordHashUtil.hashPassword(password2),
            "Different passwords should return different hashed passwords");
    }
}
