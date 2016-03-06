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
     * Példányt készít a {@link ListBooksThreadWorker} osztályból.
     *
     * @param bookShopGUI Az alkalmazás UI-a.
     */
    public ListBooksThreadWorker(BookShopGUI bookShopGUI) {
        this.bookShopGUI = bookShopGUI;
    }

    /**
     * Lekérdezi a könyveket
     * <p>
     * Megjegyzés: Bizonyos esetekben (sok tárolt könyv, távoli adatbázis szerver,
     * etc...) az adatok lekérdezése idõigényes lehet, így a lekérdezést érdemes
     * szeparált, úgynevezett worker thread-en végrehajtani.
     * </p>
     *
     * NAGYON FONTOS: Ez a metódus az általa végrehajtandó feladatokat egy
     * worker thread-en végzi. A UI frissítést a SwingUtilities.invokeLater()
     * metódus végzi, mivel az csak az Event Dispatch Thread-en tehetõ meg.
     */
    @Override
    public void run() {
        System.out.println("ListBooksThreadWorker run()...");

        // A worker szálon végrehajtuk az adatok lekérdezését
        queriedBooks = bookShopGUI.getController().getBooks();

        // Az Event Dispatch Thread-en aszinkron végrehajtjuk a UI módosítást
        SwingUtilities.invokeLater(new Runnable() {
            /**
             * Frissítjük a UI-t.
             *
             * NAGYON FONTOS: A UI frissítés az Event Dispatch Thread-en
             * hajtódik végre.
             */
            @Override
            public void run() {
                System.out.println("ListBooksThreadWorker SwingUtilities.invokeLater()...");

                // Csinálunk egy táblázatot, a BookTableModel alapjan,
                // ami megkapja a könyveket
                JTable table = new JTable(new BookTableModel(queriedBooks));

                // A táblazatot rárakjuk egy ScrollPane-re, így ha az túl nagy
                // lenne az ablak méretéhez képest, akkor is görgethetõ lesz
                JScrollPane container = new JScrollPane(table);

                // Ezt a ScrollPane-t állítjuk be a fõablak tartalmának
                bookShopGUI.setActualContent(container);
            }
        });
    }
}
