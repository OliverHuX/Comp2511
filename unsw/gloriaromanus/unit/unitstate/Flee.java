package unsw.gloriaromanus.unit.unitstate;
import unsw.gloriaromanus.Battle;
import unsw.gloriaromanus.Engagement;
import unsw.gloriaromanus.Unit;

public class Flee implements UnitState{
  private Unit myunit;

  public Flee(Unit myunit) {
    this.myunit = myunit;
  }

  @Override
  public String engage(Unit enemy, Boolean range, int engageNum, Engagement e){
    if(enemy.getCurrentState() instanceof Flee) {
      enemy.setSpareState();
      myunit.setInBattleState();
      return "Enemy escaped.\n";
    }else {
      int cNum = casualtiyNum(enemy, range, engageNum);
      myunit.minusTroop(cNum);
      if(myunit.getNumTroops() <= 0) {
        return myunit.toString() + " defeat.";
      }
      return "My " + myunit.toString() + " is fleeing. Casualty: " + cNum + 
        ", remains: " + myunit.getNumTroops() + "\n";
    }
  }

  public int casualtiyNum(Unit enemy, Boolean range, int engageNum) {
    return enemy.getCurrentState().inflict(myunit, range, engageNum);
  }

  @Override
  public void chooseAction(Unit enemy, Battle battle, Engagement e) {
    double pos = 0.5 + 0.1*(myunit.getSpeed() - enemy.getSpeed());
    if(Math.random() < pos) {
      //routed
      myunit.setSpareState();
    }
  }

  @Override
  public int inflict(Unit enemy, Boolean range, int engageNum) {
    return 0;
  }

  @Override
  public String toString() {
    return "Flee";
  }
}
