package hu.alkfejl.bookshop.model;

import hu.alkfejl.bookshop.model.bean.Book;
import hu.alkfejl.bookshop.model.bean.Customer;
import hu.alkfejl.bookshop.model.bean.Purchase;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

/**
 * Ez az osztály az adatelérést szolgálja. Tényleges perzisztens
 * tárolót, adatbázist használ.
 */
public class BookShopDAODBImpl implements BookShopDAO {

    List<Customer> customers = new ArrayList<Customer>();
    List<Book> books = new ArrayList<Book>();
    List<Purchase> purchases = new ArrayList<Purchase>();

    // Adatbázis fájlt reprezentáló string, melyet a
    // BookShopDB környezeti változóból olvasunk ki (env.bat állítja be)
    private static final String DATABASE_FILE = System.getenv("BookShopDB");

    // SQL lekérdezés mely lekérdezi az összes vásárlót
    private static final String SQL_SELECT_ALL_CUSTOMERS = "SELECT * FROM Customer";

    // SQL lekérdezés mely lekérdezi az összes könyvet
    private static final String SQL_SELECT_ALL_BOOKS = "SELECT * FROM Book";

    // SQL lekérdezés mely lekérdezi az összes vásárlást
    private static final String SQL_SELECT_ALL_PURCHASES = "SELECT * FROM SoldBookInstances";

    // SQL lekérdezés mely lekérdez egy meghatározott id-jû vásárlót
    private static final String SQL_SELECT_ONE_CUSTOMER = "SELECT * FROM Customer WHERE id=?";

    // SQL lekérdezés mely lekérdez egy meghatározott id-jû könyvet
    private static final String SQL_SELECT_ONE_BOOK = "SELECT * FROM Book WHERE id=?";

    // SQL paraméterezhetõ INSERT utasítás vásárló felvételére
    // Az egyes paramétereket utólagosan állíthatjuk be (PreparedStatement)
    private static final String SQL_INSERT_CUSTOMER =
        "INSERT INTO Customer " +
        "(name, age, female, rented, student, grantee, qualification) " +
        "VALUES (?, ?, ?, ?, ?, ?, ?)";

    // SQL paraméterezhetõ INSERT utasítás könyv felvételére
    // Az egyes paramétereket utólagosan állíthatjuk be (PreparedStatement)
    private static final String SQL_INSERT_BOOK =
        "INSERT INTO Book " +
        "(author, title, year, category, price, pieces, ancient) " +
        "VALUES (?, ?, ?, ?, ?, ?, ?)";

    // SQL paraméterezhetõ INSERT utasítás vásárlás felvételére
    // Az egyes paramétereket utólagosan állíthatjuk be (PreparedStatement)
    private static final String SQL_INSERT_PURCHASE =
        "INSERT INTO SoldBookInstances" +
        "(id_book, id_customer, sellDate)" +
        "VALUES(?, ?, ?)";

    // A konstruktorban inicializáljuk az adatbázist
    public BookShopDAODBImpl() {
        try {
            // Betoltjuk az SQLite JDBC drivert, ennek segítségével érjük majd
            // el az SQLite adatbázist
            // kulso/java/sqlitejdbc-v054.jar - a classpath-ba is bekerült
            // build.xml - javac tasknál classpath attribútum (nézzük meg)
            // Valamint ezt megadtuk a disztribúció futattásánál is a
            // run.bat-ban! (nézzük meg)
            Class.forName("org.sqlite.JDBC");
        } catch (ClassNotFoundException e) {
            System.out.println("Failed to load SQLite JDBC driver.");
            e.printStackTrace();
        }
    }

