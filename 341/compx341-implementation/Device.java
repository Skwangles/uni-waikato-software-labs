import jdk.jshell.spi.ExecutionControl;

import java.util.Date;

public class Device {

    private String deviceId;
    private Date dateConnected;
    private String deviceName;
    private DeviceType deviceType;
    private DeviceCategory deviceCategory;
    private String householdId;
    private String routerConnection;
    private boolean sends;
    private boolean receives;

    public Device(String deviceId, Date dateConnected, String deviceName, DeviceType deviceType, String householdId, String routerConnection, boolean sends, boolean receives){
        if(deviceId == null || dateConnected == null || deviceName == null || deviceType == null || householdId == null)
            throw new IllegalArgumentException("One or more inputs were null! Only routerConnection may be null!");

        this.deviceId = deviceId;
        this.dateConnected = dateConnected;
        this.deviceName = deviceName;
        this.deviceType = deviceType;
        deviceCategory = DeviceCategoriser.getDeviceCategory(deviceType);

        this.householdId = householdId;
        this.routerConnection = routerConnection;
        this.sends = sends;
        this.receives = receives;
    }

    String getDeviceId(){ return deviceId;}
    Date getDateConnected(){ return dateConnected;}
    String getDeviceName(){ return deviceName;}
    DeviceCategory getDeviceCategory(){ return deviceCategory;}
    DeviceType getDeviceType(){ return deviceType;}
    String getHouseholdId(){ return householdId;}
    String getHouseholdRegion(){ throw new RuntimeException("Not yet implemented");}
    String getRouterConnection(){ return routerConnection;}

    public boolean getSends() { return sends; }

    public boolean getReceives() { return receives; }
}
