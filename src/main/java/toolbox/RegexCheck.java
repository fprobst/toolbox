import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @version $Id: RegexCheck.java,v 1.1 2012/05/18 06:35:56 fp Exp $
 */
public final class RegexCheck
{
    /**
     * Privater Default Konstruktor
     */
    private RegexCheck()
    {}

    /**
     * @param args
     */
    public static void main(final String[] args)
    {
        String maxLengthNumber = "[0-9]{0,10}";

        Pattern p = Pattern.compile(maxLengthNumber);
        Matcher m = p.matcher("00000000001");

        System.out.println(m.matches());
    }
}
