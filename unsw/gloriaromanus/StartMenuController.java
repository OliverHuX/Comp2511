package unsw.gloriaromanus;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.ResourceBundle;
import java.util.Set;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.image.Image;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Circle;

public class StartMenuController implements Initializable{
  @FXML
  private Button startGame;

  private StartScreen startScreen;

  @FXML
  private Circle Rome;

  @FXML
  private Circle CelticBritons;

  @FXML
  private Circle Spanish;

  @FXML
  private Circle Gallic;

  @FXML
  private ChoiceBox<String> player1Faction;

  @FXML
  private ChoiceBox<String> player2Faction;
  @FXML
  private ChoiceBox<String> player3Faction;

  @FXML
  private ChoiceBox<String> player4Faction;

  @FXML
  private ChoiceBox<Integer> initialTreasury;

  @FXML
  private ChoiceBox<Integer> initialWealth;

  @FXML
  private ChoiceBox<String> firstLogic;

  @FXML
  private ChoiceBox<String> secondLogic;

  @FXML
  private Button StartGame;

  @FXML
  private CheckBox WealthGoal;

  @FXML
  private CheckBox TreasuryGoal;

  @FXML
  private CheckBox ConquestGoal;

  @FXML
  private CheckBox RandomGoal;

  @FXML
  private TextField order;


  final String[] factions = new String[]{"Rome", "Gaul", "Gallic", "Spanish", null};
  final Integer[] coins = new Integer[]{500, 1000, 10000};
  final String[] signs = new String[]{"AND", "OR", null};
  List<String> finalGoals = new ArrayList<>();


  private GloriaRomanusSystem sys;
  private MainScreen mainScreen;


  public void setStartScreen(StartScreen startScreen) {
    this.startScreen = startScreen;
  }


  public void setMainScreen(MainScreen mainScreen) {
    this.mainScreen = mainScreen;
  }

  public void setSys(GloriaRomanusSystem sys) {
    this.sys = sys;
  }

  @Override
  public void initialize(URL arg0, ResourceBundle arg1) {
    InputStream input = this.getClass().getResourceAsStream("/image/Rome.png");
    Image im = new Image(input);
    Rome.setFill(new ImagePattern(im));

    input = this.getClass().getResourceAsStream("/image/Gallic.png");
    im = new Image(input);
    Gallic.setFill(new ImagePattern(im));

    input = this.getClass().getResourceAsStream("/image/Spanish.png");
    im = new Image(input);
    Spanish.setFill(new ImagePattern(im));

    input = this.getClass().getResourceAsStream("/image/CelticBritons.png");
    im = new Image(input);
    CelticBritons.setFill(new ImagePattern(im));


    player1Faction.getItems().addAll(factions);
    player2Faction.getItems().addAll(factions);
    player3Faction.getItems().addAll(factions);
    player4Faction.getItems().addAll(factions);
  
    initialTreasury.getItems().addAll(coins);
    initialWealth.getItems().addAll(coins);
    firstLogic.getItems().addAll(signs);
    secondLogic.getItems().addAll(signs);

    initialTreasury.setValue(1000);
    initialWealth.setValue(1000);
  }

  @FXML
  public void handleBack(ActionEvent event) {
    startScreen.start();
  }

  @FXML
  public void handleWealthBox(ActionEvent event) {
    if(WealthGoal.isSelected()) {
      RandomGoal.setDisable(true);
      finalGoals.add("WEALTH");
      order.setText(finalGoals.toString());
    } else if(!TreasuryGoal.isSelected() && !ConquestGoal.isSelected()){
      RandomGoal.setDisable(false);
      finalGoals.remove(new String("WEALTH"));
      order.setText(finalGoals.toString());
    } else {
      finalGoals.remove(new String("WEALTH"));
      order.setText(finalGoals.toString());
    }
  }

  @FXML
  public void handleTreasuryBox(ActionEvent event) {
    if(TreasuryGoal.isSelected()) {
      RandomGoal.setDisable(true);
      finalGoals.add("TREASURY");
      order.setText(finalGoals.toString());
    } else if(!WealthGoal.isSelected() && !ConquestGoal.isSelected()){
      RandomGoal.setDisable(false);
      finalGoals.remove(new String("TREASURY"));
      order.setText(finalGoals.toString());
    } else {
      finalGoals.remove(new String("TREASURY"));
      order.setText(finalGoals.toString());
    }
  }

