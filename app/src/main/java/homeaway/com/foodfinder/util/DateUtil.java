package homeaway.com.foodfinder.util;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class DateUtil {

    public static String getTodaysDate() {
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat mdformat = new SimpleDateFormat("yyyyMMdd", Locale.US);
        return mdformat.format(calendar.getTime());
    }
}
