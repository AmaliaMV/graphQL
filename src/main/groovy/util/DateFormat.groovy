package util

import org.apache.commons.lang.time.FastDateFormat

class DateFormat {

    static String format(Date date) {
        if (!date) {
            return ""
        }

        return FastDateFormat.getInstance("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", TimeZone.getTimeZone('UTC')).format(date)
    }
}
