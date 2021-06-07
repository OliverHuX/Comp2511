package test;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.ArrayList;

import org.junit.jupiter.api.Test;

import unsw.gloriaromanus.Faction;
import unsw.gloriaromanus.Province;
import unsw.gloriaromanus.RecruitAction;
import unsw.gloriaromanus.RecruitItem;
import unsw.gloriaromanus.Unit;
import unsw.gloriaromanus.unit.unitType.ArtilleryType;
import unsw.gloriaromanus.unit.unitType.HorseArcherType;
import unsw.gloriaromanus.unit.unitType.InfantryType;
import unsw.gloriaromanus.unit.unitType.MeleeCavalryType;

public class RecuitTest {
  @Test
  public void testUpdateFactionWealth() {
    Faction Rome = new Faction("Rome");
    Province Gaul = new Province("Gaul");
    Gaul.setOwner(Rome);
    Rome.setTreasury(10);
    int[] property;
    property = new int[] {40, 1, 1, 1, 5, 5, 5, 5, 5, 10, 5};
    assertEquals(RecruitAction.recruitUnit("Archer", Gaul, false, property, new InfantryType()), true);
    assertEquals(RecruitAction.recruitUnit("Archer", Gaul, false, property, new InfantryType()), false);
  }
  @Test
  public void testAddToWaitList() {
    Faction Rome = new Faction("Rome");
    Province Gaul = new Province("Gaul");
    Gaul.setOwner(Rome);
    Rome.setTreasury(10);
    int[] property;
    property = new int[] {40, 1, 1, 1, 5, 5, 5, 5, 5, 10, 5};
    RecruitAction.recruitUnit("Archer", Gaul, false, property, new InfantryType());
    assertEquals(Gaul.getRecruitWaitList().get(0).getUnit().getNumTroops(), 40);
    assertEquals(Gaul.getRecruitWaitList().get(0).getTurn(), 5);
    assertEquals(Gaul.getRecruitWaitList().get(0).getUnit().getInitialMovePoint(), 10);
    assertEquals(Gaul.getRecruitWaitList().get(0).getUnit().getRemainMovePoint(), 10);
  }
  @Test
  public void testRecuit() {
    Faction Rome = new Faction("Rome");
    Province Gaul = new Province("Gaul");
    Gaul.setOwner(Rome);
    Rome.setTreasury(100);
    //free army
    int[] property = new int[] {40, 1, 1, 1, 5, 5, 5, 5, 5, 10, 5};
    RecruitAction.recruitUnit("Archer", Gaul, false, property, new InfantryType());
  }
  @Test
  public void testUnrecruitableMercenary() {
    Faction Rome = new Faction("Rome");
    Province Gaul = new Province("Gaul");
    Gaul.setOwner(Rome);
    Rome.setTreasury(20);
    //free army
    int[] property = new int[] {40, 1, 1, 1, 5, 5, 5, 5, 5, 10, 5};
    Gaul.setMercenary(new ArrayList<>());
    RecruitAction.recruitUnit("Archer", Gaul, true, property, new InfantryType());
    assertEquals(Gaul.getMyUnits(),new ArrayList<>());
    assertEquals(Gaul.sufficientTreasury(20), true);
  }
  @Test
  public void testRecuitMercenaryInfantry() {
    Faction Rome = new Faction("Rome");
    Province Gaul = new Province("Gaul");
    Gaul.setOwner(Rome);
    Rome.setTreasury(40);
    Gaul.setWealth(0);
    //free army
    int[] property = new int[] {40, 1, 1, 10, 5, 5, 5, 5, 1, 10, 5};
    RecruitAction.recruitUnit("Archer", Gaul, false, property, new InfantryType());
    Unit notMercenary = Gaul.getRecruitWaitList().get(0).getUnit();
    Gaul.updateByTurn();
    assertEquals(Gaul.getMyUnits().size(), 1);
    assertEquals(Gaul.sufficientTreasury(20), true);

    Gaul.setMercenary(new ArrayList<>());
    assertEquals(RecruitAction.recruitUnit("Archer", Gaul, true, property, new InfantryType()), false);

    ArrayList<String> m = new ArrayList<>();
    m.add("Archer");
    Gaul.setMercenary(m);

    assertEquals(RecruitAction.recruitUnit("Archer", Gaul, true, property, new InfantryType()), true);
    assertEquals(Gaul.getMyUnits().size(), 2);
  
    assertEquals(Rome.getTreasury(), 10);

    assertEquals(notMercenary.getMorale(), 9);

    assertEquals(Gaul.getMyUnits().get(0).toString(), "Archer");
    assertEquals(Gaul.getMyUnits().get(1).toString(), "Mercenary Archer");
  }
  @Test
  public void testRecuitMercenaryMeleeCavalry() {
    Faction Rome = new Faction("Rome");
    Province Gaul = new Province("Gaul");
    Gaul.setOwner(Rome);
    Rome.setTreasury(50);
    Gaul.setWealth(0);
    int[] property = new int[] {40, 1, 1, 10, 5, 5, 5, 5, 2, 10, 5};
    RecruitAction.recruitUnit("Batislla", Gaul, false, property, new ArtilleryType());
    int[] property_1 = new int[] {40, 1, 1, 10, 5, 5, 5, 5, 1, 10, 5};
    RecruitAction.recruitUnit("Druid", Gaul, false, property_1, new ArtilleryType());
    Unit notMercenary = Gaul.getRecruitWaitList().get(1).getUnit();
    Gaul.updateByTurn();

    ArrayList<String> m = new ArrayList<>();
    m.add("Elephant");
    Gaul.setMercenary(m);

    assertEquals(RecruitAction.recruitUnit("Elephant", Gaul, true, property, new MeleeCavalryType()), true);
    assertEquals(Gaul.getMyUnits().size(), 2);

    assertEquals(RecruitAction.recruitUnit("Archer", Gaul, true, property, new MeleeCavalryType()), false);

    Gaul.updateByTurn();
    assertEquals(Gaul.getMyUnits().size(), 3);
  
    assertEquals(Gaul.sufficientTreasury(1), false);
    assertEquals(notMercenary.getMorale(), 8);
  }
  @Test
  public void testRecuitMercenaryHorseArcher() {
    Faction Rome = new Faction("Rome");
    Province Gaul = new Province("Gaul");
    Gaul.setOwner(Rome);
    Rome.setTreasury(40);
    Gaul.setWealth(0);
    //free army
    int[] property = new int[] {40, 0, 1, 10, 5, 5, 4, 3, 1, 10, 5};
    RecruitAction.recruitUnit("Batislla", Gaul, false, property, new ArtilleryType());
    Unit notMercenary = Gaul.getRecruitWaitList().get(0).getUnit();
    Gaul.updateByTurn();

    ArrayList<String> m = new ArrayList<>();
    m.add("Horse");
    Gaul.setMercenary(m);

    assertEquals(RecruitAction.recruitUnit("Horse", Gaul, true, property, new HorseArcherType()), true);
    Unit unit = Gaul.getMyUnits().get(0);
    assertEquals(Gaul.getMyUnits().size(), 2);
  
    assertEquals(unit.getNumTroops(), 40);
    assertEquals(unit.getRange(), false);
    assertEquals(unit.getArmour(), 1);
    assertEquals(unit.getMorale(), 10);
    assertEquals(unit.getSpeed(), 5);
    assertEquals(unit.getAttack(), 5);
    assertEquals(unit.getDefense(), 7);
    assertEquals(unit.getShieldDefense(), 3);
    assertEquals(unit.getUpkeep(), 5);
  
    assertEquals(Gaul.sufficientTreasury(11), false);
    assertEquals(notMercenary.getMorale(), 10);
  }

