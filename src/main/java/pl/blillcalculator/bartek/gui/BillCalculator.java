package pl.blillcalculator.bartek.gui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class BillCalculator extends JFrame{
    private JPanel mainPanel;
    private JLabel titleLabel;
    private JTextField billTextField;
    private JLabel billLabel;
    private JComboBox tipPercentComboBox;
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
        setTextFieldsVisibility();

        tipPercentComboBox.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
                if (e.getStateChange() == ItemEvent.SELECTED) {
                    calculateBill();
                }
            }
        });
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
        calculateBillButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                calculateBill();
            }
        });

    }
    private void calculateBill() {
        double bill = 0;
        try {
            bill = Double.parseDouble(billTextField.getText());
            String tipPercent = (String) tipPercentComboBox.getSelectedItem();
            tipPercent = tipPercent.replaceAll("%", "");
            int iTipPercent = Integer.valueOf(tipPercent);

            double tip = bill * iTipPercent/100;
            double fullBill = tip + bill;
            String sTip = String.format("%.2f", tip);
            String sFullBill = String.format("%.2f", fullBill);
            tipTextField.setText(sTip);
            billSumTextField.setText(sFullBill);
            warningLabel.setText("");
        } catch (NumberFormatException e) {
//              Działają dwie metody ta przy użyciu HTMLa też, tylko jest zakomentowana
//            warningLabel.setText("<html><font color='red'>Wpisano błędne znaki!</font></html>");
            warningLabel.setText(("Wpisano błędne znaki!"));
            warningLabel.setForeground(Color.RED);
            tipTextField.setText("");
//
        }
    }

    private void setTextFieldsVisibility(){
        tipTextField.setEnabled(false);
        billSumTextField.setEnabled(false);
    }

}
