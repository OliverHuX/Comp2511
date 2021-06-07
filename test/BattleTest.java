package test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

import org.junit.jupiter.api.Test;

import unsw.gloriaromanus.Battle;
import unsw.gloriaromanus.Faction;
import unsw.gloriaromanus.Province;
import unsw.gloriaromanus.Unit;
import unsw.gloriaromanus.unit.unitType.ArtilleryType;
import unsw.gloriaromanus.unit.unitType.HorseArcherType;
import unsw.gloriaromanus.unit.unitType.InfantryType;
import unsw.gloriaromanus.unit.unitType.MeleeCavalryType;

public class BattleTest {
    String adjacentInfo = "{\"Britannia\":false,\"Lugdunensis\":true,\"Belgica\":false,\"Germania Inferior\":false,\"Aquitania\":false,\"Germania Superior\":false,\"Alpes Graiae et Poeninae\":false,\"XI\":false,\"Alpes Cottiae\":false,\"Alpes Maritimae\":false,\"IX\":false,\"Narbonensis\":false,\"Tarraconensis\":false,\"Baetica\":false,\"Lusitania\":false,\"Raetia\":false,\"Noricum\":false,\"X\":false,\"VIII\":false,\"VII\":false,\"VI\":false,\"IV\":false,\"V\":false,\"I\":false,\"III\":false,\"Sicilia\":false,\"Pannonia Superior\":false,\"Pannonia Inferior\":false,\"Dalmatia\":false,\"II\":false,\"Sardinia et Corsica\":false,\"Moesia Superior\":false,\"Dacia\":false,\"Moesia Inferior\":false,\"Thracia\":false,\"Macedonia\":false,\"Achaia\":false,\"Bithynia et Pontus\":false,\"Cilicia\":false,\"Creta et Cyrene\":false,\"Cyprus\":false,\"Aegyptus\":false,\"Arabia\":false,\"Iudaea\":false,\"Syria\":false,\"Africa Proconsularis\":false,\"Numidia\":false,\"Mauretania Caesariensis\":false,\"Mauretania Tingitana\":false,\"Galatia et Cappadocia\":false,\"Lycia et Pamphylia\":false,\"Asia\":false,\"Armenia Mesopotamia\":false}";

    Faction Rome = new Faction("Rome");
    Faction Gaul = new Faction("Gaul");

    Province brita = new Province("Britannia");
    Province lu = new Province("Lugdunensis");
    Province bel = new Province("Belgica");
    Unit range_1 = new Unit(40, true, 5, 5, 5, 5, 5, 5, 500, "Archer", new InfantryType(), false);
    Unit range_2 = new Unit(40, true, 5, 5, 5, 5, 5, 5, 500, "Ballista", new ArtilleryType(), false);

    @Test
    public void checkStartCondition() {
        assertEquals(brita.battle(bel, adjacentInfo), "cannot attack remote province\n");
        assertEquals(brita.battle(lu, adjacentInfo), "cannot attack friendly province\n");
    }
    @Test
    public void checkStart() {
        brita.addEnemyProvince("Lugdunensis");
        assertNotEquals(brita.battle(lu, adjacentInfo), "cannot attack friendly province\n");
    }

    @Test
    public void checkBattleInvadeF() {
        lu.setOwner(Rome);
        brita.setOwner(Gaul);
        lu.addMyUnit(range_1);
        Battle battle = new Battle(brita, lu);
        assertEquals(battle.getMsg(), "Start Battle: Province [name=Britannia] VS Province [name=Lugdunensis]\n\nProvince [name=Britannia] fail to conquer\nMy army loss");
        assertEquals(brita.getOwnerName(), "Gaul");
    }

