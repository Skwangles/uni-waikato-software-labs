import java.io.*;
import java.util.*;

/*
* Class which acts as the main console application for the ESGP
* Author: SoftFlux Engineer
*/
public class ConsoleApp {

    private static final String ENCOST_DATASET_FILEPATH = "Encost Smart Homes Dataset (bigger).txt";
    private static Boolean userIsVerified = false;
    private static Scanner scanner;
    private static DeviceGraph graph;

    public static void main(String[] args){

        // UI welcome output
        System.out.println("Welcome to Encost Smart Graph Project (ESGP)!");
        System.out.println("-------------------------------------");
        
        scanner = new Scanner(System.in);
        // Get users version request
        getUserVersion();
        // Load in the ESHD
        loadFile();
        // Allow the user to pick a feature.
        selectFeature();

        // Close scanner as program is closed.
        scanner.close();

    }

    /*
     * Returns string array of all options avaliable to the user
     * based on the given user type.
     * @param user the type of user currently using the program.
     * @returns a string array with the avaliable options
     */
    public static String[] getOptions(UserType user) throws IllegalArgumentException{

        // Initilise options strings
        String[] optionsCommunity = {"1. Visualise graph of dataset", "2. Exit"};
        String[] optionsVerified = {"1. Load custom dataset", "2. Visualise graph of dataset",
                                        "3. View summary statistics", "4. Exit"};

        if(user.equals(UserType.EncostVerified)){
            // Return verified features
            return optionsVerified;

        }
        else if(user.equals(UserType.EncostUnverified)){
            throw new IllegalArgumentException();
        }
        // Return simple edition of features
        return optionsCommunity;

    }

    /*
     * Continuously asks the user what feature they would like to use.
     * Displays different avaliable features depending on the user type.
     */
    private static void selectFeature(){

        String[] avaliableOptions = getOptions(ApplicationState.getUserType());
        while(true){
            // UI output
            System.out.println("\nFeature Options\n-------------------------------------");
            System.out.println("A dataset has been loaded! What do you want to do?\n");
            // Display features
            for(String s : avaliableOptions){
                System.out.println(s);
            }
            System.out.println("");
            String input = scanner.nextLine();

            // Community user
            if(ApplicationState.getUserType().equals(UserType.Community)){    
                if(input.equals("1")){
                    // UI output
                    System.out.println("\nGraph Visualisation\n-------------------------------------");
                    // Visualise graph
                    displayGraph();
                }
                else if(input.equals("2")){
                    // exit
                    System.exit(0);
                    return;
                }
            }
            else if(ApplicationState.getUserType().equals(UserType.EncostVerified)){
                if(input.equals("1")){
                    // UI output
                    System.out.println("\nLoad Custom Dataset\n-------------------------------------");
                    // Load Custom dataset
                    // getCustomFile();
                    // loadFile();
                    unimplementedMessage();
                }
                else if(input.equals("2")){
                    // UI output
                    System.out.println("\nGraph Visualisation\n-------------------------------------");
                    // Visualise graph
                    displayGraph();
                }
                else if(input.equals("3")){
                    // UI output
                    System.out.println("\nView Summary Statistics\n-------------------------------------");
                    // View summary statistics
                    // showSummary();
                    unimplementedMessage();
                }
                else if(input.equals("4")){
                    // exit
                    System.exit(0);
                    return;
                }
            }
        }   
    }

    /*
     * Create a graph visualiser, use it to convert the device
     * graph to a GraphStream graph and then visualise it in a new window.
     */
    public static void displayGraph(){
    	GraphVisualiser graphVisuals = new GraphVisualiser();
    
    	// Find all households in graph
        HashMap<String, List<Device>> households = new HashMap<String, List<Device>>();
    	for (Device device: graph.getDevices()) {
    		if (households.containsKey(device.getHouseholdId())){
    			households.get(device.getHouseholdId()).add(device);
    		}
    		else {
    			households.put(device.getHouseholdId(), new ArrayList<Device>() {{
    				add(device);
    			}});
    		}
    	}
    	
    	// Continually ask for a valid response
    	while (true) {
    	
	    	// Print households as options
	    	System.out.println("Choose a household to visualise:");
	    	String[] householdKeys = households.keySet().toArray(new String[households.size()]);
	    	for (int i = 1; i <= householdKeys.length; i++){
	    		System.out.println(i + ". " + householdKeys[i-1]);
	    	}
	    	System.out.println("or press 'a' for ALL");

            // User input
	    	System.out.print(": ");
	    	String input = scanner.nextLine();
	    	
            // Handle 'All' households
            if (input.equals("a")) {
                graphVisuals.convertGraph(graph);
                break;
            }    
		
		int optionIndex = -1;
		// Convert to array index & handle invalid inputs
		try {
			optionIndex = Integer.parseInt(input) - 1;
		} catch (NumberFormatException ex) {
		    	System.out.println("Your selection was invalid. Please try again!");
			continue;
		}

		if (optionIndex < 0 || optionIndex >= householdKeys.length) {
		    	System.out.println("Your selection was invalid. Please try again!");
		    	continue;
		}
		
		
		System.out.println("Displaying household: " + householdKeys[optionIndex]);
		
		// Create DeviceGraph with specifically devices in specific household
		List<Device> devicesList = households.get(householdKeys[optionIndex]);
		Device[] householdSpecificDevices = devicesList.toArray(new Device[devicesList.size()]);
		
        // Set graph
		graphVisuals.convertGraph(new DeviceGraph(householdSpecificDevices));
        break;

    	}
    
       graphVisuals.visualiseGraph();

    }

