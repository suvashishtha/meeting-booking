package com.company.meeting.booking.bean;

import org.joda.time.DateTime;

/**
 * Created by suvashishtha on 3/24/2017.
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
        return new DateTime(meetingStartTime).withTimeAtStartOfDay().hashCode();
    }

    @Override
    public boolean equals(Object object) {
        if (!(object instanceof MeetingRequestBean))
            return false;
        final MeetingRequestBean bean = (MeetingRequestBean) object;
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
