import java.util.Scanner;
import org.graphstream.graph.*;
import org.graphstream.graph.implementations.*;

public class GraphVisualiser {
    private static Graph graph;

    public GraphVisualiser(){
        // Set up all the required graphStream setups including style sheets.
        System.setProperty("org.graphstream.ui", "swing");
        graph = new SingleGraph("ESGP");
        graph.setAttribute("ui.library", "swing");
        String cssFilePath = new Scanner(GraphVisualiser.class.getResourceAsStream("stylesheet.css"), "UTF-8").useDelimiter("\\A").next();
        graph.setAttribute("ui.stylesheet", cssFilePath);
    }

    /*
     * Takes the given device graph and creates all the nodes
     * and all the edges to convert the device graph to a 
     * GraphStream graph.
     * @param the device graph we want to convert.
     */
    public void convertGraph(DeviceGraph deviceGraph){
        // Loop trhough all devices in deviceGraph and add then as nodes.
        addNodes(deviceGraph);
        // Loop through all devices using the deviceGraph.getNeighbours to creates edges for each device
        addEdges(deviceGraph);
    }

    /*
     * For every device in the device graph create a node for
     * the device and using the category to specify the style attribute 
     * for the graph.
     * @param the device graph we want to convert.
     */
    private static void addNodes(DeviceGraph deviceGraph){
        Device[] devices = deviceGraph.getDevices();

        for(Device d : devices){
            Node node = graph.addNode(d.getDeviceId());
            node.setAttribute("ui.label", d.getDeviceType());

            Device.setDeviceCategory(d.getDeviceType());
            DeviceCategory cat = d.getDeviceCategory();

            if(cat.equals(DeviceCategory.WifiRouter)){
                node.setAttribute("ui.class", "Router");
            } else if(cat.equals(DeviceCategory.HubOrController)){
                node.setAttribute("ui.class", "Hub");
            } else if(cat.equals(DeviceCategory.SmartLighting)){
                node.setAttribute("ui.class", "Light");
            } else if(cat.equals(DeviceCategory.SmartAppliance)){
                node.setAttribute("ui.class", "Appliance");
            } else if(cat.equals(DeviceCategory.SmartWhiteware)){
                node.setAttribute("ui.class", "whiteware");
            } else{
                node.setAttribute("ui.class", "Other");
            }
        }
    }

    /*
     * For every device in the device graph create the appropriate edges for
     * the devices neighbouts if the device can send data use that as the arrow direction.
     * @param the device graph we want to convert.
     */
    private static void addEdges(DeviceGraph deviceGraph){
        Device[] devices = deviceGraph.getDevices();

        for(Device d : devices){
            if(d.isSender()){
                Device[] neighbours = deviceGraph.getNeighbours(d.getDeviceId());
                for(Device n: neighbours){
                    Edge edge = graph.addEdge(n.getDeviceId() + ":" + d.getDeviceId(), d.getDeviceId(), n.getDeviceId(), true);
                    edge.setAttribute("ui.class", "canSend");
                }
            }

        }
    }

    /*
     * Display the graph in a new UI GraphStream window
     */
    public void visualiseGraph(){

        graph.display();
    }
}