    /**
     * Hozzáad egy {@link Customer}-t az adattárhoz.
     *
     * @param customer A tárolandó {@link Customer}.
     * @return Igaz, ha sikeresen tárolva, hamis, egyébként.
     */
    @Override
    public boolean addCustomer(Customer customer) {
        boolean rvSucceeded = false;

        // Adatbázis kapcsolatot reprezentáló objektum
        Connection conn = null;
        PreparedStatement pst = null;

        try {
            // Az adatbázis kapcsolatunkat a DriverManager segítségével hozzuk létre
            // Megadjuk hogy a JDBC milyen driveren keresztül milyen fájlt keressen
            conn = DriverManager.getConnection("jdbc:sqlite:" + DATABASE_FILE);

            // Új vásárló felvétele esetén egy PreparedStatement objektumot
            // kérünk a kapcsolat objektumtól
            // Ez egy paraméterezhetõ SQL utasitást vár, a paraméterek ?-ként
            // jelennek meg
            pst = conn.prepareStatement(SQL_INSERT_CUSTOMER);

            // Az egyes parametéreket sorban kell megadni, pozíció alapján, ami
            // 1-tõl indul
            // Célszerû egy indexet inkrementálni, mivel ha az egyik paraméter
            // kiesik, akkor nem kell az utána következõeket újra számozni...
            int index = 1;
            pst.setString(index++, customer.getName());
            pst.setInt(index++, customer.getAge());
            pst.setBoolean(index++, customer.isFemale());
            pst.setBoolean(index++, customer.isRented());
            pst.setBoolean(index++, customer.isStudent());
            pst.setBoolean(index++, customer.isGrantee());
            pst.setString(index++, customer.getQualification());

            // Az ExecuteUpdate paranccsal végrehajtjuk az utasítást
            // Az executeUpdate visszaadja, hogy hány sort érintett az SQL ha 
            // DML-t hajtunk végre (DDL esetén 0-t ad vissza)
            int rowsAffected = pst.executeUpdate();

            // csak akkor sikeres, ha valóban volt érintett sor
            if (rowsAffected == 1) {
                rvSucceeded = true;
            }
        } catch (SQLException e) {
            System.out.println("Failed to execute adding customer.");
            e.printStackTrace();
        } finally {
            // NAGYON FONTOS!
            // Minden adatbázis objektumot le kell zárni, mivel ha ezt nem
            // tesszük meg, akkor elõfordulhat, hogy nyitott kapcsolatok
            // maradnak az adatbázis felé. Az adatbázis pedig korlátozott
            // számban tart fenn kapcsolatokat, ezért egy idõ után akar ez be is
            // telhet!
            // Minden egyes objektumot külön try-catch ágban kell megpróbálni
            // bezárni!
            try {
                if (pst != null) {
                    pst.close();
                }
            } catch (SQLException e) {
                System.out.println("Failed to close statement when adding customer.");
                e.printStackTrace();
            }

            try {
                if (conn != null) {
                    conn.close();
                }
            } catch (SQLException e) {
                System.out.println("Failed to close connection when adding customer.");
                e.printStackTrace();
            }
        }

        return rvSucceeded;
    }

    /**
     * Visszaadja a tárolt {@link Customer} példányokat.
     *
     * @return A tárolt {@link Customer}-ek listája.
     */
    public List<Customer> getCustomers(){
        Connection conn = null;
        Statement st = null;

        // Töröljük a memóriából a vásárlókat (azért tartjuk bennt, mert
        // lehetnek késõbb olyan mûveletek, melyekhez nem kell frissíteni)
        customers.clear();

        try {
            // Az adatbázis kapcsolatunkat a DriverManager segítségével hozzuk létre
            // Megadjuk, hogy a JDBC milyen driveren keresztul milyen fájlt keressen
            conn = DriverManager.getConnection("jdbc:sqlite:" + DATABASE_FILE);

            // A kapcsolat (conn) objektumtól kérünk egy egyszerû (nem
            // paraméterezhetõ) utasítást
            st = conn.createStatement();

            // Az utasítás objektumon keresztül indítunk egy query-t
            // Az eredményeket egy ResultSet objektumban kapjuk vissza
            ResultSet rs = st.executeQuery(SQL_SELECT_ALL_CUSTOMERS);

            customers = getCustomersFromResultSet(rs);
        } catch (SQLException e) {
            System.out.println("Failed to execute listing customers.");
            e.printStackTrace();
        } finally {
            try {
                if (st != null) {
                    st.close();
                }
            } catch (SQLException e) {
                System.out.println("Failed to close statement when listing customers.");
                e.printStackTrace();
            }

            try {
                if (conn != null) {
                    conn.close();
                }
            } catch (SQLException e) {
                System.out.println("Failed to close connection when listing customers.");
                e.printStackTrace();
            }
        }

        return customers;
    }

