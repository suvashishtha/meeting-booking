package com.company.meeting.booking.helper;

import com.company.meeting.booking.bean.MeetingRequestBean;

import java.util.Comparator;

/**
 * {@link MeetingComparator} contains inner classes for comparator implementations
 * to compare Meeting Requests based on different parameters. Current implementation
 * compares the requests based on request submission time. This can be extended to compare
 * meeting requests based on other parameters
 * <h3>Typical Usage Pattern;</h3>
 * <pre>
 * import com.company.meeting.booking.helper.MeetingComparator
 * </pre>
 * public class Handler
 * {
 * 	final RequestSubmissionTimeComparator comparator = new MeetingComparator.RequestSubmissionTimeComparator();
 * 	Collections.sort(List<MeetingRequestBean>, comparator);
 * }
 */
public class MeetingComparator {

    public static class RequestSubmissionTimeComparator implements Comparator<MeetingRequestBean> {
        @Override
        public int compare(MeetingRequestBean meetingRequestBean1, MeetingRequestBean meetingRequestBean2) {
            if (meetingRequestBean1.getRequestSubmissionTime() > meetingRequestBean2.getRequestSubmissionTime())
                return 1;
            else
                return -1;
        }
    }
    //TODO: Implementation of comparators to compare using other parameters
}
