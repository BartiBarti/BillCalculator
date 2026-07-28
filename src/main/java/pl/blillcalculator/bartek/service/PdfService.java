package pl.blillcalculator.bartek.service;

import com.lowagie.text.Font;
import com.lowagie.text.Rectangle;
import com.lowagie.text.*;
import com.lowagie.text.pdf.BaseFont;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import pl.blillcalculator.bartek.model.MenuItem;

import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;
import java.util.Properties;


//         todo 1 Dane firmy mają być na paragonie z pliku - receipt_config.properties
//          todo 2 - długi paragon musi się mieścić na jednej stronie
//          todo 3 wyciągnąć powtarzające się stringi do stałych finalnych, nazwę folderu, oraz nazwę pliku z paragonem
//          todo 4 podzielić metodę generateReceiptPdf na prywatne metody (krótsze)
//          todo 5 - sprawdzić, czy nazwy zmiennych faktycznie odpowiadają temu co przechowują

public class PdfService {

    private static final String CONFIG_FILE = "src/main/resources/receipt_config.properties";

    // Główna metoda - usunęliśmy 'int receiptCounter' z parametrów!
    public void generateReceiptPDF(Map<MenuItem, Integer> choosenDinners, double tipPercentage, double total) {

        // 1. Pobranie unikalnego numeru paragonu dla bieżącego miesiąca
        int currentReceiptNumber = getAndUpdateReceiptCounter();

        // 2. Przygotowanie dynamicznej ścieżki do folderu (np. receipts/2026-07) - KROK 6
        String currentMonthFolder = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM"));
        File directory = new File("receipts/" + currentMonthFolder);
        if (!directory.exists()) {
            directory.mkdirs(); // Tworzy foldery, jeśli nie istnieją
        }

        String receiptFileName = "paragon_" + currentReceiptNumber + ".pdf";
        File receiptPdfFile = new File(directory, receiptFileName);

        // Ustawienie wąskiego formatu rolki sklepowej
        Document document = new Document(new Rectangle(150, 550), 10, 10, 10, 10);
//        todo to 550 ma być dynamiczne ( dynamiczne określenie długości paragonu w zależności od ilości zamówień)
//        odnosi sie do drugiego todo

        try {
            PdfWriter.getInstance(document, new FileOutputStream(receiptPdfFile));
            document.open();

            // Ustawienie czcionki COURIER z obsługą polskich znaków
            BaseFont baseFont = BaseFont.createFont(BaseFont.COURIER, BaseFont.CP1250, BaseFont.EMBEDDED);
            Font companyFont = new Font(baseFont, 7, Font.NORMAL);
            Font titleFont = new Font(baseFont, 9, Font.BOLD);
            Font regularFont = new Font(baseFont, 7, Font.NORMAL);
            Font totalFont = new Font(baseFont, 11, Font.BOLD);

            // Dane firmy
            Paragraph header = new Paragraph();
            header.setAlignment(Element.ALIGN_CENTER);
            header.add(new Chunk("Bar Mateusz & Bartek\n", companyFont));
            header.add(new Chunk("Komputerowa 5 version 4.0\n", companyFont));
            header.add(new Chunk("95-100 Zgierz\n", companyFont));
            header.add(new Chunk("NIP 1234567890\n", companyFont));
            header.add(new Chunk("REGON 987654321\n\n", companyFont));
            document.add(header);

            // Tytuł dokumentu
            Paragraph docType = new Paragraph("PARAGON FISKALNY\n", titleFont);
            docType.setAlignment(Element.ALIGN_CENTER);
            document.add(docType);

            Paragraph docNum = new Paragraph("Numer dokumentu: " + currentReceiptNumber + "\n", regularFont);
            docNum.setAlignment(Element.ALIGN_LEFT);
            document.add(docNum);

            document.add(new Paragraph("- - - - - - - - - - - - - - - - - -", regularFont));

            // Tabela z pozycjami zamówienia
            PdfPTable table = new PdfPTable(2);
            table.setWidthPercentage(100);
            table.setWidths(new float[]{70, 30});

            for (Map.Entry<MenuItem, Integer> entry : choosenDinners.entrySet()) {
                MenuItem item = entry.getKey();
                int quantity = entry.getValue();
                double itemSum = item.getPrice() * quantity;

                String itemDetails = String.format("%s\n  %d szt x %.2f", item.getName(), quantity, item.getPrice());
                PdfPCell cellLeft = new PdfPCell(new Phrase(itemDetails, regularFont));
                cellLeft.setBorder(Rectangle.NO_BORDER);
                cellLeft.setHorizontalAlignment(Element.ALIGN_LEFT);

                PdfPCell cellRight = new PdfPCell(new Phrase(String.format("%.2f", itemSum), regularFont));
                cellRight.setBorder(Rectangle.NO_BORDER);
                cellRight.setHorizontalAlignment(Element.ALIGN_RIGHT);
                cellRight.setVerticalAlignment(Element.ALIGN_BOTTOM);

                table.addCell(cellLeft);
                table.addCell(cellRight);
            }
            document.add(table);

            document.add(new Paragraph("- - - - - - - - - - - - - - - - - -", regularFont));

            // Obliczenia końcowe i podsumowanie
            double tipAmount = total * (tipPercentage / 100);
            double finalTotal = total + tipAmount;

            PdfPTable summaryTable = new PdfPTable(2);
            summaryTable.setWidthPercentage(100);
            summaryTable.setWidths(new float[]{65, 35});

            addSummaryRow(summaryTable, "Sprzedaż opodatkowana:", String.format("%.2f", total), regularFont);
            addSummaryRow(summaryTable, String.format("Napiwek (%.0f%%):", tipPercentage), String.format("%.2f", tipAmount), regularFont);
            document.add(summaryTable);

            document.add(new Paragraph("- - - - - - - - - - - - - - - - - -", regularFont));

            // Sekcja SUMA
            PdfPTable totalTable = new PdfPTable(2);
            totalTable.setWidthPercentage(100);
            totalTable.setWidths(new float[]{50, 50});

            PdfPCell totalLabel = new PdfPCell(new Phrase("SUMA PLN", totalFont));
            totalLabel.setBorder(Rectangle.NO_BORDER);
            totalLabel.setHorizontalAlignment(Element.ALIGN_LEFT);

            PdfPCell totalVal = new PdfPCell(new Phrase(String.format("%.2f", finalTotal), totalFont));
            totalVal.setBorder(Rectangle.NO_BORDER);
            totalVal.setHorizontalAlignment(Element.ALIGN_RIGHT);

            totalTable.addCell(totalLabel);
            totalTable.addCell(totalVal);
            document.add(totalTable);

            document.add(new Paragraph("- - - - - - - - - - - - - - - - - -", regularFont));

            // Data i dokładny czas transakcji (sekundy)
            LocalDateTime now = LocalDateTime.now();
            DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            String formattedDateTime = now.format(timeFormatter);

            Paragraph footerDate = new Paragraph(formattedDateTime, regularFont);
            footerDate.setAlignment(Element.ALIGN_CENTER);
            document.add(footerDate);

            // Unikalny kod transakcji systemowej na samym dole
            String codeTimestamp = now.format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
            String transactionCode = String.format("NR-%d-%s", currentReceiptNumber, codeTimestamp);

            Paragraph codeParagraph = new Paragraph(transactionCode, regularFont);
            codeParagraph.setAlignment(Element.ALIGN_CENTER);
            document.add(codeParagraph);

            document.close();

            // KROK 5: Automatyczne otwieranie pliku PDF po wygenerowaniu
            if (Desktop.isDesktopSupported()) {
                Desktop.getDesktop().open(receiptPdfFile);
            } else {
                JOptionPane.showMessageDialog(null, "Wygenerowano: " + receiptPdfFile.getAbsolutePath());
            }

        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(null, "Błąd podczas generowania PDF: " + ex.getMessage(), "Błąd", JOptionPane.ERROR_MESSAGE);
        }
    }

