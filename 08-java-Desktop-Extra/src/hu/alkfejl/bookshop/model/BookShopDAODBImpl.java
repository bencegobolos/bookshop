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
 * Ez az oszt�ly az adatel�r�st szolg�lja. T�nyleges perzisztens
 * t�rol�t, adatb�zist haszn�l.
 */
public class BookShopDAODBImpl implements BookShopDAO {

    List<Customer> customers = new ArrayList<Customer>();
    List<Book> books = new ArrayList<Book>();
    List<Purchase> purchases = new ArrayList<Purchase>();

    // Adatb�zis f�jlt reprezent�l� string, melyet a
    // BookShopDB k�rnyezeti v�ltoz�b�l olvasunk ki (env.bat �ll�tja be)
    private static final String DATABASE_FILE = System.getenv("BookShopDB");

    // SQL lek�rdez�s mely lek�rdezi az �sszes v�s�rl�t
    private static final String SQL_SELECT_ALL_CUSTOMERS = "SELECT * FROM Customer";

    // SQL lek�rdez�s mely lek�rdezi az �sszes k�nyvet
    private static final String SQL_SELECT_ALL_BOOKS = "SELECT * FROM Book";

    // SQL lek�rdez�s mely lek�rdezi az �sszes v�s�rl�st
    private static final String SQL_SELECT_ALL_PURCHASES = "SELECT * FROM SoldBookInstances";

    // SQL lek�rdez�s mely lek�rdez egy meghat�rozott id-j� v�s�rl�t
    private static final String SQL_SELECT_ONE_CUSTOMER = "SELECT * FROM Customer WHERE id=?";

    // SQL lek�rdez�s mely lek�rdez egy meghat�rozott id-j� k�nyvet
    private static final String SQL_SELECT_ONE_BOOK = "SELECT * FROM Book WHERE id=?";

    // SQL param�terezhet� INSERT utas�t�s v�s�rl� felv�tel�re
    // Az egyes param�tereket ut�lagosan �ll�thatjuk be (PreparedStatement)
    private static final String SQL_INSERT_CUSTOMER =
        "INSERT INTO Customer " +
        "(name, age, female, rented, student, grantee, qualification) " +
        "VALUES (?, ?, ?, ?, ?, ?, ?)";

    // SQL param�terezhet� INSERT utas�t�s k�nyv felv�tel�re
    // Az egyes param�tereket ut�lagosan �ll�thatjuk be (PreparedStatement)
    private static final String SQL_INSERT_BOOK =
        "INSERT INTO Book " +
        "(author, title, year, category, price, pieces, ancient) " +
        "VALUES (?, ?, ?, ?, ?, ?, ?)";

    // SQL param�terezhet� INSERT utas�t�s v�s�rl�s felv�tel�re
    // Az egyes param�tereket ut�lagosan �ll�thatjuk be (PreparedStatement)
    private static final String SQL_INSERT_PURCHASE =
        "INSERT INTO SoldBookInstances" +
        "(id_book, id_customer, sellDate)" +
        "VALUES(?, ?, ?)";

    // A konstruktorban inicializ�ljuk az adatb�zist
    public BookShopDAODBImpl() {
        try {
            // Betoltjuk az SQLite JDBC drivert, ennek seg�ts�g�vel �rj�k majd
            // el az SQLite adatb�zist
            // kulso/java/sqlitejdbc-v054.jar - a classpath-ba is beker�lt
            // build.xml - javac taskn�l classpath attrib�tum (n�zz�k meg)
            // Valamint ezt megadtuk a disztrib�ci� futatt�s�n�l is a
            // run.bat-ban! (n�zz�k meg)
            Class.forName("org.sqlite.JDBC");
        } catch (ClassNotFoundException e) {
            System.out.println("Failed to load SQLite JDBC driver.");
            e.printStackTrace();
        }
    }

