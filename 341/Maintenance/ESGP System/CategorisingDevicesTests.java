// import org.junit.jupiter.api.Test;
// import org.junit.assertEquals;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.Date;
import java.util.Hashtable;
import org.junit.Test;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThrows;

// import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests whether each of the values passed to the Device constructor corre-
 * spond to the appropriate fields
 */
public class CategorisingDevicesTests {
    @Test
    public void CreatedDeviceMatchesInputValues() {
        try{
            // Arrange
            String deviceId = "device1";
            // Date constructor depreciated so changing to:
            // Date dateConnected = new Date(2023, 01, 01);
            String date_string = "01/01/2023";
            SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
            Date dateConnected = formatter.parse(date_string); 
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
            assertEquals(sends, device.isSender());
            assertEquals(receives, device.isReceiver());
            assertEquals(DeviceCategory.SmartLighting, device.getDeviceCategory());
        }catch(Exception e){
            System.out.println(e);
        }
    }

    @Test
    public void CreatedDeviceMatchesInputValuesWhenRouterConnectionNotSet() {
        
        try{
            // Arrange
            String deviceId = "router1";
            // Date constructor depreciated so changing to:
            // Date dateConnected = new Date(2023, 01, 01);
            String date_string = "01/01/2023";
            SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
            Date dateConnected = formatter.parse(date_string);    
         

            String deviceName = "name";
            DeviceType deviceType = DeviceType.Router;
            String householdId = "household1";
            boolean sends = false;
            boolean receives = true;

            // This variable has been make and set to null. Test cannot run wihtout variable being created.
            String routerConnection = null;

            // Act
            Device device = new Device(deviceId, dateConnected, deviceName, deviceType, householdId, routerConnection, sends, receives);

            // Assert
            assertEquals(deviceId, device.getDeviceId());
            assertEquals(dateConnected, device.getDateConnected());
            assertEquals(deviceName, device.getDeviceName());
            assertEquals(deviceType, device.getDeviceType());
            assertEquals(householdId, device.getHouseholdId());
            assertNull(device.getRouterConnection());
            assertEquals(sends, device.isSender());
            assertEquals(receives, device.isReceiver());
            assertEquals(DeviceCategory.WifiRouter, device.getDeviceCategory());
        }catch(Exception e){
            System.out.println(e);
        } 
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
        deviceTypeMapping.put(DeviceType.HubController, DeviceCategory.HubOrController);
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
            DeviceCategoriser categoriser = new DeviceCategoriser();
            DeviceCategory actual = categoriser.getDeviceCategory(deviceType);
            DeviceCategory expectedCategory = deviceTypeMapping.get(deviceType);

            // Assert
            assertEquals(expectedCategory, actual);
        }
    }
}
