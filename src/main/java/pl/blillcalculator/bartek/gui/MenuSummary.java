package pl.blillcalculator.bartek.gui;

import javax.swing.*;
import java.awt.*;
import java.util.Map;

public class MenuSummary extends JFrame {

    private double total = 0;
    private final double tipPercentage;
    private static int receiptCounter = 1;

    public MenuSummary(Map<pl.blillcalculator.bartek.model.MenuItem, Integer> choosenDinners, double tipPercentage) {
        this.tipPercentage = tipPercentage;

        initFrame();
        JTextArea orderSummaryTextArea = getOrderSummary(choosenDinners);

        JPanel summaryPanel = getSummaryPanel();
        JPanel buttonPanel = getButtonPanel(choosenDinners);

        JPanel bottomContainer = new JPanel(new BorderLayout());
        bottomContainer.add(summaryPanel, BorderLayout.CENTER);
        bottomContainer.add(buttonPanel, BorderLayout.SOUTH);

        add(new JScrollPane(orderSummaryTextArea), BorderLayout.CENTER);
        add(bottomContainer, BorderLayout.SOUTH);
    }

    private JPanel getButtonPanel(Map<pl.blillcalculator.bartek.model.MenuItem, Integer> choosenDinners) {
        JPanel buttonPanel = new JPanel();

        JButton printReceiptButton = new JButton("Drukuj paragon");
        JButton printInvoiceButton = new JButton("Drukuj fakturę");
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
            System.out.println("Generowanie paragonu PDF...");
            generateReceiptPDF(choosenDinners);
        });

        // Akcja dla faktury
        printInvoiceButton.addActionListener(e -> {
            System.out.println("Generowanie faktury PDF...");
        });

        // Akcja dla anulowania
        cancelButton.addActionListener(e -> dispose());

        // Dodawanie komponentów do panelu
        buttonPanel.add(printReceiptButton);
        buttonPanel.add(printInvoiceButton);
        buttonPanel.add(cancelButton);

        return buttonPanel;
    }

    private void generateReceiptPDF(Map<pl.blillcalculator.bartek.model.MenuItem, Integer> choosenDinners) {
//        todo Dodać importy zamiast wskazywać pełną ścieżkę do klas
//         tą logikę wywalić do serwisu - stworzyć klasę PDFSerwis i tu będzie logika do generowania paragonu i faktury
//         1 Dane firmy mają być na paragonie z pliku
//         2 - paragony numerowane
//         3 drukowanie godziny,
//         4 - paragon jak w sklepie wg wzoru z Google
//         5 -Po generowaniu paragonu od razu go otwierać
//         6 - generować paragony do osobnego folderu
        com.lowagie.text.Document document = new com.lowagie.text.Document();
        try {
            // dynamiczna nazwa pliku, np. paragon_1.pdf, paragon_2.pdf, żeby nie nadpisywać starego
            String fileName = "paragon_" + receiptCounter + ".pdf";
            com.lowagie.text.pdf.PdfWriter.getInstance(document, new java.io.FileOutputStream(fileName));
            document.open();

            com.lowagie.text.pdf.BaseFont baseFont = com.lowagie.text.pdf.BaseFont.createFont(
                    com.lowagie.text.pdf.BaseFont.HELVETICA,
                    com.lowagie.text.pdf.BaseFont.CP1250,
                    com.lowagie.text.pdf.BaseFont.EMBEDDED
            );
            com.lowagie.text.Font titleFont = new com.lowagie.text.Font(baseFont, 16, com.lowagie.text.Font.BOLD);
            com.lowagie.text.Font regularFont = new com.lowagie.text.Font(baseFont, 12, com.lowagie.text.Font.NORMAL);

            // <<< NOWOŚĆ: Pobieranie i formatowanie aktualnej daty >>>
            java.time.LocalDate today = java.time.LocalDate.now();
            java.time.format.DateTimeFormatter formatter = java.time.format.DateTimeFormatter.ofPattern("dd.MM.yyyy");
            String formattedDate = today.format(formatter);

            // Nagłówek paragonu z numerem i datą
            com.lowagie.text.Paragraph title = new com.lowagie.text.Paragraph("PARAGON FISKALNY\n", titleFont);
            title.setAlignment(com.lowagie.text.Element.ALIGN_CENTER);
            document.add(title);

            // <<< NOWOŚĆ: Wyświetlenie numeru paragonu oraz daty >>>
            com.lowagie.text.Paragraph details = new com.lowagie.text.Paragraph(
                    String.format("Paragon nr: %d\nData transakcji: %s\n", receiptCounter, formattedDate), regularFont);
            details.setAlignment(com.lowagie.text.Element.ALIGN_CENTER);
            document.add(details);

            document.add(new com.lowagie.text.Paragraph("------------------------------------------------------------------------", regularFont));

            // Lista pozycji z zamówienia
            for (Map.Entry<pl.blillcalculator.bartek.model.MenuItem, Integer> entry : choosenDinners.entrySet()) {
                pl.blillcalculator.bartek.model.MenuItem item = entry.getKey();
                int quantity = entry.getValue();
                double itemSum = item.getPrice() * quantity;

                String line = String.format("%s x%d - %.2f zł", item.getName(), quantity, itemSum);
                document.add(new com.lowagie.text.Paragraph(line, regularFont));
            }

            // Obliczenia końcowe
            double tipAmount = total * (this.tipPercentage / 100);
            double finalTotal = total + tipAmount;

            document.add(new com.lowagie.text.Paragraph("------------------------------------------------------------------------", regularFont));
            document.add(new com.lowagie.text.Paragraph(String.format("Suma: %.2f zł", total), regularFont));
            document.add(new com.lowagie.text.Paragraph(String.format("Napiwek (%.0f%%): %.2f zł", tipPercentage, tipAmount), regularFont));
            document.add(new com.lowagie.text.Paragraph("\n", regularFont));
            document.add(new com.lowagie.text.Paragraph(String.format("RAZEM DO ZAPŁATY: %.2f zł", finalTotal), titleFont));

            document.close();

            JOptionPane.showMessageDialog(this, "Wygenerowano " + fileName + "!");

            // <<< NOWOŚĆ: Zwiększamy licznik o 1 przy każdym udanym drukowaniu >>>
            receiptCounter++;

        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Błąd podczas generowania PDF: " + ex.getMessage(), "Błąd", JOptionPane.ERROR_MESSAGE);
        }
    }

