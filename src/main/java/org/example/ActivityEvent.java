package org.example;

import java.time.LocalDateTime;

public class ActivityEvent {
    //Those fields are used to store the parameters for each requested activity.
    private final String username;
    private final String activity;
    private final LocalDateTime timestamp;
    //ActivityEvent constructor.
    public ActivityEvent(String username, String activity, LocalDateTime timestamp) {
        this.username = username;
        this.activity = activity;
        this.timestamp = timestamp;
    }
    //Getters for each field.
    public String getUsername() {
        return this.username;
    }

    public String getActivity() {
        return this.activity;
    }

    public LocalDateTime getTimestamp() {
        return this.timestamp;
    }
}
