package tony.com.goodreporter;

import android.text.TextUtils;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

/**
 * Created by tony on 2015/10/25.
 */
public class Utils {
    public static final String EXTRA_ACCESS_TOKEN = "extra_access_token";
    private static final String GMT_TIMEZONE_ID = "GMT";
    public static final long TIME_NOW = -1;
    public static final String TIME_DEFAULT_TIMEZONE = "default_timezone";

    public static String convertMillisToDateString(long millis, String format, String timezoneId, boolean showWithZone) {
        StringBuilder timeString = new StringBuilder();
        TimeZone timeZone = null;
        if(TextUtils.isEmpty(timezoneId) || timezoneId.equals(TIME_DEFAULT_TIMEZONE))
            timeZone = TimeZone.getDefault();
        else
            timeZone = getTimeZoneByString(timezoneId);

        SimpleDateFormat sdfDate = new SimpleDateFormat(format);
        sdfDate.setTimeZone(timeZone);
        if(millis == TIME_NOW)
            timeString.append(sdfDate.format(new Date()));
        else
            timeString.append(sdfDate.format(new Date(millis)));

        if(showWithZone) {
            if (!timeZone.getID().equals(TimeZone.getDefault().getID())) {
                timeString.append("(")
                        .append(timeZone.getDisplayName())
                        .append(")");
            }
        }

        return timeString.toString();
    }

    private static TimeZone getTimeZoneByString(String zone) {
        TimeZone timeZone;

        timeZone = TextUtils.isEmpty(zone) ? TimeZone.getDefault() : TimeZone.getTimeZone(zone);
        if (timeZone.getID().equals(GMT_TIMEZONE_ID) && !GMT_TIMEZONE_ID.endsWith(zone)) {
            timeZone = TimeZone.getDefault();
        }

        return timeZone;
    }
}