    /**
     * Loads in the file which is stored as the encost dataset file.
     * Using the FilePaser to do so then stores the returned devices as an array of devices
     * Using the device array to create new graph.
     */
    public static void loadFile(){
        try{
            // Create buffered reader to only load the standard dataset
            BufferedReader reader = new BufferedReader(new FileReader(ENCOST_DATASET_FILEPATH));

            FileParser parser = new FileParser();
            Device[] devices = parser.parseFile(reader);

            if(devices != null){
                graph = new DeviceGraph(devices);
            }
        }catch(Exception e){
            System.out.println(e);
        }

    }

    /*
     * This function loops until either a user has selected to continue
     * as a community user or to attempt to login as a encost verified user.
     */
    public static void getUserVersion(){
        boolean typeEntered = false;
        
        while(!typeEntered){

            // UI output
            System.out.println("Select your profile type:\n");
            System.out.println("1. Community member");
            System.out.println("2. Encost user\n");

            String input = scanner.nextLine();
            if(input.equals("1")){
                // User is a community user
                typeEntered = true;
                ApplicationState.setUserType(UserType.Community);
            }
            else if(input.equals("2")){
                // Is encost unverified
                typeEntered = true;
                ApplicationState.setUserType(UserType.EncostUnverified);
                boolean goBack = false;

                while(!goBack){
                    // Attempt login
                    userIsVerified = verifyUser();
                    // Collect prompt from result
                    String prompt = getLoginAttemptPrompt(userIsVerified);
                    System.out.println(prompt);
                    // Continue to next steps depending on result
                    if(userIsVerified){
                        ApplicationState.setUserType(UserType.EncostVerified);
                        typeEntered = true;
                        goBack = true;
                    }
                    else{
                        typeEntered = false;
                        input = scanner.nextLine();
                        if(input.equals("2")){
                            goBack = true;
                        }
                    }
                }
            }
        }
        // scanner.close();
    }

    /*
     * Collects the users input as a username and password and
     * verifes the credentials with the verifier class.
     * @returns a boolean of whether the user is verified or not
     */
    private static boolean verifyUser(){

        try{
            Console cli = System.console();
            UserVerifier verifier = new UserVerifier();
            boolean isVerified = false;

            // UI output
            System.out.println("\nAccount Login\n-------------------------------------");

            // Collect username
            System.out.println("Enter your username:");
            String username = scanner.nextLine();

            if(username.equals("") || username.equals("\n")){
                username = null;
            }

            // Collect password
            System.out.println("Enter your password:");
            char[] passwordArray = cli.readPassword();
            for(int i = 0; i < passwordArray.length; i++){
                System.out.print("*");
            }
            System.out.println("");
            String password = String.valueOf(passwordArray);

        
            isVerified = verifier.verifyCredentials(username, password);

            return isVerified;
        } catch(Exception e){
            System.out.println(e);
        }
        return false;
    }

    /*
     * Depedning on ther user verification level this method
     * will return a different prompt as a response to logging in.
     * @param userVerified - If the user has successfully logged in.
     * @returns a string response to the login attempt
     */
    public static String getLoginAttemptPrompt(boolean userVerified){
        if(userVerified){
            String success = "Successfully logged in!";
            return success;
        }
        else{
            String failure = "Those credentials don't match our records!\nWould you like to try again?\n\n1. Try again\n2. Go back";
            return failure;
        }
    }

    /*
     * Communicates to the user that the part of the program they
     * wish to use is currently not implemented due to the project scope.
     */
    public static void unimplementedMessage(){
        System.out.println("\nUnimplemented\n-------------------------------------");
        System.out.println("This part of the ESGP is unimplemented \ndue to the projects current scope.");
    }
    
}
