package unsw.gloriaromanus;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.json.JSONArray;
import org.json.JSONObject;

import unsw.gloriaromanus.unit.unitType.InfantryType;
import unsw.gloriaromanus.unit.unitType.MeleeCavalryType;

public class GloriaRomanusSystem {
  private ArrayList<Faction> faction;
  private String player1;
  private String player2;
  private String player3;
  private String player4;
  private String currentPlayer;
  private List<String> goals;
  private List<String> logicalSigns;
  int turn;
  private int playerNum;
  private ArrayList<String> players;

  public GloriaRomanusSystem() {
    this.faction = new ArrayList<>();
    this.goals = new ArrayList<>();
    this.logicalSigns = new ArrayList<>();
    this.players = new ArrayList<>();
    player1 = null;
    player2 = null;
    player3 = null;
    player4 = null;
    currentPlayer = null;
    this.playerNum = 0;
    turn = 0;
  }

  public void setFationP1(String f) {
    player1 = f;
  }
  public void setFationP2(String f) {
    player2 = f;
  }
  public void setFationP3(String f) {
    player3 = f;
  }
  public void setFationP4(String f) {
    player4 = f;
  }
  public void setPlayers(ArrayList<String> fs) {
    this.playerNum = fs.size();
    this.players = fs;
    if(playerNum >= 1) setFationP1(fs.get(0));
    if(playerNum >= 2) setFationP2(fs.get(1));
    if(playerNum >= 3) setFationP3(fs.get(2));
    if(playerNum == 4) setFationP4(fs.get(3));
  }

  public void setCurrentP(Faction player) {
    this.currentPlayer = player.getName();
  }

  public void setCurrentP(String player) {
    this.currentPlayer = player;
  }

  public ArrayList<String> getPlayers() {
    return players;
  }

  public Faction toFaction(String name) {
    for (Faction f : faction) {
      if (f.getName().equals(name))
        return f;
    }
    return null;
  }

  public void createFaction(String name, String content) {
    JSONObject ownership = new JSONObject(content);
    for (String key : ownership.keySet()) {
      // key will be the faction name
      if (key.equals(name)) {
        Faction newF = new Faction(name);
        JSONArray ja = ownership.getJSONArray(key);
        for (int i = 0; i < ja.length(); i++) {
          // value is province name
          String value = ja.getString(i);
          Province p = new Province(value);
          p.setOwner(newF);
          
          Unit druid = new Unit(3, false, 2, 10, 3, 1, 1, 1, 0, "Druid", new InfantryType(), false);
          p.addMyUnit(druid);
        }
        this.faction.add(newF);
      }
    }
  }

  private void endTurnWithOne() {
    if (player1 != null) {
      toFaction(currentPlayer).endTurn();
      toFaction(player1).updateByTurn();
    }
  }

  private void endTurnWithTwo() {
    if (player1 != null && player2 != null) {
      toFaction(currentPlayer).endTurn();
      if(currentPlayer.equals(player1)){
        currentPlayer = player2;
      } else {
        currentPlayer = player1;
      }
      if (toFaction(player1).getTurn() == toFaction(player2).getTurn()) {
        turn += 1;
        toFaction(player1).updateByTurn();
        toFaction(player2).updateByTurn();
      }
    }
  }

  private void endTurnWithThree() {
    if (player1 != null && player2 != null && player3 != null) {
      toFaction(currentPlayer).endTurn();
      if(currentPlayer.equals(player1)){
        currentPlayer = player2;
      } else if(currentPlayer.equals(player2)){
        currentPlayer = player3;
      } else{
        currentPlayer = player1;
      }
      if (toFaction(player1).getTurn() == toFaction(player2).getTurn() && toFaction(player2).getTurn() == toFaction(player3).getTurn()) {
        turn += 1;
        toFaction(player1).updateByTurn();
        toFaction(player2).updateByTurn();
        toFaction(player3).updateByTurn();
      }
    }
  }
  private void endTurnWithFour() {
    if (player1 != null && player2 != null && player3 != null) {
      toFaction(currentPlayer).endTurn();
      if(currentPlayer.equals(player1)){
        currentPlayer = player2;
      } else if(currentPlayer.equals(player2)){
        currentPlayer = player3;
      } else if (currentPlayer.equals(player3)){
        currentPlayer = player4;
      }else{
        currentPlayer = player1;
      }
      if (toFaction(player1).getTurn() == toFaction(player2).getTurn() && toFaction(player2).getTurn() == toFaction(player3).getTurn() && toFaction(player3).getTurn() == toFaction(player4).getTurn()) {
        turn += 1;
        toFaction(player1).updateByTurn();
        toFaction(player2).updateByTurn();
        toFaction(player3).updateByTurn();
        toFaction(player4).updateByTurn();
      }
    }
  }


  public void endTurn() {
    if(playerNum == 1) endTurnWithOne();
    if(playerNum == 2) endTurnWithTwo();
    else if(playerNum == 3) endTurnWithThree();
    else endTurnWithFour();
  }

  public void failGame() {
    String loser = currentPlayer;
    endTurn();

    players.remove(loser);
    playerNum--;
  
    setPlayers(players);
  }

  public boolean checkFail() {
    Faction currentP = toFaction(currentPlayer);
    if(currentP.getProvinces().size() == 0) {
      return true;
    }
    return false;
  }

  public List<String> getRandomGoal(int num) {
    List<String> list = new ArrayList<>();
    list.add("CONQUEST");
    list.add("TREASURY");
    list.add("WEALTH");
    Random r = new Random();
    List<String> goals = new ArrayList<>();

    for (int i = 0; i < 2; i++) {
      int randomIndex = r.nextInt(list.size());
      goals.add(list.get(randomIndex));
      list.remove(randomIndex);
    }

    if(num == 2) {
      goals.add(list.get(0));
    }
    return goals;
  }

