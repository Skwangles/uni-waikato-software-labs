
import java.io.*;
import java.util.Objects;

public class ConsoleApp {
    private static final String promptForInputChar = ": ";


    //Used to identify feature selected
    static final String VISUALISE_GRAPH_OF_DATASET = "Visualise graph of dataset";
    static final String LOAD_CUSTOM_DATASET = "Load custom dataset";
    static final String VIEW_SUMMARY_STATISTICS = "View summary statistics";
    static final String EXIT = "Exit";


    public static String[] getOptions(UserType input) {

        switch (input) {
            case EncostVerified:
                return new String[]{LOAD_CUSTOM_DATASET, VISUALISE_GRAPH_OF_DATASET, VIEW_SUMMARY_STATISTICS, EXIT};
            case Community:
                return new String[]{VISUALISE_GRAPH_OF_DATASET, EXIT};
            default:
                throw new IllegalArgumentException("Invalid Type provided: " + input);
        }
    }

    public static void main(String[] args) {
        getUserVersion();
        selectFeature();
    }

    private static void selectFeature() {
        while (true) {


            //Get allowed features
            String[] featureOptions = getOptions(ApplicationState.getUserType());


            //Display options
            System.out.println("Feature Options\n-------------\nWhat do you want to do?\n");
            for (int i = 0; i < featureOptions.length; i++) {
                System.out.println((i + 1) + ". " + featureOptions[i]);
            }

            //Handle user input
            String selection = System.console().readLine(promptForInputChar);
            int optionIndex = -1;

            //Convert to array index & handle invalid inputs
            try {
                optionIndex = Integer.parseInt(selection) - 1;
            } catch (NumberFormatException ex) {
            	System.out.println("Your selection was invalid. Please try again!");
                continue;
            }
            if (optionIndex < 0 || optionIndex >= featureOptions.length) {
            	System.out.println("Your selection was invalid. Please try again!");
            	continue;
		}
		
            //Determine option by getOption array index
            switch (featureOptions[optionIndex]) {
                case VISUALISE_GRAPH_OF_DATASET:
                    System.out.println("Loading dataset...");
                    Device[] devices = loadFile();
                    if (devices == null) {
                        System.out.println("Could not load file");
                        break;
                    }

                    System.out.println("Visualising Graph...");
                    GraphVisualiser graphVisualiser = new GraphVisualiser();
                    DeviceGraph deviceGraph = new DeviceGraph(devices);

                    graphVisualiser.convertGraph(deviceGraph);
                    graphVisualiser.visualiseGraph();

                    break;
                case LOAD_CUSTOM_DATASET:
                    //load custom dataset
                    System.out.println("Custom dataset loading is not currently available.");
                    break;
                case VIEW_SUMMARY_STATISTICS:
                    //summary stats
                    System.out.println("Summary statistics is not currently available.");
                    break;
                case EXIT:
                    System.out.println("Exiting...");
                    System.exit(0);
                    return;
                default:
                    System.out.println("Your selection was invalid. Please try again!");
                    break;
            }

        }
    }

    static Device[] loadFile() {
        FileParser parser = new FileParser();
        return parser.parseFile(new BufferedReader(
                new InputStreamReader(
                        Objects.requireNonNull(
                                ConsoleApp.class.getClassLoader().getResourceAsStream(ENCOST_DATASET_FILE)
                        )
                )
        ));
    }

    static final String ENCOST_DATASET_FILE = "Encost Smart Homes Dataset (small).txt";

    static void getUserVersion() {
        //Loop until valid selection made
        while (true) {
            Console in = System.console();

            System.out.println("Welcome to Encost Smart Graph Project (ESGP)!\n" +
                    "----------------------\n" +
                    "Select your profile type:\n" +
                    "1. Community member\n" +
                    "2. Encost user");

            //Input verification
            switch (in.readLine(promptForInputChar)) {
                case "1":
                    ApplicationState.setUserType(UserType.Community);
                    return;
                case "2":
                    ApplicationState.setUserType(UserType.EncostUnverified);
                    accountLogin();
                    return;
                default:
                    System.out.println("Your selection was not valid. Please try again!");
                    break;
            }
        }
    }

    //#region login methods
    public static String getLoginAttemptPrompt(boolean loginSuccess) {
        return loginSuccess ? "Successfully logged in!\n" : "Those credentials don't match our records!\nWould you like to try again?\n\n1. Try again\n2. Continue as Community User";

    }

    private static void accountLogin() {
        while (ApplicationState.getUserType() == UserType.EncostUnverified) {
            //User interaction
            String username = System.console().readLine("Account Login\n-------------\nEnter Username: ");
            String password = String.valueOf(System.console().readPassword("Password: "));
            System.out.println();

            //Verification
            if (UserVerifier.verifyCredentials(username, password)) {
                ApplicationState.setUserType(UserType.EncostVerified);
                System.out.println(getLoginAttemptPrompt(true));
            } else {
                loginFailure();
            }
        }
    }

    private static void loginFailure() {
        while (true) {
            String selection = System.console().readLine(getLoginAttemptPrompt(false) + "\n" + promptForInputChar);
            System.out.println();

            switch (selection) {
                case "1"://Try again
                    return;
                case "2":
                    ApplicationState.setUserType(UserType.Community);
                    return;
                default:
                    System.out.println("Your selection was not valid. Please try again!");
            }
        }
    }

    //#endregion
}
