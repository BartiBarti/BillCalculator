package pl.blillcalculator.bartek.gui;

import pl.blillcalculator.bartek.model.MenuItem;
import pl.blillcalculator.bartek.model.MenuType;
import pl.blillcalculator.bartek.service.BillService;
import pl.blillcalculator.bartek.service.MenuFileLoader;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.List;
import java.util.Map;

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
        setSize(350, 200);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        menuPanel.setBorder(new EmptyBorder(10, 10, 10, 10));
        menuPanel.setLayout(new BoxLayout(menuPanel, BoxLayout.Y_AXIS));
        setContentPane(menuPanel);
    }

    private void generateMenu(List<MenuItem> items) {

        menuPanel.removeAll();

        for (MenuItem item : items) {

            JPanel row = new JPanel(new GridBagLayout());
            GridBagConstraints gbc = new GridBagConstraints();
            Color defaultColor = row.getBackground();


            row.addMouseListener(new java.awt.event.MouseAdapter() {
                @Override
                public void mouseEntered(java.awt.event.MouseEvent e) {
                    row.setBackground(new Color(255, 245, 180));
                    row.setBorder(BorderFactory.createLineBorder(Color.GRAY));

                }

                @Override
                public void mouseExited(java.awt.event.MouseEvent e) {
                    row.setBackground(defaultColor);
                    row.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
                }
            });
            row.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

            gbc.insets = new Insets(2, 5, 2, 5);
            gbc.fill = GridBagConstraints.HORIZONTAL;
            row.setAlignmentX(Component.LEFT_ALIGNMENT);
            JLabel label = new JLabel("<html>" + item.getName() + "</html>");
            label.setPreferredSize(new Dimension(200, 40));
            gbc.gridx = 0;
            gbc.weightx = 0.6;
            row.add(label, gbc);

            JLabel priceLabel = new JLabel(item.getPrice() + " zł");
            priceLabel.setHorizontalAlignment(SwingConstants.CENTER);
            gbc.gridx = 1;
            gbc.weightx = 0.2;
            row.add(priceLabel, gbc);

            int currentCount = choosenDinners.getOrDefault(item, 0);

            JSpinner spinner = new JSpinner(new SpinnerNumberModel(currentCount, 0, 10, 1));
            Dimension spinnerSize = new Dimension(60, 25);
            spinner.setPreferredSize(spinnerSize);
            gbc.gridx = 2;
            gbc.weightx = 0.2;
            row.add(spinner, gbc);

// todo zapytać CHATA po co te linijki poniżej
//      label.addMouseListener(row.getMouseListeners()[0]);
//      priceLabel.addMouseListener(row.getMouseListeners()[0]);
//      spinner.addMouseListener(row.getMouseListeners()[0]);

// 🔥
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

            menuPanel.add(row);
        }

        menuPanel.revalidate();
        menuPanel.repaint();
    }

}