  public int getTurn() {
    return turn;
  }

  public int getRandomLogicalNum() {
    int num = (int) (Math.random() * 2 + 1);
    return num;
  }

  public List<String> getRandomLogicalSign(int num) {
    List<String> list = new ArrayList<>();
    list.add("AND");
    list.add("OR");

    Random r = new Random();
    List<String> logicalSign = new ArrayList<>();

    for(int i = 0; i < num; i++) {
      int randomIndex = r.nextInt(list.size());
      logicalSign.add(list.get(randomIndex));
    }

    return logicalSign;

  }

  public void setLogicalSign(int num) {
    //int num = getRandomLogicalNum();
    this.logicalSigns = getRandomLogicalSign(num);
  }

  public void setGoals(int num) {
    //int num = getRandomLogicalNum();
    this.goals = getRandomGoal(num);
  }

  public void setGoalsManually(List<String> goals) {
    this.goals = goals;
  }

  public void setLogicalSignsManually(List<String> sign) {
    this.logicalSigns = sign;
  }

  public Boolean checkGoal(String goal) {
    if (goal.equals("CONQUEST")) {
      boolean init = true;
      for (Faction e : faction) {
        if (!e.equals(toFaction(currentPlayer))) {
          if (e.getProvinces().size() != 0) {
            init = false;
          }
        }
      }
      if(init) {
        return true;
      }
    } else if (goal.equals("TREASURY")) {
      if (toFaction(currentPlayer).getTreasury() >= 100000) {
        return true;
      }
    } else {
      if (toFaction(currentPlayer).getWealth() >= 400000) {
        return true;
      }
    }
    return false;
  }

  public Boolean checkGoals(int num) {
    Boolean achieve = true;

    if(num == 1) {
      if(logicalSigns.get(0).equals("AND")) {
        for(String goal: goals) {
          if(!checkGoal(goal)) {
            achieve = false;
            break;
          }
        }
      } else {
        for(String goal: goals) {
          achieve = false;
          if(checkGoal(goal)) {
            achieve = true;
            break;
          }
        }
      }
    } else {
      if(logicalSigns.get(0).equals("AND") && logicalSigns.get(1).equals("AND")) {
        for(String goal: goals) {
          if(!checkGoal(goal)) {
            achieve = false;
            break;
          }
        }
      } else if (logicalSigns.get(0).equals("AND") && logicalSigns.get(1).equals("OR")) {
        String goal1 = goals.get(0);
        String goal2 = goals.get(1);
        String goal3 = goals.get(2);
        if(checkGoal(goal1) && checkGoal(goal2)) {
          achieve = true;
        } else if(checkGoal(goal1) && checkGoal(goal3)) {
          achieve = true;
        } else {
          achieve = false;
        }
      } else if (logicalSigns.get(0).equals("OR") && logicalSigns.get(1).equals("OR")) {
        for(String goal: goals) {
          achieve = false;
          if(checkGoal(goal)) {
            achieve = true;
            break;
          }
        }
      } else {
        String goal1 = goals.get(0);
        String goal2 = goals.get(1);
        String goal3 = goals.get(2);
        if(checkGoal(goal1)) {
          achieve = true;
        } else if(checkGoal(goal2) && checkGoal(goal3)) {
          achieve = true;
        } else {
          achieve = false;
        }
      }
    }
    return achieve;
  }

  public List<String> getGoals(){
    return goals;
  }

  public List<String> getSigns(){
    logicalSigns.remove(null);
    return logicalSigns;
  }


  public Faction getCurrentPlayer(){
    return toFaction(currentPlayer);
  }

  public String getCurrentPlayerName(){
    return currentPlayer;
  }
  public void addFaction(Faction f) {
    faction.add(f);
  }

  public void save() {
    StoreData.save(this);
  }

  public ArrayList<Faction> getFaction() {
    return faction;
  }
  
  public static void main(String[] args) {
    GloriaRomanusSystem sys =  new GloriaRomanusSystem();

    ArrayList<String> factions = new ArrayList<>();
    factions.add("Rome");
    factions.add("Gaul");
    for(String f:factions) {
      try{
        sys.createFaction(f, ReadJson.getFaction(factions));
      }catch(IOException e) {
        e.printStackTrace();
      }
    }
    sys.setFationP1("Rome");
    sys.setFationP2("Gaul");
    sys.setCurrentP("Rome");

    int num = sys.getRandomLogicalNum();
    sys.setGoals(num);
    sys.setLogicalSign(num);

    Faction Gaul = sys.toFaction("Gaul");
    Faction Rome = sys.toFaction("Rome");
    Province p = Gaul.getProvinces().get(0);


    Unit u = new Unit(1000, true, 5, 5, 5, 5, 5, 5, 5, "Archer", new MeleeCavalryType(), false);
    Trade t = new Trade(100, Gaul, Rome);
    Gaul.lend(t);


    p.addMyUnit(u);
    sys.save();
    sys = StoreData.load();

    // System.out.println(sys.getFaction().get(1).getProvinces().get(0).getMyUnits().size());

    // try{
    //   String content = ReadJson.getFaction("Rome");
    //   GloriaRomanusSystem sys = new GloriaRomanusSystem();
    //   sys.createFaction("Rome", content);
    //   Faction Rome = sys.getFaction().get(0);
    //   System.out.println(Rome);
    // }catch(IOException e) {
    //   e.printStackTrace();
    // }
  }
}
