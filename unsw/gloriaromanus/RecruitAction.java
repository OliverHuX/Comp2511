package unsw.gloriaromanus;


import org.json.JSONObject;

import unsw.gloriaromanus.unit.unitType.ArtilleryType;
import unsw.gloriaromanus.unit.unitType.HorseArcherType;
import unsw.gloriaromanus.unit.unitType.InfantryType;
import unsw.gloriaromanus.unit.unitType.MeleeCavalryType;
import unsw.gloriaromanus.unit.unitType.UnitProperty;

public class RecruitAction {

  public static Boolean checkCondition(Province p, UnitProperty mType, int cost, boolean isM, String name) {
    if (isM) {
      if(mType instanceof ArtilleryType || !p.canBeMercenary(name)) return false;
      cost = mType.changeCost(cost);
    }
    if (p.sufficientTreasury(cost)) return true;
    return false;
  }

  /**
   * reduce province's treasury, add to recruitList
   * 
   * @param province
   * @param unit
   * @param costTurn
   * @param costMoney
   */
  public static void addToWaitList(Province p, Unit u, int t, int cost, boolean isM, UnitProperty mType) {
    if(isM){
      p.spend(mType.changeCost(cost));
      p.addMyUnit(u);
    }else{
      p.spend(cost);
      p.addRecruitItem(u, t, cost);
    }
  }

  public static int getMCost(UnitProperty type, int initCost) {
    return type.changeCost(initCost);
  }

  public static Unit createUnit(String name, boolean isMercenary, int[] property, UnitProperty type){
    boolean range = true;
    if (property[1] == 0)
      range = false;
    Unit unit = new Unit(property[0], range, property[2], property[3], property[4], property[5],
        property[6], property[7], property[10], name, type, isMercenary);
    return unit;
  }

  public static Boolean recruitUnit(String name, Province p, boolean isMercenary, int[] property, UnitProperty type) {
    int cost = property[9];
    int turn = property[8];
    if (checkCondition(p, type, cost, isMercenary, name) == false)  return false;
    Unit unit = createUnit(name, isMercenary, property, type);
    addToWaitList(p, unit, turn, cost, isMercenary, type);
    return true;
    }

    public static Unit recruitNewUnit(String name, Province p, boolean isMercenary, int[] property, UnitProperty type) {
      int cost = property[9];
      int turn = property[8];
      if (checkCondition(p, type, cost, isMercenary, name) == false)  return null;
      Unit unit = createUnit(name, isMercenary, property, type);
      addToWaitList(p, unit, turn, cost, isMercenary, type);
      return unit;
    }
  
  public static int[] getUnitData(String content, String name){
    int[] result = new int[11];
    JSONObject types = new JSONObject(content);
    JSONObject unit = types.getJSONObject(name);
    result[0] = unit.getInt("numTroops");
    if (unit.getBoolean("range") == false) {
      result[1] = 0;
    } else {
      result[1] = 1;
    }
    result[2] = unit.getInt("armour");
    result[3] = unit.getInt("morale");
    result[4] = unit.getInt("speed");
    result[5] = unit.getInt("attack");
    result[6] = unit.getInt("defenseSkill");
    result[7] = unit.getInt("shieldDefense");
    result[8] = unit.getInt("turn");
    result[9] = unit.getInt("cost");
    result[10] = unit.getInt("upkeep");
    return result;
  }

  public static UnitProperty getUnitProperty(String content, String name){
    JSONObject sub = new JSONObject(content);
    JSONObject unit = sub.getJSONObject(name);
    String type = unit.getString("type");
    switch(type){
      case "Infantry":
        return (new InfantryType());
      case "MeleeCavalry":
        return (new MeleeCavalryType());
      case "Artillery":
        return (new ArtilleryType());
    }
      return (new HorseArcherType());
  }
  
}