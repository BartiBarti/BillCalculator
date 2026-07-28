package pl.blillcalculator.bartek.gui;

import pl.blillcalculator.bartek.model.MenuItem;
import pl.blillcalculator.bartek.service.PdfService;

import javax.swing.*;
import java.awt.*;
import java.util.Map;

public class MenuSummary extends JFrame {

    private final double tipPercentage;
    private double total = 0;
    private PdfService pdfService = new PdfService();

    public MenuSummary(Map<MenuItem, Integer> choosenDinners, double tipPercentage) {
        this.tipPercentage = tipPercentage;

        initFrame();
        JTextArea orderSummaryTextArea = getOrderSummary(choosenDinners);

        JPanel summaryPanel = getSummaryPanel();
        JPanel buttonPanel = getButtonPanel(choosenDinners);

        JPanel bottomContainer = new JPanel(new BorderLayout());
        bottomContainer.add(summaryPanel, BorderLayout.CENTER);
        bottomContainer.add(buttonPanel, BorderLayout.SOUTH);

        JScrollPane scrollPane = new JScrollPane(orderSummaryTextArea);
        scrollPane.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        add(scrollPane, BorderLayout.CENTER);
        add(bottomContainer, BorderLayout.SOUTH);

    }

    private JPanel getButtonPanel(Map<MenuItem, Integer> choosenDinners) {
        JPanel buttonPanel = new JPanel();

        JButton printReceiptButton = new JButton("Paragon");
        JButton printInvoiceButton = new JButton("Faktura");
        JButton cancelButton = new JButton("Anuluj");

        // KROK 1: Definiujemy wspólny rozmiar dla wszystkich przycisków (szerokość, wysokość)
        // 120 pikseli szerokości i 30 pikseli wysokości idealnie pasuje do okna o szerokości 400
        Dimension buttonSize = new Dimension(120, 30);

        // KROK 2: Ustawiamy preferowany rozmiar dla każdego przycisku z osobna
        printReceiptButton.setPreferredSize(buttonSize);
        printInvoiceButton.setPreferredSize(buttonSize);
        cancelButton.setPreferredSize(buttonSize);

        // Akcja dla paragonu
        printReceiptButton.addActionListener(e -> {
            pdfService.generateReceiptPDF(choosenDinners, tipPercentage, total);
            printReceiptButton.setEnabled(false);
            printInvoiceButton.setEnabled(false);
            cancelButton.setText("Zamknij");
            JOptionPane.showMessageDialog(this,
                    "Transakcja dokonana! Paragon został wykreowany i wydrukowany.",
                    "Status transakcji",
                    JOptionPane.INFORMATION_MESSAGE);

        });

        // Akcja dla faktury
        printInvoiceButton.addActionListener(e -> {
//            todo - implementacja - akcji przycisku analogicznie, jak przy paragonie
        });

        // Akcja dla anulowania
        cancelButton.addActionListener(e -> dispose());

        // Dodawanie komponentów do panelu
        buttonPanel.add(printReceiptButton);
        buttonPanel.add(printInvoiceButton);
        buttonPanel.add(cancelButton);

        return buttonPanel;
    }

    private JPanel getSummaryPanel() {
        double tipAmount = total * (tipPercentage / 100);
        double finalTotal = total + tipAmount;

        JPanel summaryPanel = new JPanel(new GridLayout(3, 1, 5, 5));
        summaryPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        summaryPanel.add(new JLabel(String.format("Suma: %.2f zł", total)));
        summaryPanel.add(new JLabel(String.format("Napiwek (%.0f%%): %.2f zł", tipPercentage, tipAmount)));
        summaryPanel.add(new JLabel(String.format("Razem do zapłaty: %.2f zł", finalTotal)));
        return summaryPanel;
    }

    private JTextArea getOrderSummary(Map<MenuItem, Integer> choosenDinners) {
        StringBuilder itemsText = new StringBuilder();

        for (Map.Entry<MenuItem, Integer> entry : choosenDinners.entrySet()) {
            MenuItem item = entry.getKey();
            int quantity = entry.getValue();
            double itemSum = item.getPrice() * quantity;
            total += itemSum;

            itemsText.append(item.getName())
                    .append(" x").append(quantity)
                    .append(" - ").append(String.format("%.2f", itemSum))
                    .append(" zł\n");
        }

        JTextArea textArea = new JTextArea();
        textArea.setEditable(false);
        textArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
        textArea.setText(itemsText.toString());
        textArea.setMargin(new Insets(10, 10, 10, 10));

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


