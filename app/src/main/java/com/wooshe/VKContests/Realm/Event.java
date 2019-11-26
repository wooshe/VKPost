package com.wooshe.VKContests.Realm;


import java.util.Date;

import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class Event extends RealmObject
{
    @PrimaryKey
    private String wid;
    private int intwid;

    private int adspos = 0;
    private RealmList<EventPhoto> photo;
    private RealmList<EventFriends> friends;
    private int repost_id=-1;
    private boolean pin=false;
    private String event_name=" ";
    private String event_desc=" ";

    private String event_link=" ";
    private Date event_date_add;
    private Date event_date_repost;
    private Date event_date_notif;
    private Date event_date_end;
    private int event_state=-1;

    private boolean switch_repost = false;
    private boolean switch_notif = false;
    private boolean switch_end = false;

    private String group_name=" ";
    private int group_id=-1;
    private String group_site=" ";

    private int author_id=-1;
    private int boss_id=-1;

    private Date post_date_create;
    private String post_text=" ";

    public String getEvent_link() {
        return event_link;
    }

    public void setEvent_link(String event_link) {
        this.event_link = event_link;
    }

    public boolean isADS()
    {
        return ADS;
    }

    public void setADS(boolean ADS)
    {
        this.ADS = ADS;
    }

    private boolean ADS = false;

    public int getAdspos()
    {
        return adspos;
    }

    public void setAdspos(int adspos)
    {
        this.adspos = adspos;
    }

    public static void Complete(Event obj)
    {
        obj.setIntwid(obj.getWid().hashCode());
    }

    public Event()
    {

    }

    public String getWid() {
        return wid;
    }

    public void setWid(String wid) {
        this.wid = wid;
    }

    public int getIntwid() {
        return intwid;
    }

    public void setIntwid(int intwid) {
        this.intwid = intwid;
    }

    public RealmList<EventPhoto> getPhoto() {
        return photo;
    }

    public void setPhoto(RealmList<EventPhoto> photo) {
        this.photo = photo;
    }

    public RealmList<EventFriends> getFriends() {
        return friends;
    }

    public void setFriends(RealmList<EventFriends> friends) {
        this.friends = friends;
    }

    public int getRepost_id() {
        return repost_id;
    }

    public void setRepost_id(int repost_id) {
        this.repost_id = repost_id;
    }

    public boolean isPin() {
        return pin;
    }

    public void setPin(boolean pin) {
        this.pin = pin;
    }

    public String getEvent_name() {
        return event_name;
    }

    public void setEvent_name(String event_name) {
        this.event_name = event_name;
    }

    public String getEvent_desc() {
        return event_desc;
    }

    public void setEvent_desc(String event_desc) {
        this.event_desc = event_desc;
    }

    public Date getEvent_date_add() {
        return event_date_add;
    }

    public void setEvent_date_add(Date event_date_add) {
        this.event_date_add = event_date_add;
    }

    public Date getEvent_date_repost() {
        return event_date_repost;
    }

    public void setEvent_date_repost(Date event_date_repost) {
        this.event_date_repost = event_date_repost;
    }

    public Date getEvent_date_notif() {
        return event_date_notif;
    }

    public void setEvent_date_notif(Date event_date_notif) {
        this.event_date_notif = event_date_notif;
    }

    public Date getEvent_date_end() {
        return event_date_end;
    }

    public void setEvent_date_end(Date event_date_end) {
        this.event_date_end = event_date_end;
    }

    public int getEvent_state() {
        return event_state;
    }

    public void setEvent_state(int event_state) {
        this.event_state = event_state;
    }

    public boolean isSwitch_repost() {
        return switch_repost;
    }

    public void setSwitch_repost(boolean switch_repost) {
        this.switch_repost = switch_repost;
    }

    public boolean isSwitch_notif() {
        return switch_notif;
    }

    public void setSwitch_notif(boolean switch_notif) {
        this.switch_notif = switch_notif;
    }

    public boolean isSwitch_end() {
        return switch_end;
    }

    public void setSwitch_end(boolean switch_end) {
        this.switch_end = switch_end;
    }

    public String getGroup_name() {
        return group_name;
    }

    public void setGroup_name(String group_name) {
        this.group_name = group_name;
    }

    public int getGroup_id() {
        return group_id;
    }

    public void setGroup_id(int group_id) {
        this.group_id = group_id;
    }

    public String getGroup_site() {
        return group_site;
    }

    public void setGroup_site(String group_site) {
        this.group_site = group_site;
    }

    public int getAuthor_id() {
        return author_id;
    }

    public void setAuthor_id(int author_id) {
        this.author_id = author_id;
    }

    public int getBoss_id() {
        return boss_id;
    }

    public void setBoss_id(int boss_id) {
        this.boss_id = boss_id;
    }

    public Date getPost_date_create() {
        return post_date_create;
    }

    public void setPost_date_create(Date post_date_create) {
        this.post_date_create = post_date_create;
    }

    public String getPost_text() {
        return post_text;
    }

    public void setPost_text(String post_text) {
        this.post_text = post_text;
    }
}