    @Test
    public void checkBattleInvadeS() {
        lu.setOwner(Rome);
        brita.setOwner(Gaul);
        brita.addMyUnit(range_1);
        Battle battle = new Battle(brita, lu);
        assertEquals(battle.getMsg(), "Start Battle: Province [name=Britannia] VS Province [name=Lugdunensis]\n\nProvince [name=Lugdunensis] fail to defense\nMy army win");
        assertEquals(lu.getOwnerName(), "Gaul");
    }
    @Test
    public void checkDraw() {
        lu.setOwner(Rome);
        brita.setOwner(Gaul);
        Battle battle = new Battle(brita, lu);
        assertEquals(battle.getMsg(), "Start Battle: Province [name=Britannia] VS Province [name=Lugdunensis]\n\nDraw\n");
        assertEquals(brita.getOwnerName(), "Gaul");
        assertEquals(lu.getOwnerName(), "Rome");
    }

    @Test
    public void testBattleMorale_1() {
        Unit druit_1 = new Unit(30, false, 5, 10, 5, 5, 5, 5, 5, "Druid", new InfantryType(), false);
        Unit druit_2 = new Unit(30, false, 5, 10, 5, 5, 5, 5, 5, "Druid", new InfantryType(), false);
        Unit druit_3 = new Unit(30, false, 5, 10, 5, 5, 5, 5, 5, "Druid", new InfantryType(), false);
        Unit druit_4 = new Unit(30, false, 5, 10, 5, 5, 5, 5, 5, "Druid", new InfantryType(), false);
        Unit druit_5 = new Unit(30, false, 5, 10, 5, 5, 5, 5, 5, "Druid", new InfantryType(), false);
        Unit druit_6 = new Unit(30, false, 5, 10, 5, 5, 5, 5, 5, "Druid", new InfantryType(), false);

        Unit pool = new Unit(30, false, 4, 10, 3, 3, 3, 3, 3, "Archer", new InfantryType(), false);
        lu.addMyUnit(pool);

        brita.setOwner(Rome);

        lu.setOwner(Gaul);

        brita.addMyUnit(druit_1);
        Battle battle_1 = new Battle(brita, lu);
        assertEquals(druit_1.getBattleMorale(battle_1), 11);
        assertEquals(pool.getBattleMorale(battle_1), 10);

        brita.addMyUnit(druit_2);
        Battle battle_2 = new Battle(brita, lu);
        assertEquals(druit_1.getBattleMorale(battle_2), 12);
        assertEquals(pool.getBattleMorale(battle_2), 9);

        brita.addMyUnit(druit_3);
        Battle battle_3 = new Battle(brita, lu);
        assertEquals(druit_1.getBattleMorale(battle_3), 13);

        brita.addMyUnit(druit_4);
        Battle battle_4 = new Battle(brita, lu);
        assertEquals(druit_4.getBattleMorale(battle_4), 14);

        brita.addMyUnit(druit_5);
        Battle battle_5 = new Battle(brita, lu);
        assertEquals(druit_1.getBattleMorale(battle_5), 15);
        assertEquals(pool.getBattleMorale(battle_5), 8);


        brita.addMyUnit(druit_6);
        Battle battle_6 = new Battle(brita, lu);
        assertEquals(druit_5.getBattleMorale(battle_6), 15);
        assertEquals(pool.getBattleMorale(battle_5), 8);

    }
    @Test
    public void testBattleMorale_2() {
        Unit druit_1 = new Unit(30, false, 5, 10, 5, 5, 5, 5, 5, "Druid", new InfantryType(), false);
        Unit druit_2 = new Unit(30, false, 5, 10, 5, 5, 5, 5, 5, "Druid", new InfantryType(), false);
        Unit druit_3 = new Unit(30, false, 5, 10, 5, 5, 5, 5, 5, "Druid", new InfantryType(), false);
        Unit druit_4 = new Unit(30, false, 5, 10, 5, 5, 5, 5, 5, "Druid", new InfantryType(), false);
        Unit druit_5 = new Unit(30, false, 5, 10, 5, 5, 5, 5, 5, "Druid", new InfantryType(), false);
        Unit druit_6 = new Unit(30, false, 5, 10, 5, 5, 5, 5, 5, "Druid", new InfantryType(), false);

        Unit nodruit_1 = new Unit(30, false, 5, 10, 5, 5, 5, 5, 5, "Archer", new InfantryType(), false);
        Unit nodruit_2 = new Unit(30, false, 5, 10, 5, 5, 5, 5, 5, "Archer", new InfantryType(), false);

        Unit pool = new Unit(30, false, 4, 10, 3, 3, 3, 3, 3, "Archer", new InfantryType(), false);
        lu.addMyUnit(pool);

        brita.setOwner(Rome);
        brita.addMyUnit(nodruit_1);
        brita.addMyUnit(nodruit_2);

        lu.setOwner(Gaul);

        brita.addMyUnit(druit_1);
        Battle battle_1 = new Battle(lu, brita);
        assertEquals(druit_1.getBattleMorale(battle_1), 11);
        assertEquals(pool.getBattleMorale(battle_1), 10);

        brita.addMyUnit(druit_2);
        Battle battle_2 = new Battle(lu, brita);
        assertEquals(druit_1.getBattleMorale(battle_2), 12);
        assertEquals(pool.getBattleMorale(battle_2), 9);

        brita.addMyUnit(druit_3);
        Battle battle_3 =new Battle(lu, brita);
        assertEquals(druit_1.getBattleMorale(battle_3), 13);

        brita.addMyUnit(druit_4);
        Battle battle_4 = new Battle(lu, brita);
        assertEquals(druit_4.getBattleMorale(battle_4), 14);

        brita.addMyUnit(druit_5);
        Battle battle_5 = new Battle(lu, brita);
        assertEquals(druit_1.getBattleMorale(battle_5), 15);
        assertEquals(pool.getBattleMorale(battle_5), 8);


        brita.addMyUnit(druit_6);
        Battle battle_6 = new Battle(lu, brita);
        assertEquals(druit_5.getBattleMorale(battle_6), 15);
        assertEquals(pool.getBattleMorale(battle_5), 8);

    }

