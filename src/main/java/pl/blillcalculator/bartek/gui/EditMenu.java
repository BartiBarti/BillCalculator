package pl.blillcalculator.bartek.gui;

import pl.blillcalculator.bartek.model.MenuType;
import pl.blillcalculator.bartek.service.MenuFileLoader;

import javax.swing.*;
import javax.swing.border.EmptyBorder;

public class EditMenu extends JFrame {
    private JLabel descComboBoxLabel;
    private JComboBox menuComboBox;
    private JTextArea menuContentTextArea;
    private JButton saveButton;
    private JButton cancelButton;
    private JPanel mainPanel;

    private MenuType menuType;

    private MenuFileLoader menuFileLoader = new MenuFileLoader();

    public EditMenu() {
        String choosenMenu = (String) menuComboBox.getSelectedItem();
        this.menuType = MenuType.getByTitle(choosenMenu);
        setTitle("Edytuj Menu: " + menuType.getTitle());
        setSize(400, 400);
        setLocationRelativeTo(null);
        mainPanel.setBorder(new EmptyBorder(10, 10, 10, 10));
        setContentPane(mainPanel);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        menuFileLoader.loadChoosenMenuFile(menuType, menuContentTextArea);
        addListeners();
    }

    private void addListeners() {

        menuComboBox.addActionListener(e -> {
            String choosenMenu = (String) menuComboBox.getSelectedItem();
            this.menuType = MenuType.getByTitle(choosenMenu);

            if (menuType != null) {
                setTitle("Edytuj Menu: " + menuType.getTitle());
                menuFileLoader.loadChoosenMenuFile(menuType, menuContentTextArea);
            }
        });
        saveButton.addActionListener(e -> {
            String content = menuContentTextArea.getText();
            String[] lines = content.split("\\n");
            StringBuilder allErrors = new StringBuilder();
// [a-zA-Z]{3}[0-9]{6}
            for (int i = 0; i < lines.length; i++) {
                String line = lines[i].trim();

                String errorMessage = validateLine(line);
                if (errorMessage != null) {
                    allErrors.append("Linia ").append(i + 1).append(": ").append(errorMessage).append("\n");
                }
            }
            if (allErrors.length() > 0) {
                JOptionPane.showMessageDialog(this,
                        "Znaleziono błędy w menu:\n\n" + allErrors,
                        "Błąd walidacji",
                        JOptionPane.ERROR_MESSAGE);
                return; //---> Przerywamy działanie metody i NIE DOPUSZCZAMY do zapisu!
            }

            menuFileLoader.writeChoosenMenuFile(menuType, menuContentTextArea);
            JOptionPane.showMessageDialog(this, "Zapisano pomyślnie!", "Sukces", JOptionPane.INFORMATION_MESSAGE);
        });

        cancelButton.addActionListener(e -> dispose());
    }
// Todo 1 Logika walidacji Edycji Menu - powinna byc wyciagnięta do serwisu - MenuFileLoader
//     todo 2 - za duzo komunikatów potwierdzających zapis, przy poprawnym ma zostać tylko 1 komunikat
//     todo 3 jezeli jest kilka błędów w jednej linii- prezentować dla niej wszystkie błedy, a nie tylko jeden

    private String validateLine(String line) {
        if(line.isEmpty()){
            return "Podana linia jest pusta. Wymagany format: NazwaDania;Cena;Dostępność";
        }
        String[] parts = line.split(";");

        if (parts.length != 3) {
            return "Niepoprawna struktura. Wymagany format: NazwaDania;Cena;Dostępność";
        }

        String name = parts[0].trim();
        String priceStr = parts[1].trim();
        String availabilityStr = parts[2].trim();


        if (name.isEmpty()) {
            return "Nazwa dania nie może być pusta ani składać się z samych spacji.";
        }

        try {
            double price = Double.parseDouble(priceStr);
            if (price <= 0) {
                return "Cena musi być większa od 0.";
            }
        } catch (NumberFormatException e) {
            return "Cena musi być poprawną liczbą (np. 15.50).";
        }


        if (!availabilityStr.equals("0") && !availabilityStr.equals("1") && !availabilityStr.equals("2")) {
            return "Dostępność musi mieć wartość 0, 1 lub 2.";
        }

        return null; // Linia jest poprawna
    }

}
