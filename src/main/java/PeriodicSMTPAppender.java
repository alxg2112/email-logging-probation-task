import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.net.SMTPAppender;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.boolex.EvaluationException;
import ch.qos.logback.core.boolex.EventEvaluatorBase;

import java.util.Date;

/**
 * Appender that sends logs periodically.
 */
public class PeriodicSMTPAppender extends SMTPAppender {

    /**
     * Log sending interval in seconds.
     */
    private int logSendingInterval;

    /**
     * Evaluates to true if event passed as parameter has level ERROR or if
     * time since last sync more than interval.
     */
    private class PeriodicEvaluator extends EventEvaluatorBase<ILoggingEvent> {

        /**
         * Last sync date.
         */
        private Date lastSync;

        /**
         * Log sending interval in seconds.
         */
        private int logSendingInterval;

        /**
         * Constructor.
         */
        private PeriodicEvaluator(int logSendingInterval) {
            lastSync = new Date();
            this.logSendingInterval = logSendingInterval;
        }

        /**
         * Return true if event passed as parameter has level ERROR or if
         * time since last sync more than interval.
         */
        public boolean evaluate(ILoggingEvent event) throws NullPointerException, EvaluationException {

            // Now date
            Date now = new Date();

            // Total time since last log has been sent in seconds
            double timeElapsed = (now.getTime() - lastSync.getTime()) / 1000d;

            if (timeElapsed > logSendingInterval || event.getLevel().levelInt >= Level.ERROR_INT) {
                lastSync = now;
                return true;
            }

            return false;
        }
    }

    /**
     * Override base start() method and set PeriodicEvaluator.
     */
    @Override
    public void start() {
        PeriodicEvaluator periodicEvaluator = new PeriodicEvaluator(logSendingInterval);
        periodicEvaluator.setContext(this.getContext());
        periodicEvaluator.setName("periodicEvaluator");
        periodicEvaluator.start();
        this.eventEvaluator = periodicEvaluator;

        super.start();
    }

    /**
     * Get log sending interval value.
     *
     * @return log sending interval value.
     */
    public int getLogSendingInterval() {
        return logSendingInterval;
    }

    /**
     * Set log sending interval.
     *
     * @param logSendingInterval value of log sending interval to set (in seconds).
     * @throws IllegalArgumentException if attempted to set non-positive value.
     */
    public void setLogSendingInterval(int logSendingInterval) throws IllegalArgumentException {

        if (logSendingInterval <= 0) {
            throw new IllegalArgumentException(String.format("Illegal value for the log sending interval: %s. " +
                    "Must be a positive integer.", logSendingInterval));
        }

        this.logSendingInterval = logSendingInterval;
    }
}
