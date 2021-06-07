package unsw.gloriaromanus;

import java.util.ArrayList;
import java.util.Random;

// import unsw.gloriaromanus.unit.unitType.HorseArcherType;
// import unsw.gloriaromanus.unit.unitType.InfantryType;


public class Battle implements BattleResolver{
  private Province invadeP;
  private Province defenseP;
  private ArrayList<Unit> invadeUnit;
  private ArrayList<Unit> routeUnit;
  private ArrayList<Unit> engagedInvadeU;
  private ArrayList<Unit> defenseUnit;
  private ArrayList<BattleResolver> skirmish;
  private int engageNum;
  private int invadeCasualty;
  private int defenseCasualty;
  private String battleMsg;

  public Battle(Province invadeP, Province defenseP) {
    this.invadeP = invadeP;
    this.defenseP = defenseP;
    this.invadeUnit = invadeP.getMyUnits();
    this.defenseUnit = defenseP.getMyUnits();
    this.routeUnit = new ArrayList<>();
    this.skirmish = new ArrayList<>();
    this.engageNum = 0;
    this.engagedInvadeU = new ArrayList<>();

    invadeCasualty = 0;
    defenseCasualty = 0;
  }

  @Override
  public void start() {
    battleMsg = "Start Battle: " + invadeP.toString() + " VS " + defenseP.toString() + "\n\n";
    while(engageNum < 200) {
      //check Unit
      if(invadeUnit.size() == 0 && defenseUnit.size() == 0) {
        setRoutUnitState();
        resetUnit();
        battleMsg += Draw();
        return;
      }
      if(invadeUnit.size() == 0) {
        setRoutUnitState();
        resetUnit();
        invadeP.addMyUnits(routeUnit);
        battleMsg += invadeP.toString() + " fail to conquer\nMy army loss";
        return;
      }
      if(defenseUnit.size() == 0) {
        setRoutUnitState();
        defenseP.setOwner(invadeP.getOwner());

        invadeP.getMyUnitsReference().clear();

        // defenseP.addMyUnits(routeUnit);
        for(Unit u:invadeUnit) {
          if(engagedInvadeU.contains(u)) {
            u.move(0, defenseP);
            u.setWasInBattleState();
            defenseP.addMyUnit(u);
          }else {
            invadeP.addMyUnit(u);
          }
        }
        battleMsg += defenseP.toString() + " fail to defense\nMy army win";
        defenseP.setLock(true);
        return;
      }
      //choose army, start skirmish
      Random rand = new Random();
      Unit invader = invadeUnit.get(rand.nextInt(invadeUnit.size()));
      Unit defenser = defenseUnit.get(rand.nextInt(defenseUnit.size()));

      //put units into battle
      invader.setInBattleState();
      defenser.setInBattleState();

      //recorde invaderUnit
      engagedInvadeU.add(invader);

      //start skirmish
      Skirmish newS = new Skirmish(invader, defenser, this);
      attach(newS);

      endSkirmish(defenser, invader);
    }
  
    if (engageNum == 200) {
      resetUnit();
      battleMsg += Draw();
    }
  }

  private void setRoutUnitState() {
    for(Unit u: routeUnit) {
      u.setWasInBattleState();
    }
  }

  private void resetUnit() {
    invadeP.getMyUnitsReference().clear();
    defenseP.getMyUnitsReference().clear();
    invadeP.addMyUnits(invadeUnit);
    defenseP.addMyUnits(defenseUnit);
  }
  @Override
  public String getMsg() {
    start();
    return battleMsg;
  }
  /**
   * remove the unit or move unit back to province
   * @param defenser defenseUnit
   * @param invader invadeUnit
   */
  public void endSkirmish(Unit defenser, Unit invader) {
      //remove defense Routed/defeated
      if(defenser.isRouted() || defenser.getNumTroops() == 0) {
        defenseUnit.remove(defenser);
      }
      if(invader.getNumTroops() == 0) {
        invadeUnit.remove(invader);
      }
      if(invader.isRouted()) {
        invadeUnit.remove(invader);
        routeUnit.add(invader);
      }
  }

  /**
   * record skirmsih
   * add skirmish result into BattleMsg
   * @param skirmish
   */
  public void attach(BattleResolver b) {
    skirmish.add(b);
    battleMsg += b.getMsg();
  }

  private String Draw() {
    for(Unit u: engagedInvadeU) {
      u.setWasInBattleState();
    }
    return "Draw\n";
  }
  public int getEngageNum() {
    return engageNum;
  }
  public void addEngageNum() {
    engageNum += 1;
  }

  public Province getInvadeP() {
    return invadeP;
  }

  public ArrayList<Unit> getEngagedInvadeU() {
    return engagedInvadeU;
  }
  
  public void setInvadeP(Province invadeP) {
    this.invadeP = invadeP;
  }

  public Province getDefenseP() {
    return defenseP;
  }

  public void setDefenseP(Province defenseP) {
    this.defenseP = defenseP;
  }

  public ArrayList<Unit> getInvadeUnit() {
    return invadeUnit;
  }

  public void setInvadeUnit(ArrayList<Unit> invadeUnit) {
    this.invadeUnit = invadeUnit;
  }

  public ArrayList<Unit> getDefenseUnit() {
    return defenseUnit;
  }

  public void setDefenseUnit(ArrayList<Unit> defenseUnit) {
    this.defenseUnit = defenseUnit;
  }

  public ArrayList<BattleResolver> getSkirmish() {
    return skirmish;
  }

  public void setSkirmish(ArrayList<BattleResolver> skirmish) {
    this.skirmish = skirmish;
  }

  public void setEngageNum(int engageNum) {
    this.engageNum = engageNum;
  }

  public int getInvadeCasualty() {
    return invadeCasualty;
  }

  public void addInvadeCasualty(int invadeCasualty) {
    this.invadeCasualty += invadeCasualty;
  }

  public int getDefenseCasualty() {
    return defenseCasualty;
  }

  public void addDefenseCasualty(int defenseCasualty) {
    this.defenseCasualty += defenseCasualty;
  }

  // public static void main(String[] args) {
  //   Faction Gallic = new Faction("Gallic");
  //   Faction Rome = new Faction("Rome");
  //   Province lu = new Province("Lugdunensis");
  //   Province bel = new Province("Belgica");

  //   lu.setOwner(Gallic);
  //   bel.setOwner(Rome);

  //   Unit horse = new Unit(1000, false, 3, 8, 5, 5, 5, 5, 5, "HorseArcher", new HorseArcherType(), false);
  //   Unit lancer = new Unit(1000, true, 3, 8, 5, 5, 5, 5, 5, "ElephantAfrican", new InfantryType(), false);
  //   lu.addMyUnit(horse);
  //   bel.addMyUnit(lancer);

  //   Battle battle = new Battle(lu, bel);
  //   System.out.println(battle.getMsg());
  // }
}
