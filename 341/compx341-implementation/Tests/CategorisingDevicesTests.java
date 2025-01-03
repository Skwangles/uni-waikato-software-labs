import org.junit.jupiter.api.Test;

import java.util.Date;
import java.util.Hashtable;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests whether each of the values passed to the Device constructor corre-
 * spond to the appropriate fields
 */
public class CategorisingDevicesTests {
    @Test
    public void CreatedDeviceMatchesInputValues() {
        // Arrange
        String deviceId = "device1";
        Date dateConnected = new Date(2023, 01, 01);
        String deviceName = "name";
        DeviceType deviceType = DeviceType.LightBulb;
        String householdId = "household1";
        String routerConnection = "router1";
        boolean sends = false;
        boolean receives = true;

        // Act
        Device device = new Device(deviceId, dateConnected, deviceName, deviceType, householdId, routerConnection, sends, receives);

        // Assert
        assertEquals(deviceId, device.getDeviceId());
        assertEquals(dateConnected, device.getDateConnected());
        assertEquals(deviceName, device.getDeviceName());
        assertEquals(deviceType, device.getDeviceType());
        assertEquals(householdId, device.getHouseholdId());
        assertEquals(routerConnection, device.getRouterConnection());
        assertEquals(sends, device.getSends());
        assertEquals(receives, device.getReceives());
        assertEquals(DeviceCategory.SmartLighting, device.getDeviceCategory());
    }

    @Test
    public void CreatedDeviceMatchesInputValuesWhenRouterConnectionNotSet() {
        // Arrange
        String deviceId = "router1";
        Date dateConnected = new Date(2023, 01, 01);
        String deviceName = "name";
        DeviceType deviceType = DeviceType.Router;
        String householdId = "household1";
        boolean sends = false;
        boolean receives = true;

        // Act
        Device device = new Device(deviceId, dateConnected, deviceName, deviceType, householdId, null, sends, receives);

        // Assert
        assertEquals(deviceId, device.getDeviceId());
        assertEquals(dateConnected, device.getDateConnected());
        assertEquals(deviceName, device.getDeviceName());
        assertEquals(deviceType, device.getDeviceType());
        assertEquals(householdId, device.getHouseholdId());
        assertNull(device.getRouterConnection());
        assertEquals(sends, device.getSends());
        assertEquals(receives, device.getReceives());
        assertEquals(DeviceCategory.WifiRouter, device.getDeviceCategory());
    }

    @Test
    public void DeviceConstructorThrowsExceptionIfAllArgumentsNull() {
        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> {
            Device device = new Device(null, null, null, null, null, null, true, true);
        });
    }

    @Test
    public void DeviceCategoriesMatchExpected() {
        // Arrange
        DeviceType[] deviceTypes = DeviceType.values();
        Hashtable<DeviceType, DeviceCategory> deviceTypeMapping = new Hashtable<>();

        deviceTypeMapping.put(DeviceType.Router, DeviceCategory.WifiRouter);
        deviceTypeMapping.put(DeviceType.Extender, DeviceCategory.WifiRouter);
        deviceTypeMapping.put(DeviceType.HubController, DeviceCategory.HubsOrController);
        deviceTypeMapping.put(DeviceType.LightBulb, DeviceCategory.SmartLighting);
        deviceTypeMapping.put(DeviceType.StripLighting, DeviceCategory.SmartLighting);
        deviceTypeMapping.put(DeviceType.OtherLighting, DeviceCategory.SmartLighting);
        deviceTypeMapping.put(DeviceType.Kettle, DeviceCategory.SmartAppliance);
        deviceTypeMapping.put(DeviceType.Toaster, DeviceCategory.SmartAppliance);
        deviceTypeMapping.put(DeviceType.CoffeeMaker, DeviceCategory.SmartAppliance);
        deviceTypeMapping.put(DeviceType.WashingMachineOrDryer, DeviceCategory.SmartWhiteware);
        deviceTypeMapping.put(DeviceType.RefrigeratorOrFreezer, DeviceCategory.SmartWhiteware);
        deviceTypeMapping.put(DeviceType.Dishwasher, DeviceCategory.SmartWhiteware);


        for (DeviceType deviceType: deviceTypes) {
            // Act
            DeviceCategory actual = DeviceCategoriser.getDeviceCategory(deviceType);
            DeviceCategory expectedCategory = deviceTypeMapping.get(deviceType);

            // Assert
            assertEquals(expectedCategory, actual);
        }
    }
}
