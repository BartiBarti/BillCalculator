package pl.blillcalculator.bartek.gui;

import pl.blillcalculator.bartek.model.MenuType;
import pl.blillcalculator.bartek.service.MenuFileLoader;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class EditMenu extends JFrame {
    private JLabel descComboBoxLabel;
    private JComboBox menuComboBox;
    private JTextArea menuContentTextArea;
    private JButton saveButton;
    private JButton cancelButton;
    private JPanel mainPanel;

    private MenuType menuType;
    private MenuFileLoader menuFileLoader = new MenuFileLoader();

    // NOWOŚĆ: Flaga informująca, czy w tekście są niezapisane zmiany
    private boolean isChanged = false;

    public EditMenu() {
        String choosenMenu = (String) menuComboBox.getSelectedItem();
        this.menuType = MenuType.getByTitle(choosenMenu);
        setTitle("Edytuj Menu: " + menuType.getTitle());
        setSize(400, 400);
        setLocationRelativeTo(null);
        mainPanel.setBorder(new EmptyBorder(10, 10, 10, 10));
        setContentPane(mainPanel);

        // ZMIANA: Blokujemy automatyczne zamykanie okna krzyżykiem,
        // aby nasz WindowListener mógł przejąć kontrolę i wyświetlić komunikat.
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);

        menuFileLoader.loadChoosenMenuFile(menuType, menuContentTextArea);
        addListeners();
    }

    private void addListeners() {
        // NOWOŚĆ: Nasłuchiwanie zmian tekstu w JTextArea
        menuContentTextArea.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                isChanged = true;
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                isChanged = true;
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                isChanged = true;
            }
        });

        // ZMIANA: Obsługa przełączania menu w ComboBoxie
        menuComboBox.addActionListener(e -> {
            String choosenMenu = (String) menuComboBox.getSelectedItem();
            MenuType newMenuType = MenuType.getByTitle(choosenMenu);

            if (newMenuType != null) {
                // Jeśli typ menu się nie zmienił, nic nie robimy
                if (newMenuType == this.menuType) {
                    return;
                }

                // Jeśli są zmiany, pytamy użytkownika o zgodę na przełączenie
                if (isChanged) {
                    if (!showConfirmationDialog()) {
                        // Użytkownik wybrał "NIE" -> przywracamy w ComboBoxie poprzedni wybór bez wywoływania pętli akcji
                        menuComboBox.setSelectedItem(this.menuType.getTitle());
                        return;
                    }
                }

                // Użytkownik wybrał "TAK" lub nie było zmian -> przełączamy menu
                this.menuType = newMenuType;
                setTitle("Edytuj Menu: " + menuType.getTitle());
                menuFileLoader.loadChoosenMenuFile(menuType, menuContentTextArea);
                isChanged = false; // Po załadowaniu nowego pliku resetujemy flagę zmian
            }
        });

        saveButton.addActionListener(e -> {
            String content = menuContentTextArea.getText();

            String validationErrors = menuFileLoader.validateMenuContent(content);

            if (!validationErrors.isEmpty()) {
                JOptionPane.showMessageDialog(this,
                        "Znaleziono błędy w menu:\n\n" + validationErrors,
                        "Błąd walidacji",
                        JOptionPane.ERROR_MESSAGE);
                return;
            }

            menuFileLoader.writeChoosenMenuFile(menuType, menuContentTextArea);

            // NOWOŚĆ: Po udanym zapisie resetujemy flagę, bo zmiany zostały utrwalone
            isChanged = false;

            JOptionPane.showMessageDialog(null, "Zapisano pomyślnie! Zmodyfikowano plik Menu: " +
                    menuType.getTitle(), "Sukces", JOptionPane.INFORMATION_MESSAGE);
        });

        // ZMIANA: Obsługa przycisku Anuluj
        cancelButton.addActionListener(e -> handleCloseRequest());

        // NOWOŚĆ: Obsługa kliknięcia systemowego krzyżyka (X) w prawym górnym rogu okna
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                handleCloseRequest();
            }
        });
    }

    /**
     * Pomocnicza metoda wyświetlająca okno dialogowe z pytaniem.
     * Zwraca true jeśli użytkownik kliknął TAK, false jeśli NIE.
     */
    private boolean showConfirmationDialog() {
        //            TODO 2- przejrzeć  kod / "review kodu" - powtórka
        Object[] options = {"TAK", "NIE"};
        int answer = JOptionPane.showOptionDialog(
                null,
                "Wprowadzono niezapisane zmiany. Czy chcesz kontynuowac?",
                "Niezapisane zmiany",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE,
                null, options, options[0]
        );
        return answer == JOptionPane.YES_OPTION;

    }

    /**
     * Pomocnicza metoda sprawdzająca stan zmian przed całkowitym zamknięciem okna.
     */
    private void handleCloseRequest() {
        if (isChanged) {
            if (showConfirmationDialog()) {
                dispose(); // Użytkownik klika TAK -> zamykamy okno
            }
        } else {
            dispose(); // Brak zmian -> zamykamy od razu
        }
    }
}

