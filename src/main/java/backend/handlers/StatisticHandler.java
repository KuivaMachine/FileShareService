package backend.handlers;

import backend.model.File;
import backend.postgres.repositories.FileRepo;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.OutputStream;
import java.util.List;

public class StatisticHandler implements HttpHandler {
    private final FileRepo fileRepository;
    private static final Logger log = LoggerFactory.getLogger(StatisticHandler.class);

    public StatisticHandler(FileRepo fileRepository) {
        this.fileRepository = fileRepository;
    }


    @Override
    public void handle(HttpExchange exchange) throws IOException {
        try {
            String path = exchange.getRequestURI().getPath();

            if ("/statistic".equals(path)) {
                //Получаем user_id
                String userId = exchange.getRequestHeaders().get("user_id").getFirst();

                // Получаем файлы по user_id
                List<File> files = fileRepository.getAllFilesByUserId(userId);

                // Преобразуем в JSON
                ObjectMapper mapper = new ObjectMapper();
                String jsonResponse = mapper.writeValueAsString(files);

                // Отправляем ответ
                exchange.getResponseHeaders().set("Content-Type", "application/json");
                exchange.sendResponseHeaders(200, jsonResponse.getBytes().length);

                try (OutputStream os = exchange.getResponseBody()) {
                    os.write(jsonResponse.getBytes());
                }
            } else {
                sendResponse(exchange, 404, "Not Found");
            }
        } catch (Exception e) {
            log.error(e.getMessage());
            sendResponse(exchange, 500, "Internal Server Error");
        }
    }

    private void sendResponse(HttpExchange exchange, int statusCode, String response) throws IOException {
        exchange.sendResponseHeaders(statusCode, response.getBytes().length);
        try (OutputStream os = exchange.getResponseBody()) {
            os.write(response.getBytes());
        }
    }
}