//    private JPanel getButtonPanel() {
//        JPanel buttonPanel = new JPanel();
//
//        // TODO 1- zmienić szerokość przycisków, aby wypełniały całe okno i wszystkie miały taka sama szerokość (setPreferedSize)
//        JButton printReceiptButton = new JButton("Drukuj paragon");
//        JButton printInvoiceButton = new JButton("Drukuj fakturę");
//        JButton cancelButton = new JButton("Anuluj");
//
//        // Akcja dla paragonu
//        printReceiptButton.addActionListener(e -> {
//            // TODO: Logika generująca paragon do pliku PDF
//            System.out.println("Generowanie paragonu PDF...");
//        });
//
//        // Akcja dla faktury
//        printInvoiceButton.addActionListener(e -> {
//            // TODO: Logika generująca fakturę do pliku PDF
//            System.out.println("Generowanie faktury PDF...");
//        });
//
//        // Akcja dla anulowania
//        cancelButton.addActionListener(e -> dispose());
//
//        // Dodawanie komponentów do panelu
//        buttonPanel.add(printReceiptButton);
//        buttonPanel.add(printInvoiceButton);
//        buttonPanel.add(cancelButton);
//
//        return buttonPanel;
//    }

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
// todo 3 dodać marginesy po 10 px po każdej stronie textArea
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

