package toolbox;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class LinePrinter {

	/** Logger */
	private static Logger log = LoggerFactory.getLogger(LinePrinter.class);

	/** Verzeichnis das untersucht wird */
	private final File m_RootRirectory;

	/** File Suffix (.java) */
	private final String m_FileSuffix;

	/**
	 * Konstruktor �bergibt das Start Verzeichnis und die Dateiendung
	 * 
	 * @param rootRirectory
	 * @param fileSuffix
	 */
	private LinePrinter(final String rootRirectory, final String fileSuffix) {
		m_FileSuffix = fileSuffix.toLowerCase();
		m_RootRirectory = new File(rootRirectory);
	}

	/**
	 * Aufruf welche Zeile der Dateien ausgegeben werden soll
	 * 
	 * @param line
	 */
	public void printLines(final int line) {
		for (File f : m_RootRirectory.listFiles()) {
			checkFile(f, line);
		}
	}

	/**
	 * Rekursive Methode die ein File pr�ft. Ist das File ein Verzeichnis wird
	 * die Methode auf alle Dateien im Verzeichnis aufgerufen. Andernfalls wird
	 * die Datei zeilenweise durchlaufen und die �bergebene Zeilenzahl (sofern
	 * vorhanden) ausgegeben.
	 * 
	 * @param file
	 * @param line
	 */
	private void checkFile(final File file, final int line) {
		if (file.isDirectory()) {
			for (File f : file.listFiles()) {
				checkFile(f, line);
			}
		} else {
			if (!file.getName().toLowerCase().endsWith(m_FileSuffix)) {
				return;
			}

			try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
				String lineStr;
				int lineCounter = 0;
				while ((lineStr = reader.readLine()) != null) {
					lineCounter++;
					if (lineCounter == line) {
						String lineStrTrimmed = lineStr.trim();
						log.info("{}: {}", file.getName(), lineStrTrimmed);
					}
				}
			} catch (IOException ioe) {
				ioe.printStackTrace();
			}
		}
	}

	/**
	 * @param args
	 */
	public static void main(final String[] args) {
		LinePrinter t = new LinePrinter("src/main/java", ".java");
		t.printLines(184);
	}
}
