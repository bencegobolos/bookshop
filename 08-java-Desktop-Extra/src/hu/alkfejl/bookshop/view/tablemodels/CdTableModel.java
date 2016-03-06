package hu.alkfejl.bookshop.view.tablemodels;

import hu.alkfejl.bookshop.model.bean.Cd;
import hu.alkfejl.bookshop.view.Labels;

import java.util.List;

import javax.swing.table.AbstractTableModel;

/**
 * Egyedi table modelt az AbstractTableModelb�l tudunk sz�rmaztatni.
 */
public class CdTableModel extends AbstractTableModel {

    

    /**
	 * 
	 */
	private static final long serialVersionUID = -7960484900663208023L;

	// Az egyes oszlop fejl�cek nevei
    private String[] columnNames = new String[] {
            Labels.author, Labels.title, Labels.year, Labels.hit,
            Labels.price, Labels.selection };

    List<Cd> cds;

    public CdTableModel(List<Cd> cds) {
        super();

        this.cds = cds;
    }

    /* A table model megval�s�t�s�hoz fel�l kell �rni n�h�ny fontos met�dust!
     */

    /**
     * Megadja, hogy h�ny oszlopa van a t�bl�zatnak.
     */
    @Override
    public int getColumnCount() {
        return columnNames.length;
    }

    /**
     * Megadja, hogy h�ny sora van a t�bl�zatnak.
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
     * Megadja, hogy adott sorban �s oszlopban milyen �rt�k szerepel.
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
    // �gy viszont ami boolean, az checkboxkent jelenik meg.
    @Override
    public Class<?> getColumnClass(int c) {
        return getValueAt(0, c).getClass();
    }

    // Nagyon fontos! Eredetileg egy JTable minden mez�je szerkeszthet�
    // Jelenleg ezt letiltjuk, a szerkeszt�shez a kontrolleren kereszt�l az
    // adatb�zis kommunik�ci�t is implementalni kell!
    @Override
    public boolean isCellEditable(int row, int col) {
        return false;
    }

}