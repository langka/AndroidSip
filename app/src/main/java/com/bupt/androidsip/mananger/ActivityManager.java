package com.bupt.androidsip.mananger;

import android.app.Activity;

import com.bupt.androidsip.entity.Chat;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by vita-nove on 12/07/2017.
 */

public class ActivityManager {

    private List<Activity> list = new ArrayList<>();

    private static ActivityManager instance = new ActivityManager();

    private ActivityManager() {
    }

    public static ActivityManager getActivityManager() {
        return instance;
    }

    public void addActivity(Activity act) {
        list.add(act);
    }

    public void exit() {
        try {
            for (Activity activity : list) {
                if (activity != null)
                    activity.finish();
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            System.exit(0);
        }
    }

}
