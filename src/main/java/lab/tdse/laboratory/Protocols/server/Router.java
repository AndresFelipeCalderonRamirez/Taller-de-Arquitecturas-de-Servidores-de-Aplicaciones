package lab.tdse.laboratory.Protocols.server;

import lab.tdse.laboratory.Protocols.util.Request;
import lab.tdse.laboratory.Protocols.util.Response;
import lab.tdse.laboratory.Protocols.util.WebMethod;

import java.util.HashMap;
import java.util.Map;

public class Router {
    private static final Map<String, WebMethod> routes = new HashMap<>();
    private static String staticFilesFolder = "";

    public static void get(String path, WebMethod handler) {
        routes.put(path, handler);
    }

    public static void staticfiles(String folder) {
        staticFilesFolder = folder;
    }

    public static String getStaticFilesFolder() {
        return staticFilesFolder;
    }

    public static String handle(String path, Request req, Response res) {
        WebMethod handler = routes.get(path);
        if (handler != null) {
            return handler.execute(req, res);
        }
        res.status(404);
        return "404 Not Found";
    }

    public static boolean hasRoute(String path) {
        return routes.containsKey(path);
    }
}
