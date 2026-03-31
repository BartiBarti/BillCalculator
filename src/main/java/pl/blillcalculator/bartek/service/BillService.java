package pl.blillcalculator.bartek.service;

import javax.swing.JComboBox;
import javax.swing.JTextField;

public class BillService {

  private JTextField billTextField;

  private JTextField billSumTextField;

  private JTextField tipTextField;

  private JComboBox tipPercentComboBox;

  public BillService(JTextField billTextField, JTextField billSumTextField, JTextField tipTextField,
      JComboBox tipPercentComboBox) {
    this.billTextField = billTextField;
    this.billSumTextField = billSumTextField;
    this.tipTextField = tipTextField;
    this.tipPercentComboBox = tipPercentComboBox;
  }

  public Double getCurrentBillAmount() {
    return !"".equals(billTextField.getText()) ? Double.parseDouble(billTextField.getText()) : 0.0;
  }

  public void updateBillTextField(Double amount) {
    String sAmount = String.format("%.2f", amount).replaceFirst(",", ".");
    billTextField.setText(sAmount);
  }

  public void calculateBill() {

    double bill;
    try {
      bill = Double.parseDouble(billTextField.getText());
      String tipPercent = (String) tipPercentComboBox.getSelectedItem();
      tipPercent = tipPercent.replaceAll("%", "");
      int iTipPercent = Integer.valueOf(tipPercent);

      double tip = bill * iTipPercent / 100;
      double fullBill = tip + bill;
      String sTip = String.format("%.2f", tip);
      String sFullBill = String.format("%.2f", fullBill);
      tipTextField.setText(sTip);
      billSumTextField.setText(sFullBill);
//            warningLabel.setText("");
    } catch (NumberFormatException e) {
//              Działają dwie metody ta przy użyciu HTMLa też, tylko jest zakomentowana
//            warningLabel.setText("<html><font color='red'>Wpisano błędne znaki!</font></html>");
//            warningLabel.setText(("Wpisano błędne znaki!"));
//            warningLabel.setForeground(Color.RED);
      tipTextField.setText("");
//
    }
  }

}
