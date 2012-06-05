package swing;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.util.Random;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicInteger;

import javax.swing.JDialog;
import javax.swing.JProgressBar;
import javax.swing.SwingUtilities;

/**
 * Ersatz für den aktuellen Wait Dialog der eher schlecht als recht funktioniert
 */
public final class WaitDialog
{
    /** Executor führ die Runnables aus */
    private ExecutorService m_Executor = Executors.newSingleThreadExecutor();

    /** Die interne Instanz des Wait Dialogs */
    private static WaitDialog m_Instance = null;

    /** Counter wieviele Call- bzw. Runnables im Executor enthalten sind */
    private AtomicInteger m_Counter = new AtomicInteger(0);

    /** Der Dialog der ggf. während der Ausführung angezeigt wird */
    private InnerDialog m_Dialog = null;

    /**
     * Keine öffentlichen Instanzen erlaubt
     */
    private WaitDialog()
    {
        // Macht nichts
    }

    /**
     * Nur für den internen Gebrauch
     * 
     * @return
     */
    private static WaitDialog getInstance()
    {
        if(m_Instance == null)
        {
            m_Instance = new WaitDialog();
        }
        return m_Instance;
    }

    /**
     * Gib dem Wait Dialog etwas zu tun. Während der Ausführung des übergebenen Tasks wird der übergebene Text 
     * (key aus einem ResourceBundle) angezeigt.
     * 
     * @param message
     * @param task
     */
    public static void execute(final String message, final Runnable task)
    {
        getInstance().add(message, task);
    }

    /**
     * Wartet auf das Ergebnis des übergebenen Tasks. Während der Ausführung des übergebenen Tasks wird der übergebene Text 
     * (key aus einem ResourceBundle) angezeigt.
     * 
     * @param message
     * @param task
     * @return
     */
    public static <V> V waitFor(final String message, final Callable<V> task)
    {
        return getInstance().getResult(message, task);
    }

    /**
     * Fügt dem Executor einen neuen Job hinzu. Der Text (aus einem ResourceBundle?) wird während der Ausführung des
     * Jobs angezeigt
     * 
     * @param message
     * @param task
     */
    void add(final String message, final Runnable task)
    {
        m_Counter.addAndGet(1);
        m_Executor.submit(new InnerRunnable(message, task));
    }

    /**
     * Liefert das Ergebnis des übergebenen Callables zurück. Während der Ausführung wird der übergebene Text (aus
     * Gui/Message Bundle) angezeigt.
     * 
     * @param message
     * @param task
     * @return
     */
    <V> V getResult(final String message, final Callable<V> task)
    {
        m_Counter.addAndGet(1);
        Future<V> result = m_Executor.submit(new InnerCallable<V>(message, task));

        try
        {
            return result.get();
        }
        catch(InterruptedException e)
        {
            e.printStackTrace();
        }
        catch(ExecutionException e)
        {
            e.printStackTrace();
        }

        // ggf. Exceptions verpacken und weiter werfen!
        return null;
    }

    /**
     * Ein Runnable wurde beendet - prüfe den Counter ob noch weitere Jobs offen sind. Wenn nicht, wird der Dialog
     * ausgeblendet
     */
    void doFinishRunnable()
    {
        if(m_Dialog == null)
        {
            return;
        }

        if(m_Dialog.isVisible() && m_Counter.decrementAndGet() == 0)
        {
            final JDialog dialog = m_Dialog;
            SwingUtilities.invokeLater(new Runnable()
            {
                /**
                 * @see java.lang.Runnable#run()
                 */
                public void run()
                {
                    dialog.setVisible(false);
                }
            });
        }
    }

    /**
     * Startet die Ausführung eines Tasks. Falls der Dialog nicht angezeigt wird, wird er eingeblendet
     */
    void doStartRunnable(final String message)
    {
        if(m_Dialog == null)
        {
            // ggf. den Dialog erstellen
            m_Dialog = new InnerDialog();
        }

        final InnerDialog dialog = m_Dialog;
        SwingUtilities.invokeLater(new Runnable()
        {
            /**
             * @see java.lang.Runnable#run()
             */
            public void run()
            {
                dialog.setText(message);
                if(!m_Dialog.isVisible())
                {
                    dialog.setVisible(true);
                }
            }
        });
    }

    /**
     * Inneres Callable das im Vergleich zum Runnable einen Wert zurückliefern kann. Während der Ausführung wird der
     * angegebene Text angezeigt.
     */
    class InnerCallable<V> implements Callable<V>
    {
        /** Meldung für die Progressbar */
        private String m_Message;

        /** Task der ausgeführt werden soll */
        private Callable<V> m_Callable;

