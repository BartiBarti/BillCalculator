package pl.blillcalculator.bartek.service;

import pl.blillcalculator.bartek.model.MenuItem;
import pl.blillcalculator.bartek.model.MenuType;

import javax.swing.*;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class MenuFileLoader {

    public List<MenuItem> loadMenuFromFile(String filePath) {
        List<MenuItem> items = new ArrayList<>();

        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.split(";");
                String name = parts[0];
                double price = Double.parseDouble(parts[1]);
                int availability = Integer.parseInt(parts[2]);
                MenuItem item = new MenuItem(name, price, availability);
                items.add(item);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return items;
    }

    public void writeChoosenMenuFile(MenuType menuType, JTextArea menuContentTextArea) {
        try {
            Files.writeString(
                    Paths.get(menuType.getFilePath()),
                    menuContentTextArea.getText()
            );

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void loadChoosenMenuFile(MenuType menuType, JTextArea menuContentTextArea) {
        try {
            String content = java.nio.file.Files.readString(
                    java.nio.file.Paths.get(menuType.getFilePath())
            );
            menuContentTextArea.setText(content);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Nowa metoda serwisu odpowiedzialna za pełną walidację tekstu z pola edycji
    public String validateMenuContent(String content) {
        String[] lines = content.split("\\n");
        StringBuilder allErrors = new StringBuilder();

        for (int i = 0; i < lines.length; i++) {
            String line = lines[i].trim();
            List<String> lineErrors = new ArrayList<>();

            if (line.isEmpty()) {
                allErrors.append("Linia ").append(i + 1).append(": Podana linia jest pusta. Wymagany format: NazwaDania;Cena;Dostępność\n");
                continue;
            }

            String[] parts = line.split(";");
            if (parts.length != 3) {
                allErrors.append("Linia ").append(i + 1).append(": Niepoprawna struktura. Wymagany format: NazwaDania;Cena;Dostępność\n");
                continue;
            }

            String name = parts[0].trim();
            String priceStr = parts[1].trim();
            String availabilityStr = parts[2].trim();

            //  Zamiast przerywać na pierwszym błędzie, sprawdzamy wszystkie warunki dla danej linii
            if (name.isEmpty()) {
                lineErrors.add("nazwa dania nie może być pusta");
            }

            try {
                double price = Double.parseDouble(priceStr);
                if (price <= 0) {
                    lineErrors.add("cena musi być większa od 0");
                }
            } catch (NumberFormatException e) {
                lineErrors.add("cena musi być poprawną liczbą (np. 15.50)");
            }

            if (!availabilityStr.equals("0") && !availabilityStr.equals("1") && !availabilityStr.equals("2")) {
                lineErrors.add("dostępność musi mieć wartość 0, 1 lub 2");
            }

            // Jeśli ta linia miała jakieś błędy, łączymy je w jeden ładny komunikat
            if (!lineErrors.isEmpty()) {
                allErrors.append("Linia ").append(i + 1).append(": ")
                        .append(String.join(", ", lineErrors))
                        .append(".\n");
            }
        }

        return allErrors.toString();
    }
}

