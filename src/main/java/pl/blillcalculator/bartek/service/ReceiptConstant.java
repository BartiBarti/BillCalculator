package pl.blillcalculator.bartek.service;

public class ReceiptConstant {

    private ReceiptConstant() {
    }

    public static final int RECEIPT_BASE_HEIGHT = 250;
    public static final int RECEIPT_ITEM_HEIGHT = 25;
    public static final String CONFIG_FILE = "src/main/resources/receipt_config.properties";
    public static final String RECEIPT_FOLDER_PATTERN = "yyyy-MM";
    public static final String RECEIPTS_ROOTNAME_FOLDER = "receipts/";
    public static final String RECEIPT_FILE_NAME = "paragon";
    public static final String RECEIPT_FILE_EXTENSION = ".pdf";
    public static final int RECEIPT_MIN_HEIGHT = 350;
    public static final int RECEIPT_WIDTH = 150;
    public static final String RECEIPT_HORIZONTAL_LINE = "- - - - - - - - - - - - - - - - - -";
    public static final String RECEIPT_AMOUNT_ROUNDING = "%.2f";
    public static final String RECEIPT_DATE_PATTERN = "yyyy-MM-dd HH:mm:ss";
    public static final String RECEIPT_CODE_DATE_PATTERN = "yyyyMMddHHmmss";
    public static final String LAST_MONTH_CONFIG_KEY = "lastMonth";
    public static final String COUNTER_CONFIG_KEY = "counter";
}
