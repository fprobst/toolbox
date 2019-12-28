package util;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;
import java.util.LinkedList;
import java.util.Random;

public class FiftyTwo {

	/** Distinct values */
	private final LinkedList<Integer> m_Existing = new LinkedList<Integer>();

	/** The Year that we are in */
	private final int m_Year;

	/** Should the output be in CVS (semicolon separated) or not */
	private final boolean m_AsCsv = true;

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
		System.out.println("First Friday: " + start);
		int sum = 0;

		LocalDate next = start;
		while (next.isBefore(LocalDate.ofYearDay(m_Year + 1, 1))) {

			// Amount for the current week
			int amount = nextValue();

			if (!m_AsCsv) {
				System.out.printf("|%s|%.2f|%n", next, Float.valueOf(amount));
			} else {
				System.out.printf("%s;%.2f%n", next, Float.valueOf(amount));
			}

			// Jump to next week
			next = next.plusDays(7);
			sum += amount;
		}

		System.out.printf("Sum: %.2f%n", Float.valueOf(sum));
	}

	/**
	 * Returns a random value from the list as amount for the week
	 * 
	 * @return Random int value between 1 and 52
	 */
	private int nextValue() {
		final Random r = new Random();
		Integer toReturn = m_Existing.get(r.nextInt(m_Existing.size()));
		m_Existing.remove(toReturn);
		return toReturn.intValue() + 1;
	}
}