    @Test
    public void hoplitTest() {
        Unit hoplite = new Unit(30, false, 5, 5, 5, 5, 5, 5, 5, "Hoplite", new InfantryType(), false);
        assertEquals(hoplite.getSpeed(),3);
        assertEquals(hoplite.getDefenseSkill(),10);
        assertEquals(hoplite.getShieldDefense(), 10);
        assertEquals(hoplite.getArmour(), 10);
        Unit pikemen = new Unit(30, false, 5, 5, 5, 5, 5, 5, 5, "Pikemen", new InfantryType(), false);
        assertEquals(pikemen.getSpeed(),3);
        assertEquals(pikemen.getDefenseSkill(),10);
        assertEquals(pikemen.getShieldDefense(), 10);
        assertEquals(pikemen.getArmour(), 10);
    }
    @Test
    public void checkInvaderBehavious() {
        lu.setOwner(Rome);
        brita.setOwner(Gaul);
        Unit strong = new Unit(10000, true, 100, 100, 200, 100, 100, 100, 100, "Archer", new InfantryType(), false);
        Unit weak = new Unit(1, true, 2, 2, 2, 2, 2, 2, 2, "Archer", new InfantryType(), false);
        lu.addMyUnit(strong);
        brita.addMyUnit(weak);
        Battle battle = new Battle(lu, brita);
        battle.start();
        assertEquals(strong.checkmove(1), false);
    }

