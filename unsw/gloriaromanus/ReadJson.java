package unsw.gloriaromanus;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;

import org.json.JSONObject;

public class ReadJson {
  public static String readUnitJson() throws IOException {
    try{
      String content = Files.readString(Paths.get("src/unsw/gloriaromanus/unit/initial_unit.json"));
      return content;
    }catch(IOException e){
      return e.getMessage();
    }
  }

  public static String readProvinceAdjacency() throws IOException {
    try{
      String content = Files.readString(Paths.get("src/unsw/gloriaromanus/province_adjacency_matrix_fully_connected.json"));
      return content;
    }catch(IOException e) {
      return e.getMessage();
    }
  }

  public static String getProvinceAdJsonObject(String name) throws IOException {
    try{
      String content = Files.readString(Paths.get("src/unsw/gloriaromanus/province_adjacency_matrix_fully_connected.json"));
      JSONObject provinces = new JSONObject(content);
      JSONObject province = provinces.getJSONObject(name);
      return province.toString();
    }catch(IOException e) {
      e.printStackTrace();
      return e.getMessage();
    }
  }

  public static String getFaction(ArrayList<String> players) throws IOException {
    try{
      String content = null;
      if(players.size() == 4) content = Files.readString(Paths.get("src/unsw/gloriaromanus/initial_province_ownership_four.json"));
      else if(players.size() == 3) {
        if(!players.contains("Rome")) content = Files.readString(Paths.get("src/unsw/gloriaromanus/initial_province_ownership_three_NR.json"));
        else if(!players.contains("Gaul")) content = Files.readString(Paths.get("src/unsw/gloriaromanus/initial_province_ownership_three_NoGaul.json"));
        else if(!players.contains("Gallic")) content = Files.readString(Paths.get("src/unsw/gloriaromanus/initial_province_ownership_three_NoGallic.json"));
        else content = Files.readString(Paths.get("src/unsw/gloriaromanus/initial_province_ownership_three_NS.json"));
      }
      else {
        if(players.contains("Rome")) {
          if(players.contains("Gaul"))  content = Files.readString(Paths.get("src/unsw/gloriaromanus/initial_province_ownership_two_RGual.json"));
          else if(players.contains("Gallic"))  content = Files.readString(Paths.get("src/unsw/gloriaromanus/initial_province_ownership_two_RGallic.json"));
          else content = Files.readString(Paths.get("src/unsw/gloriaromanus/initial_province_ownership_two_RS.json"));
        }else if(players.contains("Gaul")) {
          if(players.contains("Gallic"))  content = Files.readString(Paths.get("src/unsw/gloriaromanus/initial_province_ownership_two_GG.json"));
          else content = Files.readString(Paths.get("src/unsw/gloriaromanus/initial_province_ownership_two_GaulcS.json"));
        }else{
          content = Files.readString(Paths.get("src/unsw/gloriaromanus/initial_province_ownership_two_GallicS.json"));
        }
      }
      return content;
    }catch(IOException e) {
      e.printStackTrace();
      return e.getMessage();
    }
  }
}
