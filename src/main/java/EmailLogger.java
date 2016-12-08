import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Date;
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
     * Runnable used to generate errors.
     */
    private class ErrorGen implements Runnable {

        public void run() {
            // Date instance used recipient calculate time since program started
            Date now = new Date();

            // Total time since program started in seconds
            double timeElapsed = (now.getTime() - startDate.getTime()) / 1000d;

            // Write log to file
            logger.error("Error occurred");
        }
    }


    /**
     * Method used for initiation of writing and sending logs.
     */
    private void initLogging() {

        // Create executor service to send logs periodically
        ScheduledExecutorService executor = Executors.newScheduledThreadPool(2);
        LoggingTask logging = new LoggingTask();
        ErrorGen errorGen = new ErrorGen();
        executor.scheduleAtFixedRate(logging, 0, 1, TimeUnit.SECONDS);
        executor.scheduleAtFixedRate(errorGen, 5, 20, TimeUnit.SECONDS);
    }

    /**
     * Constructor.
     */
    public EmailLogger() {

        // Initialize date and log recipient
        startDate = new Date();

        // Start logging
        initLogging();
    }

    /**
     * Main method.
     *
     * @param args command line parameters. Specifies recipient of logs.
     */
    public static void main(String[] args) {
        new EmailLogger();
    }
}
