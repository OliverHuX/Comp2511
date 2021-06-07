package unsw.gloriaromanus;

import java.util.Random;

public class Engagement implements BattleResolver{
  private boolean range;
  private Unit invadeU;
  private Unit defenseU;
  private Battle battle;
  private int invadeC;
  private int defenseC;

  public Engagement(Unit invadeU, Unit defenseU, Battle battle) {
    this.invadeU = invadeU;
    this.defenseU = defenseU;
    this.battle = battle;
    battle.addEngageNum();
    setRange();
  }

  public void setRange() {
    if(invadeU.getRange() && defenseU.getRange()) {
      range = true;
    }else if(!invadeU.getRange() && !defenseU.getRange()) {
      range = false;
    }else {
      Random rand = new Random();
      if(rand.nextInt(2) == 0) {
        range = true;
      }else {
        range = false;
      }
    }
  }

  public boolean isRange() {
    return range;
  }

  @Override
  public void start() {
    //set Unit Action
    invadeU.chooseAction(defenseU, battle, this);
    defenseU.chooseAction(invadeU, battle, this);
  }

  @Override
  public String getMsg() {
    String msg = invadeU.engage(defenseU, range, battle.getEngageNum(), this);
    start();
    return msg;
  }

  public int getInvadeC() {
    return invadeC;
  }

  public int getDefenseC() {
    return defenseC;
  }

  public Unit getInvadeU() {
    return invadeU;
  }
  public Unit getDefenseU() {
    return defenseU;
  }

  public void setDefenseC(int defenseC) {
    this.defenseC = defenseC;
  }
  public void setInvadeC(int invadeC) {
    this.invadeC = invadeC;
  }
}
