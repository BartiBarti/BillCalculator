package pl.blillcalculator.bartek.gui;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.HashMap;
import java.util.Map;


public class BillCalculator extends JFrame {
    private JPanel mainPanel;
    private JLabel titleLabel;
    private JTextField billTextField;
    private JLabel billLabel;
    private JComboBox tipPercentComboBox;
    //    private JButton calculateBillButton;
    private JLabel tipLabel;
    private JTextField tipTextField;
    private JLabel billSumLabel;
    private JTextField billSumTextField;
    //    private JLabel warningLabel;
    private JLabel tipPercentLabel;
    private JButton apetizersButton;
    private JButton soupsButton;
    private JLabel menuLabel;
    private JButton mainDishesButton;
    private JButton desertsButton;
    private JButton drinksButton;
    private JButton additionalsButton;

    private static BillCalculator billCalculator = new BillCalculator();

    private Map<MenuItem, Integer> choosenDinners = new HashMap<>();

    public BillCalculator() {
        setTitle("Bill Calculator");
        setSize(700, 350);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
//        mainPanel.setBorder(new EmptyBorder(10, 10, 10, 10)); - granica i margines dodana w pliku .form w ustawieniach
        setContentPane(mainPanel);

        createCalculatorBillListeners();
        setTextFieldEditability();

        menuActionListeners();

    }

    public void calculateBill() {
        double bill = 0;
        try {
            bill = Double.parseDouble(billTextField.getText());
            String tipPercent = (String) tipPercentComboBox.getSelectedItem();
            tipPercent = tipPercent.replaceAll("%", "");
            int iTipPercent = Integer.valueOf(tipPercent);

            double tip = bill * iTipPercent / 100;
            double fullBill = tip + bill;
            String sTip = String.format("%.2f", tip);
            String sFullBill = String.format("%.2f", fullBill);
            tipTextField.setText(sTip);
            billSumTextField.setText(sFullBill);
//            warningLabel.setText("");
        } catch (NumberFormatException e) {
//              Działają dwie metody ta przy użyciu HTMLa też, tylko jest zakomentowana
//            warningLabel.setText("<html><font color='red'>Wpisano błędne znaki!</font></html>");
//            warningLabel.setText(("Wpisano błędne znaki!"));
//            warningLabel.setForeground(Color.RED);
            tipTextField.setText("");
//
        }
    }

    private void menuActionListeners() {
        apetizersButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                SwingUtilities.invokeLater(() -> new Menu(billTextField,
                        "src/main/resources/Apetizers.txt",
                        billCalculator,
                        "Przystawki",
                        choosenDinners).setVisible(true));
            }
        });
        soupsButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                SwingUtilities.invokeLater(() -> new Menu(billTextField,
                        "src/main/resources/Soups.txt",
                        billCalculator,
                        "Zupy",
                        choosenDinners).setVisible(true));
            }
        });
        mainDishesButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                SwingUtilities.invokeLater(() -> new Menu(billTextField,
                        "src/main/resources/MainDishes.txt",
                        billCalculator,
                        "Danie główne",
                        choosenDinners).setVisible(true));
            }
        });
        additionalsButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                SwingUtilities.invokeLater(() -> new Menu(billTextField,
                        "src/main/resources/Additionals.txt",
                        billCalculator,
                        "Dodatki",
                        choosenDinners).setVisible(true));

            }
        });
        desertsButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                SwingUtilities.invokeLater(() -> new Menu(billTextField,
                        "src/main/resources/Deserts.txt",
                        billCalculator,
                        "Desery",
                        choosenDinners).setVisible(true));
            }
        });
        drinksButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                SwingUtilities.invokeLater(() -> new Menu(billTextField,
                        "src/main/resources/Drinks.txt",
                        billCalculator,
                        "Napoje",
                        choosenDinners).setVisible(true));
            }
        });
    }

    private void calculateNewBillAmount(ItemEvent e, JLabel label) {
        String sDinnerPrice = label.getText();
        Double dDinnerPrice = Double.valueOf(sDinnerPrice);
        String sBillAmount = billTextField.getText();
        Double dBillAmount = 0.0;
        if (sBillAmount != null && !sBillAmount.equals("")) {
            dBillAmount = Double.valueOf(sBillAmount);
        }
        if (ItemEvent.SELECTED == e.getStateChange()) {
            dBillAmount = dBillAmount + dDinnerPrice;
        } else {
            dBillAmount = dBillAmount - dDinnerPrice;
        }
        billTextField.setText(String.valueOf(dBillAmount));
    }

    private void createUIComponents() {
    }

    private void createCalculatorBillListeners() {

        tipPercentComboBox.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
                if (e.getStateChange() == ItemEvent.SELECTED) {
                    calculateBill();
                }
            }
        });
//        billTextField.addKeyListener(new KeyAdapter() {
//            @Override
//            public void keyPressed(KeyEvent e) {
//                int keyPressed = e.getKeyCode();
//                if (KeyEvent.VK_ENTER == keyPressed) {
//                    calculateBill();
//                }
//            }
//        });
//        calculateBillButton.addActionListener(new ActionListener() {
//            @Override
//            public void actionPerformed(ActionEvent e) {
//                calculateBill();
//            }
//        });

    }

    private void setTextFieldEditability() {
        tipTextField.setEnabled(false);
        billSumTextField.setEnabled(false);
        billTextField.setEnabled(false);
//       todo zmienić nie edytowalne pola, aby czcionka w nich była czarna
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> billCalculator.setVisible(true));

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

}
