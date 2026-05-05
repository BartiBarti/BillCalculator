package pl.blillcalculator.bartek.model;

public enum MenuType {

  APETIZERS("src/main/resources/Apetizers.txt", "Przystawki"),
  SOUPS("src/main/resources/Soups.txt", "Zupy"),
  ADDITIONALS("src/main/resources/Additionals.txt", "Dodatki"),
  DESERTS("src/main/resources/Deserts.txt", "Desery"),
  DRINKS("src/main/resources/Drinks.txt", "Napoje"),
  MAIN_DISHES("src/main/resources/MainDishes.txt", "Dania główne");

  MenuType(String filePath, String title) {
    this.filePath = filePath;
    this.title = title;
  }

  private final String filePath;

  private final String title;

  public String getFilePath() {
    return filePath;
  }

  public String getTitle() {
    return title;
  }

  public static MenuType getByTitle(String title){
      for (MenuType menuType : values()){
        if(menuType.title.equals(title)){
          return menuType;
        }
      }
      return null;
  }


}
