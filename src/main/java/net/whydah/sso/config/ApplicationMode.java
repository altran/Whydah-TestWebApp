package net.whydah.sso.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;

/**
 * Get application mode from os environment or system property.
 */
public class ApplicationMode {
    public final static String IAM_MODE_KEY = "IAM_MODE";
    public final static String TEST = "TEST";
    public final static String TEST_L = "TEST_LOCALHOST";
    private static final Logger logger = LoggerFactory.getLogger(ApplicationMode.class);


    public static String getApplicationMode() {
        // Enten PROD, TEST, DEV
        String appMode = System.getenv(IAM_MODE_KEY);
        if (appMode == null) {
            appMode = System.getProperty(IAM_MODE_KEY);
        }
        if (appMode == null) {
            appMode = TEST;
//            System.err.println(IAM_MODE_KEY + " not defined in environment. System will exit(4).");
//            System.exit(4);
        }
        if (!Arrays.asList(TEST, TEST_L).contains(appMode)) {
            System.err.println("Unknown " + IAM_MODE_KEY + ": " + appMode + ". System will exit(5).");
            System.exit(5);
        }
        logger.info(String.format("Running in %s mode", appMode));
        return appMode;
    }
}
