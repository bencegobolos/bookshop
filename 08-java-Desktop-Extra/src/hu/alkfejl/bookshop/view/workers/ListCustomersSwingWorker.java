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
 * Az osztály egy kiterjesztett {@link SwingWorker}-t valósít meg mely betölti
 * és megjeleníti a vásárlókat.
 */
public class ListCustomersSwingWorker extends SwingWorker<List<Customer>, Void> {

    private BookShopGUI bookShopGUI;

    /**
     * Példányt készít a {@link ListCustomersSwingWorker} osztályból.
     *
     * @param bookShopGUI Az alkalmazás UI-a.
     */
    public ListCustomersSwingWorker(BookShopGUI bookShopGUI){
        this.bookShopGUI = bookShopGUI;
    }

    /**
     * Lekérdezi a vásárlókat.
     * Megjegyzés: Bizonyos esetekben (sok tárolt vásárló, távoli adatbázis szerver,
     * etc...) az adatok lekérdezése idõigényes lehet, így a lekérdezést érdemes
     * szeparált, úgynevezett worker thread-en végrehajtani.
     *
     * NAGYON FONTOS: Ez a metódus az általa végrehajtandó feladatokat egy
     * worker thread-en végzi, melyet az õs {@link SwingWorker} osztály
     * implementációja elrejt elõlünk.
     */
    @Override
    protected List<Customer> doInBackground() throws Exception {
        System.out.println("SwingWorker doInBackground()...");

        // A worker szálon végrehajtuk az adatok lekérdezését
        List<Customer> queriedCustomers = bookShopGUI.getController().getCustomers();

        // Visszatérünk a lekérdezett vásárló listával
        return queriedCustomers;
    }

    /**
     * Megjelenitjük a lekérdezett {@link Customer}-eket táblázatos formában a UI-on.
     *
     * NAGYON FONTOS: Az Event Dispatch Thread-en fut le amikor a doInBackground()
     * metódus végrehajtása befejezõdött.
     */
    @Override
    protected void done() {
        System.out.println("SwingWorker done()...");

        // A get() metódussal elkérjük a lekérdezett vásárlókat
        // NAGYON FONTOS: A get() metódus hívás BLOKKOLÓ hívás, itt ez nem
        // jelent problémát mivel a done() metódus hívása azután történik,
        // hogy a doInBackground() metódus véget ért
        List<Customer> queriedCustomers = null;

        try {
            queriedCustomers = get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }

        // Csinálunk egy táblázatot, a CustomerTableModel alapjan, ami
        // megkapja a controltol a customereket
        JTable table = new JTable(new CustomerTableModel(queriedCustomers));

        // A táblazatot rárakjuk egy ScrollPane-re, így ha az túl nagy lenne
        // az ablak méretéhez képest, akkor is görgetheto lesz
        JScrollPane container = new JScrollPane(table);

        // Ezt a ScrollPane-t állítjuk be a fõablak tartalmának
        bookShopGUI.setActualContent(container);
    }
}
