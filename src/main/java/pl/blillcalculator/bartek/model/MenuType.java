package pl.blillcalculator.bartek.model;

public enum MenuType {

  APETIZERS("src/main/resources/Apetizers.txt", "Przystawki"),
  SOUPS("src/main/resources/Soups.txt", "Zupy"),
  ADDITIONALS("src/main/resources/Additionals.txt", "Dodatki"),
  DESERTS("src/main/resources/Deserts.txt", "Desery"),
  DRINKS("src/main/resources/Drinks.txt", "Napoje"),
  MAIN_DISHES("src/main/resources/MainDishes.txt", "Danie główne");

  MenuType(String filePath, String title) {
    this.filePath = filePath;
    this.title = title;
  }

  public String getFilePath() {
    return filePath;
  }

  public String getTitle() {
    return title;
  }

  private final String filePath;

  private final String title;
}
