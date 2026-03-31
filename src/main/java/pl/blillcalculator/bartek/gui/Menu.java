package pl.blillcalculator.bartek.gui;

import java.util.List;
import java.util.Map;
import javax.swing.BoxLayout;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JPanel;
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

      JCheckBox checkBox = new JCheckBox(item.getName() + " - " + item.getPrice() + " zł");
      final Integer menuItemCount = choosenDinners.get(item) != null ? choosenDinners.get(item) : 0;

      if (menuItemCount == 1) {
        checkBox.setSelected(true);
      }

      checkBox.addItemListener(e -> {
        if (checkBox.isSelected()) {
          total += item.getPrice();
          choosenDinners.put(item, menuItemCount + 1);
        } else {
          total -= item.getPrice();
          choosenDinners.put(item, menuItemCount - 1);
        }

        billService.updateBillTextField(total);
        billService.calculateBill();
      });

      menuPanel.add(checkBox);
    }

    menuPanel.revalidate();
    menuPanel.repaint();
  }

}
