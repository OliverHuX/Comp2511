package unsw.gloriaromanus;

import java.io.IOException;
import java.util.ArrayList;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.text.Text;
import unsw.gloriaromanus.unit.unitType.UnitProperty;


public class RecruitMenuController extends MenuController implements FrontendObserver{

  @FXML
  private TextField mercenary;
  @FXML
  private TextField factionTreasury;

  private GloriaRomanusSystem sys;
  private Province province;

  public void setMercenry(ArrayList<String> types) {
    this.mercenary.clear();
    for(String type:types){
      this.mercenary.appendText(type + " ");
    }
  }

  @FXML
  public void showUnitInfo(MouseEvent e) {
    Text unit = (Text)e.getSource();
    String name = unit.getText();
    super.getParent().switchUnitInfo(name);
  }

  @FXML
  public void recruitUnit(ActionEvent e){
     Button btn = (Button) e.getSource();
     String id = btn.getId();
     recruitUnit(id, false);
  }


  public void recruitUnit(String unitType, boolean isMercenary){
    try{
      String content = ReadJson.readUnitJson();
      int[] property = RecruitAction.getUnitData(content, unitType);
      UnitProperty type = RecruitAction.getUnitProperty(content, unitType);
      Unit unit = RecruitAction.recruitNewUnit(unitType, province, isMercenary, property, type);

      if(unit == null) {
        notifyFail(null);
        return;
      }

      if(isMercenary) {
        super.getParent().updateTroopNum(province);
        super.getParent().addAllPointGraphics();
        notifySucceess("Recruit MERCENARY " + unitType + " sucessfully, cost $" + RecruitAction.getMCost(type, property[9]));
      }
      else notifySucceess("Recruit " + unitType + " sucessfully, cost $" + property[9]);
      update();
  
    }catch(IOException e) {
      e.printStackTrace();
    }
  }

  @FXML
  public void recruitMUnit(ActionEvent e) {
      Button btn = (Button) e.getSource();
      String id = btn.getId();
      String name = id.substring(0, id.length() - 1);
      recruitUnit(name, true);
  }

  public void setup(GloriaRomanusSystem sys, Province province) {
    this.sys = sys;
    this.province = province;
    this.factionTreasury.setText(Integer.toString(sys.getCurrentPlayer().getTreasury()) + "$");
    setMercenry(province.getRecuitableMercenry());
  }

  public void update() {
    this.factionTreasury.setText(Integer.toString(sys.getCurrentPlayer().getTreasury()) + "$");
  }

  public void notifyFail(String msg) {
    super.getParent().failMsg("Don't have enough golds or try to recruit Atillery Mecenaries");
  }

  public void notifySucceess(String msg){
    super.getParent().successMsg(msg);
  }
}
