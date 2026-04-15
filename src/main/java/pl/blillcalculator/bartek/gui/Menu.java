package pl.blillcalculator.bartek.gui;

import java.awt.*;
import java.util.List;
import java.util.Map;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import pl.blillcalculator.bartek.model.MenuItem;
import pl.blillcalculator.bartek.model.MenuType;
import pl.blillcalculator.bartek.service.BillService;
import pl.blillcalculator.bartek.service.MenuFileLoader;

public class Menu extends JFrame {

  private JPanel menuPanel;

  private double total;

  private Map<MenuItem, Integer> choosenDinners;

  private BillService billService;

  private MenuFileLoader menuFileLoader;

  public Menu(MenuType menuType, BillService billService, Map<MenuItem, Integer> choosenDinners) {
    this.total = billService.getCurrentBillAmount();
    this.choosenDinners = choosenDinners;
    this.billService = billService;
    this.menuFileLoader = new MenuFileLoader();

    configFrame(menuType.getTitle());
    List<MenuItem> menuItems = menuFileLoader.loadMenuFromFile(menuType.getFilePath());
    generateMenu(menuItems);
  }

  private void configFrame(String title) {
    setTitle(title);
    setSize(300, 200);
    setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
    setLocationRelativeTo(null);
    menuPanel.setBorder(new EmptyBorder(10, 10, 10, 10));
    menuPanel.setLayout(new BoxLayout(menuPanel, BoxLayout.Y_AXIS));
    setContentPane(menuPanel);
  }

  private void generateMenu(List<MenuItem> items) {

    menuPanel.removeAll();

    for (MenuItem item : items) {

//      JCheckBox checkBox = new JCheckBox(item.getName() + " - " + item.getPrice() + " zł");
      JPanel row = new JPanel();
      row.setLayout(new BoxLayout(row, BoxLayout.X_AXIS));
      JLabel label = new JLabel(item.getName() + " - " + item.getPrice() + " zł");
//      final Integer menuItemCount = choosenDinners.get(item) != null ? choosenDinners.get(item) : 0;

//      if (menuItemCount == 1) {
//        checkBox.setSelected(true);
//      }
      int currentCount = choosenDinners.getOrDefault(item, 0);

      JSpinner spinner = new JSpinner(
              new SpinnerNumberModel(currentCount, 0, 100, 1)
      );
      Dimension spinnerSize = new Dimension(60, spinner.getPreferredSize().height);
      spinner.setPreferredSize(spinnerSize);
      spinner.setMaximumSize(spinnerSize);

      row.add(label);
      row.add(Box.createHorizontalGlue()); // 🔥 to robi magię HA HA HA to już mój dodatek komentarza
      row.add(spinner);

//      checkBox.addItemListener(e -> {
//        if (checkBox.isSelected()) {
//          total += item.getPrice();
//          choosenDinners.put(item, menuItemCount + 1);
//        } else {
//          total -= item.getPrice();
//          choosenDinners.put(item, menuItemCount - 1);
//        }
//
//        billService.updateBillTextField(total);
//        billService.calculateBill();
//      });

      spinner.addChangeListener(e -> {
        int value = (int) spinner.getValue();

        if (value > 0) {
          choosenDinners.put(item, value);
        } else {
          choosenDinners.remove(item);
        }

        // 🔥 przelicz wszystko od nowa (lepsze niż kombinowanie z total +=) zrobić to samo w Java 7
//        todo spytać chata o wytłumaczenie kodu poniżej czym są są streamy i lambdy i przepisanie bez użycia streamu
        double newTotal = choosenDinners.entrySet().stream()
                .mapToDouble(entry -> entry.getKey().getPrice() * entry.getValue())
                .sum();

        total = newTotal;

        billService.updateBillTextField(total);
        billService.calculateBill();
      });

//      menuPanel.add(checkBox);
      row.add(label);
      row.add(spinner);

      menuPanel.add(row);
    }

    menuPanel.revalidate();
    menuPanel.repaint();
  }

}
