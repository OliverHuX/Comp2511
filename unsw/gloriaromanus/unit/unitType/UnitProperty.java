package unsw.gloriaromanus.unit.unitType;

import unsw.gloriaromanus.Unit;
import unsw.gloriaromanus.UnitObserver;

public interface UnitProperty {
  public void setPropertyM(Unit u, UnitObserver p);
  public int changeCost(int cost);
  public void setMovePoint(Unit u);
}
