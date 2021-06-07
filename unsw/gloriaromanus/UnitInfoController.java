package unsw.gloriaromanus;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import org.json.JSONObject;

import javafx.fxml.FXML;
import javafx.scene.control.TextArea;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.text.Text;

public class UnitInfoController extends MenuController {
  @FXML
  private Text unitName;
  @FXML
  private Text numTroops;
  @FXML
  private Text range;
  @FXML
  private Text armour;
  @FXML
  private Text morale;
  @FXML
  private Text speed;
  @FXML
  private Text attack;
  @FXML
  private Text defenseSkill;
  @FXML
  private Text shieldDefense;
  @FXML
  private Text turn;
  @FXML
  private Text upkeep;
  @FXML
  private Text cost;
  @FXML
  private Text type;
  @FXML
  private TextArea special;
  @FXML
  private ImageView image;

  final private String legionaryS = "provides +1 morale to all friendly units in\nthe province. For every legionary eagle lost to the\nenemy (by the unit being destroyed \ndefending a province) all friendly units across all \nprovinces suffer a 0.2 penalty to morale until \nthe settlement is recaptured (down to a minimum of 1 morale)";
  final private String berserkers = "For all Gallic berserker units:\nunit receives infinite morale and double melee attack damage, \nbut has no armor or shield protection, in all battles";
  final private String meleeCS = "Where the army has fewer than \nhalf the number of units as the enemy,\nthis cavalry unit will double its charge attack damage, \nand have 50% higher morale";
  final private String hopliteS = "For all hoplite units:\nthese hoplites have \ndouble the melee defence, \nbut half of the speed, \nas they are otherwise configured to have.";
  final private String javeS = "In ranged engagements, \ntroops fighting these skirmishers only receive \nhalf the protection from armour \nthey would receive otherwise";
  final private String horseArcherS = "When enemy missile units engage \nthis unit of horse archers, \nthe enemy missile units will suffer a 50% \nloss to missile attack damage";
  final private String druidS = "Allied units in an army with druids \nenjoy a 10% bonus to morale,\n and enemy units suffer a 5% penalty to morale, \nwhilst the druids haven't routed.\n The effect of this ability is amplified by scalar addition,\n and can be amplified up to 5 times \n(e.g. 2 druids results in allies receiving 20% bonus \nto morale and enemy units suffering 10% penalty, \nhowever 6 druids provides 50% bonus and 25% penalty respectively)";
  final private String meleeIS = "For every 4th engagement by this \nunit of melee infantry per battle, \nthe value of shield defense is added \nto this unit's attack damage value";

  public void settup(String name) {
    try {
      String content = ReadJson.readUnitJson();
      JSONObject types = new JSONObject(content);
      JSONObject unit = types.getJSONObject(name);
      unitName.setText(name);
      showImage(name);
      numTroops.setText("Number: " + unit.getInt("numTroops"));
      if (unit.getBoolean("range")) {
        range.setText("AttackType: range");
      } else {
        range.setText("AttackType: melee");
      }
      armour.setText("Amour: " + unit.getInt("armour"));
      morale.setText("Morale: " + unit.getInt("morale"));
      speed.setText("Speed: " + unit.getInt("speed"));
      attack.setText("Attack: " + unit.getInt("attack"));
      defenseSkill.setText("DefenseSkill: " + unit.getInt("defenseSkill"));
      shieldDefense.setText("ShieldDefense: " + unit.getInt("shieldDefense"));
      turn.setText("Turns: " + unit.getInt("turn"));
      upkeep.setText("Upkeep: " + unit.getInt("upkeep"));
      cost.setText("Cost: " + unit.getInt("cost"));
      type.setText("Type: " + unit.getString("type"));

      setSpecialAbility(name, unit.getString("type"), unit.getBoolean("range"));
    } catch (IOException e) {
      e.printStackTrace();
      super.getParent().failMsg("cannot get unit infomation");
    }

  }

  private void setSpecialAbility(String name, String type, boolean range) {
    special.clear();
    
    if(type.equals("Infantry") && range) {
      special.setText(meleeIS);
    }
    if(name.equals("Druid")) {
      special.setText(druidS);
    }
    if(type.equals("HorseArcher")) {
      special.setText(horseArcherS);
    }
    if(name.equals("JavelinSkirmisher")){
      special.setText(javeS);
    }
    if(name.equals("Hoplite")) {
      special.setText(hopliteS);
    }
    if(type.equals("MeleeCavalry")) {
      special.setText(meleeCS);
    }
    if(name.equals("Berserker")) {
      special.setText(berserkers);
    }
    if(name.equals("RomeLegionary")) {
      special.setText(legionaryS);
    }
  }

  private void setImage(String url) throws FileNotFoundException {

    InputStream i = this.getClass().getResourceAsStream(url);
    Image im = new Image(i);
    image.setImage(im);
    image.setX(image.getFitWidth()/2);
    image.setY(0);
  }
  
  private void showImage(String name) throws FileNotFoundException {
    
    if(name.equals("RomeLegionary")) {
      setImage("/image/s1.png");
    } else if(name.equals("Druid")) {
      setImage("/image/s2.png");
    } else if(name.equals("Berserker")) {
      setImage("/image/Berserker.png");
    } else if(name.equals("ElephantAfrican")) {
      setImage("/image/Elephant_Archers_NB.png");
    } else if(name.equals("Chariot_TwoHorses")) {
      setImage("/image/Chariot_NB.png");
    } else if(name.equals("HeavyCavalry")) {
      setImage("/image/HeavyCavalry.png");
    } else if(name.equals("Lancer")) {
      setImage("/image/s3.png");
    } else if(name.equals("Hoplite")) {
      setImage("/image/Hoplite_NB.png");
    } else if(name.equals("HorseArcherNormal")) {
      setImage("/image/HorseArcherNormal.png");
    } else if(name.equals("HorseArcherHeavy")) {
      setImage("/image/HorseArcherHeavy.png");
    } else if(name.equals("JavelinSkirmisher")) {
      setImage("/image/JavelinSkirmisher.png");
    } else if(name.equals("Archer")) {
      setImage("/image/Archer.png");
    } else if(name.equals("Slinger")) {
      setImage("/image/Slinger.png");
    } else if(name.equals("Onager")) {
      setImage("/image/Onager.png");
    } else if(name.equals("Ballista")) {
      setImage("/image/Ballista.png");
    }
  }

}
