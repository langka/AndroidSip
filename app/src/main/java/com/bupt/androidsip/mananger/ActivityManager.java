package com.bupt.androidsip.mananger;

import android.app.Activity;

import com.bupt.androidsip.entity.Chat;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by vita-nove on 12/07/2017.
 */

public class ActivityManager {
    public List<Activity> getList() {
        return list;
    }

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

}
