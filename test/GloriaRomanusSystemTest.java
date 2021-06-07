package test;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import unsw.gloriaromanus.Faction;
import unsw.gloriaromanus.GloriaRomanusSystem;
import unsw.gloriaromanus.Province;
import unsw.gloriaromanus.Unit;
import unsw.gloriaromanus.unit.unitType.ArtilleryType;
import unsw.gloriaromanus.unit.unitType.InfantryType;

public class GloriaRomanusSystemTest {
    String content = "{\"Rome\":[\"Lugdunensis\",\"Lusitania\",\"Lycia et Pamphylia\",\"Macedonia\",\"Mauretania Caesariensis\",\"Mauretania Tingitana\",\"Moesia Inferior\",\"Moesia Superior\",\"Narbonensis\",\"Noricum\",\"Numidia\",\"Pannonia Inferior\",\"Pannonia Superior\",\"Raetia\",\"Sardinia et Corsica\",\"Sicilia\",\"Syria\",\"Tarraconensis\",\"Thracia\",\"V\",\"VI\",\"VII\",\"VIII\",\"X\",\"XI\"],\"Gaul\":[\"Achaia\",\"Aegyptus\",\"Africa Proconsularis\",\"Alpes Cottiae\",\"Alpes Graiae et Poeninae\",\"Alpes Maritimae\",\"Aquitania\",\"Arabia\",\"Armenia Mesopotamia\",\"Asia\",\"Baetica\",\"Belgica\",\"Bithynia et Pontus\",\"Britannia\",\"Cilicia\",\"Creta et Cyrene\",\"Cyprus\",\"Dacia\",\"Dalmatia\",\"Galatia et Cappadocia\",\"Germania Inferior\",\"Germania Superior\",\"I\",\"II\",\"III\",\"IV\",\"IX\",\"Iudaea\"]}";

    Faction Rome = new Faction("Rome");
    Faction Gaul = new Faction("Gaul");
    Province Syria = new Province("Syria");
    Province Dacia = new Province("Decia");
    Unit archer = new Unit(40, true, 5, 5, 5, 5, 5, 5, 500, "Archer", new InfantryType(), false);
    Unit ballista = new Unit(40, true, 5, 5, 5, 5, 5, 5, 500, "Ballista", new ArtilleryType(), false);
    Unit lancer = new Unit(40, true, 5, 5, 5, 5, 5, 5, 500, "Lancer", new InfantryType(), false);
    GloriaRomanusSystem gameSystem = new GloriaRomanusSystem();
    
    @Test
    public void checkSystem(){
        Syria.setOwner(Rome);
        Dacia.setOwner(Gaul);
        gameSystem.addFaction(Rome);
        gameSystem.addFaction(Gaul);
        assertEquals(gameSystem.getFaction().get(0), Rome);
        assertEquals(gameSystem.getFaction().get(1), Gaul);
        gameSystem.setFationP1("Rome");
        gameSystem.setFationP2("empty");
        gameSystem.setFationP2("Gaul");
        gameSystem.setCurrentP(Rome);
        assertEquals(gameSystem.getCurrentPlayer(), Rome);
        List<String> goals = new ArrayList<>();
        List<String> signs = new ArrayList<>();
        goals.add("WEALTH");
        goals.add("TREASURY");
        signs.add("AND");
        gameSystem.setGoalsManually(goals);
        gameSystem.setLogicalSignsManually(signs);
        Rome.setTreasury(9999999);
        Gaul.setTreasury(9999999);
        Syria.setWealth(99999999);
        Dacia.setWealth(99999999);
        Syria.updateByTurn();
        Dacia.updateByTurn();
        assertEquals(gameSystem.checkGoals(1), true);
        Rome.setTreasury(99);
        assertEquals(gameSystem.checkGoals(1), false);
        Rome.setWealth(99);
        assertEquals(gameSystem.checkGoals(1), false);
        
        
        signs.clear();
        signs.add("OR");
        assertEquals(gameSystem.checkGoals(1), false);
        Rome.setWealth(99999999);
        assertEquals(gameSystem.checkGoals(1), true);
        Rome.setTreasury(99999999);
        assertEquals(gameSystem.checkGoals(1), true);
        Rome.setWealth(99);
        assertEquals(gameSystem.checkGoals(1), true);
        Rome.setWealth(99999999);

        signs.clear();
        signs.add("AND");
        signs.add("AND");
        goals.add("CONQUEST");
        assertEquals(gameSystem.checkGoals(2), false);
        Rome.setTreasury(99);
        assertEquals(gameSystem.checkGoals(2), false);
        Rome.setWealth(99);
        assertEquals(gameSystem.checkGoals(2), false);
        Rome.setWealth(99999999);
        Rome.setTreasury(99999999);
        Dacia.setOwner(Rome);
        assertEquals(gameSystem.checkGoals(2), true);



        signs.clear();
        signs.add("AND");
        signs.add("OR");
        assertEquals(gameSystem.checkGoals(2), true);//w t c
        Rome.setTreasury(99);
        assertEquals(gameSystem.checkGoals(2), true);//w c
        Dacia.setOwner(Gaul);
        assertEquals(gameSystem.checkGoals(2), false);//w
        Rome.setTreasury(99999999);
        assertEquals(gameSystem.checkGoals(2), true);//w t
        Rome.setWealth(99);
        assertEquals(gameSystem.checkGoals(2), false);//t
        Rome.setTreasury(99);
        assertEquals(gameSystem.checkGoals(2), false);//
        Dacia.setOwner(Rome);
        Rome.setWealth(99);
        //System.out.println(gameSystem.getCurrentPlayer().getWealth());
        //System.out.println(gameSystem.getCurrentPlayer().getTreasury());
        assertEquals(gameSystem.checkGoals(2), false);//c
        Rome.setTreasury(99999999);
        assertEquals(gameSystem.checkGoals(2), false);//c t
        
        signs.clear();
        Dacia.setOwner(Rome);
        Rome.setWealth(99999999);
        Rome.setTreasury(99);
        signs.add("OR");
        signs.add("AND");// w or t and c
        assertEquals(gameSystem.checkGoals(2), true);//w c
        Rome.setWealth(99);
        assertEquals(gameSystem.checkGoals(2), false);//c
        Rome.setTreasury(99999999);
        Dacia.setOwner(Rome);
        Rome.setWealth(99);
        assertEquals(gameSystem.checkGoals(2), true);//t c
        Rome.setWealth(99999999);
        assertEquals(gameSystem.checkGoals(2), true);//w t c
        Dacia.setOwner(Gaul);
        assertEquals(gameSystem.checkGoals(2), true);//w t
        Rome.setTreasury(99);
        assertEquals(gameSystem.checkGoals(2), true);//w
        Rome.setWealth(99);
        assertEquals(gameSystem.checkGoals(2), false);//
        Rome.setTreasury(99999999);
        assertEquals(gameSystem.checkGoals(2), false);//t


        signs.clear();
        signs.add("OR");
        signs.add("OR");
        assertEquals(gameSystem.checkGoals(2), true);
        Dacia.setOwner(Gaul);
        Rome.setTreasury(99);
        Rome.setWealth(99);
        assertEquals(gameSystem.checkGoals(2), false);


    }
    // @Test
    // public void createFaction() {
    //     GloriaRomanusSystem sys = new GloriaRomanusSystem();
    //     sys.createFaction("Rome", content);
    //     Faction Rome = sys.getFaction().get(0);
    //     assertEquals(Rome.getProvinces().get(0).getName(), "Lugdunensis");
    // }
        
}
