package unsw.gloriaromanus;

import com.google.gson.annotations.JsonAdapter;

// import unsw.gloriaromanus.unit.unitType.HorseArcherType;
// import unsw.gloriaromanus.unit.unitType.InfantryType;
import unsw.gloriaromanus.unit.unitType.UnitProperty;
import unsw.gloriaromanus.unit.unitstate.Flee;
import unsw.gloriaromanus.unit.unitstate.InBattle;
import unsw.gloriaromanus.unit.unitstate.Spare;
import unsw.gloriaromanus.unit.unitstate.UnitState;
import unsw.gloriaromanus.unit.unitstate.WasInBattle;

/**
 * Represents a basic unit of soldiers
 * 
 * incomplete - should have heavy infantry, skirmishers, spearmen, lancers, heavy cavalry, elephants, chariots, archers, slingers, horse-archers, onagers, ballista, etc...
 * higher classes include ranged infantry, cavalry, infantry, artillery
 * 
 * current version represents a heavy infantry unit (almost no range, decent armour and morale)
 */
@JsonAdapter(unsw.gloriaromanus.StoreData.UnitDeserializer.class)
public class Unit{
    private int numTroops;  // the number of troops in this unit (should reduce based on depletion)
    private boolean range;  // range of the unit
    private int armour;  // armour defense
    private int morale;  // resistance to fleeing
    private int speed;  // ability to disengage from disadvantageous battle
    private int attack;  // can be either missile or melee attack to simplify. Could improve implementation by differentiating!
    private int defenseSkill;  // skill to defend in battle. Does not protect from arrows!
    private int shieldDefense; // a shield
    private int upkeep;
    private int movePoint;
    private int initialMovePoint;
    private UnitObserver province;
    private String name;

    private boolean mercenary;
    
    private  transient UnitProperty mType;
    private  transient UnitState currentState;
    private  transient UnitState spareState;
    private  transient UnitState fleeState;
    private  transient UnitState inBattleState;
    private  transient UnitState wasInBattleState;

    public Unit(int numTroops, boolean range, int armour, int morale, int speed, int attack, int defenseSkill,
    int shieldDefense, int upkeep, String name, UnitProperty type, boolean isMercenary) {
        this.name = name;
        this.numTroops = numTroops;
        this.range = range;
        this.armour = armour;
        this.morale = morale;
        this.speed = speed;
        this.attack = attack;
        this.defenseSkill = defenseSkill;
        this.shieldDefense = shieldDefense;
        this.upkeep = upkeep;
        this.mercenary = isMercenary;
        this.province = null;
        this.mType = type;

        setMovePoint();
        spareState = new Spare(this);
        fleeState = new Flee(this);
        inBattleState = new InBattle(this);
        wasInBattleState = new WasInBattle(this);
        currentState = spareState;

        //hoplite, pikemen special - double melee defense, half of speed
        if(name == "Hoplite") {
            this.speed = (int) Math.round(0.5 * speed);
            this.defenseSkill = defenseSkill * 2;
            this.shieldDefense = shieldDefense * 2;
            this.armour = armour * 2;
        }
    }

    /**
     * check if the movepoint, and state
     * @param point
     * @return true if the unit can take the move
     */
    public boolean checkmove(int point){
        if(mercenary) return true;
        if(!(currentState instanceof WasInBattle) && point <= movePoint) return true;
        return false;
    }

    /**
     * change the morale in specific situation
     * @param battle
     * @return morale in the battle
     */
    public int getBattleMorale(Battle battle) {
        //Druid Special -- morale
        int eDruid = 0;
        int mDruid = 0;
        if(battle.getDefenseP().equals(province)) {
            for(Unit u: battle.getInvadeUnit()) {
                if(u.getName() == "Druid") {
                    eDruid += 1;
                    if(eDruid == 5) break;
                }
            }
            for(Unit u: battle.getDefenseUnit()) {
                if(u.getName() == "Druid") {
                    mDruid += 1;
                    if(mDruid == 5) break;
                }
            }
        }else {
            for(Unit u: battle.getDefenseUnit()) {
                if(u.getName() == "Druid") {
                    eDruid += 1;
                    if(eDruid == 5) break;
                }
            } 
            for(Unit u: battle.getInvadeUnit()) {
                if(u.getName() == "Druid") {
                    mDruid += 1;
                    if(mDruid == 5) break;
                }
            }
        }
        return (int) Math.round(((mDruid*0.1 - eDruid*0.05) + 1 )* morale);
    }

