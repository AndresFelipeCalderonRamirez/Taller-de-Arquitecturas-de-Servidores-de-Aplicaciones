package lab.tdse.laboratory.Protocols;

import lab.tdse.laboratory.Protocols.annotations.GetMapping;
import lab.tdse.laboratory.Protocols.annotations.RequestParam;
import lab.tdse.laboratory.Protocols.server.HttpServer;
import lab.tdse.laboratory.Protocols.server.Router;
import lab.tdse.laboratory.Protocols.annotations.RestController;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MicroSpringBootg3 {
    public static void main(String[] args) throws Exception {
        if (args.length > 0) {
            // Opción A: clase pasada como argumento
            System.out.println("Cargando controlador: " + args[0]);
            registerController(Class.forName(args[0]));
        } else {
            // Opción B: auto-scan del classpath
            System.out.println("Auto-escaneando classpath buscando @RestController...");
            scanClasspath();
        }
        HttpServer.main(new String[]{});
    }

    // Registra un controlador en el Router usando reflexión
    private static void registerController(Class<?> c) throws Exception {
        if (!c.isAnnotationPresent(RestController.class)) {
            System.out.println("ADVERTENCIA: " + c.getName() + " no tiene @RestController, ignorando.");
            return;
        }
        Object instance = c.getDeclaredConstructor().newInstance();
        for (Method m : c.getDeclaredMethods()) {
            if (m.isAnnotationPresent(GetMapping.class)) {
                String path = m.getAnnotation(GetMapping.class).value();
                System.out.println("  Registrando ruta: " + path + " -> " + m.getName());
                Router.get(path, (req, res) -> {
                    try {
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

    // Escanea target/classes buscando todas las clases con @RestController
    private static void scanClasspath() throws Exception {
        File classesDir = new File("target/classes");
        List<Class<?>> found = findClasses(classesDir, classesDir);
        for (Class<?> c : found) {
            if (c.isAnnotationPresent(RestController.class)) {
                System.out.println("Encontrado: " + c.getName());
                registerController(c);
            }
        }
    }

    // Recorre recursivamente el directorio convirtiendo .class a nombres de clase
    private static List<Class<?>> findClasses(File root, File dir) {
        List<Class<?>> classes = new ArrayList<>();
        File[] files = dir.listFiles();
        if (files == null) return classes;
        for (File file : files) {
            if (file.isDirectory()) {
                classes.addAll(findClasses(root, file));
            } else if (file.getName().endsWith(".class")) {
                String relativePath = root.toURI().relativize(file.toURI()).getPath();
                String className = relativePath.replace("/", ".").replace(".class", "");
                try {
                    classes.add(Class.forName(className));
                } catch (Throwable ignored) {}
            }
        }
        return classes;
    }
}

