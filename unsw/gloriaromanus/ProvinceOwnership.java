package unsw.gloriaromanus;

public interface ProvinceOwnership {
  /**
   * update faction when it loses province
   * @param p province
   */
  public void lostOwnership(Province p);

  /**
   * update faction when it conquers province
   * @param p province
   */
  public void gainOwnership(Province p);

  /**
   * 
   * @return name of faction
   */
  public String getOwnerName();

  /**
   * 
   * @return Teasury of faction the province belong to
   */
  public int getOwnerTreasury();

  /**
   * Province spend money from faction
   * @param cost
   * @return true if money is sufficient and reduce gold of Treasury
   */
  public Boolean incurExpense(int gold);

  public void upkeep(int cost);
  public boolean bankcrupcy();


  //observer
  /**
   * update the faction's treasury
   * @param gold
   */
  public void updateTreasury(int gold);

  /**
   * update the faction's wealth
   * @param gold
   */
  public void updateWealth(int gold);


  public boolean isEnemy(String factionName);

  public boolean justToke(String provinceName);
}
