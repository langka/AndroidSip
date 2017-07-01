package com.bupt.androidsip.mananger;

import com.bupt.androidsip.entity.EventConst;

import org.greenrobot.eventbus.EventBus;

/**
 * Created by xusong on 2017/7/1.
 * About:
 */

public class EventManager {
    private static EventManager instance;
    private EventBus eventBus;

    private EventManager() {
        eventBus = EventBus.getDefault();
    }

    public static EventManager GetInstance() {
        if (instance == null) {
            synchronized (EventManager.class) {
                if (instance == null) {
                    instance = new EventManager();
                }
            }
        }
        return instance;
    }

    public EventBus getEventBus() {
        return eventBus;
    }

    public void post(EventConst.BaseEvent event) {
        eventBus.post(event);
    }

}
