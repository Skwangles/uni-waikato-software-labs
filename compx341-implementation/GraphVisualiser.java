import org.graphstream.graph.Graph;
import org.graphstream.graph.implementations.SingleGraph;

import java.util.Scanner;

public class GraphVisualiser {
    Graph graph;
    long startTime;
    long previousTime;
    private final long nanosecondsPerMilli = 1000000;

    public void visualiseGraph() {
        if (graph == null) throw new RuntimeException("Graph object has not been initalised!");
        graph.display();
    }

    void convertGraph(DeviceGraph deviceGraph) {
        startTime = System.nanoTime();
        previousTime = System.nanoTime();

        graph = new SingleGraph("Encost Dataset Visualisation");
        addNodes(deviceGraph);
        addEdges(deviceGraph);

        String cssFilePath = new Scanner(ConsoleApp.class.getClassLoader().getResourceAsStream("stylesheet.css"), "UTF-8").useDelimiter("\\A").next();
        graph.setAttribute("ui.stylesheet", cssFilePath);
    }

    private void addNodes(DeviceGraph deviceGraph) {
        if (deviceGraph == null) throw new IllegalArgumentException("Device graph cannot be null!");
        if (graph == null) throw new RuntimeException("Graph object has not been initalised!");

        Device[] devices = deviceGraph.getDevices();

        for (Device device : devices) {
            graph.addNode(device.getDeviceId());
            graph.getNode(device.getDeviceId()).setAttribute("device", device);
            graph.getNode(device.getDeviceId()).setAttribute("ui.class", device.getDeviceCategory().toString());
            graph.getNode(device.getDeviceId()).setAttribute("ui.label", device.getDeviceName());
            previousTime = checkTiming(startTime, previousTime);
        }
    }

    private void addEdges(DeviceGraph deviceGraph) {
        if (deviceGraph == null) throw new IllegalArgumentException("Device graph cannot be null!");
        if (graph == null) throw new RuntimeException("Graph object has not been initalised!");

        Device[] devices = deviceGraph.getDevices();

        for (Device device : devices) {

            Device[] neighbours = deviceGraph.getNeighbours(device.getDeviceId());
            assert (neighbours != null); //ID will always exist
            for (Device neighbour : neighbours) {
                graph.addEdge(device.getDeviceId() + "/" + neighbour.getDeviceId(), device.getDeviceId(), neighbour.getDeviceId(), true);//Directed connection
            }
            previousTime = checkTiming(startTime, previousTime);
        }
    }

    private long checkTiming(long start, long previous) {
        double msElapsed = (System.nanoTime() - previous) / nanosecondsPerMilli;
        double msFromStart = (System.nanoTime() - start) / nanosecondsPerMilli;
        if (msFromStart > 5000) {
            throw new RuntimeException("The system took too long to process the graph! Please choose a smaller dataset!");
        } else if (msElapsed > 1000) {
            System.out.println("Loading...");
            return System.nanoTime();
        }
        return previous;
    }
}
