import org.junit.Assert;
import org.junit.Test;

public class DateTimeFormatFinderTest {

    private static final String DATE_TIME_STRING = "Mon Apr 29 16:43:49 EST 2013";
    private static final String UNEXPECTED_DATE_TIME_STRING = "22/11/2012 22:10:00:00 UTC";
    private static final String NULL_DATE_TIME_STRING = null;
    private static final String EMPTY_DATE_TIME_STRING = "";
    private static final String EXPECTED_DATE_FORMAT = "EEE MMM d HH:mm:ss z yyyy";

    @Test
    public void assertExpectedFormat() {
        String actualDateFormat = new DateTimeFormatFinder(DATE_TIME_STRING).getDateTimeFormat();

        Assert.assertEquals(EXPECTED_DATE_FORMAT, actualDateFormat);
    }

    @Test(expected = DateTimeFormatFinder.DateTimeFormatFinderParseException.class)
    public void assertExceptionWhenUnexpectedDateTimeFormat() {
        new DateTimeFormatFinder(UNEXPECTED_DATE_TIME_STRING).getDateTimeFormat();
    }

    @Test(expected = DateTimeFormatFinder.DateTimeFormatFinderIllegalArgumentException.class)
    public void assertExceptionWhenNullDateTimeFormatProvided() {
        new DateTimeFormatFinder(NULL_DATE_TIME_STRING).getDateTimeFormat();
    }

    @Test(expected = DateTimeFormatFinder.DateTimeFormatFinderIllegalArgumentException.class)
    public void assertExceptionWhenEmptyDateTimeFormatProvided() {
        new DateTimeFormatFinder(EMPTY_DATE_TIME_STRING).getDateTimeFormat();
    }

    @Test
    public void assertExceptionMessageForEmptyDateTime() {
        try {
            new DateTimeFormatFinder(EMPTY_DATE_TIME_STRING).getDateTimeFormat();
        } catch(DateTimeFormatFinder.DateTimeFormatFinderIllegalArgumentException e) {
            Assert.assertEquals(DateTimeFormatFinder.DateTimeFormatFinderIllegalArgumentException.EMPTY_DATE_TIME_EXCEPTION, e.getMessage());
            return;
        }
        Assert.fail("Expected exception type [DateTimeFormatFinderIllegalArgumentException]");
    }

    @Test
    public void assertExceptionMessageForNullDateTime() {
        try {
            new DateTimeFormatFinder(NULL_DATE_TIME_STRING).getDateTimeFormat();
        } catch(DateTimeFormatFinder.DateTimeFormatFinderIllegalArgumentException e) {
            Assert.assertEquals(DateTimeFormatFinder.DateTimeFormatFinderIllegalArgumentException.NULL_DATE_TIME_EXCEPTION, e.getMessage());
            return;
        }
        Assert.fail("Expected exception type [DateTimeFormatFinderIllegalArgumentException]");
    }
}
