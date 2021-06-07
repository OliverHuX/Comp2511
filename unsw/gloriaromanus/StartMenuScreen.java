package unsw.gloriaromanus;

import java.io.IOException;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class StartMenuScreen {
  private Stage stage;
  private String title;
  private StartMenuController controller;
  private Scene scene;

  public StartMenuScreen(Stage stage) throws IOException {
    this.stage = stage;
    title = "Setting";

    controller = new StartMenuController();
    FXMLLoader loader = new FXMLLoader(getClass().getResource("startMenu.fxml"));
    loader.setController(controller);

    Parent root = loader.load();
    scene = new Scene(root);
  }

  public void start() {
    stage.setTitle(title);
    stage.setScene(scene);
    stage.show();
  }

  public StartMenuController getController() {
    return controller;
  }
}