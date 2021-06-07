package unsw.gloriaromanus;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;

import org.json.JSONObject;

import unsw.gloriaromanus.unit.unitType.HorseArcherType;


public class Province implements UnitObserver{
    private String name;
    private ArrayList<Unit> myUnits;
    private ArrayList<RecruitItem> recruitWaitList;
    private ArrayList<String> enemyProvince;
    private double townWealthRate;
    private double taxRate;
    private int numRecuitEachTurn;
    private transient ProvinceOwnership owner;
    private ArrayList<String> recuitableMercenry;
    private boolean lock;

    private int wealth;
    private int wealthGrowth;


    public Province(String name) {
        this.name = name;
        this.myUnits = new ArrayList<>();
        this.recruitWaitList = new ArrayList<>();
        this.enemyProvince = new ArrayList<>();
        this.recuitableMercenry = new ArrayList<>();
        this.taxRate = 0.15;
        this.numRecuitEachTurn = 2;
        this.townWealthRate = 0.1;
        this.wealth = 0;
        this.wealthGrowth = wealth;
        this.lock = false;
        generateMercenary();
    }

    /**
     * Province loss battle will take charge of switching owner
     * @param invader
     */
    public void setOwner(ProvinceOwnership enemy) {
        if(owner != null) owner.lostOwnership(this);
        enemy.gainOwnership(this);
        owner = enemy;
        myUnits.clear();
        //update new owner wealth
        setWealth(wealth);
    }
    /**
     * 
     * @param owner
     */
    public void loadOwner(ProvinceOwnership owner) {
        this.owner = owner;
    }

    public String getOwnerName() {
        return owner.getOwnerName();
    }

    /**
     * set the province to be enemy
     * @param name province name
     */
    public void addEnemyProvince(String name) {
        if(!enemyProvince.contains(name)){
            enemyProvince.add(name);
        }
    }

    public ArrayList<String> getEnemyProvince() {
        return enemyProvince;
    }

    public void removeEnemyProvince(String name) {
        enemyProvince.remove(name);
    }

    public boolean isEnemy(String provinceName){
        if(enemyProvince.contains(provinceName)) return true;
        return false;
    }

    public boolean isFactionEnemy(String faction) {
        return owner.isEnemy(faction);
    }

    public String battle(Province p, String adjecent) {
        if(!checkAdjacent(adjecent, p.getName())) return "cannot attack remote province\n";
        if(!isFactionEnemy(p.getOwnerName())) return "cannot attack friendly province\n";
        if(lock) return "cannot engage in battle this turn, province is locked\n";
        BattleResolver battle = new Battle(this, p);
        return battle.getMsg();
    }

    public void setLock(boolean lock) {
        this.lock = lock;
    }

    public boolean isLock() {
        return lock;
    }

    // public String agent(Province p, Unit u) {

    // }

    public void cancleRecruit(RecruitItem target) {
        for(int i = 0 ; i < recruitWaitList.size(); i++) {
            if (recruitWaitList.get(i).equals(target)) {
                recruitWaitList.remove(i);
                //refund half price
                notifyTreasury((int) Math.round(target.getCost() * 0.5));
                break;
            }
        }
    }


    public void addMyUnit(Unit myunit) {
        myunit.attachObserver(this);
        myUnits.add(myunit);
    }

    public void addMyUnits(ArrayList<Unit> units) {
        for(Unit u:units) {
            u.attachObserver(this);
            myUnits.add(u);
        }
    }

    public ProvinceOwnership getOwner() {
        return owner;
    }

    public void addRecruitItem(Unit unit, int turn, int cost) {
        RecruitItem r = new RecruitItem(unit, turn, cost);
        recruitWaitList.add(r);
    }

    public void updateRecruitItem(){
        for(int i = 0, j = 0; i < numRecuitEachTurn && recruitWaitList.size() > j; i++, j++){
            RecruitItem troop = recruitWaitList.get(j);
            troop.updateTurn();
            if(troop.getTurn() <= 0){
                addMyUnit(troop.getUnit());
                recruitWaitList.remove(troop);
                j--;
            }
        }
    }

    /**
     * update the wealth in the province and the faction, turn by turn
     */
    public void notifyWealth() {
        updateWealthGrowth();
        wealth = wealth + wealthGrowth - calculateTreasury();
        if(owner != null) owner.updateWealth(wealthGrowth - calculateTreasury());
    }

    /**
     * 
     * @return increase of treasury
     */
    public int calculateTreasury() {
        int income = getWealthGrowth();
        return (int) Math.round((income * taxRate));
    }

    public void notifyTreasury(int gold) {
        if(owner != null) owner.updateTreasury(gold);
    }

