package pl.blillcalculator.bartek.service;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import pl.blillcalculator.bartek.model.MenuItem;
import pl.blillcalculator.bartek.model.MenuType;

import javax.swing.*;

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

      JOptionPane.showMessageDialog(null, "Zmodyfikowano plik Menu: " +
              menuType.getTitle());

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

}
