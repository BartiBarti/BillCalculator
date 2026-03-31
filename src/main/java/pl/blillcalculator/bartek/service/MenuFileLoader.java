package pl.blillcalculator.bartek.service;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import pl.blillcalculator.bartek.model.MenuItem;

public class MenuFileLoader {

  public List<MenuItem> loadMenuFromFile(String filePath) {
    List<MenuItem> items = new ArrayList<>();

    try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
      String line;
      while ((line = br.readLine()) != null) {
        String[] parts = line.split(";");
        String name = parts[0];
        double price = Double.parseDouble(parts[1]);
        items.add(new MenuItem(name, price));
      }
    } catch (IOException e) {
      e.printStackTrace();
    }

    return items;
  }
}
