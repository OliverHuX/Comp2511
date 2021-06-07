package unsw.gloriaromanus;

import javafx.fxml.FXML;
import javafx.scene.control.TextArea;

public class VictoryController extends MenuController{
    @FXML
    private TextArea goalText;

    public void setGoalText() {
        String info = super.getParent().getAchieveGoals();
        goalText.setText(info);
    }
}
