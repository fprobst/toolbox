package base64;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import org.apache.commons.io.IOUtils;

public class File2Base64
{
    /** Name der Datei die in Base64 codiert werden soll */
    private String m_FileName;

    /**
     * Konstruktor der den Namen der Datei übergeben bekommt
     * 
     * @param fileName
     */
    public File2Base64(final String fileName)
    {
        super();
        m_FileName = fileName;
    }

    /**
     * @param args
     */
    public static void main(final String[] args)
    {
        if(args.length == 0)
        {
            System.out.println("Mindestens ein Argument notwendig: Dateiname.");
            return;
        }

        File2Base64 converter = new File2Base64(args[0]);
        if(!converter.fileExists())
        {
            System.out.println("Die Datei " + args[0] + " wurde nicht gefunden!");
            return;
        }

        try
        {
            converter.convert();
        }
        catch(IOException e)
        {
            System.out.println("Fehler beim Lesen!");
            e.printStackTrace();
        }
    }

    /**
     * Liest die Datei ein und gibt sie in Base64 aus
     * 
     * @throws IOException
     */
    public void convert() throws IOException
    {
        File toRead = new File(m_FileName);

        String base64 = "";

        ByteArrayOutputStream output = null;
        InputStream input = null;
        try
        {
            input = new FileInputStream(toRead);
            output = new ByteArrayOutputStream();
            IOUtils.copy(input, output);

			// TODO: Base64 ablösen
            base64 = ""; //DEBase64.encodeBinaryAsString(output.toByteArray());
        }
        finally
        {
            IOUtils.closeQuietly(input);
            IOUtils.closeQuietly(output);
        }

        System.out.println(base64);
    }

    /**
     * Prüft ob die Datei existiert
     * 
     * @return
     */
    public boolean fileExists()
    {
        File f = new File(m_FileName);
        return f.exists();
    }
}
