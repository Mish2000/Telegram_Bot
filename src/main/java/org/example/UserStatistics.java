package org.example;

import org.telegram.telegrambots.meta.api.objects.User;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class UserStatistics {
    //This map stores the counter of the times that specific user chose an activity.
    private final Map<UserInfo, Integer> userActivityCount;
    //This map stores the counter of the uses for each activity, by all the users.
    private final Map<String, Integer> activityPopularity;
    //This map stores the counter of activity requests for every timestamp.
    private final Map<LocalDateTime, Integer> requestCounts;
    //This map stores the user id keys, next to the UserInfo object. It mostly used for counting the most active user.
    private final Map<Long, UserInfo> userInfoMap ;
    //A counter for a total number of activity requests.
    private int totalRequests;
    //UserStatistics constructor. I initialized those maps as ConcurrentHashMap because they will have to get updates
    //from different threads at the same time, and this extension is recommended to use while performing multi-thread
    //operations.
    public UserStatistics() {
        this.userActivityCount = new ConcurrentHashMap<>();
        this.activityPopularity = new ConcurrentHashMap<>();
        this.requestCounts = new ConcurrentHashMap<>();
        this.userInfoMap=new ConcurrentHashMap<>();
        this.totalRequests = 0;
    }
    //This method updates all the fields of this class when a new activity is added.
    public synchronized void addActivity(User user, String activity) {
        this.totalRequests++;
        String username;
        if (user.getUserName() != null) {
            username = user.getUserName();
        } else {
            username = user.getFirstName();
        }
        if(username.isEmpty()) {
            throw new IllegalArgumentException("Username is empty.");
        }
        UserInfo userInfo = this.userInfoMap.get(user.getId());
        if (userInfo == null) {
            userInfo = new UserInfo(user.getId(), username);
            this.userInfoMap.put(user.getId(), userInfo);
        }
        this.userActivityCount.put(userInfo, this.userActivityCount.getOrDefault(userInfo, 0) + 1);
        this.activityPopularity.put(activity, this.activityPopularity.getOrDefault(activity, 0) + 1);
        LocalDateTime now = LocalDateTime.now();
        this.requestCounts.put(now, this.totalRequests);
    }


    //Getters for the values that should be displayed in admins GUI.
    public int getTotalRequests() {
        return this.totalRequests;
    }

    public int getTotalUniqueUsers() {
        return this.userActivityCount.size();
    }

    public String getMostActiveUser() {
        return this.userActivityCount.entrySet().stream()
                .max(Map.Entry.comparingByValue())
                .map(entry -> entry.getKey().getUsername())
                .orElse("No active user");
    }


    public String getMostPopularActivity() {
        return this.activityPopularity.entrySet().stream()
                .max(Map.Entry.comparingByValue())
                .map(Map.Entry::getKey)
                .orElse("No activities");
    }


    //This method returns a copy of the requestCounts map to provide the data for updating the admin graph.
    public Map<LocalDateTime, Integer> getRequestCounts() {
        return new HashMap<>(this.requestCounts);
    }
}
