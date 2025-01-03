import java.util.*;

/*
* Class which acts as a categoriser to sort devices into their 
* according to the device type.
* Author: SoftFlux Engineer
*/
public class DeviceCategoriser {

    private static HashMap <DeviceType, DeviceCategory> mapping; 

    public DeviceCategoriser(){

        mapping = new HashMap<DeviceType, DeviceCategory>();

        // Map all the device types to their appropriate category
        mapping.put(DeviceType.Router, DeviceCategory.WifiRouter);
        mapping.put(DeviceType.Extender, DeviceCategory.WifiRouter);
        mapping.put(DeviceType.HubController, DeviceCategory.HubOrController);
        mapping.put(DeviceType.LightBulb, DeviceCategory.SmartLighting);
        mapping.put(DeviceType.StripLighting, DeviceCategory.SmartLighting);
        mapping.put(DeviceType.OtherLighting, DeviceCategory.SmartLighting);
        mapping.put(DeviceType.Kettle, DeviceCategory.SmartAppliance);
        mapping.put(DeviceType.Toaster, DeviceCategory.SmartAppliance);
        mapping.put(DeviceType.CoffeeMaker, DeviceCategory.SmartAppliance);
        mapping.put(DeviceType.WashingMachineOrDryer, DeviceCategory.SmartWhiteware);
        mapping.put(DeviceType.RefrigeratorOrFreezer, DeviceCategory.SmartWhiteware);
        mapping.put(DeviceType.Dishwasher, DeviceCategory.SmartWhiteware);
    }

    /*
     * Returns the item releveant to the key within the hash map. 
     * @params the device type of the device
     * @returns the category of the passed device
     */
    public DeviceCategory getDeviceCategory(DeviceType deviceType){

        return mapping.get(deviceType);
    }
}