  @Test
  public void testGetUnit() {
    String content = "{\"RomeLegionary\":{\"numTroops\":50,\"range\":false,\"armour\":7,\"morale\":8,\"speed\":6,\"attack\":7,\"defenseSkill\":8,\"shieldDefense\":8,\"turn\":1,\"cost\":50,\"upkeep\":4,\"type\":\"Infantry\"},\"Druid\":{\"numTroops\":3,\"range\":false,\"armour\":2,\"morale\":10,\"speed\":3,\"attack\":1,\"defenseSkill\":1,\"shieldDefense\":1,\"turn\":1,\"cost\":50,\"upkeep\":3,\"type\":\"Infantry\"},\"Berserker\":{\"numTroops\":10,\"range\":false,\"armour\":3,\"morale\":3,\"speed\":6,\"attack\":20,\"defenseSkill\":3,\"shieldDefense\":0,\"turn\":1,\"cost\":70,\"upkeep\":6,\"type\":\"Infantry\"},\"ElephantAfrican\":{\"numTroops\":3,\"range\":false,\"armour\":6,\"morale\":10,\"speed\":7,\"attack\":8,\"defenseSkill\":5,\"shieldDefense\":5,\"turn\":2,\"cost\":80,\"upkeep\":10,\"type\":\"MeleeCavalry\"},\"ElephantIndia\":{\"numTroops\":4,\"range\":false,\"armour\":6,\"morale\":10,\"speed\":7,\"attack\":8,\"defenseSkill\":5,\"shieldDefense\":5,\"turn\":2,\"cost\":80,\"upkeep\":10,\"type\":\"MeleeCavalry\"},\"Chariot_TwoHorses\":{\"numTroops\":5,\"range\":false,\"armour\":8,\"morale\":10,\"speed\":8,\"attack\":6,\"defenseSkill\":6,\"shieldDefense\":8,\"turn\":2,\"cost\":75,\"upkeep\":7,\"type\":\"MeleeCavalry\"},\"Chariot_ThreeHorses\":{\"numTroops\":7,\"range\":false,\"armour\":8,\"morale\":10,\"speed\":8,\"attack\":6,\"defenseSkill\":6,\"shieldDefense\":8,\"turn\":2,\"cost\":75,\"upkeep\":7,\"type\":\"MeleeCavalry\"},\"Chariot_FourHorses\":{\"numTroops\":7,\"range\":false,\"armour\":8,\"morale\":10,\"speed\":8,\"attack\":6,\"defenseSkill\":6,\"shieldDefense\":8,\"turn\":1,\"cost\":75,\"upkeep\":7,\"type\":\"MeleeCavalry\"},\"HeavyCavalry\":{\"numTroops\":40,\"range\":false,\"armour\":8,\"morale\":8,\"speed\":8,\"attack\":8,\"defenseSkill\":8,\"shieldDefense\":8,\"turn\":1,\"cost\":60,\"upkeep\":6,\"type\":\"MeleeCavalry\"},\"Lancer\":{\"numTroops\":40,\"range\":false,\"armour\":4,\"morale\":4,\"speed\":7,\"attack\":8,\"defenseSkill\":7,\"shieldDefense\":5,\"turn\":1,\"cost\":40,\"upkeep\":4,\"type\":\"MeleeCavalry\"},\"Pikemen\":{\"numTroops\":50,\"range\":false,\"armour\":6,\"morale\":7,\"speed\":5,\"attack\":7,\"defenseSkill\":5,\"shieldDefense\":8,\"turn\":2,\"cost\":30,\"upkeep\":4,\"type\":\"Infantry\"},\"Hoplite\":{\"numTroops\":50,\"range\":false,\"armour\":7,\"morale\":10,\"speed\":8,\"attack\":8,\"defenseSkill\":8,\"shieldDefense\":9,\"turn\":1,\"cost\":80,\"upkeep\":4,\"type\":\"Infantry\"},\"HorseArcherLight\":{\"numTroops\":30,\"range\":true,\"armour\":3,\"morale\":5,\"speed\":10,\"attack\":5,\"defenseSkill\":5,\"shieldDefense\":5,\"turn\":1,\"cost\":40,\"upkeep\":4,\"type\":\"HorseArcher\"},\"HorseArcherNormal\":{\"numTroops\":30,\"range\":true,\"armour\":6,\"morale\":5,\"speed\":8,\"attack\":6,\"defenseSkill\":6,\"shieldDefense\":5,\"turn\":1,\"cost\":40,\"upkeep\":4,\"type\":\"HorseArcher\"},\"HorseArcherHeavy\":{\"numTroops\":20,\"range\":true,\"armour\":8,\"morale\":7,\"speed\":7,\"attack\":9,\"defenseSkill\":6,\"shieldDefense\":6,\"turn\":2,\"cost\":60,\"upkeep\":6,\"type\":\"HorseArcher\"},\"JavelinSkirmisher\":{\"numTroops\":40,\"range\":true,\"armour\":3,\"morale\":5,\"speed\":4,\"attack\":5,\"defenseSkill\":3,\"shieldDefense\":7,\"turn\":1,\"cost\":40,\"upkeep\":4,\"type\":\"Infantry\"},\"Archer\":{\"numTroops\":40,\"range\":true,\"armour\":7,\"morale\":6,\"speed\":4,\"attack\":7,\"defenseSkill\":3,\"shieldDefense\":3,\"turn\":1,\"cost\":40,\"upkeep\":4,\"type\":\"Infantry\"},\"Slinger\":{\"numTroops\":40,\"range\":true,\"armour\":7,\"morale\":6,\"speed\":4,\"attack\":7,\"defenseSkill\":4,\"shieldDefense\":6,\"turn\":1,\"cost\":50,\"upkeep\":5,\"type\":\"Infantry\"},\"Scorpion\":{\"numTroops\":20,\"range\":true,\"armour\":7,\"morale\":6,\"speed\":2,\"attack\":5,\"defenseSkill\":3,\"shieldDefense\":6,\"turn\":2,\"cost\":30,\"upkeep\":4,\"type\":\"Artillery\"},\"Onager\":{\"numTroops\":30,\"range\":true,\"armour\":7,\"morale\":6,\"speed\":2,\"attack\":8,\"defenseSkill\":3,\"shieldDefense\":6,\"turn\":2,\"cost\":80,\"upkeep\":7,\"type\":\"Artillery\"},\"Ballista\":{\"numTroops\":30,\"range\":true,\"armour\":8,\"morale\":8,\"speed\":2,\"attack\":12,\"defenseSkill\":3,\"shieldDefense\":6,\"turn\":2,\"cost\":100,\"upkeep\":8,\"type\":\"Artillery\"}}";
    int[] property;
    property = new int[] {40, 1, 7, 6, 4, 7, 4, 6, 1, 50, 5};
    assertArrayEquals(RecruitAction.getUnitData(content, "Slinger"), property);
    assertEquals(RecruitAction.getUnitProperty(content, "Slinger").toString(), "Infantry");

    property = new int[] {3, 0, 2, 10, 3, 1, 1, 1, 1, 50, 3};
    assertArrayEquals(RecruitAction.getUnitData(content, "Druid"), property);

    assertEquals(RecruitAction.getUnitProperty(content, "HorseArcherLight").toString(), "HorseArcher");
    assertEquals(RecruitAction.getUnitProperty(content, "Lancer").toString(), "MeleeCavalry");
    assertEquals(RecruitAction.getUnitProperty(content, "Ballista").toString(), "Artillery");
  }

  @Test
  public void cancelRecruit() {
    Faction Rome = new Faction("Rome");
    Province Gaul = new Province("Gaul");
    Gaul.setOwner(Rome);
    Rome.setTreasury(20);

    int[] property = new int[] {40, 0, 1, 10, 5, 5, 4, 3, 1, 10, 5};
    RecruitAction.recruitUnit("Elephant", Gaul, false, property, new MeleeCavalryType());
    RecruitAction.recruitUnit("Elephant1", Gaul, false, property, new MeleeCavalryType());
    assertEquals(Gaul.getRecruitWaitList().size(), 2);
    RecruitItem item = Gaul.getRecruitWaitList().get(1);

    assertEquals(Rome.getTreasury(), 0);
    Gaul.cancleRecruit(item);
    assertEquals(Rome.getTreasury(), 5);
    Gaul.cancleRecruit(item);
    assertEquals(Rome.getTreasury(), 5);
  }
}
