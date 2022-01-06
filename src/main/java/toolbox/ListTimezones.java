package toolbox;

import java.util.Arrays;
import java.util.Date;
import java.util.SortedSet;
import java.util.TimeZone;
import java.util.TreeSet;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Klasse für allerlei Zeitzonen Krimskrams
 */
public final class ListTimezones {

	/** Logger */
	private static Logger log = LoggerFactory.getLogger(ListTimezones.class);

	/**
	 * Default Konstruktor
	 */
	private ListTimezones() {
	}

	/**
	 * @param args
	 */
	public static void main(final String[] args) {
		log.info("Aktuelle Zeitzone:");
		TimeZone timeZone = TimeZone.getDefault();
		int offset = -1 * (timeZone.getRawOffset() / (1000 * 60 * 60));
		String offsetStr = offset > 0 ? "+" + offset : Long.toString(offset);
		log.info("{} (GMT{})",
				timeZone.getDisplayName(true, TimeZone.SHORT), offsetStr);
		log.info("{}", timeZone);

		log.info("Aktuelle Uhrzeit:");
		log.info("{}", new Date());

		log.info("Verfügbare Zeitzonen:");

		SortedSet<String> set = new TreeSet<>(Arrays.asList(TimeZone
				.getAvailableIDs()));
		StringBuilder buffer = new StringBuilder();
		for (String timezone : set) {
			buffer.append(timezone);
			buffer.append(", ");

			if (buffer.length() > 100) {
				log.info("{}", buffer);
				buffer.delete(0, buffer.length());
			}
		}
	}
}
