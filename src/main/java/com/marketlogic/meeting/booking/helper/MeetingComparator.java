package com.marketlogic.meeting.booking.helper;

import com.marketlogic.meeting.booking.bean.MeetingRequestBean;

import java.util.Comparator;

/**
 * Created by suvashishtha on 3/24/2017.
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
}
