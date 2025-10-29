package com.enterprise;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import java.util.Random;

// 1. Import OpenTelemetry API classes
import io.opentelemetry.api.trace.Span;
import io.opentelemetry.api.trace.StatusCode;
import io.opentelemetry.api.trace.Tracer;
import io.opentelemetry.api.GlobalOpenTelemetry;

public class LogGenerator {

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

    // 2. Get the OpenTelemetry Tracer
    // The name identifies the logical component generating the spans.
    private static final Tracer tracer = GlobalOpenTelemetry.getTracer(
        "com.enterprise.LogGenerator", "1.0.0");

    public static void main(String[] args) throws InterruptedException {
        logger.info("--- ALPINE JAVA APPLICATION STARTING ---");

        Random random = new Random();
        int counter = 1;
        
        while (true) {
            
            // 3. START a new Root Span for the "transaction"
            // The span name describes the unit of work.
            Span span = tracer.spanBuilder("log-generation-cycle").startSpan();
            
            // Make the span the current active span
            try (var scope = span.makeCurrent()) {
                
                String level = LOG_LEVELS[random.nextInt(LOG_LEVELS.length)];
                String message = MESSAGES[random.nextInt(MESSAGES.length)];
                String logMessage = String.format("LOG ENTRY #%d: %s", counter, message);

                // Add Request ID logic
                if (random.nextDouble() < 0.25) { 
                    String reqId = "ReqID-" + random.nextInt(9999);
                    logMessage = "[" + reqId + "] " + logMessage;
                    
                    // 4. Add the Request ID as an attribute to the span
                    span.setAttribute("app.request.id", reqId);
                }

                // 5. Add the log entry number and level as attributes
                span.setAttribute("app.log.entry.number", counter);
                span.setAttribute("app.log.level", level);

                // Log the message using Log4j2
                switch (level) {
                    case "DEBUG":
                        logger.debug(logMessage);
                        break;
                    case "INFO":
                        logger.info(logMessage);
                        break;
                    case "WARN":
                        logger.warn(logMessage);
                        // 6. Set the span status to WARN
                        span.setStatus(StatusCode.OK, "Completed with Warning");
                        break;
                    case "ERROR":
                        // Simulate an error with an occasional stack trace
                        if (random.nextDouble() < 0.5) { 
                            RuntimeException ex = new RuntimeException("Simulated API timeout exception.");
                            logger.error(logMessage, ex);
                            // 7. Record the exception on the span
                            span.recordException(ex);
                        } else {
                            logger.error(logMessage);
                        }
                        // 8. Set the span status to ERROR
                        span.setStatus(StatusCode.ERROR, "Log generation failed");
                        break;
                }
                
                // If no error or warning, set the status to OK
                if (!level.equals("ERROR") && !level.equals("WARN")) {
                    span.setStatus(StatusCode.OK);
                }
                
            } catch (Exception e) {
                // In a real app, handle exceptions and set span status to ERROR
                span.setStatus(StatusCode.ERROR, "Process failed due to unhandled exception.");
            } finally {
                // 9. END the span
                span.end();
            }
            
            counter++;

            // Pause for a random interval
            Thread.sleep(random.nextInt(2000) + 1000); 
        }
    }
}