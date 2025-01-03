import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Date;
import java.util.Hashtable;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests whether the calls to getCategoryCounts and getDeviceTypeCounts
 * return the expected counts for categories and device types.
 *
 * Specific documentation available for test that deals with an invalid list.
 */
public class CalculatingDeviceDistributionTests {
    @Test
    public void DeviceDistributionStatisticsCategoryCountsAreCorrect() {
        // Arrange
        List<Device> devices = new ArrayList<Device>();

        DeviceType[] deviceTypesToMap = new DeviceType[] {
             DeviceType.Router,
             DeviceType.HubController,
             DeviceType.LightBulb,
             DeviceType.Kettle,
             DeviceType.Dishwasher
        };

        // Add two devices for each implicit device category
        for (DeviceType deviceType: deviceTypesToMap) {
            devices.add(new Device("", new Date(), "", deviceType, "", null, true, true));
            devices.add(new Device("", new Date(), "", deviceType, "", null, true, true));
        }

        // Act
        DeviceDistributionStatistics statistics = new DeviceDistributionStatistics(devices);
        Hashtable<DeviceCategory, Integer> categoryCounts = statistics.getCategoryCounts();

        // Assert
        final int expectedCategoryCount = 2;

        for (int count: categoryCounts.values())
            assertEquals(expectedCategoryCount, count);
    }

    @Test
    public void DeviceDistributionStatisticsDeviceTypeCountsAreCorrect() {
        // Arrange
        List<Device> devices = new ArrayList<Device>();

        // Add two devices for each implicit device category
        for (DeviceType deviceType: DeviceType.values()) {
            devices.add(new Device("", new Date(), "", deviceType, "", null, true, true));
            devices.add(new Device("", new Date(), "", deviceType, "", null, true, true));
        }

        // Act
        DeviceDistributionStatistics statistics = new DeviceDistributionStatistics(devices);
        Hashtable<DeviceType, Integer> deviceTypeCounts = statistics.getDeviceTypeCounts();

        // Assert
        final int expectedDeviceTypeCount = 2;

        for (int count: deviceTypeCounts.values())
            assertEquals(expectedDeviceTypeCount, count);
    }

    @Test
    public void DeviceDistributionStatisticsNumberOfCategoriesIsCorrect() {
        // Arrange
        List<Device> devices = new ArrayList<Device>();

        DeviceType[] deviceTypesToMap = new DeviceType[] {
                DeviceType.Router,
                DeviceType.HubController,
                DeviceType.LightBulb,
                DeviceType.Kettle,
                DeviceType.Dishwasher
        };

        // Add two devices for each implicit device category
        for (DeviceType deviceType: deviceTypesToMap) {
            devices.add(new Device("", new Date(), "", deviceType, "", null, true, true));
            devices.add(new Device("", new Date(), "", deviceType, "", null, true, true));
        }

        // Act
        DeviceDistributionStatistics statistics = new DeviceDistributionStatistics(devices);
        Hashtable<DeviceCategory, Integer> categoryCounts = statistics.getCategoryCounts();

        // Assert
        final int expectedNumberOfCategories = 5;

        assertEquals(expectedNumberOfCategories, categoryCounts.size());
    }

    /**
     * Tests whether the DataDistributionStatistics handles an invalid list of
     * Devices argumen
     */
    @Test
    public void DeviceDistributionStatisticsThrowsExceptionIfArgumentIsNull() {
        // Arrange
        List<Device> devices = null;

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> {
            new DeviceDistributionStatistics(devices);
        });
    }
}
