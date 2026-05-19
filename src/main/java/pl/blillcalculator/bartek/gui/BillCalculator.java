package pl.blillcalculator.bartek.gui;

import pl.blillcalculator.bartek.model.MenuItem;
import pl.blillcalculator.bartek.model.MenuType;
import pl.blillcalculator.bartek.service.BillService;

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
    private JButton editMenuButton;
    private JButton menuSummaryButtonnuButton;
    private JButton clearButton;
    private JButton printMenuButton;

    private Menu drinksMenu;
    private Menu desertsMenu;
    private Menu additionalsMenu;
    private Menu mainDishesMenu;
    private Menu soupsMenu;
    private Menu apetizersMenu;

    private Map<MenuItem, Integer> choosenDinners = new HashMap<>();

    private BillService billService;

    public BillCalculator() {
        setTitle("Bill Calculator");
        setSize(700, 350);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
//        mainPanel.setBorder(new EmptyBorder(10, 10, 10, 10)); - granica i margines dodana w pliku .form w ustawieniach
        setContentPane(mainPanel);
        billService = new BillService(billTextField, billSumTextField, tipTextField,
                tipPercentComboBox);

        createCalculatorBillListeners();
        setTextFieldEditability();
        menuActionListeners();

        editMenuButton.addActionListener(e -> {
            new EditMenu().setVisible(true);
        });
        menuSummaryButtonnuButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                showOrderSummary();
            }
        });
        clearButton.addActionListener(e -> clearOrder());

    }

    private void clearOrder() {

        choosenDinners.clear();

        billTextField.setText("");
        tipTextField.setText("");
        billSumTextField.setText("");

        if (drinksMenu != null) {
            drinksMenu.dispose();
        }
        if (desertsMenu != null) {
            desertsMenu.dispose();
        }
        if (additionalsMenu != null) {
            additionalsMenu.dispose();
        }
        if (mainDishesMenu != null) {
            mainDishesMenu.dispose();
        }
        if (soupsMenu != null) {
            soupsMenu.dispose();
        }
        if (apetizersMenu != null) {
            apetizersMenu.dispose();
        }

        JOptionPane.showMessageDialog(this,
                "Zamówienie zostało wyczyszczone.");
    }

    private void showOrderSummary() {

        if (choosenDinners.isEmpty()) {

            JOptionPane.showMessageDialog(this,
                    "Puste zamówienie, nie zamówiono.");
            return;
        }

        StringBuilder summary = new StringBuilder(); // lepszy rodzaj Stringa - bardziej rozbudowany -
        // w przypadku długich tekstów lepiej używać StringBuilder

        double total = 0;

        for (Map.Entry<MenuItem, Integer> entry : choosenDinners.entrySet()) {

            MenuItem item = entry.getKey();
            int quantity = entry.getValue();

            double itemSum = item.getPrice() * quantity;

            total += itemSum;

            summary.append(item.getName())
                    .append(" x")
                    .append(quantity)
                    .append(" - ")
                    .append(String.format("%.2f", itemSum))
                    .append(" zł\n");
        }
// TODO spolszczyć tekst wyświetlany na przyciskach
        summary.append("\nTotal: ")
                .append(String.format("%.2f", total))
                .append(" zł");
// TODO brakuje procentu napiwku i pokazania całej kwoty z napiwkiem
//  TODO użyć klasy MenuSummary zamiast dialoga  - czyli przenieść i tam stworzyć konstruktor nowego okna z przyciskami
        JOptionPane.showMessageDialog(this,
                summary.toString(),
                "Order summary",
                JOptionPane.INFORMATION_MESSAGE);
    }

    private Menu openOrRestoreMenu(Menu menu, MenuType type) {
        if (menu == null || !menu.isDisplayable()) {
            menu = new Menu(
                    type,
                    billService,
                    choosenDinners
            );
            menu.setVisible(true);
        } else {
            if (menu.getExtendedState() == JFrame.ICONIFIED) {
                menu.setExtendedState(JFrame.NORMAL);
            }

            menu.toFront();
            menu.requestFocus();
        }

        return menu;
    }

    private void menuActionListeners() {
        apetizersButton.addActionListener(e ->
                apetizersMenu = openOrRestoreMenu(apetizersMenu, MenuType.APETIZERS)
        );
        soupsButton.addActionListener(e ->
                soupsMenu = openOrRestoreMenu(soupsMenu, MenuType.SOUPS)
        );
        mainDishesButton.addActionListener(e ->
                mainDishesMenu = openOrRestoreMenu(mainDishesMenu, MenuType.MAIN_DISHES)
        );
        additionalsButton.addActionListener(e ->
                additionalsMenu = openOrRestoreMenu(additionalsMenu, MenuType.ADDITIONALS)
        );

        desertsButton.addActionListener(e ->
                desertsMenu = openOrRestoreMenu(desertsMenu, MenuType.DESERTS)
        );
        drinksButton.addActionListener(e ->
                drinksMenu = openOrRestoreMenu(drinksMenu, MenuType.DRINKS)
        );
    }

    private void createCalculatorBillListeners() {

        tipPercentComboBox.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
                if (e.getStateChange() == ItemEvent.SELECTED) {
                    billService.calculateBill();
                }
            }
        });
    }

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

    private void setTextFieldEditability() {
        tipTextField.setEditable(false);
        billSumTextField.setEditable(false);
        billTextField.setEditable(false);
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

}
