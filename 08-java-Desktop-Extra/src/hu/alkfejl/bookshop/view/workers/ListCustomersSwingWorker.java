package hu.alkfejl.bookshop.view.workers;

import hu.alkfejl.bookshop.model.bean.Customer;
import hu.alkfejl.bookshop.view.BookShopGUI;
import hu.alkfejl.bookshop.view.tablemodels.CustomerTableModel;

import java.util.List;
import java.util.concurrent.ExecutionException;

import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.SwingWorker;

/**
 * Az oszt�ly egy kiterjesztett {@link SwingWorker}-t val�s�t meg mely bet�lti
 * �s megjelen�ti a v�s�rl�kat.
 */
public class ListCustomersSwingWorker extends SwingWorker<List<Customer>, Void> {

    private BookShopGUI bookShopGUI;

    /**
     * P�ld�nyt k�sz�t a {@link ListCustomersSwingWorker} oszt�lyb�l.
     *
     * @param bookShopGUI Az alkalmaz�s UI-a.
     */
    public ListCustomersSwingWorker(BookShopGUI bookShopGUI){
        this.bookShopGUI = bookShopGUI;
    }

    /**
     * Lek�rdezi a v�s�rl�kat.
     * Megjegyz�s: Bizonyos esetekben (sok t�rolt v�s�rl�, t�voli adatb�zis szerver,
     * etc...) az adatok lek�rdez�se id�ig�nyes lehet, �gy a lek�rdez�st �rdemes
     * szepar�lt, �gynevezett worker thread-en v�grehajtani.
     *
     * NAGYON FONTOS: Ez a met�dus az �ltala v�grehajtand� feladatokat egy
     * worker thread-en v�gzi, melyet az �s {@link SwingWorker} oszt�ly
     * implement�ci�ja elrejt el�l�nk.
     */
    @Override
    protected List<Customer> doInBackground() throws Exception {
        System.out.println("SwingWorker doInBackground()...");

        // A worker sz�lon v�grehajtuk az adatok lek�rdez�s�t
        List<Customer> queriedCustomers = bookShopGUI.getController().getCustomers();

        // Visszat�r�nk a lek�rdezett v�s�rl� list�val
        return queriedCustomers;
    }

    /**
     * Megjelenitj�k a lek�rdezett {@link Customer}-eket t�bl�zatos form�ban a UI-on.
     *
     * NAGYON FONTOS: Az Event Dispatch Thread-en fut le amikor a doInBackground()
     * met�dus v�grehajt�sa befejez�d�tt.
     */
    @Override
    protected void done() {
        System.out.println("SwingWorker done()...");

        // A get() met�dussal elk�rj�k a lek�rdezett v�s�rl�kat
        // NAGYON FONTOS: A get() met�dus h�v�s BLOKKOL� h�v�s, itt ez nem
        // jelent probl�m�t mivel a done() met�dus h�v�sa azut�n t�rt�nik,
        // hogy a doInBackground() met�dus v�get �rt
        List<Customer> queriedCustomers = null;

        try {
            queriedCustomers = get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }

        // Csin�lunk egy t�bl�zatot, a CustomerTableModel alapjan, ami
        // megkapja a controltol a customereket
        JTable table = new JTable(new CustomerTableModel(queriedCustomers));

        // A t�blazatot r�rakjuk egy ScrollPane-re, �gy ha az t�l nagy lenne
        // az ablak m�ret�hez k�pest, akkor is g�rgetheto lesz
        JScrollPane container = new JScrollPane(table);

        // Ezt a ScrollPane-t �ll�tjuk be a f�ablak tartalm�nak
        bookShopGUI.setActualContent(container);
    }
}
