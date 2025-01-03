import java.util.*;

/*
* Class which acts as the device class creating a structure of how
* device information must be stored for the ESGP.
* Author: SoftFlux Engineer
*/
public class Device {
    private String deviceId;
    private Date dateConnected;
    private String deviceName;
    private DeviceType deviceType;
    private String householdId;
    private String routerConnection;
    private Boolean sends;
    private Boolean recieves;
    private static DeviceCategory deviceCategory;
    
    public Device(String deviceId, Date dateConnected, String deviceName, DeviceType deviceType, String householdId, String routerConnection, Boolean sends, Boolean recieves) throws IllegalArgumentException{
        
        // Check for null values
        if(deviceId == null || dateConnected == null || deviceName == null || deviceType == null || householdId == null || sends == null || recieves == null) {
            throw new IllegalArgumentException();
        }

        this.deviceId = deviceId;
        this.dateConnected = dateConnected;
        this.deviceName = deviceName;
        this.deviceType = deviceType;
        this.householdId = householdId;
        this.routerConnection = routerConnection;
        this.sends = sends;
        this.recieves = recieves;

        setDeviceCategory(deviceType);

    }

    /*
     * Using the given type of device get the device category using the
     * device categoriser.
     * @params the type of device to be categorised.
     */
    public static void setDeviceCategory(DeviceType deviceType){
        DeviceCategoriser categoriser = new DeviceCategoriser();
        deviceCategory = categoriser.getDeviceCategory(deviceType);
    }

    // Below are getter methods for each device attribute
    // that is required to successful create a device
    
    public String getDeviceId(){
        return deviceId;
    }

    public Date getDateConnected(){
        return dateConnected;
    }

    public String getDeviceName(){
        return deviceName;
    }

    public DeviceType getDeviceType(){
        return deviceType;
    }

    public DeviceCategory getDeviceCategory(){
        return deviceCategory;
    }

    public String getHouseholdId(){
        return householdId;
    }

    public String getRouterConnection(){
        return routerConnection;
    }

    public Boolean isSender(){
        return sends;
    }

    public Boolean isReceiver(){
        return recieves;
    }
}
