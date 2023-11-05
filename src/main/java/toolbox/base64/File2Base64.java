package toolbox.base64;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class File2Base64 {

	/** Logger */
	private static Logger log = LoggerFactory.getLogger(File2Base64.class);

	/** Name der Datei die in Base64 codiert werden soll */
	private String m_FileName;

	/**
	 * Konstruktor der den Namen der Datei �bergeben bekommt
	 * 
	 * @param fileName
	 */
	public File2Base64(final String fileName) {
		m_FileName = fileName;
	}

	/**
	 * @param args
	 */
	public static void main(final String[] args) {
		if (args.length == 0) {
			log.warn("Mindestens ein Argument notwendig: Dateiname.");
			return;
		}

		File2Base64 converter = new File2Base64(args[0]);
		if (!converter.fileExists()) {
			log.warn("Die Datei {} wurde nicht gefunden!", args[0]);
			return;
		}

		try {
			converter.convert();
		} catch (IOException e) {
			log.error("Fehler beim Lesen!", e);
			e.printStackTrace();
		}
	}

	/**
	 * Liest die Datei ein und gibt sie in Base64 aus
	 * 
	 * @throws IOException
	 */
	public void convert() throws IOException {
		File toRead = new File(m_FileName);

		String base64 = "";

		ByteArrayOutputStream output = null;
		try (InputStream input = new FileInputStream(toRead)) {
			output = new ByteArrayOutputStream();
			IOUtils.copy(input, output);
		} finally {
			IOUtils.closeQuietly(output);
		}

		base64 = new String(Base64.encodeBase64(output.toByteArray(), false), StandardCharsets.UTF_8);

		log.info(base64);
	}

	/**
	 * Pr�ft ob die Datei existiert
	 * 
	 * @return
	 */
	public boolean fileExists() {
		File f = new File(m_FileName);
		return f.exists();
	}
}
