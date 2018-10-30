package services;

public class RouteToBackend {


    private static String BackendURL = "http://localhost:9000/";

    public static String getLogInURL() {
        return BackendURL + "login";
    }
//    public static String getSignUpURL() { return BackendURL + "signup";
//    }

}