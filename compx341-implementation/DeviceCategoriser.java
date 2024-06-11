public class DeviceCategoriser {

    public static DeviceCategory getDeviceCategory(DeviceType deviceType) {
        switch (deviceType){
            case Router:
            case Extender:
                return DeviceCategory.WifiRouter;
            case HubController:
                return DeviceCategory.HubsOrController;
            case LightBulb:
            case StripLighting:
            case OtherLighting:
                return DeviceCategory.SmartLighting;
            case Kettle:
            case Toaster:
            case CoffeeMaker:
                return DeviceCategory.SmartAppliance;
            case WashingMachineOrDryer:
            case RefrigeratorOrFreezer:
            case Dishwasher:
                return DeviceCategory.SmartWhiteware;
            default:
                throw new IllegalArgumentException("Invalid DeviceType");
        }
    }
}
