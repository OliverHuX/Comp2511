package unsw.gloriaromanus.unit.unitstate;

import java.util.Random;

import unsw.gloriaromanus.Battle;
import unsw.gloriaromanus.Engagement;
import unsw.gloriaromanus.Unit;


public class InBattle implements UnitState {
  private Unit myunit;
  
  public InBattle(Unit myunit) {
    this.myunit = myunit;
  }
  
  private String chasing(Unit enemy, Boolean range, int engageNum, Engagement e) {
    int kill = inflict(enemy, range, engageNum);
    if(kill > enemy.getNumTroops()) kill = enemy.getNumTroops();

    setDeath(e, kill, enemy);
    return "My" + myunit.toString() + " is chassing " + enemy.toString() + "\n" + 
    "inflict casualties: " + kill + "\n";
  }
  
  private String flight(Unit enemy, Boolean range, int engageNum, Engagement e) {
    int dead = casualtiyNum(enemy, range, engageNum);
    myunit.minusTroop(dead);
    setDeath(e, dead, myunit);

    int kill = inflict(enemy, range, engageNum);
    if(kill > enemy.getNumTroops()) kill = enemy.getNumTroops();

    enemy.minusTroop(kill);
    setDeath(e, dead, enemy);
    return "My army inflict casualties: " + kill + ", and die: " + dead +  ", remains: " + myunit.getNumTroops() + "\n";
  }

  public int casualtiyNum(Unit enemy, Boolean range, int engageNum) {
    return enemy.getCurrentState().inflict(myunit, range, engageNum);
  }

  @Override
  public String engage(Unit enemy, Boolean range, int engageNum, Engagement e){
    //enemy is fleeing
    if(enemy.getCurrentState() instanceof Flee) {
      return chasing(enemy, range, engageNum, e);
    //inflict
    }else {
      return flight(enemy, range, engageNum, e);
    //enemy has routed
    }
  }
  public void setDeath(Engagement e, int num, Unit unit) {
    if(unit.equals(e.getInvadeU())) {
      e.setInvadeC(num);
    }else{
      e.setDefenseC(num);
    }
  } 

  @Override
  public int inflict(Unit enemy, Boolean range, int engageNum) {
    int num;

    Random rand = new Random();
    //my melee unit can not inflict in range engagement
    if(!myunit.getRange() && range) num = 0;

    //my range unit in range engagement
    else if(myunit.getRange() && range) {
      //(size of enemy unit at start of engagement x 10%) x (Missile attack damage of unit/(effective armor of enemy unit + effective shield of enemy unit)) x (N+1)

      //berserker special - no Amour and shield
      if(enemy.getName() == "Berserker" && (enemy.getFaction() == "Gallic" || enemy.getFaction() == "Celtic Briton" || enemy.getFaction() == "Germanic")) {
        num = (int) Math.round((enemy.getNumTroops() * 0.1) + enemy.getDefenseSkill()/2 * (rand.nextGaussian() + 1));
      //Javelin special - enemy have half armour in range
      }else if(myunit.getName() == "JavelinSkirmisher") {
        num = (int) Math.round((enemy.getNumTroops() * 0.1) + (myunit.getAttack()/(enemy.getShieldDefense()+enemy.getArmour()/2)) * (rand.nextGaussian() + 1));
      }else if(enemy.getmType().toString() == "HorseArcher"){
        num = (int) Math.round((enemy.getNumTroops() * 0.1) + ((myunit.getAttack()/2)/(enemy.getShieldDefense()+enemy.getArmour())) * (rand.nextGaussian() + 1));
      }else {
        num = (int) Math.round((enemy.getNumTroops() * 0.1) + (myunit.getAttack()/(enemy.getShieldDefense()+enemy.getArmour())) * (rand.nextGaussian() + 1));
      }

    //Melee Engage : (size of enemy unit at start of engagement x 10%) x (Effective melee attack damage of unit/(effective armor of enemy unit + effective shield of enemy unit + effective defense skill of enemy unit)) x (N+1)
    }else {

      //my missile unit only have half of melee attack damage
      if(myunit.getRange()) num = (int) Math.round((enemy.getNumTroops() * 0.1) + (myunit.getAttack() * 0.5/(enemy.getShieldDefense()+enemy.getArmour() + enemy.getDefense())) * (rand.nextGaussian() + 1));
      else {
        //berserker special - double melee attack
        if(enemy.getName().equals("Berserker") && (enemy.getFaction().equals("Gallic") || enemy.getFaction().equals("Celtic Briton") || enemy.getFaction().equals("Germanic"))) {
          num = (int) Math.round((enemy.getNumTroops() * 0.1) + (myunit.getAttack() * 2/(enemy.getShieldDefense()+enemy.getArmour() + enemy.getDefense())) * (rand.nextGaussian() + 1));
        }
        //MeleeCavalry Special - double attack
        else if(myunit.getmType().toString().equals("MeleeCavalry") && myunit.getNumTroops() < enemy.getNumTroops()/2){
          num = (int) Math.round((enemy.getNumTroops() * 0.1) + (myunit.getAttack() * 2/(enemy.getShieldDefense()+enemy.getArmour() + enemy.getDefense())) * (rand.nextGaussian() + 1));
        }else if(engageNum == 4 && myunit.getmType().toString().equals("Infantry")) {
          num = (int) Math.round((enemy.getNumTroops() * 0.1) + ((myunit.getAttack() + enemy.getShieldDefense())/(enemy.getArmour() + enemy.getDefense())) * (rand.nextGaussian() + 1));
        }else {
          num = (int) Math.round((enemy.getNumTroops() * 0.1) + (myunit.getAttack()/(enemy.getArmour() + enemy.getDefense())) * (rand.nextGaussian() + 1));
        }
      }
    }
    if(num > enemy.getNumTroops()) return num = enemy.getNumTroops();
    if(num < 0) num = 0;
    return num;
  }
  
  @Override
  public void chooseAction(Unit enemy, Battle battle, Engagement engagement) {
    //(casualties suffered by the unit during the engagement/number of 
    //troops in the unit at the start of the engagement)/(casualties 
    //suffered by the opposing unit during the engagement/number of troops in the opposing unit at the start of the engagement) x 10%
    double increase;
    //defense unit or invade unit
    if(enemy.getNumTroops() == 0 || myunit.getNumTroops() == 0) return;

    if(engagement.getDefenseU().equals(myunit)) {
      increase = ((double)engagement.getDefenseC()/(double)myunit.getNumTroops())/((double)engagement.getInvadeC()/enemy.getNumTroops());
    }else{
      increase = ((double)engagement.getInvadeC()/(double)myunit.getNumTroops())/((double)engagement.getDefenseC()/enemy.getNumTroops());
    }
    double pos;
    if(myunit.getmType().toString().equals("MeleeCavalry") && myunit.getNumTroops() < enemy.getNumTroops()/2){
      pos = (double)(1 - (myunit.getBattleMorale(battle) * 1.5)*0.1) + (increase * 0.1);
    }else{
      pos = (double)(1 - myunit.getBattleMorale(battle)*0.1) + (increase * 0.1);
    }
    if(pos > 1) pos = 1;
    if(pos < 0.05) pos = 0.05;
    //Berserker Special - infinite morale lead to negative chance to flee
    if(myunit.getName().equals("Berserker")) pos = 0.05;
    if(Math.random() < pos) {
      myunit.setFleeState();
    }
  }

  @Override
  public String toString() {
    return "InBattle";
  }
}
