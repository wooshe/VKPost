package com.wooshe.VKContests.Realm;

import io.realm.RealmObject;

public class EventFriends extends RealmObject
{

    private String name;
    private String id;
    private String url;
    private boolean leave = false;
    private boolean toEnter = false;

    public EventFriends()
    {

    }

    public boolean isToEnter() {
        return toEnter;
    }

    public void setToEnter(boolean toEnter) {
        this.toEnter = toEnter;
    }

    public boolean isLeave() {
        return leave;
    }

    public void setLeave(boolean leave) {
        this.leave = leave;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
