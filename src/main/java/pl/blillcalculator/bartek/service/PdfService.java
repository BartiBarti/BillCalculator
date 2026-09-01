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

import static pl.blillcalculator.bartek.service.ReceiptConstant.*;

public class PdfService {


//     Główna metoda - usunęliśmy 'int receiptCounter' z parametrów!
    public boolean generateReceiptPDF(Map<MenuItem, Integer> choosenDinners, double tipPercentage, double total) {

        // 1. Pobranie unikalnego numeru paragonu dla bieżącego miesiąca
        int currentReceiptNumber = getAndUpdateReceiptCounter();

        // 2. Przygotowanie dynamicznej ścieżki do folderu (np. receipts/2026-07) - KROK 6
        File receiptPdfFile = getReceiptPdfFile(currentReceiptNumber);
// TODO - dlaczego w niektórych przypadkach paragon jest rozciągany na dwie strony - poprawić, bo ma byc na jednej stronie w sensie bez przerwy drukowany.
         int calculatedHeight = ReceiptConstant.RECEIPT_BASE_HEIGHT + (choosenDinners.size() * ReceiptConstant.RECEIPT_ITEM_HEIGHT);
        // Zabezpieczenie: minimalna wysokość to 300, żeby krótki paragon nie wyglądał dziwnie
        calculatedHeight = Math.max(calculatedHeight, ReceiptConstant.RECEIPT_MIN_HEIGHT);
        // Ustawienie szerokości na 150 i dynamicznej wysokości
        Document rootDocument = new Document(new Rectangle(ReceiptConstant.RECEIPT_WIDTH, calculatedHeight), 10, 10, 10, 10);

        try {
            PdfWriter.getInstance(rootDocument, new FileOutputStream(receiptPdfFile));
            rootDocument.open();

            // Ustawienie czcionki COURIER z obsługą polskich znaków
            BaseFont baseFont = BaseFont.createFont(BaseFont.COURIER, BaseFont.CP1250, BaseFont.EMBEDDED);
            Font titleFont = new Font(baseFont, 9, Font.BOLD);
            Font regularFont = new Font(baseFont, 7, Font.NORMAL);
            Font totalFont = new Font(baseFont, 11, Font.BOLD);

            addCompanyData(rootDocument, regularFont);
            addReceiptBasicData(currentReceiptNumber, rootDocument, titleFont, regularFont);
            PdfPTable table = addMenuItemData(choosenDinners, regularFont);
            rootDocument.add(table);
            rootDocument.add(new Paragraph(ReceiptConstant.RECEIPT_HORIZONTAL_LINE, regularFont));
            addReceiptSummaryData(tipPercentage, total, rootDocument, regularFont, totalFont);
            addReceiptFooter(currentReceiptNumber, rootDocument, regularFont);
            rootDocument.close();

            openReceipt(receiptPdfFile);

        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(null, "Błąd podczas generowania PDF: " + ex.getMessage(), "Błąd", JOptionPane.ERROR_MESSAGE);
            return false;
        }
        return true;
    }
    private void openReceipt(File receiptPdfFile) throws IOException {
        // KROK 5: Automatyczne otwieranie pliku PDF po wygenerowaniu
        if (Desktop.isDesktopSupported()) {
            Desktop.getDesktop().open(receiptPdfFile);
        } else {
            JOptionPane.showMessageDialog(null, "Wygenerowano: " + receiptPdfFile.getAbsolutePath());
        }
    }
    private void addReceiptFooter(int currentReceiptNumber, Document rootDocument, Font regularFont) {
        // Data i dokładny czas transakcji (sekundy)
        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern(ReceiptConstant.RECEIPT_DATE_PATTERN);
        String formattedDateTime = now.format(timeFormatter);

        Paragraph footerDate = new Paragraph(formattedDateTime, regularFont);
        footerDate.setAlignment(Element.ALIGN_CENTER);
        rootDocument.add(footerDate);

        // Unikalny kod transakcji systemowej na samym dole
        String codeTimestamp = now.format(DateTimeFormatter.ofPattern(ReceiptConstant.RECEIPT_CODE_DATE_PATTERN));
        String transactionCode = String.format("NR-%d-%s", currentReceiptNumber, codeTimestamp);

        Paragraph codeParagraph = new Paragraph(transactionCode, regularFont);
        codeParagraph.setAlignment(Element.ALIGN_CENTER);
        rootDocument.add(codeParagraph);
    }

