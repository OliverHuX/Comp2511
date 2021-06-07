package test;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.Test;

import unsw.gloriaromanus.Battle;
import unsw.gloriaromanus.Faction;
import unsw.gloriaromanus.Province;
import unsw.gloriaromanus.Skirmish;
import unsw.gloriaromanus.Unit;
import unsw.gloriaromanus.unit.unitType.ArtilleryType;
import unsw.gloriaromanus.unit.unitType.InfantryType;

public class SkirmishTest {
  
  Faction Rome = new Faction("Rome");
  Faction Gaul = new Faction("Gaul");

  Province brita = new Province("Britannia");
  Province lu = new Province("Lugdunensis");
  Province bel = new Province("Belgica");
  Unit range_1 = new Unit(40, true, 5, 5, 5, 5, 5, 5, 500, "Archer", new InfantryType(), false);
  Unit range_2 = new Unit(40, true, 5, 5, 5, 5, 5, 5, 500, "Ballista", new ArtilleryType(), false);

  @Test
  public void invaderinFieldTest() {
    lu.setOwner(Rome);
    bel.setOwner(Gaul);
    bel.addMyUnit(range_1);
    lu.addMyUnit(range_2);
    range_1.setInBattleState();
    range_2.setInBattleState();

    Battle battle = new Battle(lu, bel);
    Skirmish skir = new Skirmish(range_2, range_1, battle);

    range_1.setFleeState();
    assertEquals(skir.defenserInField(), true);
    range_1.setInBattleState();
    assertEquals(skir.defenserInField(), true);
    range_1.setSpareState();
    assertEquals(skir.defenserInField(), false);
    range_1.setWasInBattleState();
    assertEquals(skir.defenserInField(), false);

    range_1.setInBattleState();
    range_1.setNumTroops(0);
    assertEquals(skir.defenserInField(), false);

    

    range_2.setFleeState();
    assertEquals(skir.invaderinField(), true);
    range_2.setInBattleState();
    assertEquals(skir.invaderinField(), true);
    range_2.setSpareState();
    assertEquals(skir.invaderinField(), false);
    range_2.setWasInBattleState();
    assertEquals(skir.invaderinField(), false);

    range_2.setInBattleState();
    range_2.setNumTroops(0);
    assertEquals(skir.invaderinField(), false);
  }
}
