package unsw.gloriaromanus.unit.unitType;

import unsw.gloriaromanus.Unit;
import unsw.gloriaromanus.UnitObserver;

public class MeleeCavalryType implements UnitProperty{
  @Override
  public void setPropertyM(Unit unit, UnitObserver p) {
    p.updateMorale(-0.2);
    unit.increaseUpkeep();
  }
  @Override
  public int changeCost(int cost) {
    return cost * 3;
  }
  @Override
  public void setMovePoint(Unit u) {
    u.setMovePoint(15);
  }
  @Override
  public String toString() {
    return "MeleeCavalry";
  }
}
