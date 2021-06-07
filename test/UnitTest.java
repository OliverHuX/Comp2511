package test;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.Test;

import unsw.gloriaromanus.Unit;
import unsw.gloriaromanus.unit.unitType.InfantryType;

public class UnitTest {
  @Test
  public void testMorale() {
    Unit archer = new Unit(40, true, 5, 5, 5, 5, 5, 5, 500, "Archer", new InfantryType(), false);
    archer.addMorale(-100);
    assertEquals(archer.getMorale(), 1);
  }
}

