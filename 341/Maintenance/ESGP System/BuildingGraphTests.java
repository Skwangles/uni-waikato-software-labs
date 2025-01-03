//import org.junit.jupiter.api.Test;

import java.util.Date;
import java.util.NoSuchElementException;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThrows;
import org.junit.Test;

//import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests whether the neighbours returned by getNeighbours is valid. A
 * DeviceGraph is constructed using an array of Devices with some neighbour relationships
 * defined.
 *
 * Tests whether the call to getNeighbours will handle invalid or unexpected
 * input correctly. The method should not accept null values or device IDs that do not
 * belong to any device.
 */
public class BuildingGraphTests {
    @Test
    public void DeviceGraphAssignsDevicesCorrectly() {
        // Arrange
        Device[] devices = new Device[] {
                new Device("", new Date(), "", DeviceType.Router, "", null, true, true)
        };

        // Act
        DeviceGraph graph = new DeviceGraph(devices);
        Device[] returnedDevices = graph.getDevices();

        // Assert
        assertArrayEquals(devices, returnedDevices);
    }

    @Test
    public void DeviceGraphDoesNotAcceptNullArgument() {
        // Arrange
        Device[] devices = null;

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> {
            DeviceGraph graph = new DeviceGraph(devices);
        });
    }

    @Test
    public void GraphNeighboursAreActualNeighboursForNonRouters() {
        // Arrange
        Device[] devices = new Device[] {
                new Device("router1", new Date(), "", DeviceType.Router, "", null, true, true),
                new Device("router2", new Date(), "", DeviceType.Router, "", null, true, true),
                new Device("device1", new Date(), "", DeviceType.LightBulb, "", "router1", true, true),
                new Device("device2", new Date(), "", DeviceType.LightBulb, "", "router1", true, true),
                new Device("device3", new Date(), "", DeviceType.LightBulb, "", "router2", true, true)
        };

        // Act
        DeviceGraph graph = new DeviceGraph(devices);
        Device[] device1Neighbours = graph.getNeighbours("device1");
        Device[] device2Neighbours = graph.getNeighbours("device2");
        Device[] device3Neighbours = graph.getNeighbours("device3");

        // Assert
        assertEquals("router1", device1Neighbours[0].getDeviceId());
        assertEquals("router1", device2Neighbours[0].getDeviceId());
        assertEquals("router2", device3Neighbours[0].getDeviceId());
    }

    @Test
    public void GraphNeighboursAreActualNeighboursForRouters() {
        // Arrange
        Device[] devices = new Device[] {
                new Device("router1", new Date(), "", DeviceType.Router, "", null, true, true),
                new Device("router2", new Date(), "", DeviceType.Router, "", null, true, true),
                new Device("device1", new Date(), "", DeviceType.LightBulb, "", "router1", true, true),
                new Device("device2", new Date(), "", DeviceType.LightBulb, "", "router1", true, true),
                new Device("device3", new Date(), "", DeviceType.LightBulb, "", "router2", true, true)
        };

        // Act
        DeviceGraph graph = new DeviceGraph(devices);
        Device[] router1Neighbours = graph.getNeighbours("router1");
        Device[] router2Neighbours = graph.getNeighbours("router2");

        // Assert
        assertEquals("device1", router1Neighbours[0].getDeviceId());
        assertEquals("device2", router1Neighbours[1].getDeviceId());
        assertEquals("device3", router2Neighbours[0].getDeviceId());
    }

    // Renamed as was renamed the same as the above test
    @Test
    public void GraphNeighboursAreActualNeighboursForRouters2() {
        // Arrange
        Device[] devices = new Device[] {
                new Device("device1", new Date(), "", DeviceType.LightBulb, "", null, true, true),
                new Device("device2", new Date(), "", DeviceType.LightBulb, "", null, true, true),
                new Device("device3", new Date(), "", DeviceType.LightBulb, "", null, true, true),
                new Device("device4", new Date(), "", DeviceType.LightBulb, "", null, true, true),
                new Device("device5", new Date(), "", DeviceType.LightBulb, "", null, true, true)
        };

        // Act
        DeviceGraph graph = new DeviceGraph(devices);

        for (Device device: devices) {
            Device[] neighbours = graph.getNeighbours(device.getDeviceId());

            // Assert
            assertEquals(0, neighbours.length);
        }
    }

    @Test
    public void GraphGetNeighboursThrowsExceptionIfDeviceIdArgumentIsNull() {
        // Arrange
        Device[] devices = new Device[] { };

        // Act
        DeviceGraph graph = new DeviceGraph(devices);

        assertThrows(IllegalArgumentException.class, () -> {
            Device[] neighbours = graph.getNeighbours(null);
        });
    }

    @Test
    public void GraphGetNeighboursThrowsExceptionIfDeviceIdArgumentDoesNotMatch() {
        // Arrange
        Device[] devices = new Device[] { };

        // Act & Assert
        DeviceGraph graph = new DeviceGraph(devices);

        assertThrows(NoSuchElementException.class, () -> {
            Device[] neighbours = graph.getNeighbours("device1");
        });
    }
}
