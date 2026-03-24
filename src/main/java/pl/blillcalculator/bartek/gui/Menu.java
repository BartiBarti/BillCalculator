package pl.blillcalculator.bartek.gui;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Menu extends JFrame {
    private JPanel menuPanel;

    private JTextField amountField;

    private double total;

    private Map<MenuItem, Integer> choosenDinners;

    public Menu(JTextField amountField, String filePath, BillCalculator billCalculator, String title, Map<MenuItem, Integer> choosenDinners) {
        setTitle(title);
        setSize(300, 200);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        menuPanel.setBorder(new EmptyBorder(10, 10, 10, 10));
        this.amountField = amountField;
        this.total = !"".equals(amountField.getText()) ? Double.parseDouble(amountField.getText()) : 0.0;
        this.choosenDinners = choosenDinners;
        menuPanel.setLayout(new BoxLayout(menuPanel, BoxLayout.Y_AXIS));
        setContentPane(menuPanel);

        List<MenuItem> menuItems = loadMenuFromFile(filePath);
        generateMenu(menuItems, billCalculator);
    }


    private void generateMenu(List<MenuItem> items, BillCalculator billCalculator) {

        menuPanel.removeAll();

        for (MenuItem item : items) {

            JCheckBox checkBox = new JCheckBox(
                    item.getName() + " - " + item.getPrice() + " zł"
            );
            final Integer menuItemCount = choosenDinners.get(item) != null ? choosenDinners.get(item) : 0;
            if (menuItemCount == 1){
                checkBox.setSelected(true);
            }
            checkBox.addItemListener(e -> {
                if (checkBox.isSelected()) {
                    total += item.getPrice();
                    choosenDinners.put(item, menuItemCount+1);
                } else {
                    total -= item.getPrice();
                    choosenDinners.put(item, menuItemCount-1);
                }
                String amount = String.format("%.2f", total).replaceFirst(",", ".");
                amountField.setText(amount);
                billCalculator.calculateBill();
            });

            menuPanel.add(checkBox);
        }

        menuPanel.revalidate();
        menuPanel.repaint();
    }

    private List<MenuItem> loadMenuFromFile(String filePath) {
        List<MenuItem> items = new ArrayList<>();

        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.split(";");
                String name = parts[0];
                double price = Double.parseDouble(parts[1]);
                items.add(new MenuItem(name, price));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return items;
    }


}
