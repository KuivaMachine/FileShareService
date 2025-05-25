package backend.services;


import backend.postgres.repositories.FileRepo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Timer;
import java.util.TimerTask;

public class FileCleaner {


    private static final Logger log = LoggerFactory.getLogger(FileCleaner.class);
    private final FileRepo fileRepo;
    private final Timer timer;

    public FileCleaner(FileRepo fileRepo) {
        this.fileRepo = fileRepo;

        this.timer = new Timer(true);
    }

    public void start() {
        //каждые 24 часа
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                try {
                    fileRepo.cleanupExpiredFiles();
                } catch (Exception e) {
                    log.error(e.getMessage());
                }
            }
        }, 0, 24 * 60 * 60 * 1000);
    }

}
