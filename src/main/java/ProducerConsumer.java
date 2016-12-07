import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.Date;
import java.util.Properties;


/**
 * Sample producer consumer problem implementation with logging functionality.
 */
public class ProducerConsumer {

    /**
     * Counter used as buffer.
     */
    private long counter;

    /**
     * Flag used for concurrent buffer access.
     */
    private boolean isProduced = false;

    /**
     * Start date.
     */
    private Date startDate;

    /**
     * Email to send log to.
     */
    private String to;

    /**
     * Producer method used to produce generate new counter value and put it in the buffer.
     */
    public void produce() {
        while (true) {
            synchronized (this) {
                if (!isProduced) {
                    System.out.println("Produced: " + ++counter);
                    isProduced = true;
                    notifyAll();
                } else {
                    try {
                        wait();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    /**
     * Consumer method used to retrieve value from the buffer.
     */
    public void consume() {
        while (true) {
            synchronized (this) {
                try {
                    if (isProduced) {
                        System.out.println("Consumed: " + counter);
                        isProduced = false;
                        notifyAll();
                        Thread.sleep(100);
                    } else {
                        wait();
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * Logging method used to log counter value and responsible for sending log reports via email.
     */
    public void logging() {

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

        // Create writer to write log to the file
        Writer writer = null;
        try {
            writer = new BufferedWriter(new FileWriter("log.txt"));
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Send log with current counter value and time since program started every 5 minutes.
        while (true) {
            try {
                Message message = new MimeMessage(session);
                message.setFrom(new InternetAddress("from-email@gmail.com"));
                message.setRecipients(Message.RecipientType.TO,
                        InternetAddress.parse(to));

                // Set message subject
                message.setSubject("Log from the ProducerConsumer application");

                // Date instance used to calculate time since program started
                Date now = new Date();

                // Total time since program started in seconds
                double timeElapsed = (now.getTime() - startDate.getTime()) / 1000d;

                // Retrieve counter value
                long counterValue = counter;

                // Compose message
                writer.append(String.format("Current counter value is: %s. " +
                        "Total time elapsed: %s seconds.\n", counterValue, timeElapsed));
                writer.flush();
                message.setText(String.format("This is the log sent from sample application.\n" +
                        "Current counter value is: %s.\n" +
                        "Total time elapsed: %s seconds.\n" +
                        "The log is sent every 5 minutes.", counterValue, timeElapsed));

                // Send message
                Transport.send(message);

                System.out.println("Log has been sent to " + to);

                // Wait 5 minutes
                Thread.sleep(30000);
            } catch (MessagingException e) {
                throw new RuntimeException(e);
            } catch (IOException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Constructor.
     */
    public ProducerConsumer(String to) {
        startDate = new Date();
        this.to = to;

        Thread producer = new Thread() {
            @Override
            public void run() {
                produce();
            }
        };
        Thread consumer = new Thread() {
            @Override
            public void run() {
                consume();
            }
        };
        Thread logger = new Thread() {
            @Override
            public void run() {
                logging();
            }
        };

        producer.start();
        consumer.start();
        logger.start();
    }

    /**
     * Main method.
     *
     * @param args command line parameters.
     */
    public static void main(String[] args) {
        new ProducerConsumer(args[0]);
    }
}
