package unsw.gloriaromanus;

public interface UnitObserver {
  /**
   * update morales for all unit in province
   * @param num
   */
  public void updateMorale(int num);

  /**
   * update morales for all unit in province
   * @param num
   */
  public void updateMorale(double num);

  /**
   * 
   * @return factionName
   */
  public String factionToUnit();
}