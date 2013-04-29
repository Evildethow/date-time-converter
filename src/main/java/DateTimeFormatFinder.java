import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

public class DateTimeFormatFinder {

    private static DateTimeFormatFinder dateTimeFormatFinder;
    private final DateTimeFormatProperties dateTimeFormatProperties;

    private DateTimeFormatFinder() {
        List<DateTimeFormatProperty> dateTimeFormatPropertyList = new ArrayList<DateTimeFormatProperty>();
        dateTimeFormatPropertyList.add(new DateTimeFormatProperty("yyyy-MM-dd'T'HH:mm:ss'Z'"));
        dateTimeFormatPropertyList.add(new DateTimeFormatProperty("yyyy-MM-dd'T'HH:mm:ssZ"));
        dateTimeFormatPropertyList.add(new DateTimeFormatProperty("yyyy-MM-dd'T'HH:mm:ss"));
        dateTimeFormatPropertyList.add(new DateTimeFormatProperty("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'"));
        dateTimeFormatPropertyList.add(new DateTimeFormatProperty("yyyy-MM-dd'T'HH:mm:ss.SSSZ"));
        dateTimeFormatPropertyList.add(new DateTimeFormatProperty("yyyy-MM-dd HH:mm:ss"));
        dateTimeFormatPropertyList.add(new DateTimeFormatProperty("MM/dd/yyyy HH:mm:ss"));
        dateTimeFormatPropertyList.add(new DateTimeFormatProperty("MM/dd/yyyy'T'HH:mm:ss.SSS'Z'"));
        dateTimeFormatPropertyList.add(new DateTimeFormatProperty("MM/dd/yyyy'T'HH:mm:ss.SSSZ"));
        dateTimeFormatPropertyList.add(new DateTimeFormatProperty("MM/dd/yyyy'T'HH:mm:ss.SSS"));
        dateTimeFormatPropertyList.add(new DateTimeFormatProperty("MM/dd/yyyy'T'HH:mm:ssZ"));
        dateTimeFormatPropertyList.add(new DateTimeFormatProperty("MM/dd/yyyy'T'HH:mm:ss"));
        dateTimeFormatPropertyList.add(new DateTimeFormatProperty("yyyy:MM:dd HH:mm:ss"));
        dateTimeFormatPropertyList.add(new DateTimeFormatProperty("EEE MMM d HH:mm:ss z yyyy"));
        dateTimeFormatProperties =  new DateTimeFormatProperties(dateTimeFormatPropertyList);
    }

    public static DateTimeFormatFinder getInstance() {
        if (dateTimeFormatFinder == null) {
            dateTimeFormatFinder = new DateTimeFormatFinder();
        }
        return dateTimeFormatFinder;
    }

    public String getDateTimeFormat(String dateTime) {
        return getDateTimeFormatInternal(dateTime);
    }

    private String getDateTimeFormatInternal(String dateTime) {
        if (dateTime == null || dateTime.isEmpty()) {
            throw new DateTimeFormatFinderIllegalArgumentException(dateTime);
        }

        SimpleDateFormat parser = new SimpleDateFormat();
        parser.setLenient(true);
        ParsePosition pos = new ParsePosition(0);
        for (DateTimeFormatProperty dateTimeFormatProperty : dateTimeFormatProperties.dateTimeFormatPropertyList) {

            String pattern = dateTimeFormatProperty.pattern;

            if (dateTimeFormatProperty.pattern.endsWith("ZZ")) {
                pattern = pattern.substring(0, pattern.length() - 1);
            }

            parser.applyPattern(pattern);
            pos.setIndex(0);

            String dateTimeCopy = dateTime;
            if (dateTimeFormatProperty.pattern.endsWith("ZZ")) {
                dateTimeCopy = dateTime.replaceAll("([-+][0-9][0-9]):([0-9][0-9])$", "$1$2");
            }

            Date date = parser.parse(dateTimeCopy, pos);
            if (date != null && pos.getIndex() == dateTimeCopy.length()) {
                return pattern;
            }
        }
        throw new DateTimeFormatFinderParseException(dateTime);
    }

    private static class DateTimeFormatProperties {

        private final List<DateTimeFormatProperty> dateTimeFormatPropertyList;

        private DateTimeFormatProperties(List<DateTimeFormatProperty> dateTimeFormatPropertyList) {
            this.dateTimeFormatPropertyList = dateTimeFormatPropertyList;
        }

        @Override
        public String toString() {
            StringBuilder builder = new StringBuilder();

            builder.append("\n").append("Available formats are:").append("\n");

            Iterator<DateTimeFormatProperty> iterator = dateTimeFormatPropertyList.iterator();
            while (iterator.hasNext()) {
                builder.append("\t").append("[").append(iterator.next().pattern).append("]");
                if (iterator.hasNext()) {
                    builder.append(",").append("\n");
                }
            }
            return builder.toString();
        }
    }

    private static class DateTimeFormatProperty {
        private final String pattern;

        private DateTimeFormatProperty(String pattern) {
            this.pattern = pattern;
        }
    }

    class DateTimeFormatFinderParseException extends RuntimeException {

        static final String EXCEPTION_MESSAGE_PREFIX =  "Unable to parse Date/Time String [";

        public DateTimeFormatFinderParseException(String dateTime) {
            super(EXCEPTION_MESSAGE_PREFIX + dateTime + "] " + dateTimeFormatProperties);
        }
    }

    class DateTimeFormatFinderIllegalArgumentException extends RuntimeException {

        static final String NULL_DATE_TIME_EXCEPTION = "Unable to parse empty Date/Time";
        static final String EMPTY_DATE_TIME_EXCEPTION = "Unable to parse empty Date/Time";

        public DateTimeFormatFinderIllegalArgumentException(String dateTime) {
            super(dateTime == null ? NULL_DATE_TIME_EXCEPTION : EMPTY_DATE_TIME_EXCEPTION);
        }
    }

}
