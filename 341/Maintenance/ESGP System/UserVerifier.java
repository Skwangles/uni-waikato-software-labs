import java.io.*;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.*;

/*
* Class which acts as the verifier of user credetials and encrypts 
* all passwords privately.
* Author: SoftFlux Engineer
*/
public class UserVerifier {
    private static final String USER_DETAILS_FILEPATH = "users.txt";
    // The test_users file path is used only for testing purposes due to FSTP #4.
    private static final String TEST_USERS = "testUsers.txt";
    private static Hashtable<String, byte[]> privateHashtable;

    /*
     * Constructor for UserVerifer. Creates the private hashtable to store the user
     * credentials and encodes the password with SHA-256.
     */
    public UserVerifier()throws FileNotFoundException, NoSuchAlgorithmException{

        privateHashtable = new Hashtable<>();

        File credentials = new File(USER_DETAILS_FILEPATH);
        Scanner scanner = new Scanner(credentials);
        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        int lineCount = 0;

        while(scanner.hasNextLine()){
            String line = scanner.nextLine();
            lineCount++;

            String[] input = line.split(",");
            byte[] encodedPassword = digest.digest(input[1].getBytes(StandardCharsets.UTF_8));
            privateHashtable.put(input[0], encodedPassword);
        }
        scanner.close();

    }

    /*
     * Takes the passed in user credentials and verifies them against the encrpyted 
     * hashtable to find if they username and password are vaild.
     * @param the username given from the user.
     * @param the password entered by the user.
     * @returns a boolean of if the credentials are valid.
     */
    public boolean verifyCredentials(String username, String password) throws IllegalArgumentException, NoSuchAlgorithmException {
        boolean verified = false;
        MessageDigest digest = MessageDigest.getInstance("SHA-256");

        // Throw error if null information is given
        if(username == null || password == null){
            throw new IllegalArgumentException();
        }
        // Return false if empty details are given.
        if(username == "" || password == ""){
            return verified;
        }

        // Check if the entered username exists in the hastable
        if(privateHashtable.containsKey(username)){
            // check for given password
            byte[] encodedInput = digest.digest(password.getBytes(StandardCharsets.UTF_8));
            
            boolean passwordIsEqual = MessageDigest.isEqual(encodedInput, privateHashtable.get(username));
            
            if(passwordIsEqual){
                // now both are true return true.
                verified = true;
            }
        }
        return verified;
    }
}
