package test;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.ArrayList;

import org.junit.jupiter.api.Test;

import unsw.gloriaromanus.Faction;
import unsw.gloriaromanus.GloriaRomanusSystem;
import unsw.gloriaromanus.Province;
import unsw.gloriaromanus.Unit;
import unsw.gloriaromanus.unit.unitType.ArtilleryType;
import unsw.gloriaromanus.unit.unitType.InfantryType;

public class FactionTest {
    Faction Rome = new Faction("Rome");
    Faction Gaul = new Faction("Gaul");
    Province Syria = new Province("Syria");
    Province Dacia = new Province("Decia");
    String provinceName = "X";
    Unit range_1 = new Unit(40, true, 5, 5, 5, 5, 5, 5, 0, "Archer", new InfantryType(), false);
    Unit range_2 = new Unit(40, true, 5, 5, 5, 5, 5, 5, 0, "Ballista", new ArtilleryType(), false);


    @Test
    public void checkAddProvince() {
        Rome.addProvince("x");
        Rome.addProvince(Syria);
        Gaul.addProvince(Dacia);
        ArrayList<Province> p = Rome.getProvinces();
        assertEquals(p.size(), 2);
        p = Gaul.getProvinces();
        assertEquals(p.size(), 1);
        assertEquals(Rome.getTurn(), 0);

    }

    @Test
    public void checkAddDeleteEnemy() {
        Rome.addProvince(Syria);
        Rome.addProvinceEnemy("Decia");
        Rome.addEnemy(Gaul);
        assertEquals(Rome.isEnemy("X"), false);
        assertEquals(Rome.isEnemy("Decia"), true);
        assertEquals(Rome.getEnemy().size(), 1);
        assertEquals(Rome.getEnemyProvince().size(), 1);
        Rome.deleteProvinceEnemy("Decia");
        Rome.deleteEnemy(Gaul);
        assertEquals(Rome.getEnemy().size(), 0);
        assertEquals(Rome.getEnemyProvince().size(), 0);
    }

    @Test
    public void checkWealth() {
        Syria.setOwner(Rome);
        assertEquals(Rome.getWealth(), 1000);
        assertEquals(Rome.getTreasury(), 100);
        Rome.updateByTurn();
        assertEquals(Rome.getWealth(), 1085);
        assertEquals(Rome.getTreasury(), 115);
        assertEquals(Rome.incurExpense(99999), false);
    }

    @Test
    public void checkWealthDefaultSetting() {
        //100 treasury, 0.15 tax rate, 1000 wealth
        Syria.setOwner(Rome);
        Dacia.setOwner(Gaul);
        Syria.addMyUnit(range_1);
        Dacia.addMyUnit(range_2);
        Rome.endTurn();
        Gaul.endTurn();
        Rome.updateByTurn();
        Gaul.updateByTurn();
        assertEquals(Syria.getWealth(), 1085);
        assertEquals(Dacia.getWealth(), 1085);
        assertEquals(Rome.getWealth(), 1085);
        assertEquals(Gaul.getWealth(), 1085);
        assertEquals(Rome.getTreasury(), 115);
        assertEquals(Gaul.getTreasury(), 115);
    }

    @Test
    public void endTurn() {
        GloriaRomanusSystem sys = new GloriaRomanusSystem();
        sys.addFaction(Rome);
        sys.addFaction(Gaul);
        Syria.setOwner(Rome);
        Dacia.setOwner(Gaul);

        sys.endTurn();
        assertEquals(sys.getTurn(), 0);
        assertEquals(Rome.getTreasury(), 100);

        sys.setFationP1("Rome");
        sys.endTurn();
        assertEquals(sys.getTurn(), 0);
        assertEquals(Rome.getTurn(), 0);
        assertEquals(Rome.getTreasury(), 100);

        sys.setFationP2("Gaul");
        sys.setCurrentP(Rome);

        sys.endTurn();
        assertEquals(Rome.getTurn(), 1);
        assertEquals(Gaul.getTurn(), 0);
        assertEquals(sys.getTurn(), 0);
        assertEquals(Rome.getTreasury(), 100);


        sys.endTurn();
        assertEquals(Rome.getTurn(), 1);
        assertEquals(Gaul.getTurn(), 1);
        assertEquals(sys.getTurn(), 1);
        assertEquals(Rome.getTreasury(), 115);

    }
}
