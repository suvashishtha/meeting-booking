package com.company.meeting.booking;

import com.company.meeting.booking.bean.MeetingRequestBean;
import com.company.meeting.booking.bean.OfficeTimingsBean;
import com.company.meeting.booking.config.ConfigReader;
import com.company.meeting.booking.process.MeetingRequestProcessor;
import com.company.meeting.booking.util.Constants;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Test Class for MeetingRequestProcessor
 */
public class MeetingRequestProcessorTest {

    private static ConfigReader config;

    @BeforeClass
    public static void setUp() {
        config = ConfigReader.getInstance();
        config.readConfig("src/test/resources/config.properties");
    }

    @Test
    public void testReadMeetingRequestsPositive() {
        final MeetingRequestProcessor processor = new MeetingRequestProcessor(config);
        try {
            final List<String> meetingReqList = processor.readMeetingRequests(config.getStringProperty(Constants.INPUT_FILE_LOCATION));
            Assert.assertEquals(true, !meetingReqList.isEmpty());
        } catch (IOException e) {
            assert false;
        }
    }

    @Test(expected = IOException.class)
    public void testReadMeetingRequestsInvalidPath() throws IOException {
        final MeetingRequestProcessor processor = new MeetingRequestProcessor(config);
        try {
            final List<String> meetingReqList = processor.readMeetingRequests("");
            Assert.assertEquals(true, !meetingReqList.isEmpty());
        } catch (IOException e) {
            throw e;
        }
    }

    @Test
    public void testGetMeetingRequestsPositive() {
        final List<String> recordsList = new ArrayList<>();
        recordsList.add("2015-08-17 10:17:06,EMP001,2015-08-21 09:00,2");
        recordsList.add("2015-08-16 12:34:56,EMP002,2015-08-21 09:00,2");
        final MeetingRequestProcessor processor = new MeetingRequestProcessor(config);
        final List<MeetingRequestBean> meetingList = processor.getMeetingRequests(recordsList);
        Assert.assertEquals(true, !meetingList.isEmpty());
        Assert.assertEquals("EMP001", meetingList.get(0).getEmployeeId());
        Assert.assertEquals("EMP002", meetingList.get(1).getEmployeeId());
    }

    @Test
    public void testGetMeetingRequestsNull() {
        final MeetingRequestProcessor processor = new MeetingRequestProcessor(config);
        final List<MeetingRequestBean> meetingList = processor.getMeetingRequests(null);
        Assert.assertEquals(true, meetingList != null);
        Assert.assertEquals(true, meetingList.isEmpty());
    }

    @Test
    public void testGetMeetingRequestsEmptyList() {
        final List<String> recordsList = new ArrayList<>();
        final MeetingRequestProcessor processor = new MeetingRequestProcessor(config);
        final List<MeetingRequestBean> meetingList = processor.getMeetingRequests(recordsList);
        Assert.assertEquals(true, meetingList != null);
        Assert.assertEquals(true, meetingList.isEmpty());
    }

    @Test
    public void testFilterInvalidMeetingsPositive() {
        final List<MeetingRequestBean> meetingList = new ArrayList<>();
        final MeetingRequestBean validMeeting = new MeetingRequestBean("EMP004", 1440252000000L, 1440255600000L, 1439803425000L);
        final MeetingRequestBean invalidMeeting = new MeetingRequestBean("EMP005", 1440165600000L, 1440176400000L, 1439652552000L);
        meetingList.add(validMeeting);
        meetingList.add(invalidMeeting);
        final OfficeTimingsBean bean = new OfficeTimingsBean(28800000L, 59400000L);
        final MeetingRequestProcessor processor = new MeetingRequestProcessor(config);
        final List<MeetingRequestBean> validMeetingList = processor.filterInvalidMeetings(meetingList, bean);
        Assert.assertEquals(true, validMeetingList.contains(validMeeting));
        Assert.assertEquals(false, validMeetingList.contains(invalidMeeting));
    }

    @Test
    public void testFilterInvalidMeetingsEmptyList() {
        final List<MeetingRequestBean> meetingList = new ArrayList<>();
        final OfficeTimingsBean bean = new OfficeTimingsBean(28800000L, 59400000L);
        final MeetingRequestProcessor processor = new MeetingRequestProcessor(config);
        final List<MeetingRequestBean> validMeetingList = processor.filterInvalidMeetings(meetingList, bean);
        Assert.assertEquals(true, validMeetingList != null);
        Assert.assertEquals(true, validMeetingList.isEmpty());
    }

    @Test
    public void testFilterInvalidMeetingsNullArgs() {
        final MeetingRequestProcessor processor = new MeetingRequestProcessor(config);
        final List<MeetingRequestBean> validMeetingList = processor.filterInvalidMeetings(null, null);
        Assert.assertEquals(true, validMeetingList != null);
        Assert.assertEquals(true, validMeetingList.isEmpty());
    }

    @Test
    public void testProcessBookingRequestsPositive() {
        final List<MeetingRequestBean> meetingList = new ArrayList<>();
        final MeetingRequestBean validMeeting1 = new MeetingRequestBean("EMP004", 1440252000000L, 1440255600000L, 1439803425000L);
        final MeetingRequestBean validMeeting2 = new MeetingRequestBean("EMP002", 1440140400000L, 1440147600000L, 1439721296000L);
        final MeetingRequestBean invalidMeeting = new MeetingRequestBean("EMP001", 1440140400000L, 1440147600000L, 1439799426000L);
        meetingList.add(validMeeting1);
        meetingList.add(validMeeting2);
        meetingList.add(invalidMeeting);
        final MeetingRequestProcessor processor = new MeetingRequestProcessor(config);
        final List<MeetingRequestBean> confirmedList = processor.processBookingRequests(meetingList);
        Assert.assertEquals(true, confirmedList.contains(validMeeting1));
        Assert.assertEquals(true, confirmedList.contains(validMeeting2));
        Assert.assertEquals(false, confirmedList.get(0).getEmployeeId().equalsIgnoreCase("EMP001"));
        Assert.assertEquals(false, confirmedList.get(1).getEmployeeId().equalsIgnoreCase("EMP001"));
    }

    @Test
    public void testProcessBookingRequestsEmptyList() {
        final List<MeetingRequestBean> meetingList = new ArrayList<>();
        final MeetingRequestProcessor processor = new MeetingRequestProcessor(config);
        final List<MeetingRequestBean> confirmedList = processor.processBookingRequests(meetingList);
        Assert.assertEquals(true, confirmedList.isEmpty());
    }

    @Test
    public void testProcessBookingRequestsNull() {
        final List<MeetingRequestBean> meetingList = new ArrayList<>();
        final MeetingRequestProcessor processor = new MeetingRequestProcessor(config);
        final List<MeetingRequestBean> confirmedList = processor.processBookingRequests(null);
        Assert.assertEquals(true, confirmedList.isEmpty());
    }

}
