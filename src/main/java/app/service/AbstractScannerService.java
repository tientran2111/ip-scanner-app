package app.service;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.StringProperty;

/**
 * Abstract Scanner Service.
 *
 * @author Si Tran
 *
 */
public abstract class AbstractScannerService implements Runnable {

    /** Stop requested flag */
    private boolean stopRequested = false;

    /** Process property */
    public DoubleProperty processProps;

    /** Text area property */
    public StringProperty txtAreaProps;

    /** Running property */
    public BooleanProperty runningProps;

    /**
     * Stop Thread by setting a flag. The flag checked in the scanners after each run.
     */
    public void stop() {
        stopRequested = true;
    }

    /**
     * Getter for the stopRequested flag.
     *
     * @return true/false
     */
    public boolean isStopRequested() {
        return stopRequested;
    }
}
