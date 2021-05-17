package com.cloud7831.goaltracker.Objects.Logging;

public abstract class Mail {

    private int id;
    private String title;
    private int wasRead = 0; // Mail always starts as unread.
    private int datePosted; // When the mail was delivered to the user.

    public abstract void open(); // Opens the mail in a dialog window.

    public String getTitle() {
        return title;
    }

    public boolean getWasRead(){
        return wasRead == 1;
    }

    private void markAsRead(){
        wasRead = 1;
    }

}
