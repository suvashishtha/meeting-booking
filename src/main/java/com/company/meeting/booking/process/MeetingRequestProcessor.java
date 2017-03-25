package com.company.meeting.booking.process;

import com.company.meeting.booking.bean.MeetingRequestBean;
import com.company.meeting.booking.bean.OfficeTimingsBean;
import com.company.meeting.booking.config.ConfigReader;
import com.company.meeting.booking.helper.MeetingComparator;
import com.company.meeting.booking.util.Constants;
import org.apache.log4j.Logger;
import org.joda.time.DateTime;
import org.joda.time.DateTimeComparator;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * {@link MeetingRequestProcessor} class contains methods for processing meeting booking requests.
 * The class contains methods for business functionalities such as reading meeting data, filter them
 * out based on the requirements(office hour timings etc.)
 * <h3>Typical Usage Pattern;</h3>
 * <pre>
 * import com.company.meeting.booking.process.MeetingRequestProcessor
 * </pre>
 * public class Handler
 * {
 * 	final MeetingRequestProcessor processor = new MeetingRequestProcessor(ConfigReader);
 * }
 */
public class MeetingRequestProcessor {

    private static Logger logger = Logger.getLogger(MeetingRequestProcessor.class);
    private ConfigReader config;

    public MeetingRequestProcessor(final ConfigReader config) {
        this.config = config;
    }

    /**
     * Method to read the input file containing meeting requests and office timings.
     *
     * @param path - Path of file on local file system
     * @return - All the records present in the file as List<String>
     * @throws IOException
     */
    public List<String> readMeetingRequests(final String path) throws IOException {
        final List<String> inputRecordsList = new ArrayList<>();
        final BufferedReader br = Files.newBufferedReader(Paths.get(path));
        String line = null;
        while ((line = br.readLine()) != null) {
            inputRecordsList.add(line);
        }
        return inputRecordsList;
    }

    /**
     * Method to get the meetingRequestBean objects from the String records list.
     * @param inputRecordsList - All meeting requests as List<String>
     * @return - List of MeetingRequests Object as List<MeetingRequestBean>
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
     * Method to filter the invalid meetings based on office timings.
     * All the meetings lying before/after office hours are considered as invalid meetings.
     * @param meetingRequestList - List of MeetingRequests Object as List<MeetingRequestBean>
     * @param officeTimingsBean - OfficeTimings as Bean Object
     * @return - List of Valid MeetingRequests Object as List<MeetingRequestBean>
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
     * Method to process the booking requests. Method determines the meeting conflicts
     * & resolves them based on priority on Request Submission time.
     * @param meetingRequestList - List of Valid MeetingRequests Object as List<MeetingRequestBean>
     * @return - - List of Confirmed MeetingRequests Object as List<MeetingRequestBean>
     */
    public List<MeetingRequestBean> processBookingRequests(final List<MeetingRequestBean> meetingRequestList) {
        final List<MeetingRequestBean> meetingConfirmedList = new ArrayList<>();
        if (null == meetingRequestList) {
            logger.info("Received Null Argument method getMeetingRequest. Returning Empty List");
            return meetingConfirmedList;
        }
        Collections.sort(meetingRequestList, new MeetingComparator.RequestSubmissionTimeComparator());
        for (MeetingRequestBean bean : meetingRequestList) {
            if (!meetingConfirmedList.contains(bean))
                meetingConfirmedList.add(bean);
        }
        return meetingConfirmedList;
    }

    /**
     * Method to compare the Meeting Requests against office timings.
     * @param bean - Meeting Request as a Bean Object
     * @param officeTimingsBean - Office Timings as Bean Object
     * @return - validation status of meeting(true/false)
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
