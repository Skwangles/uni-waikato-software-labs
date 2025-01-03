import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import junit.framework.Assert;
import org.junit.jupiter.api.Test;

/**
 * Tests the feature options shown to the user based on the UserType.
 * If UserType is Community, then only one option should be shown. If UserType is
 * EncostVerified, then three options should be shown. If UserType is EncostUnverified,
 * then an exception should be thrown, as we do not expect to present any options to an
 * EncostUnverified user
 */
public class FeatureOptionsTests {
    @Test
    public void CommunityUserTypeGetsGraphOption() {
        // Arrange
        UserType input = UserType.Community;
        ConsoleApp app = new ConsoleApp();

        // Act
        String[] options = app.getOptions(input);

        // Assert
        Assert.assertEquals(2, options.length); // Must have graph and exit options only
        Assert.assertEquals("Visualise graph of dataset", options[0]);
        Assert.assertEquals("Exit", options[1]);
    }

    @Test
    public void EncostVerifiedUserTypeGetsAllOptions() {
        // Arrange
        UserType input = UserType.EncostVerified;
        ConsoleApp app = new ConsoleApp();

        // Act
        String[] options = app.getOptions(input);

        // Assert
        Assert.assertEquals(4, options.length); // Must have all options, including exit
        Assert.assertEquals("Load custom dataset", options[0]);
        Assert.assertEquals("Visualise graph of dataset", options[1]);
        Assert.assertEquals("View summary statistics", options[2]);
        Assert.assertEquals("Exit", options[3]);
    }

    @Test
    public void EncostUnverifiedUserTypeNotAllowedOptions() {
        // Arrange
        UserType input = UserType.EncostUnverified;
        ConsoleApp app = new ConsoleApp();

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> app.getOptions(input));
    }
}
