import java.text.MessageFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.SortedSet;
import java.util.TimeZone;
import java.util.TreeSet;

/**
 * Klasse für allerlei Zeitzonen Krimskrams
 */
public final class ListTimezones
{
    /**
     * Default Konstruktor
     */
    private ListTimezones()
    {}

    /**
     * @param args
     */
    public static void main(final String[] args)
    {
        System.out.println("Aktuelle Zeitzone:");
        TimeZone timeZone = TimeZone.getDefault();
        int offset = -1 * (timeZone.getRawOffset() / (1000 * 60 * 60));
        String offsetStr = offset > 0 ? "+" + offset : Long.toString(offset);
        System.out.println(MessageFormat.format("{0} (GMT{1})", timeZone.getDisplayName(true, TimeZone.SHORT),
                offsetStr));
        System.out.println(timeZone.toString());

        System.out.println("Aktuelle Uhrzeit:");
        System.out.println(new Date().toString());

        System.out.println("Verfügbare Zeitzonen:");

        SortedSet<String> set = new TreeSet<String>(Arrays.asList(TimeZone.getAvailableIDs()));
        StringBuffer buffer = new StringBuffer();
        for(String timezone : set)
        {
            buffer.append(timezone);
            buffer.append(", ");

            if(buffer.length() > 100)
            {
                System.out.println(buffer);
                buffer.delete(0, buffer.length());
            }
        }
    }
}
