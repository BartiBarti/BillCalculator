package pl.blillcalculator.bartek.service;

import com.lowagie.text.Font;
import com.lowagie.text.*;
import com.lowagie.text.pdf.BaseFont;
import com.lowagie.text.pdf.PdfWriter;
import com.lowagie.text.pdf.draw.DottedLineSeparator;
import pl.blillcalculator.bartek.model.MenuItem;
import pl.blillcalculator.bartek.model.MenuType;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.FileOutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

public class PrintMenu {
//      TODO 2 - przenieść generowanie logiki z Print Menu do PdfService
//      TODO 3 - Podzielić metodę generowania Menu na mniejsze prywatne metody

    public void printWholeMenu() {
        try {
            // 1. Utworzenie pliku tymczasowego (skasuje się po zamknięciu aplikacji)
            Path tempFilePath = Files.createTempFile("menu-restauracji-", ".pdf");
            File menuPdfFile = tempFilePath.toFile();
            menuPdfFile.deleteOnExit(); // <-- Flaga zapewniająca kasowanie pliku tymczasowego

            // 2. Utworzenie dokumentu PDF w formacie A4
            Document rootDocument = new Document(PageSize.A4, 40, 40, 40, 40);
            PdfWriter.getInstance(rootDocument, new FileOutputStream(menuPdfFile));
            rootDocument.open();

            // Przygotowanie czcionek z obsługą polskich znaków (wzorowane na paragonie)
            BaseFont baseFont = BaseFont.createFont(BaseFont.COURIER, BaseFont.CP1250, BaseFont.EMBEDDED);
            Font titleFont = new Font(baseFont, 22, Font.BOLD);
            Font sectionFont = new Font(baseFont, 14, Font.BOLD);
            Font regularFont = new Font(baseFont, 12, Font.NORMAL);

            // Główny nagłówek dokumentu

            String restaurantName = PdfService.getRestaurantName();
            Paragraph mainTitle = new Paragraph(restaurantName + "\n", titleFont);
            mainTitle.setAlignment(Element.ALIGN_CENTER);
            rootDocument.add(mainTitle);

            Font subTitleFont = new Font(baseFont, 16, Font.BOLD);
            Paragraph subTitle = new Paragraph("MENU RESTAURACJI\n", subTitleFont);
            subTitle.setAlignment(Element.ALIGN_CENTER);
            rootDocument.add(subTitle);

            MenuFileLoader menuLoader = new MenuFileLoader();

            // 3. Pętla przechodząca przez WSZYSTKIE pliki menu (kategorie) po kolei [1]
            for (MenuType type : MenuType.values()) {
                // Pobieramy listę dań z konkretnego pliku danej kategorii [1]
                List<MenuItem> categoryItems = menuLoader.loadMenuFromFile(type.getFilePath());

                // Sprawdzamy, czy w tej kategorii są jakiekolwiek dostępne pozycje, zanim dodamy nagłówek
                boolean hasAvailableItems = false;
                for (MenuItem item : categoryItems) {
                    if (item.getAvailability() != 0) {
                        hasAvailableItems = true;
                        break;
                    }
                }

                // Jeśli kategoria ma dostępne dania, budujemy dla niej sekcję
                if (hasAvailableItems) {
                    // Dodanie nazwy sekcji (np. SOUPS, MAIN_DISHES)
                    Paragraph categoryHeader = new Paragraph(type.getTitle().toUpperCase() + "\n", sectionFont);
                    categoryHeader.setAlignment(Element.ALIGN_LEFT);
                    rootDocument.add(categoryHeader);
                    rootDocument.add(new Paragraph("-------------", regularFont));

                    DottedLineSeparator dottedLine = new DottedLineSeparator();
                    dottedLine.setGap(4f);
                    dottedLine.setLineWidth(1f);

                    for (MenuItem item : categoryItems) {
                        // Odrzucenie pozycji, które danego dnia się skończyły (mają status 0)
                        if (item.getAvailability() != 0) {
                            String formattedPrice = String.format("%.2f PLN", item.getPrice());

                            Paragraph itemLine = new Paragraph();
                            itemLine.setFont(regularFont);
                            itemLine.add(new Chunk(item.getName() + " ", regularFont));
                            itemLine.add(new Chunk(dottedLine));
                            itemLine.add(new Chunk(" " + formattedPrice, regularFont));
                            itemLine.setSpacingAfter(4f);

                            rootDocument.add(itemLine);
                        }
                    }

                    // Odstęp po całej sekcji
                    Paragraph sectionSpacer = new Paragraph("\n", regularFont);
                    sectionSpacer.setSpacingAfter(5f);
                    rootDocument.add(sectionSpacer);

                }
            }

            rootDocument.close();

            // 4. Automatyczne otwarcie pliku w systemie (od razu, bez dodatkowych okien) [1]
            if (Desktop.isDesktopSupported()) {
                Desktop.getDesktop().open(menuPdfFile);
            } else {
                JOptionPane.showMessageDialog(null, "Wygenerowano menu: " + menuPdfFile.getAbsolutePath());
            }

        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(null, "Błąd podczas generowania menu PDF: " + ex.getMessage(), "Błąd", JOptionPane.ERROR_MESSAGE);
        }
    }
}
