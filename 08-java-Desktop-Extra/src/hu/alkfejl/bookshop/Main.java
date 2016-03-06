package hu.alkfejl.bookshop;

import java.util.List;

import hu.alkfejl.bookshop.controller.BookShopController;
import hu.alkfejl.bookshop.model.bean.Book;
import hu.alkfejl.bookshop.model.bean.Customer;
import hu.alkfejl.bookshop.view.Labels;

/**
 * Az osztály az alkalmazás belépési pontja, az alkalmazás indításáért felel.
 */
public class Main {

    /**
     * A parancssorban fogadott felhasználónév.
     * <p>
     * Az egyszerûség végett itt tároljuk, lehetne szebben is, mert így a view
     * réteg hivatkozik a {@link Main}-re, a view-t nem lehet kiszedni az appból
     * és máshol felhasználni.
     * Megoldás: külön osztály a feldolgozott argumentumoknak, átadni a view-nak.
     * </p>
     */
    public static String username;

    /**
     * Az alkalmazás belépési pontja.
     *
     * @param args A parancssori argumentumok listája.
     */
    public static void main(String[] args) {
        processCommandLine(args);
        startApp();
    }

    /**
     * Feldolgozza a parancssori argumentumokat.
     *
     * @param args
     */
    private static void processCommandLine(String[] args) {
        // "buta" feldolgozás: ha van paraméter, akkor az elsõ a usernév
        if (args.length > 0) {
            username = args[0];
        }
    }

    /**
     * Indítja az alkalmazást.
     */
    private static void startApp() {
        BookShopController controller = new BookShopController();

        ensureSampleDataCreated(controller);

        controller.startDesktop();
    }

    /**
     * Biztosítja a példaadatok létrejöttét, amennyiben szükséges.
     *
     * @param controller Az alkalmazás controllere.
     */
    private static void ensureSampleDataCreated(BookShopController controller) {
        // Elkérjük a már eltárolt könyveket.
        List<Book> storedBooks = controller.getBooks();

        // Ellenõrizzük, hogy van-e eltárolt könyv.
        boolean isAnyBookStored = storedBooks.size() != 0;

        // Amennyiben nincs eltárolt könyv, úgy létrehozzuk a könyv
        // példaadatokat.
        if (!isAnyBookStored) {
            prepareBookSampleData(controller);
        }

        // Elkérjük a már eltárolt vásárlókat.
        List<Customer> storedCustomers = controller.getCustomers();

        // Ellenõrizzük, hogy van-e eltárolt vásárló.
        boolean isAnyCustomerStored = storedCustomers.size() != 0;

        // Amennyiben nincs eltárolt vásárló, úgy létrehozzuk a vásárló
        // példaadatokat.
        if (!isAnyCustomerStored) {
            prepareCustomerSampleData(controller);
        }
    }

    /**
     * Könyv példaadatokat vesz fel az alkalmazásba, amennyiben nincsenek még
     * eltárolt könyvek.
     * <p>
     * Fontos, a példa adatok felvétele is az üzleti szabályok alkalmazásáért
     * felelõs controlleren keresztül történik!
     * </p>
     *
     * @param controller Az alkalmazás controllere.
     */
    private static void prepareBookSampleData(BookShopController controller) {
        for (int i = 0; i < 10; i++) {
            Book book = new Book();

            // Ez itt nem UI címke, hanem adat, mintha a user írta volna be,
            // ezért nem tesszük Labels-be.
            book.setTitle("Cím " + i);
            book.setAuthor("Szerzõ" + i);
            book.setCategory(Labels.drama);
            book.setPiece(i);
            book.setPrice(i);
            book.setYear(i);

            controller.addBook(book);
        }
    }

    /**
     * Vásárló példaadatokat vesz fel az alkalmazásba, amennyiben nincsenek még
     * eltárolt vásárlók.
     * <p>
     * Fontos, a példa adatok felvétele is az üzleti szabályok alkalmazásáért
     * felelõs controlleren keresztül történik!
     * </p>
     *
     * @param controller Az alkalmazás controllere.
     */
    private static void prepareCustomerSampleData(BookShopController controller) {
        for (int i = 0; i < 10; i++) {
            Customer customer = new Customer();

            // Ez itt nem UI címke, hanem adat, mintha a user írta volna be,
            // ezért nem tesszük Labels-be.
            customer.setName("Név " + i);
            customer.setAge(i);
            customer.setFemale(true);
            customer.setGrantee(true);
            customer.setQualification(Labels.qualifications[0]);

            controller.addCustomer(customer);
        }
    }
}
