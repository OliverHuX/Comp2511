package test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

import org.junit.Test;

import unsw.gloriaromanus.Faction;
import unsw.gloriaromanus.Province;
import unsw.gloriaromanus.Unit;
import unsw.gloriaromanus.unit.unitType.InfantryType;

public class BankrupcyTest {
  Faction Rome = new Faction("Rome");
  Faction Gaul = new Faction("Gaul");

  Province brita = new Province("Britannia");
  Province lu = new Province("Lugdunensis");
  Province bel = new Province("Belgica");
  
  Unit range_1 = new Unit(40, true, 5, 5, 5, 5, 5, 5, 500, "Archer", new InfantryType(), false);


  @Test
  public void testUpkeep() {
    brita.setOwner(Rome);
    brita.addMyUnit(range_1);
    Rome.updateByTurn();
    // brita.upKeepUnit();
    assertEquals(Rome.getTreasury(), -500);
  }

  @Test
  public void borrow() {
    brita.setOwner(Rome);
    brita.addMyUnit(range_1);

    Rome.setTreasury(100);
    Gaul.setTreasury(100);
  
    Rome.borrow(Gaul, 100);
    assertEquals(Gaul.getTradeRequest().size(), 1);
    assertEquals(Gaul.getTradeRequest().get(0).getAmount(), 100);

    Gaul.lend(Gaul.getTradeRequest().get(0));
    
    assertEquals(Gaul.getTreasury(), 0);
    assertEquals(Rome.getTreasury(), 200);
    assertEquals(Gaul.getTradeRequest().size(), 0);
    assertEquals(Gaul.getTrades().size(), 1);
    assertEquals(Rome.getTrades().size(), 1);
    assertEquals(Rome.isInDebt(), true);

    int morale = range_1.getMorale();

    brita.bankruptcy();
    if(brita.getMyUnits().size() == 0) assertNotEquals(morale, brita.getMyUnits().get(0).getMorale());

    Rome.repayDebt(Rome.getTrades().get(0));
    assertEquals(Gaul.getTreasury(), 100);
    assertEquals(Rome.getTreasury(), 100);
    assertEquals(Gaul.getTrades().size(), 0);
    assertEquals(Rome.getTrades().size(), 0);
  }
}
