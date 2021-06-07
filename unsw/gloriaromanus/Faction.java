package unsw.gloriaromanus;

import java.util.ArrayList;


public class Faction implements ProvinceOwnership{
  private ArrayList<Province> provinces;
  private ArrayList<String> enemy;
  private ArrayList<String> provinceEnemy;
  private int wealth;
  private String name;
  private int treasury;
  private int turn;
  private ArrayList<Trade> trades;
  private ArrayList<Trade> tradeRequest;

  public Faction(String name) {
    this.name = name;
    this.provinces = new ArrayList<>();
    this.enemy = new ArrayList<>();
    this.provinceEnemy = new ArrayList<>();
    this.wealth = 0;
    this.treasury = 0;
    this.turn = 0;
    this.trades = new ArrayList<>();
    this.tradeRequest = new ArrayList<>();
  }

  /**
   * use when create or load the factions, include set up observer
   * @param name
   */
  public void addProvince(String name) {
    Province p = new Province(name);
    p.setOwner(this);
    provinces.add(p);
  }

  public void addProvince(Province p) {
    provinces.add(p);
  }

  public void addProvinceEnemy(String name) {
    for(Province p:provinces) {
      p.addEnemyProvince(name);
    }
    provinceEnemy.add(name);
  }

  public ArrayList<Trade> getTradeRequest() {
    return tradeRequest;
  }

  public ArrayList<Trade> getTrades() {
    return trades;
  }

  public void deleteProvinceEnemy(String name) {
    for(Province p:provinces) {
      p.removeEnemyProvince(name);
    }
    provinceEnemy.remove(name);
  }

  public void updateWealth(int income) {
    wealth += income;
  }

  //bankcrupcy
  public void borrow(Faction f, int gold) {
    Trade trade = new Trade(gold, f, this);
    f.addTradeRequest(trade);
  }

  public void sucecessBorrow(Trade trade) {
    trades.add(trade);
    tradeRequest.remove(trade);

    treasury += trade.getAmount();
  }

  public void removeTrade(Trade trade) {
    trades.remove(trade);
  }

  public void addTrade(Trade trade) {
    trades.add(trade);
  }

  public void lend(Trade trade) {
    trade.approve();
    trades.add(trade);
    tradeRequest.remove(trade);

    treasury -= trade.getAmount();
  }

  public void addTradeRequest(Trade trade) {
    tradeRequest.add(trade);
  }

  public boolean repayDebt(Trade trade) {
    if(incurExpense(trade.getFinalPay())){
      trades.remove(trade);
      trade.end();
      return true;
    }
    return false;
  }

  public void getPay(Trade trade) {
    treasury += trade.getFinalPay();
    trades.remove(trade);
  }

  public boolean isInDebt() {
    for(Trade trade:trades) {
      if(trade.getDebtor().equals(name)) return true;
    }
    return false;
  }

  /**
   * use for system test
   * @param num
   */
  public void setWealth(int wealth) {
    this.wealth = wealth;
  }

  /**
   * check if given faction is enemy
   * @param f faction name
   * @return 
   */
  public boolean isEnemy(String f){
    if(getEnemy().contains(f)) return true;
    return false;
  }

  public ArrayList<Province> getProvinces() {
    return provinces;
  }

  public ArrayList<String> getEnemy() {
    return enemy;
  }

  public ArrayList<String> getEnemyProvince() {
    return provinceEnemy;
  }


  public void addEnemy(Faction name) {
    enemy.add(name.getName());
    for(Province p:name.getProvinces()) {
      addProvinceEnemy(p.getName());
    }
  }


  public void deleteEnemy(Faction name) {
    enemy.remove(name.getName());
    for(Province p:name.getProvinces()) {
      deleteProvinceEnemy(p.getName());
    }
  }

  public int getWealth() {
    return wealth;
  }

  public String getName() {
    return name;
  }

  public int getTreasury() {
    return treasury;
  }

  public void setTreasury(int treasury) {
    this.treasury = treasury;
  }

  public int getTurn() {
    return turn;
  }

  /**
   * Call only when all Players finish this turn
   */
  public void updateByTurn() {
    updateTradeProfit();
    for(Province p:provinces) {
      p.updateByTurn();
    }
  }

  public void updateTradeProfit() {
    for(Trade t:trades) {
      if(t.getCreditor().equals(name)) {
        t.updateProfit();
      }
    }
  }
  
  /**
   * Player endTurn individually
   */
  public void endTurn(){
    this.turn += 1;
  }

  @Override
  public void updateTreasury(int gold) {
    treasury += gold;
  }

  @Override
  public void lostOwnership(Province p) {
    this.provinces.remove(p);
  }

  @Override
  public void gainOwnership(Province p) {
    this.addProvince(p);
  }

  @Override
  public String getOwnerName() {
    return getName();
  }

  @Override
  public int getOwnerTreasury() {
    return this.treasury;
  }
  
  @Override
  public Boolean incurExpense(int cost) {
    if(cost <= treasury) {
      this.treasury -= cost;
      return true;
    }else {
      return false;
    }
  }

  @Override
  public void upkeep(int cost) {
    treasury -= cost;
  }

  @Override
  public boolean bankcrupcy() {
    if(treasury < 0 || isInDebt()) return true;
    return false;
  }

  public boolean justToke(String name) {
    for(Province p:getProvinces()) {
      if(p.getName().equals(name)){
        if(p.getMyUnits().size() == 0) {
          return false;
        }
        if(p.getMyUnits().get(0).getCurrentState().toString() == "WasInBattle"){
          return true;
        }
      }
    }
    return false;
  }

  // public static void main(String[] args) {
  //   Faction Rome = new Faction("Rome");
  //   Faction Gaul = new Faction("Gaul");
  //   Province Syria = new Province("Syria");
  //   Rome.addProvince(Syria);
  //   Province Dacia = new Province("Decia");
  //   Gaul.addProvince(Dacia);

  //   // Unit range_1 = new Unit(40, true, 5, 5, 5, 5, 5, 5, false, 500);
  //   // Unit range_2 = new Ballista(40, true, 5, 5, 5, 5, 5, 5, false, 500);
    
  //   Syria.addMyUnit(range_1);
  //   Dacia.addMyUnit(range_2);
  //   BattleResolver battle = new Battle(Syria, Dacia);
  //   System.out.println(battle.getMsg());
  // }
}
