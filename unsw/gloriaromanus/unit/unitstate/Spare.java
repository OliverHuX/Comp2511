package unsw.gloriaromanus.unit.unitstate;

import unsw.gloriaromanus.Battle;
import unsw.gloriaromanus.Engagement;
import unsw.gloriaromanus.Unit;

public class Spare implements UnitState {
  private Unit myunit;
  
  public Spare(Unit myunit) {
    this.myunit = myunit;
  }

  @Override
  public String engage(Unit enemy, Boolean range, int engageNum, Engagement e){
    return myunit.toString() + " successfully route from the battle\n";
  }

  @Override
  public void chooseAction(Unit u, Battle b, Engagement e) {
    return;
  }

  @Override
  public int inflict(Unit enemy, Boolean range, int engageNum) {
    return 0;
  }

  @Override
  public String toString() {
    return "Spare";
  }
}