    /**
     * Hozz�ad egy {@link Customer}-t az adatt�rhoz.
     *
     * @param customer A t�roland� {@link Customer}.
     * @return Igaz, ha sikeresen t�rolva, hamis, egy�bk�nt.
     */
    @Override
    public boolean addCustomer(Customer customer) {
        boolean rvSucceeded = false;

        // Adatb�zis kapcsolatot reprezent�l� objektum
        Connection conn = null;
        PreparedStatement pst = null;

        try {
            // Az adatb�zis kapcsolatunkat a DriverManager seg�ts�g�vel hozzuk l�tre
            // Megadjuk hogy a JDBC milyen driveren kereszt�l milyen f�jlt keressen
            conn = DriverManager.getConnection("jdbc:sqlite:" + DATABASE_FILE);

            // �j v�s�rl� felv�tele eset�n egy PreparedStatement objektumot
            // k�r�nk a kapcsolat objektumt�l
            // Ez egy param�terezhet� SQL utasit�st v�r, a param�terek ?-k�nt
            // jelennek meg
            pst = conn.prepareStatement(SQL_INSERT_CUSTOMER);

            // Az egyes paramet�reket sorban kell megadni, poz�ci� alapj�n, ami
            // 1-t�l indul
            // C�lszer� egy indexet inkrement�lni, mivel ha az egyik param�ter
            // kiesik, akkor nem kell az ut�na k�vetkez�eket �jra sz�mozni...
            int index = 1;
            pst.setString(index++, customer.getName());
            pst.setInt(index++, customer.getAge());
            pst.setBoolean(index++, customer.isFemale());
            pst.setBoolean(index++, customer.isRented());
            pst.setBoolean(index++, customer.isStudent());
            pst.setBoolean(index++, customer.isGrantee());
            pst.setString(index++, customer.getQualification());

            // Az ExecuteUpdate paranccsal v�grehajtjuk az utas�t�st
            // Az executeUpdate visszaadja, hogy h�ny sort �rintett az SQL ha 
            // DML-t hajtunk v�gre (DDL eset�n 0-t ad vissza)
            int rowsAffected = pst.executeUpdate();

            // csak akkor sikeres, ha val�ban volt �rintett sor
            if (rowsAffected == 1) {
                rvSucceeded = true;
            }
        } catch (SQLException e) {
            System.out.println("Failed to execute adding customer.");
            e.printStackTrace();
        } finally {
            // NAGYON FONTOS!
            // Minden adatb�zis objektumot le kell z�rni, mivel ha ezt nem
            // tessz�k meg, akkor el�fordulhat, hogy nyitott kapcsolatok
            // maradnak az adatb�zis fel�. Az adatb�zis pedig korl�tozott
            // sz�mban tart fenn kapcsolatokat, ez�rt egy id� ut�n akar ez be is
            // telhet!
            // Minden egyes objektumot k�l�n try-catch �gban kell megpr�b�lni
            // bez�rni!
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
     * Visszaadja a t�rolt {@link Customer} p�ld�nyokat.
     *
     * @return A t�rolt {@link Customer}-ek list�ja.
     */
    public List<Customer> getCustomers(){
        Connection conn = null;
        Statement st = null;

        // T�r�lj�k a mem�ri�b�l a v�s�rl�kat (az�rt tartjuk bennt, mert
        // lehetnek k�s�bb olyan m�veletek, melyekhez nem kell friss�teni)
        customers.clear();

        try {
            // Az adatb�zis kapcsolatunkat a DriverManager seg�ts�g�vel hozzuk l�tre
            // Megadjuk, hogy a JDBC milyen driveren keresztul milyen f�jlt keressen
            conn = DriverManager.getConnection("jdbc:sqlite:" + DATABASE_FILE);

            // A kapcsolat (conn) objektumt�l k�r�nk egy egyszer� (nem
            // param�terezhet�) utas�t�st
            st = conn.createStatement();

            // Az utas�t�s objektumon kereszt�l ind�tunk egy query-t
            // Az eredm�nyeket egy ResultSet objektumban kapjuk vissza
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
     * Hozz�ad egy {@link Book}-ot az adatt�rhoz.
     *
     * @param book A t�roland� {@link Book}.
     * @return Igaz, ha sikeresen t�rolva, hamis, egy�bk�nt.
     */
    public boolean addBook(Book book) {
        boolean rvSucceeded = false;

        // Adatb�zis kapcsolatot reprezent�l� objektum
        Connection conn = null;
        PreparedStatement pst = null;

        try {
            // Az adatb�zis kapcsolatunkat a DriverManager seg�ts�g�vel hozzuk l�tre
            // Megadjuk hogy a JDBC milyen driveren kereszt�l milyen f�jlt keressen
            conn = DriverManager.getConnection("jdbc:sqlite:" + DATABASE_FILE);

            // �j k�nyv felv�tele eset�n egy PreparedStatement objektumot
            // k�r�nk a kapcsolat objektumt�l
            // Ez egy param�terezhet� SQL utasit�st v�r, a param�terek ?-k�nt
            // jelennek meg
            pst = conn.prepareStatement(SQL_INSERT_BOOK);

            // Az egyes paramet�reket sorban kell megadni, poz�ci� alapj�n, ami
            // 1-t�l indul
            // C�lszer� egy indexet inkrement�lni, mivel ha az egyik param�ter
            // kiesik, akkor nem kell az ut�na k�vetkez�eket �jra sz�mozni...
            int index = 1;
            pst.setString(index++, book.getAuthor());
            pst.setString(index++, book.getTitle());
            pst.setInt(index++, book.getYear());
            pst.setString(index++, book.getCategory());
            pst.setInt(index++, book.getPrice());
            pst.setInt(index++, book.getPiece());
            pst.setBoolean(index++, book.isAncient());

            // Az ExecuteUpdate paranccsal v�grehajtjuk az utas�t�st
            // Az executeUpdate visszaadja, hogy h�ny sort �rintett az SQL ha 
            // DML-t hajtunk v�gre (DDL eset�n 0-t ad vissza)
            int rowsAffected = pst.executeUpdate();

            // csak akkor sikeres, ha val�ban volt �rintett sor
            if (rowsAffected == 1) {
                rvSucceeded = true;
            }
        } catch (SQLException e) {
            System.out.println("Failed to execute adding book.");
            e.printStackTrace();
        } finally {
            // NAGYON FONTOS!
            // Minden adatb�zis objektumot le kell z�rni, mivel ha ezt nem
            // tessz�k meg, akkor el�fordulhat, hogy nyitott kapcsolatok
            // maradnak az adatb�zis fel�. Az adatb�zis pedig korl�tozott
            // sz�mban tart fenn kapcsolatokat, ez�rt egy id� ut�n akar ez be is
            // telhet!
            // Minden egyes objektumot k�l�n try-catch �gban kell megpr�b�lni
            // bez�rni!
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
     * Visszaadja a t�rolt {@link Book} p�ld�nyokat.
     *
     * @return A t�rolt {@link Book}-ek list�ja.
     */
    public List<Book> getBooks() {
        Connection conn = null;
        Statement st = null;

        // T�r�lj�k a mem�ri�b�l a k�nyveket (az�rt tartjuk bennt, mert lehetnek
        // k�s�bb olyan m�veletek, melyekhez nem kell friss�teni)
        books.clear();

        try {
            // Az adatb�zis kapcsolatunkat a DriverManager seg�ts�g�vel hozzuk
            // l�tre. Megadjuk, hogy a JDBC milyen driveren kereszt�l milyen
            // f�jlt keressen
            conn = DriverManager.getConnection("jdbc:sqlite:" + DATABASE_FILE);

            // A kapcsolat (conn) objektumt�l k�r�nk egy egyszer�
            // (nem param�terezhet�) utas�t�st
            st = conn.createStatement();

            // Az utas�t�s objektumon kereszt�l ind�tunk egy query-t
            // Az eredm�nyeket egy ResultSet objektumban kapjuk vissza
            ResultSet rs = st.executeQuery(SQL_SELECT_ALL_BOOKS);

            // Kinyerj�k a k�nyveket a ResultSet-b�l
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
     * Hozz�ad egy {@link Purchase}-et az adatt�rhoz.
     *
     * @param purchase A t�roland� {@link Purchase}.
     * @return Igaz, ha sikeresen t�rolva, hamis, egy�bk�nt.
     */
    @Override
    public boolean addPurchase(Purchase purchase) {
        boolean rvSucceeded = false;

        // Adatb�zis kapcsolatot reprezent�l� objektum
        Connection conn = null;
        PreparedStatement pst = null;

        try {
            // Az adatb�zis kapcsolatunkat a DriverManager seg�ts�g�vel hozzuk l�tre
            // Megadjuk hogy a JDBC milyen driveren kereszt�l milyen f�jlt keressen
            conn = DriverManager.getConnection("jdbc:sqlite:" + DATABASE_FILE);

            // �j v�s�rl�s felv�tele eset�n egy PreparedStatement objektumot
            // k�r�nk a kapcsolat objektumt�l
            // Ez egy param�terezhet� SQL utasit�st v�r, a param�terek ?-k�nt
            // jelennek meg
            pst = conn.prepareStatement(SQL_INSERT_PURCHASE);

            // Az egyes paramet�reket sorban kell megadni, poz�ci� alapj�n, ami
            // 1-t�l indul
            // C�lszer� egy indexet inkrement�lni, mivel ha az egyik param�ter
            // kiesik, akkor nem kell az ut�na k�vetkez�eket �jra sz�mozni...
            int index = 1; 
            pst.setInt(index++, purchase.getBook().getId());
            pst.setInt(index++, purchase.getCustomer().getId());
            pst.setDate(index++, new java.sql.Date(new java.util.Date().getTime()));

            // Az ExecuteUpdate paranccsal v�grehajtjuk az utas�t�st
            // Az executeUpdate visszaadja, hogy h�ny sort �rintett az SQL ha 
            // DML-t hajtunk v�gre (DDL eset�n 0-t ad vissza)
            int rowsAffected = pst.executeUpdate();

            // csak akkor sikeres, ha val�ban volt �rintett sor
            if (rowsAffected == 1) {
                rvSucceeded = true;
            }
        } catch (SQLException e) {
            System.out.println("Failed to execute adding purchase.");
            e.printStackTrace();
        } finally {
            // NAGYON FONTOS!
            // Minden adatb�zis objektumot le kell z�rni, mivel ha ezt nem
            // tessz�k meg, akkor el�fordulhat, hogy nyitott kapcsolatok
            // maradnak az adatb�zis fel�. Az adatb�zis pedig korl�tozott
            // sz�mban tart fenn kapcsolatokat, ez�rt egy id� ut�n akar ez be is
            // telhet!
            // Minden egyes objektumot k�l�n try-catch �gban kell megpr�b�lni
            // bez�rni!
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
     * Visszaadja a t�rolt {@link Purchase} p�ld�nyokat.
     *
     * @return A t�rolt {@link Purchase}-ek list�ja.
     */
    @Override
    public List<Purchase> getPurchases() {
        Connection conn = null;
        Statement st = null;

        PreparedStatement queryOneBookPst = null;
        PreparedStatement queryOneCustomerPst = null;

        // T�r�lj�k a mem�ri�b�l a v�s�rl�sokat (az�rt tartjuk bennt, mert
        // lehetnek k�s�bb olyan m�veletek, melyekhez nem kell friss�teni)
        purchases.clear();

        try {
            // Az adatb�zis kapcsolatunkat a DriverManager seg�ts�g�vel hozzuk l�tre
            // Megadjuk, hogy a JDBC milyen driveren keresztul milyen f�jlt keressen
            conn = DriverManager.getConnection("jdbc:sqlite:" + DATABASE_FILE);

            // ResultSet bej�r�sa k�zben szeretn�nk tov�bbi SELECT-eket v�grehajtani
            // Auto Commit m�dban minden SQL utas�t�s k�l�n tranzakci�.
            // Az SQLite engine nem k�pes egym�sba�gyazott tranzakci�k kezel�s�re
            conn.setAutoCommit(false);

            // A kapcsolat (conn) objektumt�l k�r�nk egy egyszer� (nem
            // param�terezhet�) utas�t�st
            st = conn.createStatement();

            // Az utas�t�s objektumon kereszt�l ind�tunk egy query-t
            // Az eredm�nyeket egy ResultSet objektumban kapjuk vissza
            ResultSet rs = st.executeQuery(SQL_SELECT_ALL_PURCHASES);

            queryOneBookPst = conn.prepareStatement(SQL_SELECT_ONE_BOOK);
            queryOneCustomerPst = conn.prepareStatement(SQL_SELECT_ONE_CUSTOMER);

            // Bej�rjuk a visszakapott ResultSet-et (ami a v�s�rl�sokat tartalmazza)
            while (rs.next()) {
                // Elmentj�k a v�s�rl�s id-j�t
                int purchaseId = rs.getInt("id");

                // Felparam�terezz�k az egy k�nyvet id alapj�n lek�rdez� query
                // templatet a lek�rdezni k�v�nt k�nyv id-j�vel
                queryOneBookPst.setInt(1, rs.getInt("id_book"));

                // Lek�rdezz�k a k�nyvet
                ResultSet queryOneBookResultSet = queryOneBookPst.executeQuery();

                // "Na�v" megk�zel�t�s, mindenk�ppen csak az els� elemre vagyunk k�v�ncsiak
                Book queriedBook = getBooksFromResultSet(queryOneBookResultSet).get(0);

                // Felparam�terezz�k az egy v�s�rl�t id alapj�n lek�rdez� query
                // templatet a lek�rdezni k�v�nt v�s�rl� id-j�vel
                queryOneCustomerPst.setInt(1, rs.getInt("id_customer"));

                // Lek�rdezz�k a v�s�rl�t
                ResultSet queryOneCustomerResultSet = queryOneCustomerPst.executeQuery();

                // "Na�v" megk�zel�t�s, mindenk�ppen csak az els� elemre vagyunk k�v�ncsiak
                Customer queriedCustomer = getCustomersFromResultSet(queryOneCustomerResultSet).get(0);

                // L�trhozzuk egy �j v�s�rl�st
                Purchase purchase = new Purchase();

                purchase.setId(purchaseId);
                purchase.setBook(queriedBook);
                purchase.setCustomer(queriedCustomer);

                purchases.add(purchase);
            }

            // Mivel az Auto Commit m�dot false-ra �ll�tottuk, �gy nek�nk
            // kell manu�lisan gondoskodnunk a tranzakci� z�r�s�r�l (commit / rollback)
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
     * Seg�df�ggv�ny mely egy {@link ResultSet}-b�l {@link Book} list�t k�sz�t.
     *
     * @param resultSet A {@link ResultSet} melyb�l a {@link Book} list�t kinyerj�k.
     * @return A {@link Book}-okat tartalmaz� lista.
     * @throws SQLException A segg�df�ggv�nyben keletkez� kiv�telt tov�bb dobjuk.
     */
    private List<Book> getBooksFromResultSet(ResultSet resultSet) throws SQLException {
        List<Book> rvBooksList = new ArrayList<Book>();

        // Bej�rjuk a ResultSet-et (ami a k�nyveket tartalmazza)
        while (resultSet.next()) {
            // �j k�nyvet hozunk l�tre
            Book book = new Book();

            // A k�nyv id-j�t a ResultSet aktu�lis sor�b�l olvassuk (id column)
            book.setId(resultSet.getInt("id"));

            // �s �gy tov�bb...
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
     * Seg�df�ggv�ny mely egy {@link ResultSet}-b�l {@link Customer} list�t k�sz�t.
     *
     * @param resultSet A {@link ResultSet} melyb�l a {@link Customer} list�t kinyerj�k.
     * @return A {@link Customer}-eket tartalmaz� lista.
     * @throws SQLException A segg�df�ggv�nyben keletkez� kiv�telt tov�bb dobjuk.
     */
    private List<Customer> getCustomersFromResultSet(ResultSet resultSet) throws SQLException {
        List<Customer> rvCustomersList = new ArrayList<Customer>();

        // Bej�rjuk a ResultSet-et (ami a v�s�rl�kat tartalmazza)
        while (resultSet.next()) {
            // �j v�s�rl�t hozunk l�tre
            Customer customer = new Customer();

            // A v�s�rl� id-j�t a ResultSet aktu�lis sor�b�l olvassuk (id column)
            customer.setId(resultSet.getInt("id"));

            // �s �gy tov�bb...
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
