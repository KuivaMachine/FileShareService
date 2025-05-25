package backend;

import backend.handlers.*;
import backend.postgres.PostgresConfig;
import backend.postgres.repositories.FileRepo;
import backend.postgres.repositories.UserRepo;
import backend.services.AuthService;
import backend.services.FileCleaner;
import com.sun.net.httpserver.HttpServer;

import javax.sql.DataSource;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.concurrent.Executors;

public class Main {
    public static void main(String[] args) throws IOException {
        int port = 9092;

        //Инициализация сервисов
        DataSource postgres = PostgresConfig.createDataSource();
        UserRepo userRepository = new UserRepo(postgres);
        FileRepo fileRepository = new FileRepo(postgres);
        AuthService authService = new AuthService(userRepository);

        //Настройка сервера
        HttpServer server = HttpServer.create(new InetSocketAddress(port), 0);

        //Регистрация обработчиков
        server.createContext("/upload", new UploadHandler(fileRepository));
        server.createContext("/download/", new DownloadHandler(fileRepository));
        server.createContext("/", new ViewHandler());
        server.createContext("/auth", new AuthHandler(authService));
        server.createContext("/statistic", new StatisticHandler(fileRepository));
        server.setExecutor(Executors.newCachedThreadPool());
        server.start();

        //Запуск очистки файлов, которые последний раз скачивали 30 дней назад
        new FileCleaner(fileRepository).start();
    }
}