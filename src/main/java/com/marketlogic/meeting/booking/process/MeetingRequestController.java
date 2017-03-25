package com.marketlogic.meeting.booking.process;

import com.marketlogic.meeting.booking.bean.MeetingRequestBean;
import com.marketlogic.meeting.booking.bean.OfficeTimingsBean;
import com.marketlogic.meeting.booking.config.ConfigReader;
import com.marketlogic.meeting.booking.helper.MeetingComparator;
import com.marketlogic.meeting.booking.util.Constants;
import org.apache.log4j.Logger;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * Created by suvashishtha on 3/24/2017.
 */
public class MeetingRequestController {

    private static final Logger logger = Logger.getLogger(MeetingRequestController.class);
    private ConfigReader config;

    public MeetingRequestController(final ConfigReader config) {
        this.config = config;
    }

    /**
     *
     */
    public void process() {
        try {
            final List<String> inputRequestsList = readInputData(config.getStringProperty(Constants.INPUT_FILE_LOCATION));
            if (null != inputRequestsList && !inputRequestsList.isEmpty()) {
                final OfficeTimingsBean officeTimingsBean = getOfficeTimings(inputRequestsList);
                final MeetingRequestProcessor processor = new MeetingRequestProcessor(config);
                final List<MeetingRequestBean> meetingReqList = processor.getMeetingRequests(inputRequestsList);
                final List<MeetingRequestBean> validMeetingsReqList = processor.filterInvalidMeetings(meetingReqList, officeTimingsBean);
                Collections.sort(validMeetingsReqList, new MeetingComparator.RequestSubmissionTimeComparator());
                final List<MeetingRequestBean> confirmedMeetingList = processor.processBookingRequests(validMeetingsReqList);
                final Map<Long, List<MeetingRequestBean>> outputMap = processor.formatOutput(confirmedMeetingList);
                displayCalendar(outputMap);
            }
            else
                logger.info("Empty Input File Received. Nothing to Process");

        } catch (Exception e) {
            logger.error("Exception in process method. " + e.getMessage(), e);
        }
    }

    /**
     * @param path
     * @return
     * @throws IOException
     */
    public List<String> readInputData(final String path) throws IOException {
        final List<String> inputRecordsList = new ArrayList<>();
        final BufferedReader br = Files.newBufferedReader(Paths.get(path));
        String line = null;
        while ((line = br.readLine()) != null) {
            inputRecordsList.add(line);
        }
        return inputRecordsList;
    }

    /**
     * @param inputRecordsList
     * @return
     */
    private OfficeTimingsBean getOfficeTimings(final List<String> inputRecordsList) {
        final DateTimeFormatter officeTimeFormatter = DateTimeFormat.forPattern(config.getStringProperty(Constants.OFFICE_TIMING_FORMAT));
        final String[] officeTimings = inputRecordsList.get(0).split(Constants.COMMA);
        final long startTime = officeTimeFormatter.parseDateTime(officeTimings[0]).getMillis();
        final long endTime = officeTimeFormatter.parseDateTime(officeTimings[1]).getMillis();
        final OfficeTimingsBean officeTimingsBean = new OfficeTimingsBean(startTime, endTime);
        inputRecordsList.remove(0);
        return officeTimingsBean;
    }

    /**
     * @param resultMap
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
