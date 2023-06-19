package org.example;

import java.util.*;

public class ActivityHistory {
    //I chose to use a deque object for maintaining the Activity history because this object allows adding an element
    //in front of it, and in case of reaching size 10, this object allows removing the last request from the tail of
    //the deque.
    private final Deque<ActivityEvent> history;
    //ActivityHistory constructor.
    public ActivityHistory() {
        this.history = new ArrayDeque<>();
    }
    //This method adds a request to the top of the deque, and in case of reaching max size, it removes the oldest
    //request from the tail.
    public synchronized void addActivity(ActivityEvent entry) {
        if (this.history.size() == 10) {
            this.history.removeLast();
        }
        this.history.addFirst(entry);
    }
    //This is a getter for the history deque.
    public synchronized List<ActivityEvent> getHistory() {
        return new ArrayList<>(this.history);
    }
}
