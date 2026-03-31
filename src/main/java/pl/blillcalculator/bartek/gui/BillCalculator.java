package pl.blillcalculator.bartek.gui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.HashMap;
import java.util.Map;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import pl.blillcalculator.bartek.model.MenuItem;
import pl.blillcalculator.bartek.model.MenuType;
import pl.blillcalculator.bartek.service.BillService;


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

  }

  private void menuActionListeners() {
    apetizersButton.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        SwingUtilities.invokeLater(() -> new Menu(
            MenuType.APETIZERS,
            billService,
            choosenDinners).setVisible(true));
      }
    });
    soupsButton.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        SwingUtilities.invokeLater(() -> new Menu(
            MenuType.SOUPS,
            billService,
            choosenDinners).setVisible(true));
      }
    });
    mainDishesButton.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        SwingUtilities.invokeLater(() -> new Menu(
            MenuType.MAIN_DISHES,
            billService,
            choosenDinners).setVisible(true));
      }
    });
    additionalsButton.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        SwingUtilities.invokeLater(() -> new Menu(
            MenuType.ADDITIONALS,
            billService,
            choosenDinners).setVisible(true));

      }
    });
    desertsButton.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        SwingUtilities.invokeLater(() -> new Menu(
            MenuType.DESERTS,
            billService,
            choosenDinners).setVisible(true));
      }
    });
    drinksButton.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        SwingUtilities.invokeLater(() -> new Menu(
            MenuType.DRINKS,
            billService,
            choosenDinners).setVisible(true));
      }
    });
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
