package controllers;

import play.mvc.*;

/**
 * This controller contains an action to handle HTTP requests
 * to the application's home page.
 */
public class HomeController extends Controller {

    /**
     * An action that renders an HTML page with a welcome message.
     * The configuration in the <code>routes</code> file means that
     * this method will be called when the application receives a
     * <code>GET</code> request with a path of <code>/</code>.
     */
    public Result index() {
        String viewerId = null;
        try {
            viewerId = session("username");
        } catch (Exception e) {
            System.out.println("session error");
            e.printStackTrace();
        }
        if (viewerId == null || viewerId.isEmpty()) {
            return redirect("/login");
        }
        return ok(views.html.index.render(viewerId, ""));
    }

    public Result logout() {
        session().clear();
        return redirect("/login");
    }

}
