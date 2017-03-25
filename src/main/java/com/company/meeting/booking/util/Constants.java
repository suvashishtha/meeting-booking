package com.company.meeting.booking.util;

/**
 * {@link Constants} is a interface for holding application constants
 * The class also helps to avoid magic string implementations in the code
 * <h3>Typical Usage Pattern;</h3>
 * <pre>
 * import com.company.meeting.booking.util.Constants
 * </pre>
 */
public interface Constants {

    //Config Constants
    String INPUT_FILE_LOCATION = "input.file.location";
    String OFFICE_TIMING_FORMAT = "office.timings.format";
    String REQUEST_SUB_DATE_FORMAT = "request.submission.date.format";
    String MEETING_START_DATE_FORMAT = "meeting.start.date.format";
    String OUTPUT_DATE_FORMAT = "output.date.format";
    String OUTPUT_TIME_FORMAT = "output.time.format";

    //Utility String Constants
    String COMMA = ",";
}
