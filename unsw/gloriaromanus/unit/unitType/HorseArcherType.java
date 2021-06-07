package unsw.gloriaromanus.unit.unitType;

import unsw.gloriaromanus.Unit;
import unsw.gloriaromanus.UnitObserver;

public class HorseArcherType implements UnitProperty{
  @Override
  public void setPropertyM(Unit unit, UnitObserver p) {
    unit.increaseUpkeep();
  }
  @Override
  public int changeCost(int cost) {
    return cost * 2;
  }
  @Override
  public void setMovePoint(Unit u) {
    u.setMovePoint(15);
  }
  @Override
  public String toString() {
    return "HorseArcher";
  }
}
