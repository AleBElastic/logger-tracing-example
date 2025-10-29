package com.enterprise;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import java.util.Random;

public class LogGenerator {

    // Initialize Log4j2 Logger (no need for manual formatter, Log4j handles it)
    private static final Logger logger = LogManager.getLogger(LogGenerator.class);

    private static final String[] LOG_LEVELS = {"DEBUG", "INFO", "WARN", "ERROR"};
    private static final String[] MESSAGES = {
        "User 'john.doe' logged in successfully.",
        "Attempting database query: SELECT * FROM owners WHERE id = 10.",
        "Database connection pool exhausted. Retrying in 5s.",
        "Processing request /api/vets/1: Payload size is 12KB.",
        "Configuration reload triggered by user 'admin'.",
        "Record update failed due to validation error: Pet name too long.",
        "System health check: All services OK."
    };

    public static void main(String[] args) throws InterruptedException {
        logger.info("--- ALPINE JAVA APPLICATION STARTING ---"); // Use Log4j for startup message

        Random random = new Random();
        int counter = 1;
        
        // Loop indefinitely to generate logs
        while (true) {
            
            // 1. Get a random log level and message
            String level = LOG_LEVELS[random.nextInt(LOG_LEVELS.length)];
            String message = MESSAGES[random.nextInt(MESSAGES.length)];
            
            // 2. Add an optional request ID for fake transaction tracking
            String logMessage = String.format("LOG ENTRY #%d: %s", counter, message);
            if (random.nextDouble() < 0.25) { // 25% chance to add a request ID
                logMessage = "[ReqID-" + random.nextInt(9999) + "] " + logMessage;
            }

            // 3. Log the message using Log4j2
            switch (level) {
                case "DEBUG":
                    logger.debug(logMessage);
                    break;
                case "INFO":
                    logger.info(logMessage);
                    break;
                case "WARN":
                    logger.warn(logMessage);
                    break;
                case "ERROR":
                    // Simulate an error with an occasional stack trace
                    if (random.nextDouble() < 0.5) { 
                        logger.error(logMessage, new RuntimeException("Simulated API timeout exception."));
                    } else {
                        logger.error(logMessage);
                    }
                    break;
            }
            
            counter++;

            // 4. Pause for a random interval (1-3 seconds)
            Thread.sleep(random.nextInt(2000) + 1000); 
        }
    }
}
