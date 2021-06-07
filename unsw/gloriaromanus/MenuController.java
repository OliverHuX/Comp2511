package unsw.gloriaromanus;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;

public abstract class MenuController {
    private GloriaRomanusController parent;

    public void setParent(GloriaRomanusController parent) {
        if (parent == null){
            System.out.println("GOT NULL");
        }
        this.parent = parent;
    }

    public GloriaRomanusController getParent(){
        return parent;
    }

    @FXML
    public void toRecruitMenus(ActionEvent e) throws Exception {
        parent.switchRecruit();
    }
    
    @FXML
    public void toUnitMenus(ActionEvent event) throws Exception {
        parent.switchUnit();
    }

    @FXML
    public void toFactionMenus(ActionEvent e) throws Exception {
        parent.switchFaction();
    }

    public void setup(Faction o, String goals) {};


    @FXML
    public void toProvinceMenus(ActionEvent e){
        parent.switchProvince();
    }
    @FXML
    public void toDiplomacyMenus(ActionEvent e) {
        parent.switchDiplomacy();
    }

    @FXML
    public void saveGame(ActionEvent e) {
      parent.saveGame();
    }
}
