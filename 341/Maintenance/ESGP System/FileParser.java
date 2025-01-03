import java.io.*;
import java.util.*;
import java.text.SimpleDateFormat;  

/*
* Class which acts as the file paser to collect device information
* Author: SoftFlux Engineer
*/
public class FileParser {
    
    public FileParser(){

    }

    /*
     * Using the information given in the passed file, read in the
     * infermation and create a device for every succesffully read in device.
     * @params the buffered reader with a file path for input already set
     * @returns a list of devices created using the file information
     */
    public Device[] parseFile(BufferedReader reader){
        if(reader == null){
            throw new IllegalArgumentException();
        }

        String line = "";
        int count = 0;
        Device[] devicesNull = null;
        List<Device> devs = new ArrayList<>();
        String[] tempReader;
        String id;
        Date date;
        String name;
        DeviceType type;
        String house;
        String router;
        Boolean sends;
        Boolean recieves;

        try{
            if(reader.readLine() == null){
                return null;
            }
            while((line = reader.readLine()) != null) {
                tempReader = line.split(",");

                id = tempReader[0];
                date = new SimpleDateFormat("dd/MM/yy").parse(tempReader[1]);  
                name = tempReader[2];

                // Deal with the difficult device types.
                if(tempReader[3].equals("Washing Machine/ Dryer"))
                    type = DeviceType.WashingMachineOrDryer;
                else if(tempReader[3].equals("Refrigerator/ Freezer"))
                    type = DeviceType.RefrigeratorOrFreezer;
                // Assign if simple type
                else
                    type = DeviceType.valueOf(tempReader[3]);

                house = tempReader[4];
                router = tempReader[5];

                // Boolean checks
                if(tempReader[6].equals("Yes"))
                    sends = true;
                else
                    sends = false;

                if(tempReader[7].equals("Yes"))
                    recieves = true;
                else
                    recieves = false;

                // Create Device
                Device newDevice = new Device(id, date, name, type, house, router, sends, recieves);
                devs.add(newDevice);

            }
            reader.close();
            // return

            Device[] devices = new Device[devs.size()];
            for(int i=0; i<devs.size(); i++){
                devices[i] = devs.get(i);
            }
            return devices;

        } catch(Exception e) {
            System.out.println(e);
        }
        return devicesNull;

    }
}
