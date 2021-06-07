package unsw.gloriaromanus;

import java.io.IOException;
import java.net.URL;

import com.fasterxml.jackson.databind.JsonMappingException;
import com.google.gson.JsonParseException;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.text.Text;

public class FactionMenuController  extends MenuController implements FrontendObserver{
  
  @FXML
  private URL location; // has to be called location

  @FXML
  private Text faction_name;

  @FXML
  private TextArea goals;
  @FXML
  private TextField province_num;
  @FXML
  private TextField treasury;
  @FXML
  private TextField wealth;
  @FXML
  private TextField turn;

  Boolean achieveGoals = false;
  Faction myfaction;
  GloriaRomanusSystem sys;

  @FXML
  public void handleEndTurn(ActionEvent event) throws JsonParseException, JsonMappingException, IOException{
    super.getParent().successMsg(sys.getCurrentPlayer().getName() + " is calling end turn" + "\n");
    super.getParent().endTurn();
    notifySucceess(null);
  }

  public void setup(GloriaRomanusSystem sys, String goals) {
    setup(sys);
    setGoals(goals);
  }

  public void settingInfo() {
    faction_name.setText(myfaction.getName());
    treasury.setText(Integer.toString(myfaction.getTreasury()));
    wealth.setText(Integer.toString(myfaction.getWealth()));
    province_num.setText(Integer.toString(myfaction.getProvinces().size()));
    turn.setText(Integer.toString(myfaction.getTurn()));
  }

  public void setup(GloriaRomanusSystem sys) {
    this.sys = sys;
    this.myfaction = sys.getCurrentPlayer();
    settingInfo();
  }

  public void setGoals(String goals){
    this.goals.setText(goals);
  }

  public void notifySucceess(String msg) {
    if(!achieveGoals) {
      super.getParent();
    }
    super.getParent().successMsg("success" + " current player is " + sys.getCurrentPlayer().getName() + "\n" + "now is turn " + sys.getTurn());
  }

  @Override
  public void notifyFail(String msg) {
    
  }
}