        /**
         * Konstruktor übergibt die notwendigen Felder Meldung und Task
         * 
         * @param message
         * @param task
         */
        InnerCallable(final String message, final Callable<V> task)
        {
            m_Message = message;
            m_Callable = task;
        }

        /**
         * @see java.util.concurrent.Callable#call()
         */
        public V call() throws Exception
        {
            try
            {
                doStartRunnable(m_Message);
                return m_Callable.call();
            }
            finally
            {
                doFinishRunnable();
            }
        }
    }

    /**
     * Spezielles Runnable das zusätzlich einen Text speichert der angezeigt werden kann
     */
    class InnerRunnable implements Runnable
    {
        /** Meldung für die Progressbar */
        private String m_Message;

        /** Task der ausgeführt werden soll */
        private Runnable m_Runnable;

        /**
         * Konstruktor übergibt die notwendigen Felder Meldung und Task
         * 
         * @param message
         * @param task
         */
        InnerRunnable(final String message, final Runnable task)
        {
            m_Message = message;
            m_Runnable = task;
        }

        /**
         * @see java.lang.Runnable#run()
         */
        public void run()
        {
            try
            {
                doStartRunnable(m_Message);
                m_Runnable.run();
            }
            finally
            {
                doFinishRunnable();
            }
        }
    }

    /**
     * Pseudo Wait Dialog
     */
    class InnerDialog extends JDialog
    {
        /** Die Progressbar zur Visualisierung */
        private JProgressBar m_ProgressBar;

        /**
         * Default Konstruktor initialisiert die Komponenten
         */
        InnerDialog()
        {
            initComponents();
            pack();
        }

        /**
         * Erzeugt eine Progressbar
         */
        private void initComponents()
        {
            getContentPane().setLayout(new BorderLayout());
            m_ProgressBar = new JProgressBar();
            m_ProgressBar.setIndeterminate(true);
            m_ProgressBar.setMinimum(0);
            m_ProgressBar.setMaximum(100);
            m_ProgressBar.setStringPainted(true);
            m_ProgressBar.setString("");
            m_ProgressBar.setPreferredSize(new Dimension(300, 20));
            getContentPane().add(m_ProgressBar, BorderLayout.CENTER);
            getContentPane().setSize(300, 20);
        }

        /**
         * Aktualisiert den Text der Progressbar
         * 
         * @param text
         */
        void setText(final String text)
        {
            m_ProgressBar.setString(text);
        }
    }

    public static void main(final String[] args)
    {
        // Zeige einen Wait Dialog an
        WaitDialog.execute("Task 1", new Runnable()
        {
            /**
             * @see java.lang.Runnable#run()
             */
            public void run()
            {
                try
                {
                    Thread.sleep(2000);
                }
                catch(InterruptedException e)
                {
                    e.printStackTrace();
                }
            }
        });

        // Neuer Wait Dialog der ein Ergebnis liefert und sich danach ausblendet
        Integer result = WaitDialog.waitFor("Berechne Zufallszahl", new Callable<Integer>()
        {
            /**
             * @see java.util.concurrent.Callable#call()
             */
            public Integer call() throws Exception
            {
                Random r = new Random();
                Thread.sleep(500);
                return Integer.valueOf(r.nextInt(9999));
            }
        });

        // Ausgabe des Ergebnisses
        System.out.println("Zufallszahl: " + result);

        // Neuer Wait Dialog wird eingeblendet
        WaitDialog.execute("Task 2", new Runnable()
        {
            /**
             * @see java.lang.Runnable#run()
             */
            public void run()
            {
                try
                {
                    Thread.sleep(2000);
                }
                catch(InterruptedException e)
                {
                    e.printStackTrace();
                    Thread.currentThread().interrupt();
                }
            }
        });

        // Fehlermeldung ausgeben
        WaitDialog.execute("Task 3", new Runnable()
        {
            /**
             * @see java.lang.Runnable#run()
             */
            public void run()
            {
                throw new IllegalStateException("Dumm gelaufen!");
            }
        });

        // Mehrere Threads erzeugen einen Wait Dialog
        for(int i = 0; i < 10; i++)
        {
            final int counter = i;
            Thread t = new Thread(new Runnable()
            {
                /**
                 * @see java.lang.Runnable#run()
                 */
                public void run()
                {
                    WaitDialog.execute("Wait " + counter, new Runnable()
                    {
                        /**
                         * @see java.lang.Runnable#run()
                         */
                        public void run()
                        {
                            try
                            {
                                Thread.sleep(500);
                            }
                            catch(InterruptedException e)
                            {
                                e.printStackTrace();
                            }
                        }
                    });
                }
            });
            t.start();
        }
    }
}