    public Boolean spend(int cost){
        if(sufficientTreasury(cost)) {
            owner.incurExpense(cost);
            return true;
        }else{
            return false;
        }
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public ArrayList<Unit> getMyUnits() {
        return new ArrayList<>(myUnits);
    }

    public ArrayList<Unit> getMyUnitsReference() {
        return myUnits;
    }

    public void setMyUnits(ArrayList<Unit> myUnits) {
        this.myUnits = myUnits;
    }

    public ArrayList<RecruitItem> getRecruitWaitList() {
        return recruitWaitList;
    }

    public void setRecruitWaitList(ArrayList<RecruitItem> recruitWaitList) {
        this.recruitWaitList = recruitWaitList;
    }

    public double getTownWealthRate() {
        return townWealthRate;
    }

    public void setTownWealthRate(Double townWealthRate) {
        this.townWealthRate = townWealthRate;
    }

    public double getTaxRate() {
        return taxRate;
    }

    public void setTaxRate(Double taxRate) {
        this.taxRate = taxRate;
    }

    public int getNumRecuitEachTurn() {
        return numRecuitEachTurn;
    }

    public void setNumRecuitEachTurn(int numRecuitEachTurn) {
        this.numRecuitEachTurn = numRecuitEachTurn;
    }

    public Boolean sufficientTreasury(int cost) {
        if(owner.getOwnerTreasury() >= cost) {
            return true;
        } else {
            return false;
        }
    }

    public void updateWealthGrowth() {
        int growth = (int) Math.round(wealth * townWealthRate);
        this.wealthGrowth = growth;
        taxEffect();
    }
    public int getWealthGrowth() {
        return wealthGrowth;
    }

    /**
     * only use when set the initial wealth, create or conquer
     * update total wealth
     * @param wealth
     */
    public void setWealth(int wealth) {
        this.wealth = wealth;
        owner.updateWealth(wealth);
    }

    public int getWealth() {
        return wealth;
    }

    public void effectWealth(int effect, String action) {
        if(action.equals("-")) {
            wealthGrowth -= effect;
        } else {
            wealthGrowth += effect;
        }
    }

    public void removeUnit(Unit u) {
        myUnits.remove(u);
    }

    public boolean BFS(JSONObject o, String src, String dest, HashMap<String, Boolean> visited, HashMap<String, String> pred) {
        LinkedList<String> queue = new LinkedList<String>(); 
        visited.replace(src, true);
        queue.add(src);
        //JSONObject o1 = o.getJSONObject("Asia");
        while(!queue.isEmpty()) {
            String province = queue.remove();
            JSONObject o1 = o.getJSONObject(province);
            for(String keyStr: o1.keySet()) {
                Object keyvalue = o1.get(keyStr);
                if((boolean) keyvalue && !visited.get(keyStr)) {
                    if(isEnemy(keyStr) && !keyStr.equals(dest)) {
                        continue;
                    }
                    //System.out.println("key: " + keyStr + " value: " + keyvalue);
                    visited.replace(keyStr, true);
                    pred.put(keyStr, province);
                    queue.add(keyStr);
                    if(keyStr.equals(dest)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    public LinkedList<String> getShortestPath(Province end, String content) {
        JSONObject o = new JSONObject(content);
        JSONObject o1 = o.getJSONObject(this.getName());//province string name changed by front end
        HashMap<String, Boolean> visited = new HashMap<>();
        HashMap<String, String> pred = new HashMap<>();
        
        o1.keySet().forEach(keyStr -> {
            pred.put(keyStr, null);
            visited.put(keyStr, false);
        });
        String src = this.getName();
        String dest = end.getName();//province string name changed by front end
        if(!BFS(o, src, dest, visited, pred)) {
            //System.out.println("Given source and destination" + "are not connected"); 
            return null;
        } else {
            LinkedList<String> path = new LinkedList<String>(); 
            String crawl = dest;
            path.add(crawl);
            while (pred.get(crawl) != null) {
                path.add(pred.get(crawl));
                crawl = pred.get(crawl);
            }
            /*System.out.println("The shortest path is the following"); 
            for (int i = path.size() - 1; i >= 0; i--) { 
                System.out.println(path.get(i) + " "); 
            }*/
            return path;
        }
    }

    public int getDistance(Province dest, String content) {
        LinkedList<String> path = getShortestPath(dest, content);
        int distance = 0;
        if(path != null) {
            distance = getShortestPath(dest, content).size() - 1;
        }
        return distance;
    }

    public boolean checkAdjacent(String provinceInfo, String dest) {
        JSONObject province = new JSONObject(provinceInfo);
        for(String keyStr: province.keySet()) {
            Object keyvalue = province.get(keyStr);
            if(keyStr.equals(dest)) {
                return (boolean) keyvalue;
            }
        }
        return false;
    }

    public boolean moveUnit(Unit unit, Province dest, String content)  {
        if(isEnemy(dest.getName())) return false;
        int distance = getDistance(dest, content);
        if(distance == 0 && !unit.isMercenary()) return false;
        if(dest.isLock()) return false;

        if(unit.checkmove(distance)) {
            myUnits.remove(unit);
            dest.addMyUnit(unit);
            unit.move(distance, dest);
            // System.out.println("here");
            // System.out.println(dest.getMyUnits().size());
            return true;
        }
        return false;
    }

    public boolean moveUnits(ArrayList<Unit> units, Province dest, String content) {
        if(isEnemy(dest.getName())) return false;
        int distance = getDistance(dest, content);
        if(dest.isLock()) return false;

        boolean normalCase = true;
        if(distance == 0) normalCase = false;


        boolean flag = true;
        for(Unit u: units) {
            if(!u.checkmove(distance)) {
                flag = false;
                break;
            }
            if(!u.isMercenary() && normalCase == false) {
                return false;
            }
        }

        if(flag) {
            for(Unit u: units) {
                u.move(distance, dest);
                dest.addMyUnit(u);
                myUnits.remove(u);
            }
            return true;
        }else {
            return false;
        }
    }


    //bankruptcy in extension
    public void upKeepUnit() {
        int cost = 0;
        for(Unit u: myUnits) {
            cost += u.getUpkeep();
        }
        owner.upkeep(cost);
    }

    /**
     * implement bankruptcy effect if faction in debt
     */
    public void bankruptcy() {
        Iterator<Unit> units = myUnits.iterator();
        if(owner.bankcrupcy()) {
            while(units.hasNext()) {
                Unit u = units.next();
                if(Math.random() < 0.1) {
                    units.remove();
                }else{
                    u.setMorale(-0.2);
                }
            }
        }
    }



    /**
     * apply tex effect on wealth
     * @return new wealthGrowth
     */
    public void taxEffect() {
        if(this.taxRate < 0.15) {
            effectWealth(10, "+");
        } else if(this.taxRate < 0.2) {
            return;
        } else if(this.taxRate < 0.25) {
            effectWealth(10, "-");
        } else {
            effectWealth(30, "-");
            updateMorale(-1);
        }
    }

    /**
     * reset the unit and recuimentList after ending a turn
    */
    public void updateByTurn() {
        notifyWealth();
        notifyTreasury(calculateTreasury());
        upKeepUnit();

        for(Unit u: myUnits) {
            u.updateByTurn();
        }

        updateRecruitItem();
        generateMercenary();

        bankruptcy();
        lock = false;
    }

    public ArrayList<String> getRecuitableMercenry() {
        return recuitableMercenry;
    }

    public boolean canBeMercenary(String name) {
        if(recuitableMercenry.contains(name)) return true;
        return false;
    }

    /**
     * Test purpose
     * @param types type of unit
     */
    public void setMercenary(ArrayList<String> types) {
        this.recuitableMercenry = types;
    } 

    public void generateMercenary() {
        recuitableMercenry.clear();
        ArrayList<String> mercenary = recuitableMercenry;
        ArrayList<String> horseArcher = new ArrayList<>();
        horseArcher.add("HorseArcherNormal");
        horseArcher.add("HorseArcherHeavy");
        ArrayList<String> mCavalry = new ArrayList<>();
        mCavalry.add("ElephantAfrican");
        mCavalry.add("Chariot_TwoHorses");
        mCavalry.add("HeavyCavalry");
        mCavalry.add("Lancer");
        ArrayList<String> infantry = new ArrayList<>();
        infantry.add("RomeLegionary");
        infantry.add("Druid");
        infantry.add("Berserker");
        infantry.add("Hoplite");
        infantry.add("JavelinSkirmisher");
        infantry.add("Archer");
        infantry.add("Slinger");
        for(String unit: horseArcher) {
            if(Math.random() < 0.03) {
                mercenary.add(unit);
            }
        }
        for(String unit: mCavalry) {
            if(Math.random() < 0.05) {
                mercenary.add(unit);
            }
        }
        for(String unit: infantry) {
            if(Math.random() < 0.1) {
                mercenary.add(unit);
            }
        }
    }

    @Override
    public String factionToUnit() {
        return owner.getOwnerName();
    }

    @Override
    public void updateMorale(int num) {
        for(Unit u:myUnits) {
            u.addMorale(num);
        }
    }

    @Override
    public void updateMorale(double num) {
        for(Unit u:myUnits) {
            u.setMorale(num);
        }
    }

    @Override
    public String toString() {
        return "Province [name=" + name + "]";
    }

    public static void main(String[] args) {
        try{
            Faction Rome = new Faction("Rome");
            Province b = new Province("Britannia");
            Province l = new Province("Lugdunensis");
            b.setOwner(Rome);
            l.setOwner(Rome);

            Unit u = new Unit(50, true, 5, 5, 5, 5, 5, 5, 5, "A", new HorseArcherType(), true);
            b.addMyUnit(u);

            String provinceInfo = ReadJson.getProvinceAdJsonObject("Britannia");
            System.out.println(b.checkAdjacent(provinceInfo, "a"));
            System.out.println(b.battle(l, provinceInfo));
            System.out.println(b.moveUnit(u, l, ReadJson.readProvinceAdjacency()));
            // System.out.println(ReadJson.readProvinceAdjacency());
        }catch(IOException e){
            e.printStackTrace();
        }
    }
}