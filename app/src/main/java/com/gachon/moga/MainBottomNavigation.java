package com.gachon.moga;

import com.google.android.material.badge.BadgeDrawable;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.gachon.moga.R;

public class MainBottomNavigation {


    private BadgeDrawable notificationBadge = null;
    private BottomNavigationView bottomNavigationView;
    private int unread;

    public MainBottomNavigation(BottomNavigationView view) {
        this.bottomNavigationView = view;

    }


    public void setBadge(int count) {
        if(count != 0 ) {
            if(notificationBadge == null)
                notificationBadge = bottomNavigationView.getOrCreateBadge(R.id.bottomNavigationNotification);
            notificationBadge.setVisible(true);
            notificationBadge.setNumber(count);
        } else if(notificationBadge != null) {
            notificationBadge.setVisible(false);
            notificationBadge.clearNumber();
        }
    }

    public void setUnread(int unread) {
        this.unread = unread;
        DataIOKt.setUnread(unread);
        setBadge(unread);
    }

    public void setBottomNavigationView(BottomNavigationView bottomNavigationView) {
        this.bottomNavigationView = bottomNavigationView;
    }

    public void setNotificationBadge(BadgeDrawable notificationBadge) {
        this.notificationBadge = notificationBadge;
    }

    public BadgeDrawable getNotificationBadge() {
        return notificationBadge;
    }

    public BottomNavigationView getBottomNavigationView() {
        return bottomNavigationView;
    }

}
