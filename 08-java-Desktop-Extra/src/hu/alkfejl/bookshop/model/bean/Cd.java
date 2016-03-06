package hu.alkfejl.bookshop.model.bean;

/**
 * Az osztály egy könyvet ír le a JavaBean konvenciók betartásával:
 */
public class Cd {
	private int id;
    private String author;
    private String title;
    private int price;
    private int year;
    private boolean hit;
    private boolean selection;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }
    
    public boolean isHit() {
        return hit;
    }

    public void setHit(boolean hit) {
        this.hit = hit;
    }
    
    public boolean isSelection() {
        return selection;
    }

    public void setSelection(boolean selection) {
        this.selection = selection;
    }


    @Override
    public String toString() {
        return "Cd [" +
                "author=" + author + ", title=" + title + ", year=" + year +
                ", price=" + price + ", hit=" + hit +", selection=" +
                selection + "]";
    }
}