    /**
     * Hozzáad egy {@link Book}-ot az adattárhoz.
     *
     * @param book A tárolandó {@link Book}.
     * @return Igaz, ha sikeresen tárolva, hamis, egyébként.
     */
    public boolean addBook(Book book) {
        boolean rvSucceeded = false;

        // Adatbázis kapcsolatot reprezentáló objektum
        Connection conn = null;
        PreparedStatement pst = null;

        try {
            // Az adatbázis kapcsolatunkat a DriverManager segítségével hozzuk létre
            // Megadjuk hogy a JDBC milyen driveren keresztül milyen fájlt keressen
            conn = DriverManager.getConnection("jdbc:sqlite:" + DATABASE_FILE);

            // Új könyv felvétele esetén egy PreparedStatement objektumot
            // kérünk a kapcsolat objektumtól
            // Ez egy paraméterezhetõ SQL utasitást vár, a paraméterek ?-ként
            // jelennek meg
            pst = conn.prepareStatement(SQL_INSERT_BOOK);

            // Az egyes parametéreket sorban kell megadni, pozíció alapján, ami
            // 1-tõl indul
            // Célszerû egy indexet inkrementálni, mivel ha az egyik paraméter
            // kiesik, akkor nem kell az utána következõeket újra számozni...
            int index = 1;
            pst.setString(index++, book.getAuthor());
            pst.setString(index++, book.getTitle());
            pst.setInt(index++, book.getYear());
            pst.setString(index++, book.getCategory());
            pst.setInt(index++, book.getPrice());
            pst.setInt(index++, book.getPiece());
            pst.setBoolean(index++, book.isAncient());

            // Az ExecuteUpdate paranccsal végrehajtjuk az utasítást
            // Az executeUpdate visszaadja, hogy hány sort érintett az SQL ha 
            // DML-t hajtunk végre (DDL esetén 0-t ad vissza)
            int rowsAffected = pst.executeUpdate();

            // csak akkor sikeres, ha valóban volt érintett sor
            if (rowsAffected == 1) {
                rvSucceeded = true;
            }
        } catch (SQLException e) {
            System.out.println("Failed to execute adding book.");
            e.printStackTrace();
        } finally {
            // NAGYON FONTOS!
            // Minden adatbázis objektumot le kell zárni, mivel ha ezt nem
            // tesszük meg, akkor elõfordulhat, hogy nyitott kapcsolatok
            // maradnak az adatbázis felé. Az adatbázis pedig korlátozott
            // számban tart fenn kapcsolatokat, ezért egy idõ után akar ez be is
            // telhet!
            // Minden egyes objektumot külön try-catch ágban kell megpróbálni
            // bezárni!
            try {
                if (pst != null) {
                    pst.close();
                }
            } catch (SQLException e) {
                System.out.println("Failed to close statement when adding book.");
                e.printStackTrace();
            }

            try {
                if (conn != null) {
                    conn.close();
                }
            } catch (SQLException e) {
                System.out.println("Failed to close connection when adding book.");
                e.printStackTrace();
            }
        }

        return rvSucceeded;
    }

