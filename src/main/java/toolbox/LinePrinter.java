package toolbox;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

public final class LinePrinter
{
	/** Verzeichnis das untersucht wird */
	private final File m_RootRirectory;

	/** File Suffix (.java) */
	private final String m_FileSuffix;

	/**
	 * Konstruktor übergibt das Start Verzeichnis und die Dateiendung
	 * 
	 * @param rootRirectory
	 * @param fileSuffix
	 */
	private LinePrinter(final String rootRirectory, final String fileSuffix)
	{
		m_FileSuffix = fileSuffix.toLowerCase();
		m_RootRirectory = new File(rootRirectory);
	}

	/**
	 * Aufruf welche Zeile der Dateien ausgegeben werden soll
	 * 
	 * @param line
	 */
	public void printLines(final int line)
	{
		for (File f : m_RootRirectory.listFiles())
		{
			checkFile(f, line);
		}
	}

	/**
	 * Rekursive Methode die ein File prüft. Ist das File ein Verzeichnis wird
	 * die Methode auf alle Dateien im Verzeichnis aufgerufen. Andernfalls wird
	 * die Datei zeilenweise durchlaufen und die übergebene Zeilenzahl (sofern
	 * vorhanden) ausgegeben.
	 * 
	 * @param file
	 * @param line
	 */
	private void checkFile(final File file, final int line)
	{
		if (file.isDirectory())
		{
			for (File f : file.listFiles())
			{
				checkFile(f, line);
			}
		} else
		{
			if (!file.getName().toLowerCase().endsWith(m_FileSuffix))
			{
				return;
			}

			BufferedReader reader = null;
			FileReader fileReader = null;
			try
			{
				fileReader = new FileReader(file);
				reader = new BufferedReader(fileReader);
				String lineStr;
				int lineCounter = 0;
				while ((lineStr = reader.readLine()) != null)
				{
					lineCounter++;
					if (lineCounter == line)
					{
						System.out.println(file.getName() + ": "
								+ lineStr.trim());
					}
				}
			} catch (IOException ioe)
			{
				ioe.printStackTrace();
			} finally
			{
				if (fileReader != null)
				{
					try
					{
						fileReader.close();
					} catch (IOException e)
					{
						e.printStackTrace();
					}
				}
				if (reader != null)
				{
					try
					{
						reader.close();
					} catch (IOException e)
					{
						e.printStackTrace();
					}
				}
			}
		}
	}

	/**
	 * @param args
	 */
	public static void main(final String[] args)
	{
		if(args == null)
		{
			System.err.println("Command line: <path> <fileSuffix> <lineNr>");
		}
		
		String folder = null, suffix = null;
		int line = 0;
		
		for(int i=0; i < args.length; i++)
		{
			switch(i) 
			{
				case 0:
					folder = args[i];
					break;
				case 1:
					suffix = args[i];
					break;
				case 2:
					try
					{
						line = Integer.parseInt(args[i]);
						break;
					} 
					catch(java.parse.NumberFormatException e)
					{
						System.err.println("Illegal number: " + args[i]);
					}
				default:
					break;
			}
		}
		
		if(folder == null || suffix == null || line == 0)
		{
			System.err.println("Command line: <path> <fileSuffix> <lineNr>");
		}
		
		LinePrinter t = new LinePrinter(folder, suffix);
		t.printLines(line);
	}
}
