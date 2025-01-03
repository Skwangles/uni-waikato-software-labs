/*
* Store the state of the application given a particular users access rights.
* Author: SoftFlux Engineer
*/
public class ApplicationState {

    private static UserType userType;

    public ApplicationState(){
        UserType userType = UserType.Community;
    }

    /*
     * Returns the user type of the current application
     */
    public static UserType getUserType()
    {
        return userType;
    }

    /*
     * Sets the user type of the current application
     */
    public static void setUserType(UserType UserType)
    {
        userType = UserType;
    }
    
}
