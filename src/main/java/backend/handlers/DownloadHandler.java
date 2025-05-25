package backend.handlers;

import backend.model.File;
import backend.postgres.repositories.FileRepo;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.OutputStream;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

public class DownloadHandler implements HttpHandler {
    private static final Logger log = LoggerFactory.getLogger(DownloadHandler.class);
    private final FileRepo fileRepository;

    public DownloadHandler(FileRepo fileRepository) {
        this.fileRepository = fileRepository;
    }


    @Override
    public void handle(HttpExchange exchange) throws IOException {
        if (!"GET".equals(exchange.getRequestMethod())) {
            sendResponse(exchange, 405, "Method Not Allowed");
            return;
        }
        try {
            String path = exchange.getRequestURI().getPath();
            String fileId = path.substring(path.lastIndexOf('/') + 1);

            //Получаем информацию о файле и сам файл
            File file = fileRepository.findById(fileId);

            //Отправляем файл

            exchange.getResponseHeaders().set("Content-Type", "application/octet-stream; charset=UTF-8");
            exchange.getResponseHeaders().set(
                    "Content-Disposition",
                    String.format("attachment; filename=\"%s\"", URLEncoder.encode(file.getFile_name(), StandardCharsets.UTF_8).replace("+", "%20"))
            );
            exchange.sendResponseHeaders(200, file.getFile_data().length);
            try (OutputStream os = exchange.getResponseBody()) {
                os.write(file.getFile_data());
                os.flush();
            }

        } catch (IOException e) {
            log.error(e.getMessage());
            sendResponse(exchange, 404, "Файл не найден");
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