package org.s8r.test.mock;

import org.s8r.application.port.ConfigurationPort.ConfigurationChangeListener;
import org.s8r.application.port.ConfigurationPort.ChangeType;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * A mock implementation of ConfigurationChangeListener for testing.
 * This class is designed to be used both by JUnit tests and Karate tests.
 */
public class MockConfigurationListener implements ConfigurationChangeListener {
    private final List<String> notifiedKeys = new ArrayList<>();
    private final List<String> notifiedValues = new ArrayList<>();
    private final List<ChangeType> notifiedChangeTypes = new ArrayList<>();
    private final AtomicBoolean notified = new AtomicBoolean(false);

    /**
     * Creates a new instance of MockConfigurationListener.
     * This method is used by Karate to instantiate the class.
     *
     * @return A new MockConfigurationListener instance
     */
    public static MockConfigurationListener createInstance() {
        return new MockConfigurationListener();
    }

    @Override
    public void onConfigurationChange(String key, String value, ChangeType changeType) {
        synchronized (this) {
            notifiedKeys.add(key);
            notifiedValues.add(value);
            notifiedChangeTypes.add(changeType);
            notified.set(true);
        }
    }

    /**
     * Checks if this listener was ever notified of a configuration change.
     *
     * @return true if notified, false otherwise
     */
    public boolean wasNotified() {
        return notified.get();
    }

    /**
     * Gets the most recent key that triggered a notification.
     *
     * @return The most recent key or null if never notified
     */
    public String getLastKey() {
        return notifiedKeys.isEmpty() ? null : notifiedKeys.get(notifiedKeys.size() - 1);
    }

    /**
     * Gets the most recent value that triggered a notification.
     *
     * @return The most recent value or null if never notified
     */
    public String getLastValue() {
        return notifiedValues.isEmpty() ? null : notifiedValues.get(notifiedValues.size() - 1);
    }

    /**
     * Gets the most recent change type that triggered a notification.
     *
     * @return The most recent change type or null if never notified
     */
    public ChangeType getLastChangeType() {
        return notifiedChangeTypes.isEmpty() ? null : notifiedChangeTypes.get(notifiedChangeTypes.size() - 1);
    }

    /**
     * Gets the total count of notifications received.
     *
     * @return The count of notifications
     */
    public int getNotificationCount() {
        return notifiedKeys.size();
    }

    /**
     * Resets the notification state of this listener.
     */
    public void reset() {
        synchronized (this) {
            notifiedKeys.clear();
            notifiedValues.clear();
            notifiedChangeTypes.clear();
            notified.set(false);
        }
    }
}