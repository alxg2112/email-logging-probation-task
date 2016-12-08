import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.boolex.EvaluationException;
import ch.qos.logback.core.boolex.EventEvaluatorBase;

import java.util.Date;

/**
 * Evaluates to true if event passed as parameter has level ERROR or if
 * last sync occurred more than 30 minutes ago.
 */
public class PeriodicEvaluator extends EventEvaluatorBase<ILoggingEvent> {

    /**
     * Last sync date.
     */
    private Date lastSync;

    /**
     * Constructor.
     */
    public PeriodicEvaluator() {
        lastSync = new Date();
    }

    /**
     * Return true if event passed as parameter has level ERROR or if
     * last sync occurred more than 30 minutes ago.
     */
    public boolean evaluate(ILoggingEvent event) throws NullPointerException, EvaluationException {

        // Now date
        Date now = new Date();

        // Total time since last log has been sent
        double timeElapsed = (now.getTime() - lastSync.getTime()) / 1000d;

        if(timeElapsed > 30) {
            lastSync = now;
            return true;
        }

        return event.getLevel().levelInt >= Level.ERROR_INT;
    }
}
