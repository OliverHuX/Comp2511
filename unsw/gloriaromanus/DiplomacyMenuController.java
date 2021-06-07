package unsw.gloriaromanus;

import java.util.ArrayList;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;



public class DiplomacyMenuController extends MenuController implements FrontendObserver{
  @FXML
  private GridPane enemies;
  @FXML
  private GridPane debt;
  @FXML
  private GridPane loan; 
  @FXML
  private GridPane request;
  @FXML
  private ChoiceBox<String> borrowFaction;
  @FXML
  private TextField amount;


  private GloriaRomanusSystem sys;
  private Faction myFaction;

  public void setEmenies() {
    int num = 0;
    enemies.getChildren().clear();
    for(String f:sys.getPlayers()) {
      if(!f.equals(myFaction.getName())){
         enemies.add(createCheckBox(f), 0, num);
         num++;
      }
    }
  }

  public Node createCheckBox(String name) {
    CheckBox box = new CheckBox(name);
    if(myFaction.getEnemy().contains(name)){
      box.setSelected(true);
    }else{
      box.setSelected(false);
    }
    box.setOnAction(e -> handleEnemies(e));
    return box;
  }

  public void handleEnemies(ActionEvent e) {
    CheckBox cb = (CheckBox) e.getSource();
    String factionName = cb.getText();
    if(cb.isSelected()) myFaction.addEnemy(sys.toFaction(factionName));
    else myFaction.deleteEnemy(sys.toFaction(factionName));
  }

  public void setup(GloriaRomanusSystem sys){
    this.sys = sys;
    this.myFaction = sys.getCurrentPlayer();
    setEmenies();
    setTrade();
    setTradeRequest();
    setBorrowBtn();
  }

  private void setBorrowBtn() {
    borrowFaction.getItems().clear();
    ArrayList<String> factions = new ArrayList<>(sys.getPlayers());
    factions.remove(myFaction.getName());
    String[] factionArray = factions.toArray(new String[0]);

    borrowFaction.getItems().addAll(factionArray);
  }

  public void setTrade() {
    loan.getChildren().clear();
    debt.getChildren().clear();
    int Dnum = 0;
    int Lnum = 0;
    for(Trade trade:myFaction.getTrades()){
      if(trade.getDebtor().equals(myFaction.getName())){
        debt.add(createDebtNode(trade), 0, Dnum);
        Dnum++;
      }else{
        loan.add(createDebtNode(trade), 0, Lnum);
        Lnum++;
      }
    }
  }

  private void setTradeRequest() {
    request.getChildren().clear();
    int num = 0;
    for(Trade trade:myFaction.getTradeRequest()){
      request.add(createRequest(trade), 0, num);
      num++;
    }
  }

  public Node createDebtNode(Trade trade){
    TextField newTrade = new TextField();
    String record = "Amount: " + trade.getAmount() + ", Creditor: " + trade.getCreditor() + ", Profit: " + trade.getProfit(); 
    newTrade.setText(record);
    newTrade.setOnMouseClicked(e->pay(e));
    return newTrade;
  }

  public Node createLoansNode(Trade trade) {
    TextField newTrade = new TextField();
    String record = "Amount: " + trade.getAmount() + ", Debtor: " + trade.getDebtor() + ", Profit: " + trade.getProfit(); 
    newTrade.setText(record);
    return newTrade;
  }

  private Node createRequest(Trade trade) {
    TextField newTrade = new TextField();
    String record = "Amount: " + trade.getAmount() + ", From: " + trade.getDebtor(); 
    newTrade.setText(record);
    newTrade.setOnMouseClicked(e->lend(e));
    return newTrade;
  }

  @FXML
  public void handleBorrow(ActionEvent e) {
    try{
      String name = borrowFaction.getValue();
      if(name == null || !sys.getPlayers().contains(name)) {
        notifyFail("invalid fation name");
        return;
      }
      if(myFaction.isEnemy(name)){
        notifyFail("Can not have trade with ENEMIES");
        return;
      }
      int num = Integer.parseInt(amount.getText());
      myFaction.borrow(sys.toFaction(name), num);
      notifySucceess("Request Sent!");
      amount.clear();
    }catch(NumberFormatException err) {
      notifyFail("Please enter integer in amount textfield");
    }
  }

  private void pay(MouseEvent e) {
    Node item = (Node)e.getSource();
    int i = GridPane.getRowIndex(item);
    int num = 0;
    for(Trade t:myFaction.getTrades()) {
      if(t.getDebtor().equals(myFaction.getName()) && num == i){
        if(myFaction.repayDebt(t)) {
          notifySucceess("Pay sucessfully!");
        }else{
          notifyFail("Do not have enough golds");
        }
        break;
      }else if(t.getDebtor().equals(myFaction.getName())){
        num++;
      }
    }

    setTrade();
  }

  private void lend(MouseEvent e) {
    Node item = (Node)e.getSource();
    int i = GridPane.getRowIndex(item);
    myFaction.lend(myFaction.getTradeRequest().get(i));
    notifySucceess("Lend sucessfully!");

    setTradeRequest();
    setTrade();
  }

  @Override
  public void notifyFail(String msg) {
    super.getParent().failMsg(msg);
  }

  @Override
  public void notifySucceess(String msg) {
    super.getParent().successMsg(msg); 
  }
}
