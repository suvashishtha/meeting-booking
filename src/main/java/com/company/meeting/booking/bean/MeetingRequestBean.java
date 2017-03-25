package com.company.meeting.booking.bean;

import org.joda.time.DateTime;

/**
 * {@link MeetingRequestBean} is a Java Bean Class for Holding Meeting Requests
 * <h3>Typical Usage Pattern;</h3>
 * <pre>
 * import com.company.meeting.booking.bean.MeetingRequestBean
 * </pre>
 * public class Handler
 * {
 * 	final MeetingRequestBean bean = new MeetingRequestBean(String, long, long, long);
 * }
 */
public class MeetingRequestBean {

    private String employeeId;
    private long meetingStartTime;
    private long meetingEndTime;
    private long requestSubmissionTime;

    public MeetingRequestBean(final String employeeId, final long meetingStartTime, final long meetingEndTime, final long requestSubmissionTime) {
        this.employeeId = employeeId;
        this.meetingStartTime = meetingStartTime;
        this.meetingEndTime = meetingEndTime;
        this.requestSubmissionTime = requestSubmissionTime;
    }

    public String getEmployeeId() {
        return employeeId;
    }

    public long getMeetingStartTime() {
        return meetingStartTime;
    }

    public long getMeetingEndTime() {
        return meetingEndTime;
    }

    public long getRequestSubmissionTime() {
        return requestSubmissionTime;
    }

    @Override
    public int hashCode() {
        //All meetings for same day should have the same hashcode. The meeting will be compared for overlapping in equals()
        return new DateTime(meetingStartTime).withTimeAtStartOfDay().hashCode();
    }

    @Override
    public boolean equals(Object object) {
        if (!(object instanceof MeetingRequestBean))
            return false;
        final MeetingRequestBean bean = (MeetingRequestBean) object;
        //All meetings which have a overlap are considered as equals so that overlap can be determined by comparing objects
        if (bean.getMeetingStartTime() > this.getMeetingStartTime() || bean.getMeetingEndTime() < this.getMeetingEndTime())
            return false;
        else
            return true;
    }

    @Override
    public String toString() {
        return "EmployeeId: " + employeeId + " " +
                "Meeting Start Time: " + meetingStartTime + " " + " Meeting End Time: " + meetingEndTime + " " +
                "Request Submission Time: " + requestSubmissionTime;
    }
}
