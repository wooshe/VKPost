package com.wooshe.VKContests.no_use;

import android.content.Context;
import android.content.Intent;
import android.support.multidex.MultiDex;
import android.widget.Toast;

import com.vk.sdk.VKAccessToken;
import com.vk.sdk.VKAccessTokenTracker;
import com.vk.sdk.VKSdk;
import com.vk.sdk.util.VKUtil;
import com.wooshe.VKContests.MainActivity;
import com.wooshe.VKContests.Util.ErrorReporter;

import java.util.Arrays;

public class Application extends android.app.Application
{
    private static Context Staticcontext;

    VKAccessTokenTracker vkAccessTokenTracker = new VKAccessTokenTracker()
    {
        @Override
        public void onVKAccessTokenChanged(VKAccessToken oldToken, VKAccessToken newToken)
        {
            if (newToken == null)
            {
                Toast.makeText(Application.this, "AccessToken invalidated", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(Application.this, MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
            }
        }
    };

    @Override
    public void onCreate()
    {
        ErrorReporter.bindReporter(this);
        super.onCreate();
        vkAccessTokenTracker.startTracking();
        VKSdk.initialize(this);
    }

    public static Context getAppContext() {
        return Application.Staticcontext;
    }

    @Override
    protected void attachBaseContext(Context context) {
        super.attachBaseContext(context);
        MultiDex.install(this);
    }
}