    /**
     * Visszaadja a tárolt {@link Book} példányokat.
     *
     * @return A tárolt {@link Book}-ek listája.
     */
    public List<Book> getBooks() {
        Connection conn = null;
        Statement st = null;

        // Töröljük a memóriából a könyveket (azért tartjuk bennt, mert lehetnek
        // késõbb olyan mûveletek, melyekhez nem kell frissíteni)
        books.clear();

        try {
            // Az adatbázis kapcsolatunkat a DriverManager segítségével hozzuk
            // létre. Megadjuk, hogy a JDBC milyen driveren keresztül milyen
            // fájlt keressen
            conn = DriverManager.getConnection("jdbc:sqlite:" + DATABASE_FILE);

            // A kapcsolat (conn) objektumtól kérünk egy egyszerû
            // (nem paraméterezhetõ) utasítást
            st = conn.createStatement();

            // Az utasítás objektumon keresztül indítunk egy query-t
            // Az eredményeket egy ResultSet objektumban kapjuk vissza
            ResultSet rs = st.executeQuery(SQL_SELECT_ALL_BOOKS);

            // Kinyerjük a könyveket a ResultSet-bõl
            books = getBooksFromResultSet(rs);
        } catch (SQLException e) {
            System.out.println("Failed to execute listing books.");
            e.printStackTrace();
        } finally {
            try {
                if (st != null) {
                    st.close();
                }
            } catch (SQLException e) {
                System.out.println("Failed to close statement when listing books.");
                e.printStackTrace();
            }

            try {
                if (conn != null) {
                    conn.close();
                }
            } catch (SQLException e) {
                System.out.println("Failed to close connection when listing books.");
                e.printStackTrace();
            }
        }

        return books;
    }

    /**
     * Hozzáad egy {@link Purchase}-et az adattárhoz.
     *
     * @param purchase A tárolandó {@link Purchase}.
     * @return Igaz, ha sikeresen tárolva, hamis, egyébként.
     */
    @Override
    public boolean addPurchase(Purchase purchase) {
        boolean rvSucceeded = false;

        // Adatbázis kapcsolatot reprezentáló objektum
        Connection conn = null;
        PreparedStatement pst = null;

        try {
            // Az adatbázis kapcsolatunkat a DriverManager segítségével hozzuk létre
            // Megadjuk hogy a JDBC milyen driveren keresztül milyen fájlt keressen
            conn = DriverManager.getConnection("jdbc:sqlite:" + DATABASE_FILE);

            // Új vásárlás felvétele esetén egy PreparedStatement objektumot
            // kérünk a kapcsolat objektumtól
            // Ez egy paraméterezhetõ SQL utasitást vár, a paraméterek ?-ként
            // jelennek meg
            pst = conn.prepareStatement(SQL_INSERT_PURCHASE);

            // Az egyes parametéreket sorban kell megadni, pozíció alapján, ami
            // 1-tõl indul
            // Célszerû egy indexet inkrementálni, mivel ha az egyik paraméter
            // kiesik, akkor nem kell az utána következõeket újra számozni...
            int index = 1; 
            pst.setInt(index++, purchase.getBook().getId());
            pst.setInt(index++, purchase.getCustomer().getId());
            pst.setDate(index++, new java.sql.Date(new java.util.Date().getTime()));

            // Az ExecuteUpdate paranccsal végrehajtjuk az utasítást
            // Az executeUpdate visszaadja, hogy hány sort érintett az SQL ha 
            // DML-t hajtunk végre (DDL esetén 0-t ad vissza)
            int rowsAffected = pst.executeUpdate();

            // csak akkor sikeres, ha valóban volt érintett sor
            if (rowsAffected == 1) {
                rvSucceeded = true;
            }
        } catch (SQLException e) {
            System.out.println("Failed to execute adding purchase.");
            e.printStackTrace();
        } finally {
            // NAGYON FONTOS!
            // Minden adatbázis objektumot le kell zárni, mivel ha ezt nem
            // tesszük meg, akkor elõfordulhat, hogy nyitott kapcsolatok
            // maradnak az adatbázis felé. Az adatbázis pedig korlátozott
            // számban tart fenn kapcsolatokat, ezért egy idõ után akar ez be is
            // telhet!
            // Minden egyes objektumot külön try-catch ágban kell megpróbálni
            // bezárni!
            try {
                if (pst != null) {
                    pst.close();
                }
            } catch (SQLException e) {
                System.out.println("Failed to close statement when adding purchase.");
                e.printStackTrace();
            }

            try {
                if (conn != null) {
                    conn.close();
                }
            } catch (SQLException e) {
                System.out.println("Failed to close connection when adding purchase.");
                e.printStackTrace();
            }
        }

        return rvSucceeded;
    }

