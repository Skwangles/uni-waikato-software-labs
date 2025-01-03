// import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.Assert;
import org.junit.Test;
//import org.junit.jupiter.api.Test;

public class CategorisingUsersTests {
    /**
     * Tests that the ApplicationState class is correctly setting the UserType
     */
    @Test
    public void UserTypeSetCorrectly() {
        // Arrange
        UserType input = UserType.Community;

        // Act
        ApplicationState.setUserType(input);
        UserType output = ApplicationState.getUserType();

        // Assert
        Assert.assertEquals(input, output);
    }
}
