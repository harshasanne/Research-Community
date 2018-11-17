package util;

public class Constants {
    public final static String BACKEND = "http://localhost:9001";

    public final static String getAuthorPapersURL = Constants.BACKEND + "/author/";
    public final static String postPaperURL = Constants.BACKEND + "/paper";
    public final static String getFollowingNewsURL = Constants.BACKEND + "/following/news/";
    public final static String getAllFollowingsURL = Constants.BACKEND + "/following/";
    public final static String followURL = Constants.BACKEND + "/follow";
    public final static String unfollowURL = Constants.BACKEND + "/unfollow";
    public final static String userProfileURL = Constants.BACKEND + "/userProfile";
    public final static String loginURL = Constants.BACKEND + "/loginResult";
}
