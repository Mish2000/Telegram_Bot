package org.example;
public class UserInfo {
    //Users ID.
    private final long id;
    //Users Username.
    private final String username;
    //UserInfo constructor.
    public UserInfo(long id, String username) {
        this.id = id;
        this.username = username;
    }
    //Username getter.
    public String getUsername() {
        return username;
    }

}
