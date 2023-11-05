package toolbox;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;
import java.util.LinkedList;
import java.util.Random;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FiftyTwo {

	/** Logger */
	private static Logger log = LoggerFactory.getLogger(FiftyTwo.class);

	/** Distinct values */
	private final LinkedList<Integer> m_Existing = new LinkedList<>();

	/** The Year that we are in */
	private final int m_Year;

	/** Generate random values */
	private final Random random = new Random();

	/** Should the output be in CVS (semicolon separated) or not */
	private static final boolean AS_CSV = true;

	/**
	 * Constructor
	 * 
	 * @param year Year that Fridays are printed out
	 */
	public FiftyTwo(int year) {
		m_Year = year;
		for (int i = 0; i < 52; i++) {
			m_Existing.add(Integer.valueOf(i));
		}
	}

	/**
	 * Main-Method
	 * 
	 * @param args Optional as first argument the year
	 */
	public static void main(String[] args) {

		final int year;
		if (args.length == 0) {
			year = LocalDate.now().getYear();
		} else {
			year = Integer.parseInt(args[0]);
		}

		FiftyTwo ft = new FiftyTwo(year);
		ft.start();
	}

	/**
	 * Start the calculation
	 */
	private void start() {

		// Find the first Friday in the given year
		LocalDate start = (LocalDate) TemporalAdjusters.next(DayOfWeek.FRIDAY)
				.adjustInto(LocalDate.ofYearDay(m_Year, 1));
		log.info("First Friday: {}", start);
		int sum = 0;

		StringBuilder builder = new StringBuilder();

		LocalDate next = start;
		while (next.isBefore(LocalDate.ofYearDay(m_Year + 1, 1))) {

			// Amount for the current week
			int amount = nextValue();

			final String logString;
			if (!AS_CSV) {
				logString = String.format("|%s|%.2f|%n", next, Float.valueOf(amount));
			} else {
				logString = String.format("%s;%.2f%n", next, Float.valueOf(amount));
			}
			builder.append(logString);

			// Jump to next week
			next = next.plusDays(7);
			sum += amount;
		}

		log.info("Values:\n{}", builder);
		log.info("Sum: {}", Float.valueOf(sum));
	}

	/**
	 * Returns a random value from the list as amount for the week
	 * 
	 * @return Random int value between 1 and 52
	 */
	private int nextValue() {
		Integer toReturn = m_Existing.get(random.nextInt(m_Existing.size()));
		m_Existing.remove(toReturn);
		return toReturn.intValue() + 1;
	}
}
