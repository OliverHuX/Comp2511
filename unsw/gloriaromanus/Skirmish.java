package unsw.gloriaromanus;

import java.util.ArrayList;

import unsw.gloriaromanus.unit.unitstate.*;

public class Skirmish implements BattleResolver{
  private Unit invadeU;
  private Unit defenseU;
  private ArrayList<BattleResolver> engagement;
  private Battle battle;
  private String skirmishMsg;

  public Skirmish(Unit invader, Unit defenser, Battle b) {
    this.invadeU = invader;
    this.defenseU = defenser;
    this.engagement = new ArrayList<>();
    this.battle = b;
    this.skirmishMsg = "Skirmish start: My " + invader.toString() + " VS Enemy" + defenser.toString() + "\n";
  }

  @Override
  public void start() {
    while(battle.getEngageNum() < 200) {
      Engagement newEngage = new Engagement(invadeU, defenseU, battle);
      if(invaderinField() && defenserInField()) {
        attach(newEngage);
      //skrimish end
      }else if(!invaderinField() && !defenserInField()) {
        skirmishMsg += "==========Skirmish End==============\n";
        return;
      }
      else if(invaderinField()) {
        if(defenseU.getCurrentState().toString().equals("Spare")) skirmishMsg += defenseU.toString() + " has successfully escaped\n";
        skirmishMsg += invadeU.toString() + " win\n";
        skirmishMsg += "==========Skirmish End==============\n";
        return;
      }else {
        if(invadeU.getCurrentState().toString().equals("Spare")) skirmishMsg += invadeU.toString() + " has successfully escaped\n";
        skirmishMsg += defenseU.toString() + " win\n";
        skirmishMsg += "==========Skirmish End==============\n";
        return;
      }
    }
    skirmishMsg += "==========Skirmish End==============\n";
  }

  @Override
  public String getMsg() {
    //defeat or route
    this.start();
    return skirmishMsg;
  }

  public boolean invaderinField() {
    return (invadeU.getCurrentState() instanceof Flee || invadeU.getCurrentState() instanceof InBattle)
      && invadeU.getNumTroops() > 0;
  }

  public boolean defenserInField() {
    return (defenseU.getCurrentState() instanceof Flee || defenseU.getCurrentState() instanceof InBattle)
      && defenseU.getNumTroops() > 0;
  }

  private void attach(BattleResolver b) {
    engagement.add(b);
    skirmishMsg += b.getMsg();
  }

}
