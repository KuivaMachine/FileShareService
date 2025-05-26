package backend.handlers;

import backend.postgres.repositories.FileRepo;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.OutputStream;

public class DeleteHandler implements HttpHandler {
    private static final Logger log = LoggerFactory.getLogger(DeleteHandler.class);
    private final FileRepo fileRepository;

    public DeleteHandler(FileRepo fileRepository) {
        this.fileRepository = fileRepository;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        try {
            String path = exchange.getRequestURI().getPath();
            String fileId = path.substring(path.lastIndexOf('/') + 1);

            fileRepository.deleteById(fileId);

            exchange.getResponseHeaders().set("Content-Type", "application/json");
            exchange.sendResponseHeaders(204, -1);
            exchange.getResponseBody().close();
        } catch (Exception e) {
            log.error(e.getMessage());
            sendResponse(exchange, 500, "Ошибка сервера");
        }
    }

    private void sendResponse(HttpExchange exchange, int code, String message) throws IOException {
        exchange.sendResponseHeaders(code, message.length());
        try (OutputStream os = exchange.getResponseBody()) {
            os.write(message.getBytes());
        }
    }
}
