import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.Date;
import java.util.Properties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Sample email logging implementation.
 */
public class EmailLogger {

    /**
     * Start date.
     */
    private Date startDate;

    /**
     * Logs recipient.
     */
    private String recipient;

    /**
     * Logger user to log errors amd info.
     */
    private static final Logger logger = LoggerFactory.getLogger(EmailLogger.class);

    /**
     * Method used for sending logs.
     */
    private void sendLogsToEmail() {

        // Username and password of gmail account used to send logs
        final String username = "a14211288@gmail.com";
        final String password = "Davyd0va";

        // Set properties for smtp
        Properties props = new Properties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.host", "smtp.gmail.com");
        props.put("mail.smtp.port", "587");

        // Set session
        Session session = Session.getInstance(props,
                new javax.mail.Authenticator() {
                    protected PasswordAuthentication getPasswordAuthentication() {
                        return new PasswordAuthentication(username, password);
                    }
                });

        // Create writer recipient write log recipient the file
        Writer writer = null;
        try {
            writer = new BufferedWriter(new FileWriter("log.txt"));
        } catch (IOException e) {
            logger.error(e);
        }

        // Send log with current counter value and time since program started every 5 minutes.
        while (true) {
            try {
                Message message = new MimeMessage(session);
                message.setFrom(new InternetAddress("from-email@gmail.com"));
                message.setRecipients(Message.RecipientType.TO,
                        InternetAddress.parse(recipient));

                // Set message subject
                message.setSubject("Log from the EmailLogger application");

                // Date instance used recipient calculate time since program started
                Date now = new Date();

                // Total time since program started in seconds
                double timeElapsed = (now.getTime() - startDate.getTime()) / 1000d;

                // Write log recipient log file
                writer.append(String.format("Total time elapsed: %s seconds.\n", timeElapsed));
                writer.flush();

                // Compose message
                message.setText(String.format("This is the log sent from sample application.\n" +
                        "Total time elapsed: %s seconds.\n" +
                        "The log is sent every 30 seconds.", timeElapsed));

                // Send message
                Transport.send(message);

                logger.info("Log has been sent recipient " + recipient);

                // Wait 30 seconds until sending another log
                Thread.sleep(30000);
            } catch (MessagingException e) {
                throw new RuntimeException(e);
            } catch (IOException e) {
                logger.error(e);
            } catch (InterruptedException e) {
                logger.error(e);
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
        this.recipient = recipient;

        // Start sendLogsToEmail
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