    /**
     * 
     * @param point cost of movementpoint
     * @param province dest
     */
    public void move(int point, UnitObserver p){
        movePoint -= point;
        disattachObserver();
        attachObserver(p);
    }

    public int getDefenseSkill() {
        return defenseSkill;
    }

    public boolean getRange() {
        return range;
    }

    public int getUpkeep() {
        return upkeep;
    }

    public UnitProperty getmType() {
        return mType;
    }

    /**
     * only use this function in create constructor
     * call by unitProperty
     * @param point default movepoint
     */
    public void setMovePoint(int point) {
        initialMovePoint = point;
        movePoint = point;
    }

    /**
     * only use when reload the game
     * @param point
     */
    public void setRemainMovePoint(int point) {
        movePoint = point;
    }

    public int getInitialMovePoint() {
        return initialMovePoint;
    }

    public int getRemainMovePoint() {
        return movePoint;
    }

    /**
     * mercenaries have higher upkeep
     */
    public void increaseUpkeep(){
        upkeep = (int) Math.round(upkeep * 1.3);
    }

    public int getNumTroops() {
        return numTroops;
    }

    public void setNumTroops(int numTroops) {
        this.numTroops = numTroops;
    }

    public boolean isRange() {
        return range;
    }

    public void setRange(boolean range) {
        this.range = range;
    }

    public int getArmour() {
        return armour;
    }

    public void setArmour(int armour) {
        this.armour = armour;
    }

    public int getMorale() {
        return morale;
    }

    /**
     * +-(x%)
     * @param rate change rate of morale
     */
    public void setMorale(double rate) {
        int num = (int) Math.round(this.morale * rate);
        addMorale(num);
    }

    /**
     * 
     * @param morale change of morale
     */
    public void addMorale(int morale) {
        if(this.morale + morale >= 1) this.morale += morale;
        else this.morale = 1;
    }

    public int getSpeed() {
        return speed;
    }

    public int getAttack() {
        return attack;
    }

    public int getDefense() {
        return defenseSkill + shieldDefense;
    }

    

    public boolean isMercenary() {
        return mercenary;
    }

    public void setMType(UnitProperty m){
        mType = m;
    }

    public void setMovePoint(){
        mType.setMovePoint(this);
    }

    /**
     * update the effect of mercenary to province
     */
    public void toMercenary(){
        mType.setPropertyM(this, province);
    }

    public UnitState getCurrentState() {
        return currentState;
    }

    public void setCurrentState(UnitState currentState) {
        this.currentState = currentState;
    }

    public void setSpareState() {
        currentState = spareState;
    }

    public void setFleeState() {
        currentState = fleeState;
    }

    public void setInBattleState() {
        currentState = inBattleState;
    }

    public void setWasInBattleState() {
        currentState = wasInBattleState;
    }
    
    @Override
    public String toString() {
        if(mercenary) return "Mercenary " + getName();
        return getName();
    }

    public String getName() {
        return name;
    }

    public String engage(Unit enemy, boolean range, int engageNum, Engagement e) {
        return currentState.engage(enemy, range, engageNum, e);
    }

    public void chooseAction(Unit enemy, Battle battle, Engagement e) {
        currentState.chooseAction(enemy, battle, e);
    }

    /**
     * only use in Battle
     * Unit route from battle. InBattle to Spare
     */
    public boolean isRouted() {
        if(currentState instanceof Spare) return true;
        return false;
    }

    public void minusTroop(int num) {
        numTroops -= num;
    }

    public int getShieldDefense() {
        return shieldDefense;
    }

    public void attachObserver(UnitObserver p) {
        province = p;
        if(mercenary) toMercenary();
    }

    public void disattachObserver() {
        province = null;
    }

    /**
     * after ending the turn, reset the state and movepoint
     */
    public void updateByTurn() {
        movePoint = initialMovePoint;
        setSpareState();
    }
    
    /**
     * 
     * @return faction name
     */
    public String getFaction() {
        return province.factionToUnit();
    }

    // public static void main(String[] args) {
    //     Province p = new Province("Rome");
    //     Unit u = new Unit(1, true, 1, 9, 9, 9, 9, 9, 8, "HorseArcher", new HorseArcherType(), false);
    //     p.addMyUnit(u);
    //     System.out.println(u.toString());
    //     Unit i = new Unit(1, true, 1, 9, 9, 9, 9, 9, 8, "HorseArcher", new InfantryType(), true);
    //     System.out.println(i.getRemainMovePoint());
    // }
}
