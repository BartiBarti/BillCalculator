package pl.blillcalculator.bartek.gui;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

public class BillCalculator extends JFrame{
    private JPanel mainPanel;
    private JLabel titleLabel;
    private JTextField billTextField;
    private JLabel billLabel;
    private JComboBox sortByPercentComboBox;
    private JButton calculateBillButton;
    private JLabel tipLabel;
    private JTextField tipTextField;
    private JLabel billSumLabel;
    private JTextField billSumTextField;
    private JLabel warningLabel;

    public BillCalculator() {
        setTitle("Bill Calculator");
        setSize(420, 340);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
//        mainPanel.setBorder(new EmptyBorder(10, 10, 10, 10)); - granica i margines dodana w pliku .form w ustawieniach
        setContentPane(mainPanel);

        createListeners();
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new BillCalculator().setVisible(true));

//        List<String> list = null;

//        System.out.println("czy lista jest nullem " + (list == null));
////    list.isEmpty(); Tu poleci wyjątek NullPointerException ponieważ zmienna list jest null.
//
//        list = new ArrayList<>(); // Tu inizajalizujemy obiekt wywołując konstruktor przez new i tworząc instancje zmiennej
//        System.out.println("czy lista jest nullem 22222 " + (list == null));
//        System.out.println("czy lista jest pusta 22222 "  + list.isEmpty()); // Tu wyjątek NullPointerException nie poleci ponieważ zmienna list jest zainicajlizowana 2 linijki wyżej
//
//        list.add("abc");
//        System.out.println("czy lista jest nullem 33333 " + (list == null));
//        System.out.println("czy lista jest pusta 33333 "  + list.isEmpty());
    }

    private void createUIComponents() {
        // TODO: place custom component creation code here
    }
    private void createListeners() {
        billTextField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                int keyPressed = e.getKeyCode();
                if (KeyEvent.VK_ENTER == keyPressed) {
                    calculateBill();
                }
            }
        });

    }
    private void calculateBill() {
        double bill = 0;
        try {
            bill = Double.parseDouble(billTextField.getText());
            warningLabel.setText("");
        } catch (NumberFormatException e) {
            warningLabel.setText(("Wpisano błędną kwotę!"));
        }
    }

}
