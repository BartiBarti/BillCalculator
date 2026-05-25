package pl.blillcalculator.bartek.gui;

import javax.swing.*;
import java.awt.*;
import java.util.Map;

public class MenuSummary extends JFrame {

    private double total = 0;

    public MenuSummary(Map<pl.blillcalculator.bartek.model.MenuItem, Integer> choosenDinners) {

        initFrame();
        JTextArea orderSummaryTextArea = getOrderSummary(choosenDinners);
        JPanel summaryPanel = getSummaryPanel();
        JPanel buttonPanel = getButtonPanel();

        JPanel bottomContainer = new JPanel(new BorderLayout());
        bottomContainer.add(summaryPanel, BorderLayout.CENTER);
        bottomContainer.add(buttonPanel, BorderLayout.SOUTH);

        add(new JScrollPane(orderSummaryTextArea), BorderLayout.CENTER);
        add(bottomContainer, BorderLayout.SOUTH);
    }

    private JPanel getButtonPanel() {
//         todo mają byc przyciski - "Drukuj paragon" "Drukuj fakturę" "Anuluj"
        JPanel buttonPanel = new JPanel();
        JButton printReceiptButton = new JButton("Potwierdź zamówienie");
//        print invoice button
        JButton cancelButton = new JButton("Anuluj");

        printReceiptButton.addActionListener(e -> {
            // logika generująca paragon do pliku PDF

        });

        cancelButton.addActionListener(e -> dispose());

        buttonPanel.add(printReceiptButton);
        buttonPanel.add(cancelButton);
        return buttonPanel;
    }

    private JPanel getSummaryPanel() {
        // TODO: Wartość napiwku musi być przekazywana przez parametr do konstruktora z ComboBoxa nie na stałe 10%
        double tipPercentage = 10.0; // Przykładowe 10% napiwku
        double tipAmount = total * (tipPercentage / 100);
        double finalTotal = total + tipAmount;

        JPanel summaryPanel = new JPanel(new GridLayout(3, 1, 5, 5));
        summaryPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        summaryPanel.add(new JLabel(String.format("Suma: %.2f zł", total)));
        summaryPanel.add(new JLabel(String.format("Napiwek (%.0f%%): %.2f zł", tipPercentage, tipAmount)));
        summaryPanel.add(new JLabel(String.format("Razem do zapłaty: %.2f zł", finalTotal)));
        return summaryPanel;
    }

    private JTextArea getOrderSummary(Map<pl.blillcalculator.bartek.model.MenuItem, Integer> choosenDinners) {
        StringBuilder itemsText = new StringBuilder();

        for (Map.Entry<pl.blillcalculator.bartek.model.MenuItem, Integer> entry : choosenDinners.entrySet()) {
            pl.blillcalculator.bartek.model.MenuItem item = entry.getKey();
            int quantity = entry.getValue();
            double itemSum = item.getPrice() * quantity;
            total += itemSum;

            itemsText.append(item.getName())
                    .append(" x").append(quantity)
                    .append(" - ").append(String.format("%.2f", itemSum))
                    .append(" zł\n");
        }
// todo dodać marginesy po 10 px po każdej stronie textArea
        JTextArea textArea = new JTextArea();
        textArea.setEditable(false);
        textArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
        textArea.setText(itemsText.toString());

        return textArea;
    }

    private void initFrame() {
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setSize(400, 450);
        setLocationRelativeTo(null);
        setTitle("Podsumowanie zamówienia");
        setLayout(new BorderLayout(10, 10));
    }
}

//}

