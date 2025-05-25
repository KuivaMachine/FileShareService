package backend.handlers;

import backend.model.File;
import backend.postgres.repositories.FileRepo;
import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.OutputStream;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.UUID;

public class UploadHandler implements HttpHandler {
    private static final Logger log = LoggerFactory.getLogger(UploadHandler.class);
    private final FileRepo fileRepository;
    private final Gson gson = new Gson();

    public UploadHandler(FileRepo fileRepository) {
        this.fileRepository = fileRepository;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {

        // Обработка загрузки файла
        try {
            String fileName = exchange.getRequestHeaders().getFirst("X-File-Name");

            String decodedName = URLDecoder.decode(fileName, StandardCharsets.UTF_8);
            String userId = exchange.getRequestHeaders().getFirst("User-Id");
            int size = Integer.parseInt(exchange.getRequestHeaders().getFirst("X-File-Size"));

            File file = new File(UUID.randomUUID().toString(), userId, decodedName , exchange.getRequestBody().readAllBytes(), size, null, null);
            fileRepository.saveFile(file);
            String downloadUrl = String.format("/download/%s", file.getId());

            Map<String, String> response = Map.of(
                    "status", "success",
                    "downloadUrl", downloadUrl

            );
            sendJsonResponse(exchange, 200, response);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            sendResponse(exchange, 500, "Ошибка сервера");
        }
    }


    private void sendJsonResponse(HttpExchange exchange, int statusCode, Object data) throws IOException {
        String response = gson.toJson(data);
        exchange.getResponseHeaders().set("Content-Type", "application/json");
        exchange.sendResponseHeaders(statusCode, response.getBytes(StandardCharsets.UTF_8).length);
        try (OutputStream os = exchange.getResponseBody()) {
            os.write(response.getBytes(StandardCharsets.UTF_8));
        }
    }

    private void sendResponse(HttpExchange exchange, int statusCode, String message) throws IOException {
        exchange.sendResponseHeaders(statusCode, message.getBytes(StandardCharsets.UTF_8).length);
        exchange.getResponseHeaders().set("message", message);
        try (OutputStream os = exchange.getResponseBody()) {
            os.write(message.getBytes(StandardCharsets.UTF_8));
        }
    }
}