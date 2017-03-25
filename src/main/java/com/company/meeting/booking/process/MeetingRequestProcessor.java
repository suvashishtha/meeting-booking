package com.company.meeting.booking.process;

import com.company.meeting.booking.bean.MeetingRequestBean;
import com.company.meeting.booking.bean.OfficeTimingsBean;
import com.company.meeting.booking.config.ConfigReader;
import com.company.meeting.booking.util.Constants;
import org.apache.log4j.Logger;
import org.joda.time.DateTime;
import org.joda.time.DateTimeComparator;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/**
 * Created by suvashishtha on 3/24/2017.
 */
public class MeetingRequestProcessor {

    private static Logger logger = Logger.getLogger(MeetingRequestProcessor.class);
    private ConfigReader config;

    public MeetingRequestProcessor(final ConfigReader config) {
        this.config = config;
    }

    /**
     * @param inputRecordsList
     * @return
     * @throws IOException
     */
    public List<MeetingRequestBean> getMeetingRequests(final List<String> inputRecordsList) {
        final List<MeetingRequestBean> meetingReqList = new ArrayList<>();
        if (null == inputRecordsList) {
            logger.info("Received Null Argument method getMeetingRequest. Returning Empty List");
            return meetingReqList;
        }
        final DateTimeFormatter reqSubFormatter = DateTimeFormat.forPattern(config.getStringProperty(Constants.REQUEST_SUB_DATE_FORMAT));
        final DateTimeFormatter meetingStartFormatter = DateTimeFormat.forPattern(config.getStringProperty(Constants.MEETING_START_DATE_FORMAT));
        for (String line : inputRecordsList) {
            final String[] fields = line.split(Constants.COMMA);
            final DateTime requestSubmitTime = reqSubFormatter.parseDateTime(fields[0]);
            final String employeeId = fields[1];
            final DateTime meetingStartTime = meetingStartFormatter.parseDateTime(fields[2]);
            final DateTime meetingEndTime = meetingStartTime.plusHours(Integer.parseInt(fields[3]));
            final MeetingRequestBean meetingRequestBean = new MeetingRequestBean(employeeId, meetingStartTime.getMillis(),
                    meetingEndTime.getMillis(), requestSubmitTime.getMillis());
            meetingReqList.add(meetingRequestBean);
        }
        return meetingReqList;
    }

    /**
     * @param meetingRequestList
     * @param officeTimingsBean
     * @return
     */
    public List<MeetingRequestBean> filterInvalidMeetings(final List<MeetingRequestBean> meetingRequestList, final OfficeTimingsBean officeTimingsBean) {
        final List<MeetingRequestBean> meetingReqList = new ArrayList<>();
        if (null == meetingRequestList || null == officeTimingsBean) {
            logger.info("Received Null Arguments in Method filterInvalidMeetings(). Returning Empty List");
            return meetingReqList;
        }
        for (MeetingRequestBean bean : meetingRequestList) {
            final boolean isValidMeeting = compareMeetings(bean, officeTimingsBean);
            if (isValidMeeting)
                meetingReqList.add(bean);
        }
        return meetingReqList;
    }

    /**
     * @param meetingRequestList
     * @return
     */
    public List<MeetingRequestBean> processBookingRequests(final List<MeetingRequestBean> meetingRequestList) {
        final List<MeetingRequestBean> meetingConfirmedList = new ArrayList<>();
        if (null == meetingRequestList) {
            logger.info("Received Null Argument method getMeetingRequest. Returning Empty List");
            return meetingConfirmedList;
        }
        for (MeetingRequestBean bean : meetingRequestList) {
            if (!meetingConfirmedList.contains(bean))
                meetingConfirmedList.add(bean);
        }
        return meetingConfirmedList;
    }

    /**
     * @param confirmedMeetingList
     * @return
     */
    public Map<Long, List<MeetingRequestBean>> formatOutput(final List<MeetingRequestBean> confirmedMeetingList) {
        final Map<Long, List<MeetingRequestBean>> outputMap = new TreeMap<>();
        if (null == confirmedMeetingList) {
            logger.info("Received Null Argument method getMeetingRequest. Returning Empty Map");
            return outputMap;
        }
        for (MeetingRequestBean bean : confirmedMeetingList) {
            final long meetingDate = new DateTime(bean.getMeetingStartTime()).withTimeAtStartOfDay().getMillis();
            if (!outputMap.containsKey(meetingDate)) {
                final List<MeetingRequestBean> meetingList = new ArrayList<>();
                meetingList.add(bean);
                outputMap.put(meetingDate, meetingList);
            } else {
                outputMap.get(meetingDate).add(bean);
            }
        }
        return outputMap;
    }

    /**
     * @param bean
     * @param officeTimingsBean
     * @return
     */
    private boolean compareMeetings(final MeetingRequestBean bean, final OfficeTimingsBean officeTimingsBean) {
        final DateTimeComparator comparator = DateTimeComparator.getTimeOnlyInstance();
        if (comparator.compare(bean.getMeetingStartTime(), officeTimingsBean.getStartTime()) < 0)
            return false;
        else if (comparator.compare(bean.getMeetingEndTime(), officeTimingsBean.getEndTime()) > 0)
            return false;
        else
            return true;
    }
}
