package pl.blillcalculator.bartek.gui;

import java.util.Objects;

public class MenuItem {

  private String name;
  private double price;

  public MenuItem(String name, double price) {
    this.name = name;
    this.price = price;
  }

  public String getName() {
    return name;
  }

  public double getPrice() {
    return price;
  }

  @Override
  public boolean equals(Object o) {
      if (this == o) {
          return true;
      }
      if (o == null || getClass() != o.getClass()) {
          return false;
      }
    MenuItem menuItem = (MenuItem) o;
    return Objects.equals(name, menuItem.name);
  }

  @Override
  public int hashCode() {
    return Objects.hash(name);
  }
}
