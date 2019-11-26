package com.wooshe.VKContests.Realm;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;


public class User extends RealmObject
{

    @PrimaryKey
    private int user_id;

    public int getMobile() {
        return mobile;
    }

    public void setMobile(int mobile) {
        this.mobile = mobile;
    }

    private int mobile;
    private String url;
    private String first_name;
    private String last_namel;
    private String photo_50;
    private String photo_100;
    private String photo_200;
    private String photo_400;
    private String path_photo_50;
    private String path_photo_100;
    private String path_photo_200;
    private String path_photo_400;

    public User()
    {

    }

    public int getUser_id() {
        return user_id;
    }

    public void setUser_id(int user_id) {
        this.user_id = user_id;
    }

    public String getFirst_name() {
        return first_name;
    }

    public void setFirst_name(String first_name) {
        this.first_name = first_name;
    }

    public String getLast_namel() {
        return last_namel;
    }

    public void setLast_namel(String last_namel) {
        this.last_namel = last_namel;
    }

    public String getPhoto_50() {
        return photo_50;
    }

    public void setPhoto_50(String photo_50) {
        this.photo_50 = photo_50;
    }

    public String getPhoto_100() {
        return photo_100;
    }

    public void setPhoto_100(String photo_100) {
        this.photo_100 = photo_100;
    }

    public String getPhoto_200() {
        return photo_200;
    }

    public void setPhoto_200(String photo_200) {
        this.photo_200 = photo_200;
    }

    public String getPhoto_400() {
        return photo_400;
    }

    public void setPhoto_400(String photo_400) {
        this.photo_400 = photo_400;
    }

    public String getPath_photo_50() {
        return path_photo_50;
    }

    public void setPath_photo_50(String path_photo_50) {
        this.path_photo_50 = path_photo_50;
    }

    public String getPath_photo_100() {
        return path_photo_100;
    }

    public void setPath_photo_100(String path_photo_100) {
        this.path_photo_100 = path_photo_100;
    }

    public String getPath_photo_200() {
        return path_photo_200;
    }

    public void setPath_photo_200(String path_photo_200) {
        this.path_photo_200 = path_photo_200;
    }

    public String getPath_photo_400() {
        return path_photo_400;
    }

    public void setPath_photo_400(String path_photo_400) {
        this.path_photo_400 = path_photo_400;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