    private void addReceiptSummaryData(double tipPercentage, double total, Document rootDocument, Font regularFont, Font totalFont) {
        // Obliczenia końcowe i podsumowanie
        double tipAmount = total * (tipPercentage / 100);
        double finalTotal = total + tipAmount;

        PdfPTable summaryTable = new PdfPTable(2);
        summaryTable.setWidthPercentage(100);
        summaryTable.setWidths(new float[]{65, 35});

        addSummaryRow(summaryTable, "Sprzedaż opodatkowana:", String.format(ReceiptConstant.RECEIPT_AMOUNT_ROUNDING, total), regularFont);
        addSummaryRow(summaryTable, String.format("Napiwek (%.0f%%):", tipPercentage), String.format(ReceiptConstant.RECEIPT_AMOUNT_ROUNDING, tipAmount), regularFont);
        rootDocument.add(summaryTable);

        rootDocument.add(new Paragraph(ReceiptConstant.RECEIPT_HORIZONTAL_LINE, regularFont));

        // Sekcja SUMA
        PdfPTable totalTable = new PdfPTable(2);
        totalTable.setWidthPercentage(100);
        totalTable.setWidths(new float[]{50, 50});

        PdfPCell totalLabel = new PdfPCell(new Phrase("SUMA PLN", totalFont));
        totalLabel.setBorder(Rectangle.NO_BORDER);
        totalLabel.setHorizontalAlignment(Element.ALIGN_LEFT);

        PdfPCell totalVal = new PdfPCell(new Phrase(String.format(ReceiptConstant.RECEIPT_AMOUNT_ROUNDING, finalTotal), totalFont));
        totalVal.setBorder(Rectangle.NO_BORDER);
        totalVal.setHorizontalAlignment(Element.ALIGN_RIGHT);

        totalTable.addCell(totalLabel);
        totalTable.addCell(totalVal);
        rootDocument.add(totalTable);

        rootDocument.add(new Paragraph(ReceiptConstant.RECEIPT_HORIZONTAL_LINE, regularFont));
    }

    private PdfPTable addMenuItemData(Map<MenuItem, Integer> choosenDinners, Font regularFont) {
        // Tabela z pozycjami zamówienia
        PdfPTable menuItemTable = new PdfPTable(2);
        menuItemTable.setWidthPercentage(100);
        menuItemTable.setWidths(new float[]{70, 30});

        for (Map.Entry<MenuItem, Integer> entry : choosenDinners.entrySet()) {
            MenuItem menuItem = entry.getKey();
            int quantity = entry.getValue();
            double itemSum = menuItem.getPrice() * quantity;

            String menuItemDetails = String.format("%s\n  %d szt x %.2f", menuItem.getName(), quantity, menuItem.getPrice());
            PdfPCell menuItemDetailsCellLeft = new PdfPCell(new Phrase(menuItemDetails, regularFont));
            menuItemDetailsCellLeft.setBorder(Rectangle.NO_BORDER);
            menuItemDetailsCellLeft.setHorizontalAlignment(Element.ALIGN_LEFT);

            PdfPCell menuItemAmountCellRight = new PdfPCell(new Phrase(String.format(ReceiptConstant.RECEIPT_AMOUNT_ROUNDING, itemSum), regularFont));
            menuItemAmountCellRight.setBorder(Rectangle.NO_BORDER);
            menuItemAmountCellRight.setHorizontalAlignment(Element.ALIGN_RIGHT);
            menuItemAmountCellRight.setVerticalAlignment(Element.ALIGN_BOTTOM);

            menuItemTable.addCell(menuItemDetailsCellLeft);
            menuItemTable.addCell(menuItemAmountCellRight);
        }
        return menuItemTable;
    }