    /**
     * Visszaadja a tárolt {@link Purchase} példányokat.
     *
     * @return A tárolt {@link Purchase}-ek listája.
     */
    @Override
    public List<Purchase> getPurchases() {
        Connection conn = null;
        Statement st = null;

        PreparedStatement queryOneBookPst = null;
        PreparedStatement queryOneCustomerPst = null;

        // Töröljük a memóriából a vásárlásokat (azért tartjuk bennt, mert
        // lehetnek késõbb olyan mûveletek, melyekhez nem kell frissíteni)
        purchases.clear();

        try {
            // Az adatbázis kapcsolatunkat a DriverManager segítségével hozzuk létre
            // Megadjuk, hogy a JDBC milyen driveren keresztul milyen fájlt keressen
            conn = DriverManager.getConnection("jdbc:sqlite:" + DATABASE_FILE);

            // ResultSet bejárása közben szeretnénk további SELECT-eket végrehajtani
            // Auto Commit módban minden SQL utasítás külön tranzakció.
            // Az SQLite engine nem képes egymásbaágyazott tranzakciók kezelésére
            conn.setAutoCommit(false);

            // A kapcsolat (conn) objektumtól kérünk egy egyszerû (nem
            // paraméterezhetõ) utasítást
            st = conn.createStatement();

            // Az utasítás objektumon keresztül indítunk egy query-t
            // Az eredményeket egy ResultSet objektumban kapjuk vissza
            ResultSet rs = st.executeQuery(SQL_SELECT_ALL_PURCHASES);

            queryOneBookPst = conn.prepareStatement(SQL_SELECT_ONE_BOOK);
            queryOneCustomerPst = conn.prepareStatement(SQL_SELECT_ONE_CUSTOMER);

            // Bejárjuk a visszakapott ResultSet-et (ami a vásárlásokat tartalmazza)
            while (rs.next()) {
                // Elmentjük a vásárlás id-jét
                int purchaseId = rs.getInt("id");

                // Felparaméterezzük az egy könyvet id alapján lekérdezõ query
                // templatet a lekérdezni kívánt könyv id-jével
                queryOneBookPst.setInt(1, rs.getInt("id_book"));

                // Lekérdezzük a könyvet
                ResultSet queryOneBookResultSet = queryOneBookPst.executeQuery();

                // "Naív" megközelítés, mindenképpen csak az elsõ elemre vagyunk kíváncsiak
                Book queriedBook = getBooksFromResultSet(queryOneBookResultSet).get(0);

                // Felparaméterezzük az egy vásárlót id alapján lekérdezõ query
                // templatet a lekérdezni kívánt vásárló id-jével
                queryOneCustomerPst.setInt(1, rs.getInt("id_customer"));

                // Lekérdezzük a vásárlót
                ResultSet queryOneCustomerResultSet = queryOneCustomerPst.executeQuery();

                // "Naív" megközelítés, mindenképpen csak az elsõ elemre vagyunk kíváncsiak
                Customer queriedCustomer = getCustomersFromResultSet(queryOneCustomerResultSet).get(0);

                // Létrhozzuk egy új vásárlást
                Purchase purchase = new Purchase();

                purchase.setId(purchaseId);
                purchase.setBook(queriedBook);
                purchase.setCustomer(queriedCustomer);

                purchases.add(purchase);
            }

            // Mivel az Auto Commit módot false-ra állítottuk, így nekünk
            // kell manuálisan gondoskodnunk a tranzakció zárásáról (commit / rollback)
            conn.commit();
        } catch (SQLException e) {
            System.out.println("Failed to execute listing purchases.");
            e.printStackTrace();
        } finally {
            try {
                if (st != null) {
                    st.close();
                }
            } catch (SQLException e) {
                System.out.println("Failed to close statement when listing purchases.");
                e.printStackTrace();
            }

            try {
                if (queryOneBookPst != null) {
                    queryOneBookPst.close();
                }
            } catch (SQLException e) {
                System.out.println("Failed to close statement when querying book for listing purchases.");
                e.printStackTrace();
            }

            try {
                if (queryOneCustomerPst != null) {
                    queryOneCustomerPst.close();
                }
            } catch (SQLException e) {
                System.out.println("Failed to close statement when querying customer for listing purchases.");
                e.printStackTrace();
            }
            
            try {
                if (conn != null) {
                    conn.close();
                }
            } catch (SQLException e) {
                System.out.println("Failed to close connection when listing purchases.");
                e.printStackTrace();
            }
        }

        return purchases;
    }

