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
 * A {@link BookShopMenuBar} oszt�ly reprezent�lja a men�t.
 * Az�rt teheti ezt meg, mert a {@link JMenuBar} oszt�lyb�l sz�rmazik.
 * �gy a men�re a this-szel hivatkozhatunk.
 */
public class BookShopMenuBar extends JMenuBar implements ActionListener {

    // A JMenuBar implement�lja a Serializable interf�szt, emiatt kell a serialVersionUID
    // Ez m�shol is el�fordulhat, felold�sa: az oszt�ly mellett bal oldalt s�rga
    // felki�lt�jel Eclipse-ben, r�klikkelve 'Add generated serial version id'.
    // Kipr�b�lhatjuk, ha ezt a sort t�r�lj�k.
    private static final long serialVersionUID = 2973555574160940115L;

    private BookShopGUI bookShopGUI;

    public BookShopMenuBar(BookShopGUI bookShopGUI) {
        super();
        this.bookShopGUI = bookShopGUI;

        // H�rom men�pontot gy�rtunk �ltal�nosan, a createMenuPoint met�dussal
        createMenuPoint(Labels.customer, Labels.add_customer, Labels.list_customers);
        createMenuPoint(Labels.book, Labels.buy_book, Labels.list_books);
        createMenuPoint(Labels.sell, Labels.sell_book, Labels.list_sold_books);
    }

    private void createMenuPoint(String name, String... subnames) {
        // L�trehozunk egy menupontot az els� param�ter alapj�n
        JMenu menu = new JMenu(name);

        // A menupontot hozz�adjuk a BookShopMenuBar-hoz
        this.add(menu);

        // Az egyes menu itemeket a marad�k param�ter �rt�keivel hozzuk l�tre
        for (String subname : subnames) {
            JMenuItem menuItem = new JMenuItem(subname);

            menu.add(menuItem);

            // Minden egyes menu itemet figyel�nk
            // A menu itemek eset�n a megfigyel�st az ActionListener interf�sz
            // biztos�tja, ez�rt a menubar implement�lja ezt az interf�szt �s
            // fel�l�rja az actionPerformed met�dust
            menuItem.addActionListener(this);
        }
    }

    /*
     * Az interf�szekhez tartoz� met�dusokat c�lszer� gener�lni, azut�n, hogy
     * megadtuk az implements kulcs� ut�n az interf�szt,
     * Jobb klikk + Source + Override/Implement 
     */
    @Override
    public void actionPerformed(ActionEvent e) {
        String actionCommand = e.getActionCommand();

        System.out.println("A k�vetkez� men�t megh�vta a felhaszn�l�:" + actionCommand);

        if (actionCommand.equals(Labels.add_customer)) {
            // Ha az �j �gyf�l felv�tel�t v�lasztott�k, akkor egy
            // AddCustomerDialog-ot ind�tunk
            new AddCustomerDialog(bookShopGUI, true);
        } else if (actionCommand.equals(Labels.list_customers)) {
            // K�szit�nk egy tempor�lis p�ld�nyt a swing worker-b�l.
            ListCustomersSwingWorker listCustomersSwingWorker = new ListCustomersSwingWorker(bookShopGUI);
            // Elinditjuk a swing workert. A v�s�rl�k lek�rdez�se a worker �ltal megkezd�dik egy
            // szepar�lt worker thread-en.
            listCustomersSwingWorker.execute();
        } else if(actionCommand.equals(Labels.buy_book)) {
            new BuyBookDialog(bookShopGUI, true);
        } else if(actionCommand.equals(Labels.list_books)) {
            // K�szit�nk egy tempor�lis p�ld�nyt a thread worker-b�l.
            Thread workerThread = new Thread(new ListBooksThreadWorker(bookShopGUI), "ListBooksThreadWorker");
            // Elind�tjuk a thread workert. A k�nyvek lek�rdez�se a worker
            // �ltal megkezd�dik egy szepar�lt worker thread-en.
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
