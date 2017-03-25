package com.company.meeting.booking.bean;

/**
 * {@link OfficeTimingsBean} is a Java Bean Class for Holding Office Timings
 * <h3>Typical Usage Pattern;</h3>
 * <pre>
 * import com.company.meeting.booking.bean.OfficeTimingsBean
 * </pre>
 * public class Handler
 * {
 * 	final OfficeTimingsBean bean = new OfficeTimingsBean(long, long);
 * }
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

    @Override
    public String toString() {
        return "Office Start Time: " + startTime + " " + "Office End Time: " + endTime;
    }
}
