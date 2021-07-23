package com.apk4android.salluu3alayhie.utils;

/**
 * Created by Ahmed on 10/11/2017.
 */

public class CheckOnTask {

    private boolean run;
    private String notificationType;

    public void setRunOnTaskRemoved(boolean run) {
        this.run = run;
    }

    public boolean isRun() {
        return run;
    }

    public String getNotificationType() {
        return notificationType;
    }

    public void setNotificationType(String notificationType) {
        this.notificationType = notificationType;
    }


}
