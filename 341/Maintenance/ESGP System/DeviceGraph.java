import java.util.*;

public class DeviceGraph {

    private Device[] devices;
    private int[][] adjacencyMatrix;

    public DeviceGraph(Device[] devices){
        if(devices != null){
            this.devices = devices;
            // Create edges
            this.adjacencyMatrix = new int[devices.length][devices.length];
            this.createEdges();
        } else{
            throw new IllegalArgumentException();
        }
    }

    /*
     * Creates the edges which 'connect' the different
     * values within the graph.
     */
    private void createEdges(){
        Device router = null;
        String routerName = null;
        for(Device d: devices){
            // Ensure device is actually connected to a router
            routerName = d.getRouterConnection();
            {
                for(Device r: devices){
                    if(r.getDeviceId().equals(routerName)){
                        router = r;
                    }
                }
            }
            if(router != null && router.getDeviceId().equals(d.getRouterConnection())){
                // if device can send
                if(d.isSender()){
                    adjacencyMatrix[Arrays.asList(devices).indexOf(d)][Arrays.asList(devices).indexOf(router)] = 1;
                }
                // if device can recieve
                if(d.isReceiver()){
                    adjacencyMatrix[Arrays.asList(devices).indexOf(router)][Arrays.asList(devices).indexOf(d)] = 1;
                }
            }
        }
    }


    /*
     * Returns a list of all the devices in the graph
     * @returns the list of devices.
     */
    public Device[] getDevices(){
        return devices;
    }


    /*
     * Returns the neighbours of the given device.
     * @param the device that we want the neighbour of.
     * @returns a list of all the devices which are neighbours.
     */
    public Device[] getNeighbours(String deviceId){
        if(deviceId != null){
            Device[] neighbours = new Device[] {};
            ArrayList<Device> arrayList = new ArrayList<Device>(Arrays.asList(neighbours));  
            int indexOf = -1;

            for(Device d: devices){
                if(d.getDeviceId().equals(deviceId)){
                    indexOf = Arrays.asList(devices).indexOf(d);
                }
            }

            if(indexOf != -1){
                for(int i = 0; i < adjacencyMatrix.length; i++){
                    // device sends to
                    if(adjacencyMatrix[indexOf][i] == 1){
                        arrayList.add(devices[i]);
                    }
                    else if(adjacencyMatrix[i][indexOf] == 1){
                        arrayList.add(devices[i]);
                    }
                }
            }
            else{
                throw new NoSuchElementException();
            }
            neighbours = arrayList.toArray(neighbours);  
            return neighbours;
        }else{
            throw new IllegalArgumentException();
        }
    }

}
