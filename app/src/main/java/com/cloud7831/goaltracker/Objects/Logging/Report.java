package com.cloud7831.goaltracker.Objects.Logging;

public abstract class Report extends Mail {

    // data stores all the goals progress for the week. Each goal is stored in the format:
    // "Goal Name" \ unit id \ quotaAchieved \ goalQuota
    // This data is used to form each row in the form
    private String data;

}
