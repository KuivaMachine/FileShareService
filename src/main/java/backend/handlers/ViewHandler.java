package backend.handlers;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class ViewHandler implements HttpHandler {

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String url = exchange.getRequestURI().getPath();
        if (url.equals("/")) {
            url = "/page.html";
        }
        Path baseDir = Paths.get("src/main/resources/frontend");

        Path filePath = baseDir.resolve(url.substring(1)).normalize();
        //Определяем MIME-тип
        String mimeType = selectContentType(filePath);
        //Если файл существует
        try (OutputStream os = exchange.getResponseBody()) {
            byte[] fileBytes = Files.readAllBytes(filePath);

            exchange.getResponseHeaders().set("Content-Type", mimeType);
            exchange.sendResponseHeaders(200, fileBytes.length);

            os.write(fileBytes);
            os.flush();
        }
    }

    private String selectContentType(Path filePath) {
        String fileName = filePath.getFileName().toString();

        if (fileName.endsWith(".css")) return "text/css";
        if (fileName.endsWith(".js")) return "application/javascript";
        if (fileName.endsWith(".png")) return "image/png";
        if (fileName.endsWith(".jpg") || fileName.endsWith(".jpeg")) return "image/jpeg";
        if (fileName.endsWith(".html")) return "text/html";

        return "application/octet-stream";
    }
}
