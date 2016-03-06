package hu.alkfejl.bookshop.view;

import hu.alkfejl.bookshop.model.bean.Purchase;
import hu.alkfejl.bookshop.view.dialogs.AddCustomerDialog;
import hu.alkfejl.bookshop.view.dialogs.BuyBookDialog;
import hu.alkfejl.bookshop.view.dialogs.SellBookDialog;
import hu.alkfejl.bookshop.view.tablemodels.PurchaseTableModel;
import hu.alkfejl.bookshop.view.workers.ListBooksThreadWorker;
import hu.alkfejl.bookshop.view.workers.ListCustomersSwingWorker;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JScrollPane;
import javax.swing.JTable;

/**
 * A {@link BookShopMenuBar} osztály reprezentálja a menüt.
 * Azért teheti ezt meg, mert a {@link JMenuBar} osztályból származik.
 * Így a menüre a this-szel hivatkozhatunk.
 */
public class BookShopMenuBar extends JMenuBar implements ActionListener {

    // A JMenuBar implementálja a Serializable interfészt, emiatt kell a serialVersionUID
    // Ez máshol is elõfordulhat, feloldása: az osztály mellett bal oldalt sárga
    // felkiáltójel Eclipse-ben, ráklikkelve 'Add generated serial version id'.
    // Kipróbálhatjuk, ha ezt a sort töröljük.
    private static final long serialVersionUID = 2973555574160940115L;

    private BookShopGUI bookShopGUI;

    public BookShopMenuBar(BookShopGUI bookShopGUI) {
        super();
        this.bookShopGUI = bookShopGUI;

        // Három menüpontot gyártunk általánosan, a createMenuPoint metódussal
        createMenuPoint(Labels.customer, Labels.add_customer, Labels.list_customers);
        createMenuPoint(Labels.book, Labels.buy_book, Labels.list_books);
        createMenuPoint(Labels.sell, Labels.sell_book, Labels.list_sold_books);
    }

    private void createMenuPoint(String name, String... subnames) {
        // Létrehozunk egy menupontot az elsõ paraméter alapján
        JMenu menu = new JMenu(name);

        // A menupontot hozzáadjuk a BookShopMenuBar-hoz
        this.add(menu);

        // Az egyes menu itemeket a maradék paraméter értékeivel hozzuk létre
        for (String subname : subnames) {
            JMenuItem menuItem = new JMenuItem(subname);

            menu.add(menuItem);

            // Minden egyes menu itemet figyelünk
            // A menu itemek esetén a megfigyelést az ActionListener interfész
            // biztosítja, ezért a menubar implementálja ezt az interfészt és
            // felülírja az actionPerformed metódust
            menuItem.addActionListener(this);
        }
    }

    /*
     * Az interfészekhez tartozó metódusokat célszerû generálni, azután, hogy
     * megadtuk az implements kulcsó után az interfészt,
     * Jobb klikk + Source + Override/Implement 
     */
    @Override
    public void actionPerformed(ActionEvent e) {
        String actionCommand = e.getActionCommand();

        System.out.println("A következõ menüt meghívta a felhasználó:" + actionCommand);

        if (actionCommand.equals(Labels.add_customer)) {
            // Ha az új ügyfél felvételét választották, akkor egy
            // AddCustomerDialog-ot indítunk
            new AddCustomerDialog(bookShopGUI, true);
        } else if (actionCommand.equals(Labels.list_customers)) {
            // Készitünk egy temporális példányt a swing worker-bõl.
            ListCustomersSwingWorker listCustomersSwingWorker = new ListCustomersSwingWorker(bookShopGUI);
            // Elinditjuk a swing workert. A vásárlók lekérdezése a worker által megkezdõdik egy
            // szeparált worker thread-en.
            listCustomersSwingWorker.execute();
        } else if(actionCommand.equals(Labels.buy_book)) {
            new BuyBookDialog(bookShopGUI, true);
        } else if(actionCommand.equals(Labels.list_books)) {
            // Készitünk egy temporális példányt a thread worker-bõl.
            Thread workerThread = new Thread(new ListBooksThreadWorker(bookShopGUI), "ListBooksThreadWorker");
            // Elindítjuk a thread workert. A könyvek lekérdezése a worker
            // által megkezdõdik egy szeparált worker thread-en.
            workerThread.start();
        } else if (actionCommand.equals(Labels.sell_book)) {
            new SellBookDialog(bookShopGUI, true);
        } else if (actionCommand.equals(Labels.list_sold_books)) {
            List<Purchase> purchases = bookShopGUI.getController().getPurchases();
            JTable table = new JTable(new PurchaseTableModel(purchases));
            JScrollPane container = new JScrollPane(table);
            bookShopGUI.setActualContent(container);
        }
    }

}
