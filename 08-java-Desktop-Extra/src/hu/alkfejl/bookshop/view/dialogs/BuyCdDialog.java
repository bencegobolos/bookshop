package hu.alkfejl.bookshop.view.dialogs;

import hu.alkfejl.bookshop.model.bean.Cd;
import hu.alkfejl.bookshop.view.BookShopGUI;
import hu.alkfejl.bookshop.view.Labels;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.JTextField;

/**
 * Az oszt�ly az �j k�nyv felv�tel�n�l megjelen� dial�gus.
 */
public class BuyCdDialog extends JDialog implements ActionListener {

	/**
	 * 
	 */
	private static final long serialVersionUID = 5767821106263821823L;

	private BookShopGUI gui;

    // A dial�gus azon vez�rl�it melyekre sz�ks�g lesz az esem�nykezel�s sor�n
    // oszt�lyv�ltoz�k�nt defini�ljuk
    private JTextField authorTextfield = new JTextField(10);
    private JTextField titleTextfield = new JTextField(10);
    private JSpinner yearSpinner = new JSpinner();
    private JSpinner priceSpinner = new JSpinner();
    private JCheckBox selectionCheckbox = new JCheckBox();
    private JButton okButton = new JButton(Labels.ok);
    private JButton cancelButton = new JButton(Labels.cancel);

    public BuyCdDialog(BookShopGUI gui, boolean modal) {
        super(gui.getWindow(), modal);
        this.gui = gui;

        // A dial�gus c�m�nek be�ll�t�sa
        this.setTitle(Labels.buy_cd);

        // A be�ll�t�sokat tartalmaz� panel gy�rt�sa
        JPanel settingPanel = createSettingPanel();

        // A gombokat tartalmaz� panel gy�rt�sa
        JPanel buttonPanel = createButtonPanel();

        // Az el�z� k�t panelt egy panelre rakjuk
        JPanel dialogPanel = createDialogPanel(settingPanel, buttonPanel); 

        // A dialogPanelt r�rakjuk a dial�gusra
        getContentPane().add(dialogPanel);

        // A dial�gus megfelel� m�ret�nek be�ll�t�sa (a tartalmazott elemek alapj�n)
        pack();

        // A dial�gust a BookShopGUI-hoz k�pest rajzolja ki
        setLocationRelativeTo(gui.getWindow());

        // Dialogus megjelen�t�se
        setVisible(true);
    }

    private JPanel createSettingPanel() {
        JPanel settingPanel = new JPanel();

        // A panel elrendez�se m�trix, tetsz�leges sz�m� sor �s 2 oszlop,
        // a cell�k egyforma m�ret�ek
        settingPanel.setLayout(new GridLayout(0, 2));

        // author
        settingPanel.add(new JLabel(Labels.author));
        settingPanel.add(this.authorTextfield);

        // title
        settingPanel.add(new JLabel(Labels.title));
        settingPanel.add(this.titleTextfield);

        // year
        settingPanel.add(new JLabel(Labels.year));
        settingPanel.add(this.yearSpinner);

        // price
        settingPanel.add(new JLabel(Labels.price));
        settingPanel.add(this.priceSpinner);
        
        // collection
        settingPanel.add(new JLabel(Labels.selection));
        settingPanel.add(this.selectionCheckbox);

        return settingPanel;
    }

    private JPanel createButtonPanel() {
        JPanel buttonPanel = new JPanel();

        // A panel elrendez�se folytonos, k�z�pre igaz�tva
        buttonPanel.setLayout(new FlowLayout(FlowLayout.CENTER));

        // Hozz�adjuk az ok gombot, �s figyel�nk r�
        buttonPanel.add(okButton);
        okButton.addActionListener(this);

        // Hozz�adjuk a cancel gombot, �s figyel�nk r�
        buttonPanel.add(cancelButton);
        cancelButton.addActionListener(this);

        return buttonPanel;
    }

    private JPanel createDialogPanel(JPanel settingPanel, JPanel buttonPanel) {
        JPanel dialogPanel = new JPanel();

        // A panel elrendez�se BorderLayout
        dialogPanel.setLayout(new BorderLayout());

        // K�z�pen lesz a settingPanel
        dialogPanel.add(settingPanel, BorderLayout.CENTER);

        // Alul pedig a gombok
        dialogPanel.add(buttonPanel, BorderLayout.SOUTH);

        return dialogPanel;
    }

    public void actionPerformed(ActionEvent e) {
        if (okButton == e.getSource()) {
            // Ha az OK gombot nyomt�k meg, akkor megpr�b�ljuk felvenni a
            // CD-t.

            if (titleTextfield.getText().isEmpty()) {
                // Ha nem adtak meg c�met, akkor egy hiba�zenetet �runk ki egy
                // error dialogra (JOptionPane.ERROR_MESSAGE)
                JOptionPane.showMessageDialog(
                        gui.getWindow(),
                        Labels.cd_title_is_required,
                        Labels.error,
                        JOptionPane.ERROR_MESSAGE);
                return;
            }

            // l�trehozzuk a k�nyvet
            Cd cd = new Cd();

            cd.setAuthor(authorTextfield.getText());
            cd.setPrice((int) priceSpinner.getValue());
            cd.setTitle(titleTextfield.getText());
            cd.setYear((int) yearSpinner.getValue());
            cd.setSelection(selectionCheckbox.isSelected());

            if (!gui.getController().addCd(cd)) {
                JOptionPane.showMessageDialog(
                        gui.getWindow(),
                        Labels.cd_fail,
                        Labels.error,
                        JOptionPane.ERROR_MESSAGE);
            } else {
                setVisible(false);
            }
        } else if (cancelButton == e.getSource()) {
            // cancel eset�n egyszer�en bez�rjuk az ablakot
            setVisible(false);
        }
    }

}