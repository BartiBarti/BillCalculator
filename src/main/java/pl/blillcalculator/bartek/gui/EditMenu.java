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

            // TODO 1 & 3: Walidacja została przeniesiona do serwisu i zbiera wszystkie błędy
            String validationErrors = menuFileLoader.validateMenuContent(content);

            if (!validationErrors.isEmpty()) {
                JOptionPane.showMessageDialog(this,
                        "Znaleziono błędy w menu:\n\n" + validationErrors,
                        "Błąd walidacji",
                        JOptionPane.ERROR_MESSAGE);
                return; // Przerywamy zapis, jeśli są błędy
            }

            menuFileLoader.writeChoosenMenuFile(menuType, menuContentTextArea);
            //  Zostawiliśmy tylko ten jeden komunikat o sukcesie zapisu
            JOptionPane.showMessageDialog(null, "Zapisano pomyślnie! Zmodyfikowano plik Menu: " +
                    menuType.getTitle(), "Sukces", JOptionPane.INFORMATION_MESSAGE);
        });

        cancelButton.addActionListener(e -> dispose());
    }
}