    @Test
    public void checkMaxEngagement() {
        lu.setOwner(Rome);
        brita.setOwner(Gaul);
        Unit strong = new Unit(10000, true, 100, 100, 200, 100, 100, 100, 100, "Archer", new InfantryType(), false);
        Unit weak = new Unit(1, true, 2, 2, 2, 2, 2, 2, 2, "Archer", new InfantryType(), false);
        lu.addMyUnit(strong);
        brita.addMyUnit(weak);
        Battle battle = new Battle(lu, brita);
        battle.setEngageNum(200);
        assertEquals(battle.getMsg(), "Start Battle: Province [name=Lugdunensis] VS Province [name=Britannia]\n\nDraw\n");
    }
    @Test
    public void drawTest() {
        lu.setOwner(Rome);
        brita.setOwner(Gaul);
        Unit strong = new Unit(10000, true, 100, 100, 200, 100, 100, 100, 100, "Archer", new InfantryType(), false);
        Unit weak = new Unit(1, true, 2, 2, 2, 2, 2, 2, 2, "Archer", new InfantryType(), false);
        lu.addMyUnit(strong);
        brita.addMyUnit(weak);
        Battle battle = new Battle(lu, brita);
        battle.setEngageNum(199);
        battle.start();
        assertEquals(strong.getCurrentState().toString(), "WasInBattle");
    }
    @Test
    public void routeTest() {
        lu.setOwner(Rome);
        bel.setOwner(Gaul);
        bel.addMyUnit(range_1);
        lu.addMyUnit(range_2);
        assertEquals(lu.getMyUnits().size(), 1);

        Battle battle = new Battle(lu, bel);
        range_1.setSpareState();
        battle.endSkirmish(range_1, range_2);
        assertEquals(bel.getMyUnits().size(), 0);
        assertEquals(battle.getDefenseUnit().size(), 0);
        
        range_2.setSpareState();
        battle.endSkirmish(range_1, range_2);
        assertEquals(lu.getMyUnits().size(), 1);
        assertEquals(battle.getInvadeUnit().size(), 0);
    }
    @Test
    public void invaderrouteTest() {
        lu.setOwner(Rome);
        bel.setOwner(Gaul);
        bel.addMyUnit(range_1);
        lu.addMyUnit(range_2);
        range_1.setInBattleState();
        range_2.setInBattleState();

        Battle battle = new Battle(lu, bel);
        battle.endSkirmish(range_1, range_2);
        assertEquals(bel.getMyUnits().size(), 1);
        assertEquals(battle.getDefenseUnit().size(), 1);
        
        range_2.setNumTroops(0);
        battle.endSkirmish(range_1, range_2);
        assertEquals(lu.getMyUnits().size(), 0);
        assertEquals(battle.getInvadeUnit().size(), 0);
    }
    @Test
    public void completeBattle() {
        Faction Gallic = new Faction("Gallic");
        Faction Rome = new Faction("Rome");

        lu.setOwner(Gallic);
        bel.setOwner(Rome);

        Unit berserker_1 = new Unit(100, false, 4, 4, 4, 4, 4, 4, 4, "Berserker", new InfantryType(), false);
        Unit elephant_1 = new Unit(100, false, 3, 3, 5, 5, 5, 5, 5, "ElephantAfrican", new MeleeCavalryType(), false);
        Unit pikemen_1 = new Unit(100, false, 3, 3, 5, 5, 5, 5, 5, "Pikemen", new InfantryType(), false);
        Unit javelin_1 = new Unit(100, false, 3, 3, 5, 5, 5, 5, 5, "JavelinSkirmisher", new InfantryType(), false);
        Unit horseA_1 = new Unit(100, true, 3, 3, 5, 5, 5, 5, 5, "HorseArcher", new HorseArcherType(), false);
        Unit rome_1 = new Unit(100, true, 3, 3, 5, 5, 5, 5, 5, "RomeLegionary", new InfantryType(), false);
        Unit other_1 = new Unit(100, true, 3, 3, 5, 5, 5, 5, 5, "RomeLegionary", new InfantryType(), false);
        lu.addMyUnit(berserker_1);
        lu.addMyUnit(elephant_1);
        lu.addMyUnit(pikemen_1);
        lu.addMyUnit(javelin_1);
        lu.addMyUnit(horseA_1);
        lu.addMyUnit(rome_1);
        lu.addMyUnit(other_1);


        Unit berserker_2 = new Unit(100, false, 4, 4, 4, 4, 4, 4, 4, "Berserker", new InfantryType(), false);
        Unit elephant_2 = new Unit(100, false, 3, 3, 5, 5, 5, 5, 5, "ElephantAfrican", new MeleeCavalryType(), false);
        Unit pikemen_2 = new Unit(100, false, 3, 3, 5, 5, 5, 5, 5, "Pikemen", new InfantryType(), false);
        Unit javelin_2 = new Unit(100, true, 3, 3, 5, 5, 5, 5, 5, "JavelinSkirmisher", new InfantryType(), false);
        Unit horseA_2 = new Unit(100, true, 3, 3, 5, 5, 5, 5, 5, "HorseArcher", new HorseArcherType(), false);
        Unit rome_2 = new Unit(100, true, 3, 3, 5, 5, 5, 5, 5, "RomeLegionary", new InfantryType(), false);
        Unit other_2 = new Unit(100, true, 3, 3, 5, 5, 5, 5, 5, "RomeLegionary", new InfantryType(), false);

        bel.addMyUnit(berserker_2);
        bel.addMyUnit(elephant_2);
        bel.addMyUnit(pikemen_2);
        bel.addMyUnit(javelin_2);
        bel.addMyUnit(horseA_2);
        bel.addMyUnit(rome_2);
        bel.addMyUnit(other_2);


        Battle battle = new Battle(lu, bel);
        battle.start();
        if(!bel.getOwnerName().equals("Rome")) {
            assertEquals(bel.getOwnerName(), "Gallic");
            assertEquals(battle.getEngagedInvadeU().size(), bel.getMyUnits().size());
        }
    }

