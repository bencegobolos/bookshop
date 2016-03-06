package hu.alkfejl.bookshop.view.workers;

import hu.alkfejl.bookshop.model.bean.Book;
import hu.alkfejl.bookshop.view.BookShopGUI;
import hu.alkfejl.bookshop.view.tablemodels.BookTableModel;

import java.util.List;

import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.SwingUtilities;

public class ListBooksThreadWorker implements Runnable {

    private BookShopGUI bookShopGUI;
    private List<Book> queriedBooks;

    /**
     * P�ld�nyt k�sz�t a {@link ListBooksThreadWorker} oszt�lyb�l.
     *
     * @param bookShopGUI Az alkalmaz�s UI-a.
     */
    public ListBooksThreadWorker(BookShopGUI bookShopGUI) {
        this.bookShopGUI = bookShopGUI;
    }

    /**
     * Lek�rdezi a k�nyveket
     * <p>
     * Megjegyz�s: Bizonyos esetekben (sok t�rolt k�nyv, t�voli adatb�zis szerver,
     * etc...) az adatok lek�rdez�se id�ig�nyes lehet, �gy a lek�rdez�st �rdemes
     * szepar�lt, �gynevezett worker thread-en v�grehajtani.
     * </p>
     *
     * NAGYON FONTOS: Ez a met�dus az �ltala v�grehajtand� feladatokat egy
     * worker thread-en v�gzi. A UI friss�t�st a SwingUtilities.invokeLater()
     * met�dus v�gzi, mivel az csak az Event Dispatch Thread-en tehet� meg.
     */
    @Override
    public void run() {
        System.out.println("ListBooksThreadWorker run()...");

        // A worker sz�lon v�grehajtuk az adatok lek�rdez�s�t
        queriedBooks = bookShopGUI.getController().getBooks();

        // Az Event Dispatch Thread-en aszinkron v�grehajtjuk a UI m�dos�t�st
        SwingUtilities.invokeLater(new Runnable() {
            /**
             * Friss�tj�k a UI-t.
             *
             * NAGYON FONTOS: A UI friss�t�s az Event Dispatch Thread-en
             * hajt�dik v�gre.
             */
            @Override
            public void run() {
                System.out.println("ListBooksThreadWorker SwingUtilities.invokeLater()...");

                // Csin�lunk egy t�bl�zatot, a BookTableModel alapjan,
                // ami megkapja a k�nyveket
                JTable table = new JTable(new BookTableModel(queriedBooks));

                // A t�blazatot r�rakjuk egy ScrollPane-re, �gy ha az t�l nagy
                // lenne az ablak m�ret�hez k�pest, akkor is g�rgethet� lesz
                JScrollPane container = new JScrollPane(table);

                // Ezt a ScrollPane-t �ll�tjuk be a f�ablak tartalm�nak
                bookShopGUI.setActualContent(container);
            }
        });
    }
}
