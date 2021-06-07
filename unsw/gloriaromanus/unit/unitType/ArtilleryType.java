package unsw.gloriaromanus.unit.unitType;

import unsw.gloriaromanus.Unit;
import unsw.gloriaromanus.UnitObserver;

public class ArtilleryType implements UnitProperty{
  @Override
  public void setPropertyM(Unit unit, UnitObserver p) {
    return;
  }
  @Override
  public int changeCost(int cost) {
    return cost;
  }
  @Override
  public void setMovePoint(Unit u) {
    u.setMovePoint(4);
  }
  @Override
  public String toString() {
    return "Artillery";
  }
}