    @Test
    public void javelinTest() {
        Faction Gallic = new Faction("Gallic");
        Faction Rome = new Faction("Rome");

        lu.setOwner(Gallic);
        bel.setOwner(Rome);

        Unit other_1 = new Unit(10, true, 3, 3, 5, 5, 5, 5, 5, "RomeLegionary", new InfantryType(), false);
        Unit javelin_1 = new Unit(100, true, 3, 3, 5, 5, 5, 5, 5, "JavelinSkirmisher", new InfantryType(), false);
        Unit other_2 = new Unit(100, true, 3, 3, 5, 5, 5, 5, 5, "RomeLegionary", new InfantryType(), false);
        lu.addMyUnit(other_1);
        bel.addMyUnit(javelin_1);
        bel.addMyUnit(other_2);

        
        Battle battle = new Battle(bel, lu);
        battle.start();
        if(!bel.getOwnerName().equals("Rome")) {
            assertEquals(bel.getOwnerName(), "Gallic");
            assertEquals(battle.getEngagedInvadeU().size(), bel.getMyUnits().size());
        }
    }

    @Test
    public void BerserkerTest() {
        Faction Gallic = new Faction("Gallic");
        Faction Rome = new Faction("Rome");

        lu.setOwner(Gallic);
        bel.setOwner(Rome);

        Unit other_1 = new Unit(10, true, 3, 3, 5, 5, 5, 5, 5, "RomeLegionary", new InfantryType(), false);
        Unit berserker_1 = new Unit(100, false, 3, 3, 5, 5, 5, 5, 5, "Berserker", new InfantryType(), false);
        Unit other_2 = new Unit(100, true, 3, 3, 5, 5, 5, 5, 5, "RomeLegionary", new InfantryType(), false);
        lu.addMyUnit(other_1);
        lu.addMyUnit(berserker_1);
        bel.addMyUnit(other_2);

        Battle battle = new Battle(lu, bel);
        battle.start();
        if(!bel.getOwnerName().equals("Rome")) {
            assertEquals(bel.getOwnerName(), "Gallic");
            assertEquals(battle.getEngagedInvadeU().size(), bel.getMyUnits().size());
        }
    }

