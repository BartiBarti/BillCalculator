package pl.blillcalculator.bartek.model;

import java.util.Objects;

public class MenuItem {

  private String name;
  private double price;

  /**
   * Available values of atribute: 0,1,2
   * 0 - menu position not available
   * 1 - menu position limited
   * 2 - menu postion full availability
   */
  private int availability;

  public MenuItem(String name, double price, int availability) {
    this.name = name;
    this.price = price;
//     todo sprawdzić, (Za pomocą IF) czy parametr availability ma wartość 0, 1 albo 2 w przeciwnym razie rzecamy błąd.
    this.availability = availability;
  }

  public void setName(String name) {
    this.name = name;
  }

  public void setPrice(double price) {
    this.price = price;
  }

  public int getAvailability() {
    return availability;
  }

  public void setAvailability(int availability) {
    this.availability = availability;
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
