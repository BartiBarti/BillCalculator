package pl.blillcalculator.bartek.service;

import pl.blillcalculator.bartek.model.MenuItem;

import javax.swing.*;
import java.util.Map;

public class PdfService {


    public void generateReceiptPDF(Map<MenuItem, Integer> choosenDinners, int receiptCounter, double tipPercentage, double total) {
//        todo Dodać importy zamiast wskazywać pełną ścieżkę do klas,
//        todo zastanowić się jak numerować paragony, ponieważ przy obecnym mechanizmie po wyłączeniu aplikacji licznik znowu numeruje od 1
//         1 Dane firmy mają być na paragonie z pliku - tu może być trzymany numer paragonu aktualny kolejny (podpowiedź Mateusza)
//        todo po wyłączeniu programu musza być zapamiętane numery paragonów.
//         2 - paragony numerowane
//         3 drukowanie godziny i daty,
//         4 - paragon jak w sklepie wg wzoru z Google
//         5 -Po generowaniu paragonu od razu go otwierać
//         6 - generować paragony do osobnego folderu receipts oddzielnie za każdy miesiąc
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
            double tipAmount = total * (tipPercentage / 100);
            double finalTotal = total + tipAmount;

            document.add(new com.lowagie.text.Paragraph("------------------------------------------------------------------------", regularFont));
            document.add(new com.lowagie.text.Paragraph(String.format("Suma: %.2f zł", total), regularFont));
            document.add(new com.lowagie.text.Paragraph(String.format("Napiwek (%.0f%%): %.2f zł", tipPercentage, tipAmount), regularFont));
            document.add(new com.lowagie.text.Paragraph("\n", regularFont));
            document.add(new com.lowagie.text.Paragraph(String.format("RAZEM DO ZAPŁATY: %.2f zł", finalTotal), titleFont));

            document.close();

            JOptionPane.showMessageDialog(null, "Wygenerowano " + fileName + "!");

            // <<< NOWOŚĆ: Zwiększamy licznik o 1 przy każdym udanym drukowaniu >>>
            receiptCounter++;

        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(null, "Błąd podczas generowania PDF: " + ex.getMessage(), "Błąd", JOptionPane.ERROR_MESSAGE);
        }
    }


}
