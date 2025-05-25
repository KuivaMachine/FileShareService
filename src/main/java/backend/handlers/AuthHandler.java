package backend.handlers;

import backend.services.AuthService;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpCookie;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;

public class AuthHandler implements HttpHandler {
    private static final ObjectMapper objectMapper = new ObjectMapper();
    private final AuthService authService;
    private final Gson gson = new Gson();

    public AuthHandler(AuthService authService) {
        this.authService = authService;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String uri = exchange.getRequestURI().getPath();
        if (uri.equals("/auth/login")) {
            try (InputStream requestBody = exchange.getRequestBody()) {

                JsonNode rootNode =objectMapper.readTree(requestBody);

                String name = rootNode.path("username").asText();
                String password = rootNode.path("password").asText();
                String userId = authService.authenticate(name, password);
                String response = gson.toJson(Map.of(
                        "userId", userId));

                exchange.getResponseHeaders().set("Content-Type", "application/json");
                exchange.sendResponseHeaders(200, response.getBytes(StandardCharsets.UTF_8).length);
                try (OutputStream os = exchange.getResponseBody()) {
                    os.write(response.getBytes(StandardCharsets.UTF_8));
                    os.flush();
                }
            }
        }
    }
}
