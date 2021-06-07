package unsw.gloriaromanus;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Alert.AlertType;

public class StartController {
  @FXML
  private Button newGame;

  @FXML
  private Button loadGame;

  private StartMenuScreen startMenuScreen;
  private MainScreen mainScreen;
  private GloriaRomanusSystem sys;

  @FXML
  public void handleNewGame(ActionEvent event) {
    startMenuScreen.start();
  }

  @FXML
  public void handleLoading(ActionEvent event) {
    this.sys = StoreData.load();
    if(sys == null) {
      Alert errorAlert = new Alert(AlertType.ERROR);
      errorAlert.setHeaderText("Loading fail");
      errorAlert.setContentText("No loading cache, please start a game first");
      errorAlert.showAndWait();
      return;
    }
    mainScreen.getController().setSys(sys);
    mainScreen.start();
  }

  public void setStartMenuScreen(StartMenuScreen startMenuScreen) {
    this.startMenuScreen = startMenuScreen;
  }


  public void setSys(GloriaRomanusSystem sys) {
    this.sys = sys;
  }

  public void setMainScreen(MainScreen mainScreen) {
    this.mainScreen = mainScreen;
  }
}
