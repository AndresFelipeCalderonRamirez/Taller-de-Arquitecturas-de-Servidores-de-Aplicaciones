package lab.tdse.laboratory.Protocols.util;

import java.util.HashMap;
import java.util.Map;

public class Request {

    private String path;
    private String method;
    private Map<String, String> queryParams = new HashMap<>();

    public Request(String path, String method, String query) {
        this.path = path;
        this.method = method;
        if (query != null) {
            for (String param : query.split("&")) {
                String[] kv = param.split("=");
                if (kv.length == 2) {
                    queryParams.put(kv[0], kv[1]);
                }
            }
        }
    }

    public String getPath() { return path; }
    public String getMethod() { return method; }

    public String getValues(String key) {
        return queryParams.getOrDefault(key, "");
    }

}