  @FXML
  public void handleConquestBox(ActionEvent event) {
    if(ConquestGoal.isSelected()) {
      RandomGoal.setDisable(true);
      finalGoals.add("CONQUEST");
      order.setText(finalGoals.toString());
    } else if(!WealthGoal.isSelected() && !TreasuryGoal.isSelected()){
      RandomGoal.setDisable(false);
      finalGoals.remove(new String("CONQUEST"));
      order.setText(finalGoals.toString());
    } else {
      finalGoals.remove(new String("CONQUEST"));
      order.setText(finalGoals.toString());
    }
  }

  @FXML
  public void handleRandomBox(ActionEvent event) {
    if(RandomGoal.isSelected()){
      WealthGoal.setDisable(true);
      TreasuryGoal.setDisable(true);
      ConquestGoal.setDisable(true);
      firstLogic.setDisable(true);
      secondLogic.setDisable(true);
    } else {
      WealthGoal.setDisable(false);
      TreasuryGoal.setDisable(false);
      ConquestGoal.setDisable(false);
      firstLogic.setDisable(false);
      secondLogic.setDisable(false);
    }
  }


  @FXML
  public void handleStartNewGame(ActionEvent event) {
  
    Alert errorAlert = new Alert(AlertType.ERROR);
    errorAlert.setHeaderText("Invalid Action");

    ArrayList<String> players = new ArrayList<>();

    String player1 = player1Faction.getValue();
    if(player1 != null) players.add(player1);
    String player2 = player2Faction.getValue();
    if(player2 != null) players.add(player2);
    String player3 = player3Faction.getValue();
    if(player3 != null) players.add(player3);
    String player4 = player4Faction.getValue();
    if(player4 != null) players.add(player4);


    Boolean wealthGoal = WealthGoal.isSelected();
    Boolean treasuryGoal = TreasuryGoal.isSelected();
    Boolean conquestGoal = ConquestGoal.isSelected();
    Boolean randomGoal = RandomGoal.isSelected();

    int numGoals = 0;

    if(players.size() < 2) {
      errorAlert.setContentText("Need at least two players");
      errorAlert.showAndWait();
      return;
    }
  
    if(!areAllUnique(players)){
      errorAlert.setContentText("Please choose different fractions");
      errorAlert.showAndWait();
      return;
    }

    if(wealthGoal){
      numGoals++;
      //finalGoals.add("WEALTH");
    } 
    if(treasuryGoal) {
      numGoals++;
      //finalGoals.add("TREASURY");
    } 
    if(conquestGoal) {
      numGoals++;
      //finalGoals.add("CONQUEST");
    }

    if(!randomGoal && numGoals == 0) {
      errorAlert.setContentText("Please select at least one of the goals");
      errorAlert.showAndWait();
      return;
    }
    if(!randomGoal && firstLogic.getValue() == null && numGoals > 1) {
      errorAlert.setContentText("Please select the first logical conjunction");
      errorAlert.showAndWait();
      return;
    }
    if(!randomGoal && numGoals == 3 && secondLogic.getValue() == null) {
      errorAlert.setContentText("Please select the second logical conjunction");
      errorAlert.showAndWait();
      return;
    }
    if(!randomGoal && numGoals == 2 && secondLogic.getValue() != null) {
      errorAlert.setContentText("Please do not select the second logical conjunction");
      errorAlert.showAndWait();
      return;
    }


    int treasury = initialTreasury.getValue();
    int wealth = initialWealth.getValue();

    //initialize factions
    for(String f:players) {
      try{
        sys.createFaction(f, ReadJson.getFaction(players));
      }catch(IOException e) {
        // e.printStackTrace();
      }
    }

    for(Faction faction :sys.getFaction()) {
      faction.setTreasury(treasury);
      for(Province p: faction.getProvinces()) {
        p.setWealth(wealth);
      }
      for(Faction f1: sys.getFaction()) {
        if(!f1.getName().equals(faction.getName())){
          faction.addEnemy(f1);
        }
      }
    }


    //set player's faction
    sys.setPlayers(players);
    sys.setCurrentP(players.get(0));

    if(randomGoal) {
      int num = sys.getRandomLogicalNum();
      sys.setLogicalSign(num);
      sys.setGoals(num);
    } else {
      List<String> finalSigns = new ArrayList<>();
      finalSigns.add(firstLogic.getValue());
      finalSigns.add(secondLogic.getValue());
      sys.setLogicalSignsManually(finalSigns);
      sys.setGoalsManually(finalGoals);
    }

    System.out.println("Start");

    mainScreen.start();
  }

  public static <T> boolean areAllUnique(ArrayList<T> list){
    Set<T> set = new HashSet<>();
    for (T t: list){
      if (!set.add(t))
        return false;
    }
    return true;  
  }
}
