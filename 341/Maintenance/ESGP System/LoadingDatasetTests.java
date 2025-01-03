// import static org.junit.jupiter.api.Assertions.assertThrows;
// import static org.junit.jupiter.api.Assertions.assertTrue;

// import junit.framework.Assert;
// import org.junit.jupiter.api.Test;

import org.junit.Assert;
import org.junit.Test;
import static org.junit.Assert.assertThrows;

import java.io.BufferedReader;
import java.io.StringReader;
import java.util.HashSet;

/**
 * Tests whether, after reading all Device data, the expected number of
 * devices matches the actual number of devices. A test case for both one and two lines of
 * data are used to ensure the FileParser class can handle new lines correctly.
 */
public class LoadingDatasetTests {
    @Test
    public void OneCsvLineReturnsOneObject() {
        // Arrange
        String header = "Device ID,Date Connected,Device name,Device type,Household ID,Router Connection,Sends,Receives";
        String line = "EWR-1234,01/04/22,Encost Router 360,Router,WKO-1234,-,Yes,Yes";
        String[] lines = new String[] { header, line };

        // Convert string array to a single string
        String joinedString = String.join(System.lineSeparator(), lines);

        // Create a BufferedReader from the string
        BufferedReader reader = new BufferedReader(new StringReader(joinedString));

        FileParser parser = new FileParser();

        // Act
        Device[] devices = parser.parseFile(reader);

        // Assert
        Assert.assertEquals(1, devices.length);
    }

    @Test
    public void TwoCsvLinesReturnsTwoObjects() {
        // Arrange
        String header = "Device ID,Date Connected,Device name,Device type,Household ID,Router Connection,Sends,Receives";
        String firstLine = "EWR-1234,01/04/22,Encost Router 360,Router,WKO-1234,-,Yes,Yes";
        // Light bulb -> lightBulb as per sprint 4 documentation
        String secondLine = "ELB-4567,01/04/22,Encost Smart Bulb B22 (multi colour),LightBulb,WKO-1234,EWR-1234,No,Yes";
        String[] lines = new String[] { header, firstLine, secondLine };

        // Convert string array to a single string
        String joinedString = String.join(System.lineSeparator(), lines);

        // Create a BufferedReader from the string
        BufferedReader reader = new BufferedReader(new StringReader(joinedString));

        FileParser parser = new FileParser();

        // Act
        Device[] devices = parser.parseFile(reader);

        // Assert
        Assert.assertEquals(2, devices.length);
    }

    @Test
    public void DevicesReadFromCsvAreUnique() {
        // Arrange
        String header = "Device ID,Date Connected,Device name,Device type,Household ID,Router Connection,Sends,Receives";
        String firstLine = "EWR-1234,01/04/22,Encost Router 360,Router,WKO-1234,-,Yes,Yes";
        // Light bulb -> lightBulb as per sprint documentation
        String secondLine = "ELB-4567,01/04/22,Encost Smart Bulb B22 (multi colour),LightBulb,WKO-1234,EWR-1234,No,Yes";
        String[] lines = new String[] { header, firstLine, secondLine };
        HashSet<String> deviceIds = new HashSet<>();

        // Convert string array to a single string
        String joinedString = String.join(System.lineSeparator(), lines);

        // Create a BufferedReader from the string
        BufferedReader reader = new BufferedReader(new StringReader(joinedString));
        FileParser parser = new FileParser();

        // Act
        Device[] devices = parser.parseFile(reader);

        for (Device device: devices)
            deviceIds.add(device.getDeviceId());

        // Assert
        Assert.assertEquals(2, deviceIds.size());
    }

    @Test
    public void FileParserThrowsErrorIfReaderIsNull() {
        // Arrange
        BufferedReader reader = null;
        FileParser parser = new FileParser();

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> parser.parseFile(reader));
    }

    @Test
    public void FileParserReturnsNullIfCsvIsMalformed() {
        // Arrange
        String header = "Device ID,Date Connected,Device name,Device type,Household ID,Router Connection,Sends,Receives";
        String firstLine = "EWR-1234,01/04/22,Encost Router 360Router,WKO-1234,-,Yes,Yes";
        String[] lines = new String[] { header, firstLine };
        // FileParser parser = new FileParser();

        // Convert string array to a single string
        String joinedString = String.join(System.lineSeparator(), lines);

        // Create a BufferedReader from the string
        BufferedReader reader = new BufferedReader(new StringReader(joinedString));
        FileParser parser = new FileParser();

        // Act
        Device[] devices = parser.parseFile(reader);

        // Assert
        Assert.assertNull(devices);
    }

    @Test
    public void FileParserReturnsNullIfCsvIsEmpty() {
        // Arrange
        FileParser parser = new FileParser();

        // Create a BufferedReader from the string
        BufferedReader reader = new BufferedReader(new StringReader(""));
        //FileParser parser = new FileParser();

        // Act
        Device[] devices = parser.parseFile(reader);

        // Assert
        Assert.assertNull(devices);
    }
}
