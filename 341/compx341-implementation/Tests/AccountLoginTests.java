import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.Assert;
import org.junit.jupiter.api.Test;

/**
 * Tests the validation of an input username and password pair against the existing credentials stored in the application.
 * If a matching username is not found, the method returns false. If a matching password for a matched username is not found, the method returns false.
 * Otherwise, it returns true.
 */
public class AccountLoginTests {

    /*
    encostUserA,password789
encostUserB,password234
encostUserC,password456
encostUserD,password901
encostUserE,password678
encostUserF,password567
encostUserG,password345
encorstUserH,password890
encostUserI,password123
encostUserJ,password012*/
    @Test
    public void ValidCredentialsHaveLoginSuccess() {
        // Arrange
        String username = "encostUserA";
        String password = "password789";

        // Act
        boolean success = UserVerifier.verifyCredentials(username, password);

        // Assert
        Assert.assertTrue(success);
    }

    @Test
    public void IncorrectCredentialsHaveLoginFailure() {
        // Arrange
        String username = "encostUserA";
        String password = "invalid000";
        UserVerifier verifier = new UserVerifier();

        // Act
        boolean success = verifier.verifyCredentials(username, password);

        // Assert
        Assert.assertFalse(success);
    }

    @Test
    public void ExceptionThrownWithNullPassword() {
        // Arrange
        String username = "encostUserA";
        String password = null;
        UserVerifier verifier = new UserVerifier();

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> verifier.verifyCredentials(username, password));
    }

    @Test
    public void OtherValidCredentialsHaveLoginSuccess() {
        // Arrange
        String username = "encostUserB";
        String password = "password234";
        UserVerifier verifier = new UserVerifier();

        // Act
        boolean success = verifier.verifyCredentials(username, password);

        // Assert
        Assert.assertTrue(success);
    }

    @Test
    public void EmptyStringsReturnFalse() {
        // Arrange
        String username = "";
        String password = "";
        UserVerifier verifier = new UserVerifier();

        // Act
        boolean success = verifier.verifyCredentials(username, password);

        // Assert
        Assert.assertFalse(success);
    }

    @Test
    public void ExceptionThrownWithNullUsernameAndPassword() {
        // Arrange
        String username = null;
        String password = null;
        UserVerifier verifier = new UserVerifier();

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> verifier.verifyCredentials(username, password));
    }

    @Test
    public void ExceptionThrownWithNullUsername() {
        // Arrange
        String username = null;
        String password = "password234";
        UserVerifier verifier = new UserVerifier();

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> verifier.verifyCredentials(username, password));
    }

    @Test
    public void LoginSuccessPromptWhenSuccess() {
        // Arrange
        boolean loginSuccess = true;
        ConsoleApp app = new ConsoleApp();

        // Act
        String promptResponse = app.getLoginAttemptPrompt(loginSuccess);

        // Assert
        Assert.assertEquals("Successfully logged in!\n", promptResponse);
    }

    @Test
    public void LoginFailurePromptWhenFailure() {
        // Arrange
        boolean loginSuccess = false;
        ConsoleApp app = new ConsoleApp();

        // Act
        String promptResponse = app.getLoginAttemptPrompt(loginSuccess);

        // Assert
        Assert.assertEquals("Those credentials don't match our records!\nWould you like to try again?\n\n1. Try again\n2. Continue as Community User", promptResponse);
    }
}
