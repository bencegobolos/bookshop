package hu.alkfejl.bookshop.controller;

import hu.alkfejl.bookshop.model.BookShopDAO;
import hu.alkfejl.bookshop.model.BookShopDAODBImpl;
import hu.alkfejl.bookshop.model.BookShopDAOMemImpl;
import hu.alkfejl.bookshop.model.bean.Book;
import hu.alkfejl.bookshop.model.bean.Cd;
import hu.alkfejl.bookshop.model.bean.Customer;
import hu.alkfejl.bookshop.model.bean.Purchase;
import hu.alkfejl.bookshop.view.BookShopGUI;

import java.util.List;

/**
 * Ez az osztály vezérli az egész programot, valamint a view és model csomagokat
 * köti össze. Itt található az üzleti logika (business logic) is.
 */
public class BookShopController {

    // Data Access Object - az adat elérését szolgáló objektum
    // FONTOS!!! A BookShopDAO az adatelérési réteg interfésze (absztraktciója)
    // a réteget mindig az interfészen keresztül érjük el.
    // A réteg implementációját egyszer használjuk, példányosításkor,
    // visszacastolni TILOS!!!
    private BookShopDAO dao = new BookShopDAODBImpl();

    /**
     * Elindítja az alkalmazás desktopra specializált user interface-ét.
     */
    public void startDesktop() {
        BookShopGUI vc = new BookShopGUI(this);

        // GUI felület elindítása
        vc.startGUI();
    }

    public boolean addCustomer(Customer c) {
        // Controller, business logic-ra (üzleti logika, szabályok) példa
        // Szabály: valaki akkor hallgató ha 14-nél fiatalabb, valaki akkor
        // nyugdíjas ha 62-nel idõsebb
        if (c.getAge() < 14) {
            c.setStudent(true);
        } else if(c.getAge() > 62) {
            c.setRented(true);
        }

        return dao.addCustomer(c);
    }

    public List<Customer> getCustomers() {
        // A customer listázásnál nincs üzleti szabály, ezért csak visszaadjuk a
        // model-tõl kapott listát.
        return dao.getCustomers();
    }

    public boolean addBook(Book book) {
        if (book.getYear() < 1900 ) {
            book.setAncient(true);
        }

        return dao.addBook(book);
    }

    public List<Book> getBooks(){
        return dao.getBooks();
    }

    public boolean addPurchase(Purchase p){
        return dao.addPurchase(p);
    }

    public List<Purchase> getPurchases(){
        return dao.getPurchases();
    }
    
    public boolean addCd(Cd cd) {
        if (cd.getYear() < 2016 && cd.getYear() > 1979) {
            cd.setHit(true);
        }

        return dao.addCd(cd);
    }
    
    public List<Cd> getCds(){
    	return dao.getCds();
    }

}
