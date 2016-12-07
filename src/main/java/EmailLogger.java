import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Date;


/**
 * Sample email logging implementation.
 */
public class EmailLogger {

    /**
     * Start date.
     */
    private Date startDate;

    /**
     * Logger user to log errors amd info.
     */
    private static Logger logger;

    /**
     * Method used for sending logs.
     */
    private void sendLogsToEmail() {

        // Send log with current counter value and time since program started every 5 minutes.
        while (true) {
            try {
                // Date instance used recipient calculate time since program started
                Date now = new Date();

                // Total time since program started in seconds
                double timeElapsed = (now.getTime() - startDate.getTime()) / 1000d;

                // Write log to file
                logger.error(String.format("Total time elapsed: %s seconds.", timeElapsed));

                // Wait 30 seconds until sending another log
                Thread.sleep(30000);
            } catch (InterruptedException e) {
                logger.error(String.format("%s has occurred", e.getClass().getSimpleName()));
            }
        }
    }

    /**
     * Constructor.
     *
     * @param recipient email to send log to.
     */
    public EmailLogger(String recipient) {

        // Initialize date and log recipient
        startDate = new Date();
        LoggingPropertiesDefiner.addProperty("recipient", recipient);

        // Set logger
        logger = LoggerFactory.getLogger(EmailLogger.class);

        // Start logging
        sendLogsToEmail();
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
