import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

public class DateTimeFormatFinder {

    private final DateTimeFormatProperties dateTimeFormatProperties;
    private final String dateTime;

    public DateTimeFormatFinder(String dateTime) {
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
        this.dateTime = dateTime;
    }

    public String getDateTimeFormat() {
        if (dateTime == null || dateTime.isEmpty()) {
            throw new DateTimeFormatFinderIllegalArgumentException(dateTime);
        }

        for (DateTimeFormatProperty dateTimeFormatProperty : dateTimeFormatProperties) {
            if (dateTimeFormatProperty.isMatch) {
                return dateTimeFormatProperty.pattern;
            }
        }
        throw new DateTimeFormatFinderParseException(dateTime);
    }

    private class DateTimeFormatProperties implements Iterable<DateTimeFormatProperty> {

        private final List<DateTimeFormatProperty> dateTimeFormatPropertyList;

        private DateTimeFormatProperties(List<DateTimeFormatProperty> dateTimeFormatPropertyList) {
            this.dateTimeFormatPropertyList = dateTimeFormatPropertyList;
        }

        @Override
        public Iterator<DateTimeFormatProperty> iterator() {
            return new DateTimeFormatPropertyIterator();
        }

        private class DateTimeFormatPropertyIterator implements Iterator<DateTimeFormatProperty> {
            private final SimpleDateFormat parser;
            private final ParsePosition pos;

            private int position;

            private DateTimeFormatPropertyIterator() {
                parser = new SimpleDateFormat();
                pos = new ParsePosition(0);
                position = 0;

                parser.setLenient(true);
            }

            @Override
            public boolean hasNext() {
                return position < dateTimeFormatPropertyList.size();
            }

            @Override
            public DateTimeFormatProperty next() {
                DateTimeFormatProperty dateTimeFormatProperty = dateTimeFormatPropertyList.get(position);

                if (hasNext()) {
                    assertIsMatch(dateTimeFormatPropertyList.get(position));
                    ++position;
                } else {
                    throw new DateTimeFormatFinderParseException(dateTime);
                }
                return dateTimeFormatProperty;
            }

            private void assertIsMatch(DateTimeFormatProperty dateTimeFormatProperty) {
                String pattern = dateTimeFormatProperty.pattern;
                String dateTimeCopy = dateTime;
                if (dateTimeFormatProperty.endsWithZZOutput()) {
                    pattern = dateTimeFormatProperty.removeZZOutputTail();
                    dateTimeCopy = dateTime.replaceAll("([-+][0-9][0-9]):([0-9][0-9])$", "$1$2");
                }

                parser.applyPattern(pattern);
                pos.setIndex(0);
                Date date = parser.parse(dateTimeCopy, pos);
                if (date != null && pos.getIndex() == dateTimeCopy.length()) {
                    dateTimeFormatProperty.isMatch = true;
                }
            }

            @Override
            public void remove() {
                throw new UnsupportedOperationException();
            }
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

        private static final String ZZ_OUTPUT = "ZZ";

        private final String pattern;
        private boolean isMatch = false;

        private DateTimeFormatProperty(String pattern) {
            this.pattern = pattern;
        }

        private boolean endsWithZZOutput() {
            return pattern.endsWith(ZZ_OUTPUT);
        }

        private String removeZZOutputTail() {
            return pattern.substring(0, pattern.length() - 1);
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
