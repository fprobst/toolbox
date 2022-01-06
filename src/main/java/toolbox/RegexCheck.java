package toolbox;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class RegexCheck {

	/** Logger */
	private static Logger log = LoggerFactory.getLogger(RegexCheck.class);

	/**
	 * Privater Default Konstruktor
	 */
	private RegexCheck() {
	}

	/**
	 * @param args
	 */
	public static void main(final String[] args) {
		String maxLengthNumber = "[0-9]{0,10}";

		Pattern p = Pattern.compile(maxLengthNumber);
		Matcher m = p.matcher("00000000001");

		log.info("matches: {}", Boolean.valueOf(m.matches()));
	}
}