    private void addReceiptBasicData(int currentReceiptNumber, Document rootDocument, Font titleFont, Font regularFont) {
        // Tytuł dokumentu
        Paragraph docType = new Paragraph("PARAGON FISKALNY\n", titleFont);
        docType.setAlignment(Element.ALIGN_CENTER);
        rootDocument.add(docType);

        Paragraph docNum = new Paragraph("Numer dokumentu: " + currentReceiptNumber + "\n", regularFont);
        docNum.setAlignment(Element.ALIGN_LEFT);
        rootDocument.add(docNum);

        rootDocument.add(new Paragraph(ReceiptConstant.RECEIPT_HORIZONTAL_LINE, regularFont));
    }

    private File getReceiptPdfFile(int currentReceiptNumber) {
        String currentMonthFolder = LocalDate.now().format(DateTimeFormatter.ofPattern(ReceiptConstant.RECEIPT_FOLDER_PATTERN));
        File directory = new File(ReceiptConstant.RECEIPTS_ROOTNAME_FOLDER + currentMonthFolder);
        if (!directory.exists()) {
            directory.mkdirs(); // Tworzy foldery, jeśli nie istnieją
        }
        String receiptFileName = ReceiptConstant.RECEIPT_FILE_NAME + currentReceiptNumber + ReceiptConstant.RECEIPT_FILE_EXTENSION;
        File receiptPdfFile = new File(directory, receiptFileName);
        return receiptPdfFile;
    }

    // Pomocnicza metoda do zapisu licznika i sprawdzania nowego miesiąca
    private int getAndUpdateReceiptCounter() {
        Properties props = new Properties();
        int counter = 1;
        String currentYearAndMonth = LocalDate.now().format(DateTimeFormatter.ofPattern(ReceiptConstant.RECEIPT_FOLDER_PATTERN));
        String savedYearAndMonth = "";

        if (new File(ReceiptConstant.CONFIG_FILE).exists()) {
            try (InputStream input = new FileInputStream(ReceiptConstant.CONFIG_FILE)) {
                props.load(input);
                savedYearAndMonth = props.getProperty(ReceiptConstant.LAST_MONTH_CONFIG_KEY, "");
                if (currentYearAndMonth.equals(savedYearAndMonth)) {
                    counter = Integer.parseInt(props.getProperty(ReceiptConstant.COUNTER_CONFIG_KEY, "1"));
                }
            } catch (IOException | NumberFormatException e) {
                e.printStackTrace();
            }
        }

        try (OutputStream output = new FileOutputStream(ReceiptConstant.CONFIG_FILE)) {
            props.setProperty(ReceiptConstant.COUNTER_CONFIG_KEY, String.valueOf(counter + 1));
            if (!savedYearAndMonth.equals(currentYearAndMonth)) {
                props.setProperty(ReceiptConstant.LAST_MONTH_CONFIG_KEY, currentYearAndMonth);
            }
            props.store(output, null);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return counter;
    }
    private void addCompanyData(Document rootDocument, Font regularFont) throws DocumentException {
        Properties properties = new Properties();
        if (new File(ReceiptConstant.CONFIG_FILE).exists()) {
            try (InputStream input = new FileInputStream(ReceiptConstant.CONFIG_FILE)) {
                properties.load(input);
                String name = properties.getProperty(COMPANY_NAME_CONFIG_KEY, null);
                String street = properties.getProperty(COMPANY_STREET_CONFIG_KEY, null);
                String city = properties.getProperty(COMPANY_CITY_CONFIG_KEY, null);
                String nip = properties.getProperty(COMPANY_NIP_CONFIG_KEY, null);
                String regon = properties.getProperty(COMPANY_REGON_CONFIG_KEY, null);
                if (name == null || street == null || city == null || nip == null || regon == null) {
                    JOptionPane.showMessageDialog(null, "Błąd generowania paragonu, brak danych firmy.",
                                                                            "Błąd!", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                Paragraph companyParagraph = new Paragraph();
                companyParagraph.setAlignment(Element.ALIGN_CENTER);
                companyParagraph.add(new Chunk(name + "\n", regularFont));
                companyParagraph.add(new Chunk(street + "\n", regularFont));
                companyParagraph.add(new Chunk(city + "\n", regularFont));
                companyParagraph.add(new Chunk(nip + "\n", regularFont));
                companyParagraph.add(new Chunk(regon + "\n", regularFont));
                rootDocument.add(companyParagraph);

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
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
