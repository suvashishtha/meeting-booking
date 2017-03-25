package com.company.meeting.booking.process;

import com.company.meeting.booking.bean.MeetingRequestBean;
import com.company.meeting.booking.bean.OfficeTimingsBean;
import com.company.meeting.booking.config.ConfigReader;
import com.company.meeting.booking.util.Constants;
import org.apache.log4j.Logger;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/**
 * {@link MeetingRequestController} is a controller class for Meeting Booking Application.
 * The class calls various methods of different other classes to process the meeting requests.
 * The proces() method contains the overall flow for processing meeting booking requests.
 * <h3>Typical Usage Pattern;</h3>
 * <pre>
 * import com.company.meeting.booking.process.MeetingRequestController
 * </pre>
 * public class Handler
 * {
 * final MeetingRequestController controller = new MeetingRequestController(ConfigReader);
 * controller.process();
 * }
 */
public class MeetingRequestController {

    private static final Logger logger = Logger.getLogger(MeetingRequestController.class);
    private ConfigReader config;

    public MeetingRequestController(final ConfigReader config) {
        this.config = config;
    }

    /**
     * Method to process Meeting Requests. Contains overall flow for the application
     */
    public void process() {
        try {
            final MeetingRequestProcessor processor = new MeetingRequestProcessor(config);
            final List<String> inputRequestsList = processor.readMeetingRequests(config.getStringProperty(Constants.INPUT_FILE_LOCATION));
            if (null != inputRequestsList && !inputRequestsList.isEmpty()) {
                final OfficeTimingsBean officeTimingsBean = getOfficeTimings(inputRequestsList);
                final List<MeetingRequestBean> meetingReqList = processor.getMeetingRequests(inputRequestsList);
                final List<MeetingRequestBean> validMeetingsReqList = processor.filterInvalidMeetings(meetingReqList, officeTimingsBean);
                final List<MeetingRequestBean> confirmedMeetingList = processor.processBookingRequests(validMeetingsReqList);
                final Map<Long, List<MeetingRequestBean>> outputMap = formatOutput(confirmedMeetingList);
                displayCalendar(outputMap);
            } else
                logger.info("Empty Input File Received. Nothing to Process");

        } catch (Exception e) {
            logger.error("Exception in process method. " + e.getMessage(), e);
        }
    }

    /**
     * Method for Getting Office Timings from the Input data read from text file.
     * Method reads the first record in the list & returns the OfficeTimingsBean object
     *
     * @param inputRecordsList - List of All the records read from file
     * @return Bean Object containing office timings
     */
    private OfficeTimingsBean getOfficeTimings(final List<String> inputRecordsList) {
        final DateTimeFormatter officeTimeFormatter = DateTimeFormat.forPattern(config.getStringProperty(Constants.OFFICE_TIMING_FORMAT));
        //Assumption that office timings will always be first record
        //TODO: Future implementation can be based on regex to avoid hardcoding of office timings position in the file
        final String[] officeTimings = inputRecordsList.get(0).split(Constants.COMMA);
        final long startTime = officeTimeFormatter.parseDateTime(officeTimings[0]).getMillis();
        final long endTime = officeTimeFormatter.parseDateTime(officeTimings[1]).getMillis();
        final OfficeTimingsBean officeTimingsBean = new OfficeTimingsBean(startTime, endTime);
        inputRecordsList.remove(0); //Office Timings no longer required in the input records list
        return officeTimingsBean;
    }

    /**
     * Method to format the confirmed meeting requests in chronological order
     *
     * @param confirmedMeetingList Confirmed Meeting Requests
     * @return Map containing meeting request sorted & grouped on meeting start date.
     */
    private Map<Long, List<MeetingRequestBean>> formatOutput(final List<MeetingRequestBean> confirmedMeetingList) {
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
     * Method to display the final output to the console.
     *
     * @param resultMap Map containing the confirmed meeting requests chrnologically sorted & grouped on meeting start date
     */
    private void displayCalendar(final Map<Long, List<MeetingRequestBean>> resultMap) {
        final DateTimeFormatter dateFormatter = DateTimeFormat.forPattern(config.getStringProperty(Constants.OUTPUT_DATE_FORMAT));
        final DateTimeFormatter timeFormatter = DateTimeFormat.forPattern(config.getStringProperty(Constants.OUTPUT_TIME_FORMAT));
        for (Map.Entry<Long, List<MeetingRequestBean>> entry : resultMap.entrySet()) {
            System.out.println(dateFormatter.print(entry.getKey()));
            for (MeetingRequestBean bean : entry.getValue()) {
                System.out.println(timeFormatter.print(bean.getMeetingStartTime()) + " " + timeFormatter.print(bean.getMeetingEndTime()) + " " + bean.getEmployeeId());
            }
        }
    }
}
