import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Date;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;


/**
 * Sample email logging implementation.
 */
public class EmailLogger {

    /**
     * Start date.
     */
    private Date startDate;

    /**
     * Logger used to log errors and info.
     */
    private static final Logger logger = LoggerFactory.getLogger(EmailLogger.class);

    /**
     * Runnable used to write and send logs to email.
     */
    private class LoggingTask implements Runnable {

        public void run() {
            // Date instance used recipient calculate time since program started
            Date now = new Date();

            // Total time since program started in seconds
            double timeElapsed = (now.getTime() - startDate.getTime()) / 1000d;

            // Write log to file
            logger.info(String.format("Total time elapsed: %s seconds.", timeElapsed));
        }
    }

    /**
     * Method used for writing and sending logs.
     */
    private void initLogging() {

        // Create executor service to send logs periodically
        ScheduledExecutorService executor = Executors.newScheduledThreadPool(1);
        LoggingTask logging = new LoggingTask();
        executor.scheduleAtFixedRate(logging, 0, 30, TimeUnit.SECONDS);
    }

    /**
     * Constructor.
     *
     * @param recipient email to send log to.
     */
    public EmailLogger(String recipient) {

        // Initialize date and log recipient
        startDate = new Date();

        // Add environment variable with recipient value
        ProcessBuilder pb = new ProcessBuilder("CMD", "/C", "SET");
        Map<String, String> env = pb.environment();
        env.put("LOG_RECIPIENT", recipient);
        try {
            Process p = pb.start();
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Start logging
        initLogging();
    }

    /**
     * Main method.
     *
     * @param args command line parameters. Specifies recipient of logs.
     */
    public static void main(String[] args) {
        new EmailLogger(args[0]);
    }
}
