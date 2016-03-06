package hu.alkfejl.bookshop.view.tablemodels;

import hu.alkfejl.bookshop.model.bean.Cd;
import hu.alkfejl.bookshop.view.Labels;

import java.util.List;

import javax.swing.table.AbstractTableModel;

/**
 * Egyedi table modelt az AbstractTableModelbõl tudunk származtatni.
 */
public class CdTableModel extends AbstractTableModel {

    

    /**
	 * 
	 */
	private static final long serialVersionUID = -7960484900663208023L;

	// Az egyes oszlop fejlécek nevei
    private String[] columnNames = new String[] {
            Labels.author, Labels.title, Labels.year, Labels.hit,
            Labels.price, Labels.selection };

    List<Cd> cds;

    public CdTableModel(List<Cd> cds) {
        super();

        this.cds = cds;
    }

    /* A table model megvalósításához felül kell írni néhány fontos metódust!
     */

    /**
     * Megadja, hogy hány oszlopa van a táblázatnak.
     */
    @Override
    public int getColumnCount() {
        return columnNames.length;
    }

    /**
     * Megadja, hogy hány sora van a táblázatnak.
     */
    @Override
    public int getRowCount() {
        return cds.size();
    }

    /**
     * Megadja, hogy adott oszlopnak mi a neve.
     */
    @Override
    public String getColumnName(int col) {
        return columnNames[col];
    }

    /**
     * Megadja, hogy adott sorban és oszlopban milyen érték szerepel.
     */
    @Override
    public Object getValueAt(int row, int col) {
        Cd cd = cds.get(row);
        String askedColumnName = columnNames[col];

        if (askedColumnName.equals(Labels.author)) {
            return cd.getAuthor();
        }else if (askedColumnName.equals(Labels.title)) {
            return cd.getTitle();
        }else if (askedColumnName.equals(Labels.year)) {
            return cd.getYear();
        }else if (askedColumnName.equals(Labels.hit)) {
            return cd.isHit();
        }else if (askedColumnName.equals(Labels.price)) {
            return cd.getPrice();
        }else if (askedColumnName.equals(Labels.selection)) {
            return cd.isSelection();
        }

        return Labels.unknown;
    }

    // Nagyon fontos! Eredetileg egy JTable-ben minden sztring
    // Így viszont ami boolean, az checkboxkent jelenik meg.
    @Override
    public Class<?> getColumnClass(int c) {
        return getValueAt(0, c).getClass();
    }

    // Nagyon fontos! Eredetileg egy JTable minden mezõje szerkeszthetõ
    // Jelenleg ezt letiltjuk, a szerkesztéshez a kontrolleren keresztül az
    // adatbázis kommunikációt is implementalni kell!
    @Override
    public boolean isCellEditable(int row, int col) {
        return false;
    }

}