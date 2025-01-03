import java.util.ArrayList;
import java.util.NoSuchElementException;

public class DeviceGraph {
    Device[] devices;
    boolean[][] adjacencyMatrix;

    public DeviceGraph(Device[] devices) {
        if (devices == null) throw new IllegalArgumentException("Null device array!");

        this.devices = devices;
        adjacencyMatrix = new boolean[devices.length][devices.length];

        //Check each device for individual adjacencies
        for (int i = 0; i < devices.length; i++) {
            for (int j = 0; j < devices.length; j++) {
                if (devices[i].getDeviceId().equals(devices[j].getDeviceId())) continue; //Cannot connect to self

                // Check adjacency with must send-to, and be received, along with one being the routerConnection of the other.
                adjacencyMatrix[i][j] = devices[i].getSends() &&
                        devices[j].getReceives() &&
                        ((devices[i].getRouterConnection() != null && devices[i].getRouterConnection().equals(devices[j].getDeviceId())) || (devices[j].getRouterConnection() != null && devices[j].getRouterConnection().equals(devices[i].getDeviceId())));
            }
        }
    }

    Device[] getDevices() {
        return devices;
    }

    Device[] getNeighbours(String deviceId) {
        if (deviceId == null) throw new IllegalArgumentException("Null device ID passed");

        // Find Device by ID
        for (int i = 0; i < devices.length; i++) {
            if (devices[i].getDeviceId().equals(deviceId)) {
                ArrayList<Device> neighbours = new ArrayList<>();
                assert (adjacencyMatrix[i].length == devices.length);

                //Find all adjacent devices
                for (int j = 0; j < adjacencyMatrix.length; j++) {
                    if (adjacencyMatrix[i][j]) {
                        neighbours.add(devices[j]);
                    }
                }
                return neighbours.toArray(new Device[neighbours.size()]);
            }
        }
        throw new NoSuchElementException("No Device found from ID");
    }
}
