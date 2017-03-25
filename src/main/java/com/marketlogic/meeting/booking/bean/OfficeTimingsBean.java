package com.marketlogic.meeting.booking.bean;

/**
 * Created by suvashishtha on 3/24/2017.
 */
public class OfficeTimingsBean {

    private long startTime;
    private long endTime;

    public OfficeTimingsBean(final long startTime, final long endTime) {
        this.startTime = startTime;
        this.endTime = endTime;
    }

    public long getStartTime() {
        return startTime;
    }

    public long getEndTime() {
        return endTime;
    }
}
