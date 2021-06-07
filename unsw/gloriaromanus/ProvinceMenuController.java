package unsw.gloriaromanus;

import java.io.IOException;
import java.net.URL;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

public class ProvinceMenuController extends MenuController{
    @FXML
    private TextField invading_province;
    @FXML
    private TextField opponent_province;
    @FXML
    private TextArea output_terminal;
    @FXML
    private TextField wealth;
    @FXML
    private TextField factionTreasury;
    @FXML
    private TextField wealthRate;
    @FXML
    private TextField taxRate;

    // https://stackoverflow.com/a/30171444
    @FXML
    private URL location; // has to be called location

    public void setInvadingProvince(String p) {
        invading_province.setText(p);
    }

    public void setOpponentProvince(String p) {
        opponent_province.setText(p);
    }

    public void appendToTerminal(String message) {
        output_terminal.clear();
        output_terminal.appendText(message + "\n");
    }

    @FXML
    public void clickedInvadeButton(ActionEvent e) throws IOException {
        getParent().clickedInvadeButton(e);
    }

    public void setWealth(String wealth) {
        this.wealth.setText(wealth);
    }

    public void setTreasury(String treasury) {
        this.factionTreasury.setText(treasury);
    }

    public void setWealthRate(String rate) {
        this.wealthRate.setText(rate);
    }

    @FXML
    public void handleUpdateNewTaxRate(ActionEvent event) {
        if(!getTaxRate().isEmpty()) {
            double rate = Double.parseDouble(getTaxRate());
            if(rate >= 1) {
                getParent().failMsg("Please enter the tax rate which is less than 1!");
            } else {
                getParent().setTaxRate(getTaxRate());
            }
        } else {
            getParent().failMsg("Please enter a tax rate!");
        }
    }

    public String getTaxRate(){
        return this.taxRate.getText();
    }
    public void setTaxRate(String taxRate) {
        this.taxRate.setText(taxRate);
    }
}
