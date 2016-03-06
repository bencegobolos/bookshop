package hu.alkfejl.bookshop.model;

import hu.alkfejl.bookshop.model.bean.Customer;

import java.util.List;
import java.util.Random;

public class BookShopDAOTests {

    /**
     * A unit tesztek belépési pontja. Ezt a nagy könyv szerint nem így kell
     * csinálni, hanem felhasználni egy unit test framework-ot, de az
     * egyszerûség végett most így csináljuk.
     *
     * @param args A parancssori argumentumok.
     */
    public static void main(String[] args) {
        TestAddCustomerWithSameName();
        TestAddAndListCustomers();
    }

    public static void TestAddCustomerWithSameName() {
        BookShopDAO dao = new BookShopDAODBImpl();

        // az elsõ vásárló hozzáadása, elvárjuk, hogy sikeres legyen
        Customer customer = new Customer();

        customer.setName("same");

        boolean succeeded = dao.addCustomer(customer);

        assertBoolean(succeeded, true, "Nem sikerült hozzáadni az elsõ vásárlót!");

        // azanos nevû vásárló hozzáadása, elvárjuk, hogy !sikertelen! legyen
        Customer existingCustomer = new Customer();

        existingCustomer.setName("same");

        succeeded = dao.addCustomer(existingCustomer);

        assertBoolean(succeeded, false, "Sikerült hozzáadni azonos nevû vásárlót!");
    }

    public static void TestAddAndListCustomers() {
        BookShopDAO dao = new BookShopDAODBImpl();

        // Teszteljük 10-szer az addCustomer-t és utána a getCustomer-t
        // A neve mindig legyen TestCustomer + i
        // A kora legyen véletlen, 0 és 100 közötti
        for (int i = 0; i < 10; i++) {
            Customer customer = new Customer();

            customer.setName("TestCustomer_" + i);
            Random r = new Random();
            int randInt = r.nextInt(100);
            customer.setAge(randInt);

            assertBoolean(dao.addCustomer(customer), true, "Nem sikerült ügyfelet hozzáadni!");
        }

        // A végén kérdezzük le az adatbázist, hogy milyen Customerek vannak
        // (valamint ellenõrizzük SQLite shellben is)
        List<Customer> customers = dao.getCustomers();

        // lehet más is vett fel customer-eket, ezért nem teljesen korrekt ez a teszt
        assertBoolean(customers.size() >= 10, true, "Nem sikerült lekérdezni a felvett ügyfeleket!");

        System.out.println(customers.toString());
    }

    private static void assertBoolean(boolean actual, boolean expected, String message) {
        if (actual != expected) {
            System.out.println(String.format("TESZT HIBA: %1$s", message));
        }
    }
}
