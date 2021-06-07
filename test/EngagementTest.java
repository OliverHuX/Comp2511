package test;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import unsw.gloriaromanus.Battle;
import unsw.gloriaromanus.Engagement;
import unsw.gloriaromanus.Faction;
import unsw.gloriaromanus.Province;
import unsw.gloriaromanus.Unit;
import unsw.gloriaromanus.unit.unitType.ArtilleryType;
import unsw.gloriaromanus.unit.unitType.InfantryType;

public class EngagementTest{
  Faction Rome = new Faction("Rome");
  Faction Gaul = new Faction("Gaul");
  Province Syria = new Province("Syria");
  Province Dacia = new Province("Decia");
  Unit range_1 = new Unit(40, true, 5, 5, 5, 5, 5, 5, 500, "Archer", new InfantryType(), false);
  Unit range_2 = new Unit(40, true, 5, 5, 5, 5, 5, 5, 500, "Ballista", new ArtilleryType(), false);
  Unit melee_1 = new Unit(40, false, 5, 5, 5, 5, 5, 5, 500, "Hopilite", new InfantryType(), false);
  Unit melee_2 = new Unit(40, false, 5, 5, 5, 5, 5, 5, 500, "Druid", new InfantryType(), false);


  @Test
  public void checkRange() {
    Syria.setOwner(Rome);
    Dacia.setOwner(Gaul);
    Syria.addMyUnit(range_1);
    Dacia.addMyUnit(range_2);
    Battle battle = new Battle(Syria, Dacia);
    Engagement mustRange = new Engagement(range_1, range_2, battle);

    assertEquals(mustRange.isRange(), true);
  }
  
  @Test
  public void checkMelee(){
    Syria.addMyUnit(melee_1);
    Dacia.addMyUnit(melee_2);
    Battle battle = new Battle(Syria, Dacia);
    Engagement mustRange = new Engagement(melee_1, melee_2, battle);

    assertEquals(mustRange.isRange(), false);
  }
}