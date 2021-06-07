package unsw.gloriaromanus;

import java.io.IOException;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class MainScreen {
  private Stage stage;
  private Scene scene;
  private GloriaRomanusController controller;

  public MainScreen(Stage stage) throws IOException {
    this.stage = stage;
    // set up the scene
    // controller = new GloriaRomanusController();
    FXMLLoader loader = new FXMLLoader(getClass().getResource("main.fxml"));
    Parent root = loader.load();
    this.controller = loader.getController();
  
    scene = new Scene(root);
  }

  public void start() {
    // set up the stage
    stage.setTitle("Gloria Romanus");
    stage.setWidth(800);
    stage.setHeight(700);
    stage.setScene(scene);
    stage.show();
    try{
      controller.start();
    }catch(Exception e) {
      e.printStackTrace();
    }
  }

  public GloriaRomanusController getController() {
    return controller;
  }
}
