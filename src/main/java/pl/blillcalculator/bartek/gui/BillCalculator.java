package pl.blillcalculator.bartek.gui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;


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
    private JLabel warningLabel;
    private JLabel tipPercentLabel;
    private JLabel mainCourseLabel;
    private JCheckBox porkChopCheckBox;
    private JCheckBox chickenSteakJCheckBox;
    private JCheckBox salmonCheckBox;
    private JLabel desertsLabel;
    private JCheckBox applePieCheckBox;
    private JCheckBox tortCheckBox;
    private JCheckBox cheeseCakeCheckBox;
    private JLabel drinksLabel;
    private JCheckBox coffeeCheckBox;
    private JCheckBox teaCheckBox;
    private JCheckBox lemonadeCheckBox;
    private JCheckBox compoteCheckBox;
    private JCheckBox beerCheckBox;
    private JCheckBox wineCheckBox;
    private JLabel porkChopLabel;
    private JLabel chickenSteakLabel;
    private JLabel salmonLabel;
    private JLabel applePielabel;
    private JLabel tortLabel;
    private JLabel cheeseCakeLabel;
    private JLabel coffeeLabel;
    private JLabel teaLabel;
    private JLabel lemonadeLabel;
    private JLabel compoteLabel;
    private JLabel beerLabel;
    private JLabel wineLabel;
    private JButton additionalsButton;
    private JButton soupsButton;
    private JLabel menuLabel;

    private static BillCalculator billCalculator = new BillCalculator();

    public BillCalculator() {
        setTitle("Bill Calculator");
        setSize(1000, 840);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
//        mainPanel.setBorder(new EmptyBorder(10, 10, 10, 10)); - granica i margines dodana w pliku .form w ustawieniach
        setContentPane(mainPanel);

        createListeners();
        setTextFieldsVisibility();
// Todo uzupełnic dodatki oraz surówki napoje zimne i ciepłe podzielić po 3 plus ItemListenery do checkboxów przenieść napoje na druga kolumnę
//        Wyszarzyć pole z kwotą rachunku
        tipPercentComboBox.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
                if (e.getStateChange() == ItemEvent.SELECTED) {
                    calculateBill();
                }
            }
        });
        
        porkChopCheckBox.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
                calculateNewBillAmount(e, porkChopLabel);
                calculateBill();
            }
        });
        chickenSteakJCheckBox.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
                calculateNewBillAmount(e, chickenSteakLabel);
                calculateBill();
            }
        });
        salmonCheckBox.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
                calculateNewBillAmount(e, salmonLabel);
                calculateBill();
            }
        });
        applePieCheckBox.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
                calculateNewBillAmount(e, applePielabel);
                calculateBill();
            }
        });
        tortCheckBox.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
                calculateNewBillAmount(e, tortLabel);
                calculateBill();
            }
        });
        cheeseCakeCheckBox.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
                calculateNewBillAmount(e, cheeseCakeLabel);
                calculateBill();
            }
        });
        coffeeCheckBox.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
                calculateNewBillAmount(e, coffeeLabel);
                calculateBill();
            }
        });
        teaCheckBox.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
                calculateNewBillAmount(e, teaLabel);
                calculateBill();
            }
        });
        lemonadeCheckBox.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
                calculateNewBillAmount(e, lemonadeLabel);
                calculateBill();
            }
        });
        compoteCheckBox.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
                calculateNewBillAmount(e, compoteLabel);
                calculateBill();
            }
        });
        beerCheckBox.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
                calculateNewBillAmount(e, beerLabel);
                calculateBill();
            }
        });
        wineCheckBox.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
                calculateNewBillAmount(e, wineLabel);
                calculateBill();
            }
        });
        additionalsButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                SwingUtilities.invokeLater(() -> new Menu(billTextField,
                        "src/main/resources/Apetizers.txt",
                        billCalculator).setVisible(true));
            }
        });
        soupsButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                SwingUtilities.invokeLater(() ->new Menu(billTextField,
                        "src/main/resources/Soups.txt",
                        billCalculator).setVisible(true));
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
//        calculateBillButton.addActionListener(new ActionListener() {
//            @Override
//            public void actionPerformed(ActionEvent e) {
//                calculateBill();
//            }
//        });

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


    private void setTextFieldsVisibility() {
        tipTextField.setEnabled(false);
        billSumTextField.setEnabled(false);
        billTextField.setEnabled(false);
    }

}
