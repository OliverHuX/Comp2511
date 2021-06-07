package unsw.gloriaromanus;


import javafx.application.Application;
import javafx.stage.Stage;

public class GloriaRomanusApplication extends Application {
  @Override
  public void start(Stage primaryStage) throws Exception {
    GloriaRomanusSystem sys = new GloriaRomanusSystem();
    
    StartScreen startScreen = new StartScreen(primaryStage);
    StartMenuScreen startMenuScreen = new StartMenuScreen(primaryStage);
    MainScreen mainScreen = new MainScreen(primaryStage);

    startMenuScreen.getController().setStartScreen(startScreen);
    startMenuScreen.getController().setMainScreen(mainScreen);
    startMenuScreen.getController().setSys(sys);
    
    startScreen.getController().setSys(sys);
    startScreen.getController().setMainScreen(mainScreen);
    startScreen.getController().setStartMenuScreen(startMenuScreen);
    
    mainScreen.getController().setSys(sys);

    startScreen.start();
  }
    
    /**
     * Opens and runs application.
     *
     * @param args arguments passed to this application
     */
    public static void main(String[] args) {
      
      Application.launch(args);
    }
  }




  // private static GloriaRomanusController controller;

  // @Override
  // public void start(Stage stage) throws IOException {
  //   // set up the scene
  //   FXMLLoader loader = new FXMLLoader(getClass().getResource("main.fxml"));
  //   Parent root = loader.load();
  //   controller = loader.getController();
  //   Scene scene = new Scene(root);

  //   // set up the stage
  //   stage.setTitle("Gloria Romanus");
  //   stage.setWidth(800);
  //   stage.setHeight(700);
  //   stage.setScene(scene);
  //   stage.show();

  // }