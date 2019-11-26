package com.wooshe.VKContests.no_use;


public class Constants
{
    public static final String MY_DATA_FORMAT="dd MMMM yyyy HH:mm";
    public static final String MyDebugTag = "MyTag1";
    public final static int UPDATE_OK=200;
    public final static int UPDATE_ERROR=201;
    public final static int UPDATE_OBJECT_EXIST=202;
    public static final int UPDATE_DOWNLOAD = 203;
    public static final int UPDATE_UPDATE = 204;

    public static final int APP_NOTIFICATION_ALL = 2000;
    public static final int APP_NOTIFICATION_DOWNLOAD = 2001;
    public static final int APP_NOTIFICATION_NOTIFY = 2002;
    public static final int APP_NOTIFICATION_REPOST = 2003;
    public static final int APP_NOTIFICATION_END = 2004;
    public static final int APP_NOTIFICATION_NEW = 2005;
    public static final String APP_NOTIFICATION_TAG= "APP_NOTIFICATION_TAG";

    public static final int type_photo = 0;
    public static final int type_drawable = 1;

    public static final String APP_PREFERENCES = "APP_PREFERENCES";
    public static final String SETTINGS_VIEW_TYPE= "SETTINGS_VIEW_TYPE";
    public static final String SETTINGS_TRAFFIC = "SETTINGS_TRAFFIC";
    public static final String SETTINGS_HOOK = "SETTINGS_HOOK";
    public static final String SETTINGS_NAVIGATION_GROUP = "SETTINGS_NAVIGATION_GROUP";
    public static final String CARD_COLOR_RANDOM = "CARD_COLOR_RANDOM";

    public static final int event_state_repost = 10;
    public static final int event_state_soon = 12;
    public static final int event_state_end = 13;
    public static final int event_state_new = 14;

    public static final String RECYCLER_TYPE="RECYCLER_TYPE";
    public static final int RECYCLER_TYPE_ALL=2000;
    public static final int RECYCLER_TYPE_REPOST =2001;
    public static final int RECYCLER_TYPE_SOON=2002;
    public static final int RECYCLER_TYPE_END=2003;
    public static final int RECYCLER_TYPE_NEW=2004;

    public static final String ALARM_PHOTO="ALARM_PHOTO";
    public static final String ALARM_TYPE="ALARM_TYPE";
    public static final String ALARM_TYPE_REPOST="ALARM_TYPE_REPOST";
    public static final String ALARM_TYPE_NOTIFY="ALARM_TYPE_NOTIFY";
    public static final String ALARM_TYPE_END ="ALARM_TYPE_END";
    public static final String ALARM_TYPE_REFRESH ="ALARM_TYPE_REFRESH";
    public static final String ALARM_TYPE_BOOT ="ALARM_TYPE_BOOT";

    public static final String WID="WID";

    public static final String POSITION_EVENT="POSITION_EVENT";
    public static final String POSITION_RECYCLER="POSITION_RECYCLER";

    public static String PUBLIC = "https://vk.com/public";
    public static String USER = "https://vk.com/id";
    public static String WALL = "vk.com/wall";
    public static String ALLWALL = "https://vk.com/wall";

    public static final int refresh_timeout = 20;

    public static final long REFRESH_SERVICE_SECOND=1000;
    public static final long REFRESH_SERVICE_DAY=86400000;

    public static final String DATE_TIME_TYPE="DATE_TIME_TYPE";
    public static final int DATE_TIME_NOTIF=14;
    public static final int DATE_TIME_REPOST=15;
    public static final int DATE_TIME_END=16;


    public static final String COUNT_OF_INTER = "COUNT_OF_INTER";
    public static final String COUNT_OF_LAUNCH = "COUNT_OF_LAUNCH";
    public static final String RATE_INFO = "RATE_INFO";
    public final static int RATE_BEFORE=5;
    public final static int INTER=0;
    public final static int VIDEO=1;


    public static int MAIN = 0;
    public static int DETAIL = 0;

    public static int ACT_DET = 0;
    public static int SETTINGS = 0;
    public static int RECYCLER = 0;

    public static final int REQUEST_CODE_ACTIVITY_DETAIL=10;
    public static final int REQUEST_CODE_DATE=11;
    public static final int REQUEST_CODE_CONFIRM=12;
    public static final int REQUEST_CODE_DELETE=14;
    public static final int REQUEST_CODE_SOON=13;


    public static final String ERROR = "error";

    public static final int OK = 9000;
    public static final int ERR = 9001;

    public static final int NET_ACCESS_ERROR=10000;
    public static final int LOGGED_ERROR=10001;

}
