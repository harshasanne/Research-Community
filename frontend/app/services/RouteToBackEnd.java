package services;

public class RouteToBackEnd {
    private static String BackendURL = "http://localhost:9001/";

    public static String getLoginURL() {
        return BackendURL + "loginResult";
    }
    public static String getUserProfileURL(){return BackendURL + "userProfile";}
}