    @Test
    public void meleeCTest() {
        Faction Gallic = new Faction("Gallic");
        Faction Rome = new Faction("Rome");

        lu.setOwner(Gallic);
        bel.setOwner(Rome);

        Unit other_1 = new Unit(100, false, 3, 3, 5, 5, 5, 5, 5, "RomeLegionary", new InfantryType(), false);
        Unit elephant = new Unit(30, false, 3, 3, 5, 5, 5, 5, 5, "ElephantAfrican", new MeleeCavalryType(), false);
        lu.addMyUnit(elephant);
        bel.addMyUnit(other_1);

        Battle battle = new Battle(lu, bel);
        battle.start();
        if(!bel.getOwnerName().equals("Rome")) {
            assertEquals(bel.getOwnerName(), "Gallic");
            assertEquals(battle.getEngagedInvadeU().size(), bel.getMyUnits().size());
        }
    }
    @Test
    public void BerserkerinRange() {
        Faction Gallic = new Faction("Gallic");
        Faction Rome = new Faction("Rome");

        lu.setOwner(Gallic);
        bel.setOwner(Rome);

        Unit berserker = new Unit(100, false, 3, 10, 5, 5, 5, 5, 5, "Berserker", new InfantryType(), false);
        Unit lancer = new Unit(100, true, 3, 3, 10, 5, 5, 5, 5, "Lancer", new InfantryType(), false);
        lu.addMyUnit(berserker);
        bel.addMyUnit(lancer);

        Battle battle = new Battle(bel, lu);
        battle.start();
        if(!bel.getOwnerName().equals("Rome")) {
            assertEquals(bel.getOwnerName(), "Gallic");
            assertEquals(battle.getEngagedInvadeU().size(), bel.getMyUnits().size());
        }
    }
    @Test
    public void HorseArcherinRange() {
        Faction Gallic = new Faction("Gallic");
        Faction Rome = new Faction("Rome");
        Province lu = new Province("Lugdunensis");
        Province bel = new Province("Belgica");
    
        lu.setOwner(Gallic);
        bel.setOwner(Rome);
    
        Unit horse = new Unit(1000, false, 3, 8, 5, 5, 5, 5, 5, "HorseArcher", new HorseArcherType(), false);
        Unit lancer = new Unit(1000, true, 3, 8, 5, 5, 5, 5, 5, "ElephantAfrican", new InfantryType(), false);
        lu.addMyUnit(horse);
        bel.addMyUnit(lancer);

        Battle battle = new Battle(lu, bel);
        battle.start();
        if(!bel.getOwnerName().equals("Rome")) {
            assertEquals(bel.getOwnerName(), "Gallic");
            assertEquals(battle.getEngagedInvadeU().size(), bel.getMyUnits().size());
        }
    }

    @Test
    public void JavlininRange() {
        Faction Gallic = new Faction("Gallic");
        Faction Rome = new Faction("Rome");
        Province lu = new Province("Lugdunensis");
        Province bel = new Province("Belgica");
    
        lu.setOwner(Gallic);
        bel.setOwner(Rome);
    
        Unit javlin = new Unit(100, false, 3, 8, 5, 5, 5, 5, 5, "JavelinSkirmisher", new InfantryType(), false);
        Unit lancer = new Unit(100, true, 3, 8, 5, 5, 5, 5, 5, "Berserker", new InfantryType(), false);
        lu.addMyUnit(javlin);
        bel.addMyUnit(lancer);

        Battle battle = new Battle(lu, bel);
        battle.start();
        if(!bel.getOwnerName().equals("Rome")) {
            assertEquals(bel.getOwnerName(), "Gallic");
            assertEquals(battle.getEngagedInvadeU().size(), bel.getMyUnits().size());
        }
    }

    @Test
    public void BerserkerInMe() {
        Faction Gallic = new Faction("Gallic");
        Faction Rome = new Faction("Rome");
        Province lu = new Province("Lugdunensis");
        Province bel = new Province("Belgica");
    
        lu.setOwner(Gallic);
        bel.setOwner(Rome);
    
        Unit javlin = new Unit(100, false, 3, 11, 5, 5, 5, 5, 5, "JavelinSkirmisher", new InfantryType(), false);
        Unit lancer = new Unit(100, false, 3, 11, 5, 5, 5, 5, 5, "Berserker", new InfantryType(), false);
        lu.addMyUnit(javlin);
        bel.addMyUnit(lancer);

        Battle battle = new Battle(lu, bel);
        battle.start();
        if(!bel.getOwnerName().equals("Rome")) {
            assertEquals(bel.getOwnerName(), "Gallic");
            assertEquals(battle.getEngagedInvadeU().size(), bel.getMyUnits().size());
        }
    }
}
