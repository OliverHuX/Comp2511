package unsw.gloriaromanus.unit.unitstate;

import unsw.gloriaromanus.Unit;
import unsw.gloriaromanus.Battle;
import unsw.gloriaromanus.Engagement;

public interface UnitState {
  /**
   * army join this engagement, and engagement effect on unit
   * @param enemyUnit
   * @param range type of engagement
   * @return engagement result
   */
  public String engage(Unit enemy, Boolean range, int engageNum, Engagement e);

  /**
   * army's reaction in the engagement
   * @param enemyUnit
   * @param battle
   */
  public void chooseAction(Unit enemy, Battle battle, Engagement e);

  /**
   * only do the calculation
   * @param enemy
   * @param range
   * @return number of inflict enemy
   */
  public int inflict(Unit enemy, Boolean range, int engageNum);
}
