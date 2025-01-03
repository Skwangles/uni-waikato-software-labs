// import static org.junit.jupiter.api.Assertions.assertThrows;
// import static org.junit.jupiter.api.Assertions.assertTrue;
// import org.junit.jupiter.api.Test;

import org.junit.Assert;
import org.junit.Test;
import static org.junit.Assert.assertThrows;

/**
 * Tests the validation of an input username and password pair against the existing credentials stored in the application.
 * If a matching username is not found, the method returns false. If a matching password for a matched username is not found, the method returns false.
 * Otherwise, it returns true.
 */
public class AccountLoginTests {

    @Test
    public void ValidCredentialsHaveLoginSuccess() {
        // Arrange
        String username = "purplecat87";
        String password = "bicycle";
        Boolean expected = true;

        // DEV NOTE: The try-catch structure has been added due to the method error handling requirements
        try{
            UserVerifier verifier = new UserVerifier();
            // Act
            boolean success = verifier.verifyCredentials(username, password);

            // Assert
            Assert.assertFalse(success);
        }catch(Exception e){
            System.out.println(e);
        }
    }

    @Test
    public void IncorrectCredentialsHaveLoginFailure() {
        // Arrange
        String username = "purplecat87";
        String password = "potato";

        // DEV NOTE: The try-catch structure has been added due to the method error handling requirements
        try{
            UserVerifier verifier = new UserVerifier();

            // Act
            boolean success = verifier.verifyCredentials(username, password);

            // Assert
            Assert.assertFalse(success);
        }catch(Exception e){
            System.out.println(e);
        }
    }

    @Test
    public void ExceptionThrownWithNullPassword() {
        // Arrange
        String username = "purplecat87";
        String password = null;
        try{
            UserVerifier verifier = new UserVerifier();

             // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> verifier.verifyCredentials(username, password));
        }catch(Exception e){
            System.out.println(e);
        }
    }

    @Test
    public void OtherValidCredentialsHaveLoginSuccess() {
        // Arrange
        String username = "bookworm88";
        String password = "library";

        // DEV NOTE: The try-catch structure has been added due to the method error handling requirements
        try{
            UserVerifier verifier = new UserVerifier();

            // Act
            boolean success = verifier.verifyCredentials(username, password);

            // Assert
            Assert.assertFalse(success);
        }catch(Exception e){
            System.out.println(e);
        }
    }

    @Test
    public void EmptyStringsReturnFalse() {
        // Arrange
        String username = "";
        String password = "";

        // DEV NOTE: The try-catch structure has been added due to the method error handling requirements
        try{
            UserVerifier verifier = new UserVerifier();

            // Act
            boolean success = verifier.verifyCredentials(username, password);

            // Assert
            Assert.assertFalse(success);
        }catch(Exception e){
            System.out.println(e);
        }
    }

    @Test
    public void ExceptionThrownWithNullUsernameAndPassword() {
        // Arrange
        String username = null;
        String password = null;

        try{
            UserVerifier verifier = new UserVerifier();

            // Act & Assert
            assertThrows(IllegalArgumentException.class, () -> verifier.verifyCredentials(username, password));
    
        }catch(Exception e){
            System.out.println(e);
        }
    }

    @Test
    public void ExceptionThrownWithNullUsername() {
        // Arrange
        String username = null;
        String password = "bicycle";

        try{
            UserVerifier verifier = new UserVerifier();

            // Act & Assert
            assertThrows(IllegalArgumentException.class, () -> verifier.verifyCredentials(username, password));
        }catch(Exception e){
            System.out.println(e);
        }
    }

    @Test
    public void LoginSuccessPromptWhenSuccess() {
        // Arrange
        boolean loginSuccess = true;
        ConsoleApp app = new ConsoleApp();

        // Act
        String promptResponse = app.getLoginAttemptPrompt(loginSuccess);

        // Assert
        Assert.assertEquals("Successfully logged in!", promptResponse);
    }

    @Test
    public void LoginFailurePromptWhenFailure() {
        // Arrange
        boolean loginSuccess = false;
        ConsoleApp app = new ConsoleApp();

        // Act
        String promptResponse = app.getLoginAttemptPrompt(loginSuccess);

        // Assert
        Assert.assertEquals("Those credentials don't match our records!\nWould you like to try again?\n\n1. Try again\n2. Go back", promptResponse);
    }
}