    /**
     * Segédfüggvény mely egy {@link ResultSet}-bõl {@link Book} listát készít.
     *
     * @param resultSet A {@link ResultSet} melybõl a {@link Book} listát kinyerjük.
     * @return A {@link Book}-okat tartalmazó lista.
     * @throws SQLException A seggédfüggvényben keletkezõ kivételt tovább dobjuk.
     */
    private List<Book> getBooksFromResultSet(ResultSet resultSet) throws SQLException {
        List<Book> rvBooksList = new ArrayList<Book>();

        // Bejárjuk a ResultSet-et (ami a könyveket tartalmazza)
        while (resultSet.next()) {
            // Új könyvet hozunk létre
            Book book = new Book();

            // A könyv id-jét a ResultSet aktuális sorából olvassuk (id column)
            book.setId(resultSet.getInt("id"));

            // És így tovább...
            book.setAuthor(resultSet.getString("author"));
            book.setTitle(resultSet.getString("title"));
            book.setYear(resultSet.getInt("year"));
            book.setCategory(resultSet.getString("category"));
            book.setPrice(resultSet.getInt("price"));
            book.setPiece(resultSet.getInt("pieces"));
            book.setAncient(resultSet.getInt("ancient") == 1);

            rvBooksList.add(book);
        }

        return rvBooksList;
    }

    /**
     * Segédfüggvény mely egy {@link ResultSet}-bõl {@link Customer} listát készít.
     *
     * @param resultSet A {@link ResultSet} melybõl a {@link Customer} listát kinyerjük.
     * @return A {@link Customer}-eket tartalmazó lista.
     * @throws SQLException A seggédfüggvényben keletkezõ kivételt tovább dobjuk.
     */
    private List<Customer> getCustomersFromResultSet(ResultSet resultSet) throws SQLException {
        List<Customer> rvCustomersList = new ArrayList<Customer>();

        // Bejárjuk a ResultSet-et (ami a vásárlókat tartalmazza)
        while (resultSet.next()) {
            // Új vásárlót hozunk létre
            Customer customer = new Customer();

            // A vásárló id-jét a ResultSet aktuális sorából olvassuk (id column)
            customer.setId(resultSet.getInt("id"));

            // És így tovább...
            customer.setName(resultSet.getString("name"));
            customer.setAge(resultSet.getInt("age"));
            customer.setFemale(resultSet.getInt("female") == 1);
            customer.setRented(resultSet.getInt("rented") == 1);
            customer.setStudent(resultSet.getInt("student") == 1);
            customer.setGrantee(resultSet.getInt("grantee") == 1);
            customer.setQualification(resultSet.getString("qualification"));

            rvCustomersList.add(customer);
        }

        return rvCustomersList;
    }

}
