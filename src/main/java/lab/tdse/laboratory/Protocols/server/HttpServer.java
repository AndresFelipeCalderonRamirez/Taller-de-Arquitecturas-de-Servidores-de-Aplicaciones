package lab.tdse.laboratory.Protocols.server;

import lab.tdse.laboratory.Protocols.util.Request;
import lab.tdse.laboratory.Protocols.util.Response;
import lab.tdse.laboratory.Protocols.server.Router;

import java.net.*;
import java.io.*;
import java.nio.file.Files;

public class HttpServer {

    public static void main(String[] args) throws IOException, URISyntaxException {
        ServerSocket serverSocket = null;
        try {
            serverSocket = new ServerSocket(8080);
        } catch (IOException e) {
            System.err.println("Could not listen on port: 8080.");
            System.exit(1);
        }

        boolean running = true;

        while (running) {
            Socket clientSocket = null;
            try {
                System.out.println("Listo para recibir ...");
                clientSocket = serverSocket.accept();
            } catch (IOException e) {
                System.err.println("Accept failed.");
                System.exit(1);
            }

            PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);
            BufferedReader in = new BufferedReader(
                    new InputStreamReader(clientSocket.getInputStream()));

            boolean firstLine = true;
            String reqpath = "";
            URI requri = null;

            String inputLine;
            while ((inputLine = in.readLine()) != null) {
                System.out.println("Received: " + inputLine);
                if (firstLine) {
                    String[] reqTokens = inputLine.split(" ");
                    String struri = reqTokens[1];
                    requri = new URI(struri);
                    reqpath = requri.getPath();
                    System.out.println("Request path: " + reqpath);
                    firstLine = false;
                }
                if (!in.ready()) break;
            }

            String query = (requri != null) ? requri.getQuery() : null;
            String routePath = reqpath.startsWith("/App") ? reqpath.substring(4) : reqpath;
            Request req = new Request(routePath, "GET", query);
            Response res = new Response();

            // Primero intenta resolver como ruta registrada
            if (Router.hasRoute(routePath)) {
                String body = Router.handle(routePath, req, res);
                String outputLine = "HTTP/1.1 " + res.getStatus() + " OK\r\n"
                        + "Content-Type: " + res.getContentType() + "\r\n"
                        + "\r\n"
                        + "<!DOCTYPE html><html><body>" + body + "</body></html>";
                out.println(outputLine);

            } else {
                // Si no, busca el archivo estático
                serveStaticFile(reqpath, out, clientSocket.getOutputStream());
            }

            out.close();
            in.close();
            clientSocket.close();
        }
        serverSocket.close();
    }

    private static void serveStaticFile(String reqpath, PrintWriter out, OutputStream rawOut) throws IOException {
        // Ruta base: target/classes/<carpeta definida en staticfiles()>
        String folder = Router.getStaticFilesFolder();
        String basePath = "target/classes" + folder;

        // Si piden "/", servir index.html por defecto
        if (reqpath.equals("/")) {
            reqpath = "/index.html";
        }

        File file = new File(basePath + reqpath);

        if (file.exists() && !file.isDirectory()) {
            String contentType = getContentType(reqpath);
            byte[] fileBytes = Files.readAllBytes(file.toPath());

            // Encabezado HTTP
            String header = "HTTP/1.1 200 OK\r\n"
                    + "Content-Type: " + contentType + "\r\n"
                    + "Content-Length: " + fileBytes.length + "\r\n"
                    + "\r\n";
            rawOut.write(header.getBytes());
            rawOut.write(fileBytes);
            rawOut.flush();
        } else {
            out.println("HTTP/1.1 404 Not Found\r\n"
                    + "Content-Type: text/html\r\n"
                    + "\r\n"
                    + "<html><body><h1>404 - File Not Found</h1></body></html>");
        }
    }

    private static String getContentType(String path) {
        if (path.endsWith(".html")) return "text/html";
        if (path.endsWith(".css"))  return "text/css";
        if (path.endsWith(".js"))   return "application/javascript";
        if (path.endsWith(".png"))  return "image/png";
        if (path.endsWith(".jpg") || path.endsWith(".jpeg")) return "image/jpeg";
        if (path.endsWith(".json")) return "application/json";
        return "text/plain";
    }
}