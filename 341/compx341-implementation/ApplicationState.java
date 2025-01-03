public class ApplicationState {
        private static UserType userType;
        static public UserType getUserType() {return userType;}
        static public void setUserType(UserType newType) {userType =  newType;}
}
