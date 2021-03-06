package hu.alkfejl.bookshop.view;

/**
 * Ez az oszt�ly tartalmazza a programban el�fordul� {@link String} konstansokat.
 * Csak itt fordulhat el� {@link String} konstans!
 * <p>
 * �ltal�noss�gban elfogadott �s szok�s, hogy a k�d nem tartalmaz olyan
 * {@link String} konstansokat, melyek a felhaszn�l�i fel�leten jelennek meg.
 * Ezeket mindig egy k�z�s helyre gy�jtj�k, �gy k�s�bb lokaliz�lhat� az alkalmaz�s
 * �s eleve �tl�that�bb �gy.
 * </p>
 */
public class Labels {

    // General labels
    public final static String name = "Name";
    public final static String ok = "OK";
    public final static String cancel = "Cancel";
    public final static String error = "Error";
    public final static String empty = "";
    public final static String unknown = "Unknown";

    // Main window
    public static final String main_window_title_format = "Book Shop (%1$s)";
    public static final String main_window_title_unknown_user = "ismeretlen";

    // Customer labels
    public final static String customer = "Customer";
    public final static String add_customer = "Add customer";
    public final static String list_customers = "List customers";
    public final static String gender = "Gender";
    public final static String female = "Female";
    public final static String male = "Male";
    public final static String age = "Age";
    public final static String grantee = "Grantee";
    public final static String university = "University";
    public final static String college = "College";
    public final static String high_school = "High school";
    public final static String elementary_school = "Elementary school";
    public final static String[] qualifications = {
        Labels.university,
        Labels.college,
        Labels.high_school,
        Labels.elementary_school
    };
    public final static String qualification = "Qualification";
    public final static String student = "Student";
    public final static String rented = "Rented";
    public final static String customer_name_is_required = "Customer name is required!";
    public final static String customer_exists = "Customer already exists!";

    // Book labels
    public final static String book = "Book";
    public final static String buy_book = "Buy book";
    public final static String list_books = "List books";
    public final static String author = "Author";
    public final static String title = "Title";
    public final static String year = "Year";
    public final static String category = "Category";
    public final static String price = "Price";
    public final static String piece = "Piece";
    public final static String sci_fi = "sci-fi";
    public final static String horror = "horror";
    public final static String drama = "Dr�ma";
    public final static String[] categories = new String[] {
        Labels.sci_fi,
        Labels.horror,
        Labels.drama
    };
    public final static String ancient = "Ancient";
    public final static String book_title_is_required = "Book title is required!";
    public final static String book_exists = "Such book is already bought!";

    // Sell labels
    public final static String sell = "Sell";
    public final static String sell_book = "Sell book";
    public final static String list_sold_books = "List sold books";
    public final static String purchase_choose_book_and_customer =
            "V�lassz ki k�nyvet �s v�s�rl�t!";
    public final static String purchase_failed =
            "Nem siker�lt r�gz�teni a v�s�rl�st!";
    
    // CD labels
    public final static String cd = "CD";
    public final static String buy_cd = "Buy CD";
    public final static String list_cds = "List CDs";
    public final static String hit = "Hit";
    public final static String selection = "Selection";
    public final static String cd_title_is_required = "CD title is required!";
    public final static String cd_fail = "Title and author must be unique!";
}
