package unsw.gloriaromanus.unit.unitType;


import unsw.gloriaromanus.Unit;
import unsw.gloriaromanus.UnitObserver;

public class InfantryType implements UnitProperty{
  @Override
  public void setPropertyM(Unit unit, UnitObserver p) {
    p.updateMorale(-0.1);
  }

  @Override
  public int changeCost(int cost) {
    return cost * 2;
  }

  @Override
  public void setMovePoint(Unit u) {
    u.setMovePoint(10);
  }
  @Override
  public String toString() {
    return "Infantry";
  }
}
