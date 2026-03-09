package lab.tdse.project.homework;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

public class MicroSpringBootg3 {
    public static void main(String[] args) throws Exception {
        Class<?> c = Class.forName(args[0]);
        if (c.isAnnotationPresent(RestController.class)) {
            Object instance = c.getDeclaredConstructor().newInstance();
            for (Method m : c.getDeclaredMethods()) {
                if (m.isAnnotationPresent(GetMapping.class)) {
                    String path = m.getAnnotation(GetMapping.class).value();
                    Router.get(path, (req, res) -> {
                        try {
                            // Resolver @RequestParam de cada parámetro
                            Parameter[] params = m.getParameters();
                            Object[] methodArgs = new Object[params.length];
                            for (int i = 0; i < params.length; i++) {
                                if (params[i].isAnnotationPresent(RequestParam.class)) {
                                    RequestParam rp = params[i].getAnnotation(RequestParam.class);
                                    String val = req.getValues(rp.value());
                                    methodArgs[i] = val.isEmpty() ? rp.defaultValue() : val;
                                }
                            }
                            return (String) m.invoke(instance, methodArgs);
                        } catch (Exception e) {
                            return "Error: " + e.getMessage();
                        }
                    });
                }
            }
        }
        HttpServer.main(args); // ← arranca el servidor del Repo 2
    }
}
