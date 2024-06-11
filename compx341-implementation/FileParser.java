import java.io.BufferedReader;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;

public class FileParser {

    private final long nanosecondsPerMilli = 1000000;

    Device[] parseFile(BufferedReader dataset){
        if(dataset == null) throw new IllegalArgumentException("File does not exist, or could not be read!"); // This is the only 'uncaught' exception

        long startTime = System.nanoTime();
        long previousTime = startTime;

        // Reverse index lookup by column name
        HashMap<String, Integer> lookup = new HashMap<>(){{
            put("Device ID", -1);
            put("Date Connected", -1);
            put("Device name", -1);
            put("Device type", -1);
            put("Household ID", -1);
            put("Router Connection", -1);
            put("Sends", -1);
            put("Receives", -1);
        }};
        DateFormat dateFormat = new SimpleDateFormat("dd/MM/yy");
        HashMap<String, Device> devices = new HashMap<>();
        try {


            // First line should always be CSV headers
            String firstLine = dataset.readLine();
            if(firstLine == null) throw new IllegalArgumentException("Dataset file was empty!");
            String[] columns = firstLine.split(",");

            // Validate columns, and determine order in CSV
            if(columns.length != lookup.size()){
                throw new IllegalArgumentException("Invalid number of columns");
            }
            for(int i = 0; i < columns.length; i++){
                if(!lookup.containsKey(columns[i])){
                    return null;
                }
                //Update entry with new index
                lookup.put(columns[i], i);
            }
            if(lookup.containsValue(-1)) {
                throw new IllegalArgumentException("Invalid columns, or duplicate columns!");
            }

            previousTime = checkTiming(startTime, previousTime);


            //Parse device lines
            String line;
            while((line = dataset.readLine()) != null){

                String[] deviceInfo = line.split(",");
                if(deviceInfo.length != lookup.size()) {
                    throw new IllegalArgumentException("A device had too many columns!");
                }

                // Check ID is unique
                String deviceId = deviceInfo[lookup.get("Device ID")];
                if(devices.containsKey(deviceId)) {
                    throw new IllegalArgumentException("Duplicate device ID!");
                }

                Date dateConnected = dateFormat.parse(deviceInfo[lookup.get("Date Connected")]);

                // Check DeviceType can be parsed
                DeviceType deviceType = determineTypeByString(deviceInfo[lookup.get("Device type")]);
                if(deviceType == null){
                    throw new IllegalArgumentException("Invalid Device type was entered for a device!");
                }

                String routerConnection = deviceInfo[lookup.get("Router Connection")];

                // Assumed to be valid
                String deviceName = deviceInfo[lookup.get("Device name")];
                String householdId = deviceInfo[lookup.get("Household ID")];

                //Validate and parse send/receive
                String sendString = deviceInfo[lookup.get("Sends")];
                String receiveString = deviceInfo[lookup.get("Receives")];
                if(!sendString.equals("Yes") && !sendString.equals("No") || (!receiveString.equals("Yes") && !receiveString.equals("No"))) {
                    throw new IllegalArgumentException("A Device has an invalid send/receive status");
                }
                boolean sends = sendString.equals("Yes") ? true : false;
                boolean receives = receiveString.equals("Yes") ? true : false;


                //Add unique device to Hashmap, with
                devices.put(deviceId, new Device(deviceId, dateConnected, deviceName, deviceType, householdId,
                        routerConnection.equals("-") ? null : routerConnection, // Will be checked for validity later
                        sends, receives
                ));


                previousTime = checkTiming(startTime, previousTime);
            }


            for (Device device : devices.values()){
                if(device.getRouterConnection() != null && !devices.containsKey(device.getRouterConnection()))
                    throw new IllegalArgumentException("A device had an invalid router connection!"); // Invalid router connection

                previousTime = checkTiming(startTime, previousTime);
            }

        }
        catch (Exception ex){
            System.out.print("The selected dataset was invalid: ");
            System.out.println(ex.getMessage());
            return null;
        }

        return devices.values().toArray(new Device[devices.size()]);
    }

    private long checkTiming(long start, long previous){
        double msElapsed = (System.nanoTime() - previous) / nanosecondsPerMilli;
        double msFromStart = (System.nanoTime() - start) / nanosecondsPerMilli;
        if(msFromStart > 10000) {
            throw new RuntimeException("The system took too long to load the file! Please choose a smaller dataset!");
        }
        else if(msElapsed > 1000) {
            System.out.println("Loading...");
            return System.nanoTime();
        }
        return previous;
    }

    public DeviceType determineTypeByString(String deviceType){
        switch (deviceType){
            case "Router":
                return DeviceType.Router;
            case "Extender":
                return DeviceType.Extender;
            case "Hub/Controller":
                return DeviceType.HubController;
            case "Light Bulb":
                return DeviceType.LightBulb;
            case "Strip Lighting":
                return DeviceType.StripLighting;
            case "Other Lighting":
                return DeviceType.OtherLighting;
            case "Kettle":
                return DeviceType.Kettle;
            case "Toaster":
                return DeviceType.Toaster;
            case "Coffee Maker":
                return DeviceType.CoffeeMaker;
            case "Washing Machine/Dryer":
                return DeviceType.WashingMachineOrDryer;
            case "Refrigerator/Freezer":
                return DeviceType.RefrigeratorOrFreezer;
            case "Dishwasher":
                return DeviceType.Dishwasher;
            default:
                return null;
        }
    }

}