    // Pomocnicza metoda do zapisu licznika i sprawdzania nowego miesiąca
    private int getAndUpdateReceiptCounter() {
        Properties props = new Properties();
        int counter = 1;
        String currentYearAndMonth = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM"));
        String savedYearAndMonth = "";

        if (new File(CONFIG_FILE).exists()) {
            try (InputStream input = new FileInputStream(CONFIG_FILE)) {
                props.load(input);
                savedYearAndMonth = props.getProperty("lastMonth", "");
                if (currentYearAndMonth.equals(savedYearAndMonth)) {
                    counter = Integer.parseInt(props.getProperty("counter", "1"));
                }
            } catch (IOException | NumberFormatException e) {
                e.printStackTrace();
            }
        }

        try (OutputStream output = new FileOutputStream(CONFIG_FILE)) {
            props.setProperty("counter", String.valueOf(counter + 1));
            if (!savedYearAndMonth.equals(currentYearAndMonth)) {
                props.setProperty("lastMonth", currentYearAndMonth);
            }
            props.store(output, null);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return counter;
    }

    private void addSummaryRow(PdfPTable table, String label, String value, Font font) {
        PdfPCell cellLbl = new PdfPCell(new Phrase(label, font));
        cellLbl.setBorder(Rectangle.NO_BORDER);
        cellLbl.setHorizontalAlignment(Element.ALIGN_LEFT);

        PdfPCell cellVal = new PdfPCell(new Phrase(value, font));
        cellVal.setBorder(Rectangle.NO_BORDER);
        cellVal.setHorizontalAlignment(Element.ALIGN_RIGHT);

        table.addCell(cellLbl);
        table.addCell(cellVal);
    }
}

