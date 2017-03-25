package com.company.meeting.booking;

import com.company.meeting.booking.config.ConfigReader;
import com.company.meeting.booking.process.MeetingRequestController;
import org.apache.log4j.Logger;

import java.nio.file.Files;
import java.nio.file.Paths;

/**
 * {@link AppMain} is responsible for instantiating Meeting Booking Program.
 * AppMain instantiates {@link ConfigReader}, {@link MeetingRequestController} for further processing.
 */
public class AppMain {

    private static final Logger logger = Logger.getLogger(AppMain.class);

    /**
     * @param args
     */
    public static void main(String[] args) {
        try {
            final boolean isArgValid = validateArgs(args);
            if (!isArgValid) {
                logger.error("Error in validating arguments. Exiting the program");
                System.exit(-1);
            }
            final ConfigReader config = ConfigReader.getInstance();
            config.readConfig(args[0]);
            final MeetingRequestController controller = new MeetingRequestController(config);
            controller.process();
        }
        catch (Exception e) {
            logger.error("Exception in Method main() " + e.getMessage(), e);
        }
    }

    /**
     * @param args
     * @return
     */
    private static boolean validateArgs(String[] args) {
        if (args.length < 1) {
            logger.error("Usage: <Application Properties File Path>");
            return false;
        } else if (!Files.exists(Paths.get(args[0]))) {
            logger.error("Application Properties File Does Not Exist on Path: " + args[0]);
            return false;
        }
        return true;
    }
}
