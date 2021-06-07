package unsw.gloriaromanus;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Iterator;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.google.gson.reflect.TypeToken;

import unsw.gloriaromanus.unit.unitType.ArtilleryType;
import unsw.gloriaromanus.unit.unitType.HorseArcherType;
import unsw.gloriaromanus.unit.unitType.InfantryType;
import unsw.gloriaromanus.unit.unitType.MeleeCavalryType;
import unsw.gloriaromanus.unit.unitType.UnitProperty;
import unsw.gloriaromanus.unit.unitstate.Spare;
import unsw.gloriaromanus.unit.unitstate.UnitState;
import unsw.gloriaromanus.unit.unitstate.WasInBattle;

public class StoreData{
  public static void save(GloriaRomanusSystem sys) {
    GsonBuilder builder = new GsonBuilder();

    JsonSerializer<Unit> unitSerializer = new JsonSerializer<Unit>(){
      @Override
      public JsonElement serialize(Unit src, Type typeOfSrc, JsonSerializationContext context) {
        JsonObject jsonType = new JsonObject();
        jsonType.addProperty("numTroop", src.getNumTroops());
        jsonType.addProperty("range", src.isRange());
        jsonType.addProperty("armour", src.getArmour());
        jsonType.addProperty("morale", src.getMorale());
        jsonType.addProperty("speed", src.getSpeed());
        jsonType.addProperty("attack", src.getAttack());
        jsonType.addProperty("defenseSkill", src.getDefense());
        jsonType.addProperty("shieldDefense", src.getShieldDefense());
        jsonType.addProperty("upkeep", src.getUpkeep());
        jsonType.addProperty("movePoint", src.getRemainMovePoint());
        jsonType.addProperty("name", src.getName());
        jsonType.addProperty("mercenary", src.isMercenary());
        jsonType.addProperty("mType", src.getmType().toString());
        jsonType.addProperty("currentState", src.getCurrentState().toString());
        return jsonType;
      }
    };

    JsonSerializer<Trade> tradeSerializer = new JsonSerializer<Trade>(){
      @Override
      public JsonElement serialize(Trade src, Type typeOfSrc, JsonSerializationContext context) {
        JsonObject jsonType = new JsonObject();
        jsonType.addProperty("creditorName", src.getCreditor());
        jsonType.addProperty("debtorName", src.getDebtor());
        jsonType.addProperty("gold", src.getAmount());
        jsonType.addProperty("profit", src.getProfit());
        return jsonType;
      }
    };
      
    builder.registerTypeAdapter(Unit.class, unitSerializer);
    builder.registerTypeAdapter(Trade.class, tradeSerializer);

    Gson gson = builder.create();

    String data = gson.toJson(sys);

    // Write JSON file
    try (FileWriter file = new FileWriter("DATA.json")) {
      file.write(data);
      file.flush();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  public static class UnitDeserializer implements JsonDeserializer<Unit> {
    @Override
    public Unit deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
      JsonObject jsonO = json.getAsJsonObject();

      String typeName = jsonO.get("mType").getAsString();
      UnitProperty type;
      switch (typeName) {
        case "Artillery":
          type = new ArtilleryType();
          break;
        case "HorseArcher":
          type = new HorseArcherType();
          break;
        case "MeleeCavalry":
          type = new MeleeCavalryType();
          break;
        default:
          type = new InfantryType();
      }
      Unit u = new Unit(
        jsonO.get("numTroop").getAsInt(),
        jsonO.get("range").getAsBoolean(),
        jsonO.get("armour").getAsInt(),
        jsonO.get("morale").getAsInt(),
        jsonO.get("speed").getAsInt(),
        jsonO.get("attack").getAsInt(),
        jsonO.get("defenseSkill").getAsInt(),
        jsonO.get("shieldDefense").getAsInt(),
        jsonO.get("upkeep").getAsInt(),
        jsonO.get("name").getAsString(),
        type,
        jsonO.get("mercenary").getAsBoolean()
      );

      String stateName = jsonO.get("currentState").getAsString(); 
      UnitState state;
      switch (stateName) {
        case "WasInBattle":
          state = new WasInBattle(u);
          break;
        default:
          state = new Spare(u);
      }
      u.setCurrentState(state);
      u.setRemainMovePoint(jsonO.get("movePoint").getAsInt());
      return u;
    }
  };

  public static class TradeDeserializer implements JsonDeserializer<Trade> {
    @Override
    public Trade deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {

      JsonObject jsonO = json.getAsJsonObject();

      Trade t = new Trade(
        jsonO.get("creditorName").getAsString(),
        jsonO.get("debtorName").getAsString(),
        jsonO.get("gold").getAsInt(),
        jsonO.get("profit").getAsInt()
      );
      return t;
    }
  };

  public static GloriaRomanusSystem load() {
    try {
      Gson gson = new Gson();
      // create a reader
      FileReader file = new FileReader("DATA.json");
      // convert JSON string to User object
      GloriaRomanusSystem sys = new GloriaRomanusSystem();
      sys = gson.fromJson(file, new TypeToken<GloriaRomanusSystem>(){}.getType());
      for(Faction f:sys.getFaction()){
        for(Province p:f.getProvinces()) {
          p.loadOwner(f);
          ArrayList<Unit> units = p.getMyUnits();
          for(Unit u: units){
            u.attachObserver(p);
          }
        }
        Iterator<Trade> tradeIterator = f.getTrades().iterator();
        while(tradeIterator.hasNext()){
          Trade trade = tradeIterator.next();
          if(trade.getCreditor().equals(f.getName())) {
            trade.setCreditor(f);
            Faction debtor = sys.toFaction(trade.getDebtor());
            trade.setDebtor(debtor);
            debtor.addTrade(trade);
          }else{
            tradeIterator.remove();
          }
        }
        
        for(Trade trade: f.getTradeRequest()){
          trade.setCreditor(f);
          trade.setDebtor(sys.toFaction(trade.getDebtor()));
        }
      }
      return sys;
    } catch (IOException e) {
      // e.printStackTrace();
      return null;
    }
  }
}