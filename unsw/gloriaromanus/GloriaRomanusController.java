package unsw.gloriaromanus;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

import com.esri.arcgisruntime.concurrent.ListenableFuture;
import com.esri.arcgisruntime.data.Feature;
import com.esri.arcgisruntime.data.FeatureTable;
import com.esri.arcgisruntime.data.GeoPackage;
import com.esri.arcgisruntime.geometry.Point;
import com.esri.arcgisruntime.geometry.SpatialReferences;
import com.esri.arcgisruntime.layers.FeatureLayer;
import com.esri.arcgisruntime.loadable.LoadStatus;
import com.esri.arcgisruntime.mapping.ArcGISMap;
import com.esri.arcgisruntime.mapping.Basemap;
import com.esri.arcgisruntime.mapping.view.Graphic;
import com.esri.arcgisruntime.mapping.view.GraphicsOverlay;
import com.esri.arcgisruntime.mapping.view.IdentifyLayerResult;
import com.esri.arcgisruntime.mapping.view.MapView;
import com.esri.arcgisruntime.symbology.PictureMarkerSymbol;
import com.esri.arcgisruntime.symbology.TextSymbol;
import com.esri.arcgisruntime.symbology.TextSymbol.HorizontalAlignment;
import com.esri.arcgisruntime.symbology.TextSymbol.VerticalAlignment;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.geojson.FeatureCollection;
import org.geojson.LngLatAlt;
import org.json.JSONArray;
import org.json.JSONObject;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Point2D;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.image.Image;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.util.Pair;

public class GloriaRomanusController{

  @FXML
  private MapView mapView;

  @FXML
  private StackPane stackPaneMain;

  // could use ControllerFactory?
  private ArrayList<Pair<MenuController, VBox>> controllerParentPairs;

  private ArcGISMap map;

  private Map<String, String> provinceToOwningFactionMap;

  private Map<String, Integer> provinceToNumberTroopsMap;

  private String humanFaction;

  private Feature currentlySelectedHumanProvince;
  private Feature currentlySelectedEnemyProvince;

  private FeatureLayer featureLayer_provinces;

  private GloriaRomanusSystem sys;

  private Pair<MenuController, VBox> currentMenu;
  private Boolean achieved = false;

  @FXML
  private void initialize() throws JsonParseException, JsonMappingException, IOException, InterruptedException {
    String []menus = {"faction_menu.fxml", "province_menu.fxml", "recruit_menu.fxml", "unit_menu.fxml", "diplomacy_menu.fxml", "unitInfo_menu.fxml", "victory_page.fxml", "fail_page.fxml"};
    controllerParentPairs = new ArrayList<Pair<MenuController, VBox>>();
    for (String fxmlName: menus){
      System.out.println(fxmlName);
      FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlName));
      VBox root = (VBox)loader.load();
      MenuController menuController = (MenuController)loader.getController();
      menuController.setParent(this);
      controllerParentPairs.add(new Pair<MenuController, VBox>(menuController, root));
    }

    currentMenu = controllerParentPairs.get(0);
    stackPaneMain.getChildren().add(currentMenu.getValue());
  }


  public void clickedInvadeButton(ActionEvent e) throws IOException {
    if (currentlySelectedHumanProvince != null && currentlySelectedEnemyProvince != null){
      String humanProvince = (String)currentlySelectedHumanProvince.getAttributes().get("name");
      String enemyProvince = (String)currentlySelectedEnemyProvince.getAttributes().get("name");
      if (confirmIfProvincesConnected(humanProvince, enemyProvince)){
        Province invader = getProvince(humanProvince);
        Province defenser = getProvince(enemyProvince);
        String adjacent = ReadJson.getProvinceAdJsonObject(humanProvince);
        String msg = invader.battle(defenser, adjacent);


        updateTroopNum(invader);
        updateTroopNum(defenser);

        updateOwingFaction(invader);
        updateOwingFaction(defenser);

        printMessageToTerminal(msg);

        resetSelections();  // reset selections in UI
        addAllPointGraphics(); // reset graphics
      }
      else{
        printMessageToTerminal("Provinces not adjacent, cannot invade!");
      }

    }
  }

  /**
   * run this initially to update province owner, change feature in each
   * FeatureLayer to be visible/invisible depending on owner. Can also update
   * graphics initially
   */
  private void initializeProvinceLayers() throws JsonParseException, JsonMappingException, IOException {

    Basemap myBasemap = Basemap.createImagery();
    // myBasemap.getReferenceLayers().remove(0);
    map = new ArcGISMap(myBasemap);
    mapView.setMap(map);

    // note - tried having different FeatureLayers for AI and human provinces to
    // allow different selection colors, but deprecated setSelectionColor method
    // does nothing
    // so forced to only have 1 selection color (unless construct graphics overlays
    // to give color highlighting)
    GeoPackage gpkg_provinces = new GeoPackage("src/unsw/gloriaromanus/provinces_right_hand_fixed.gpkg");
    gpkg_provinces.loadAsync();
    gpkg_provinces.addDoneLoadingListener(() -> {
      if (gpkg_provinces.getLoadStatus() == LoadStatus.LOADED) {
        // create province border feature
        featureLayer_provinces = createFeatureLayer(gpkg_provinces);
        map.getOperationalLayers().add(featureLayer_provinces);

      } else {
        System.out.println("load failure");
      }
    });

    addAllPointGraphics();
  }

  public void addAllPointGraphics() throws JsonParseException, JsonMappingException, IOException {
    mapView.getGraphicsOverlays().clear();

    InputStream inputStream = new FileInputStream(new File("src/unsw/gloriaromanus/provinces_label.geojson"));
    FeatureCollection fc = new ObjectMapper().readValue(inputStream, FeatureCollection.class);

    GraphicsOverlay graphicsOverlay = new GraphicsOverlay();

    for (org.geojson.Feature f : fc.getFeatures()) {
      if (f.getGeometry() instanceof org.geojson.Point) {
        org.geojson.Point p = (org.geojson.Point) f.getGeometry();
        LngLatAlt coor = p.getCoordinates();
        Point curPoint = new Point(coor.getLongitude(), coor.getLatitude(), SpatialReferences.getWgs84());
        PictureMarkerSymbol s = null;
        String province = (String) f.getProperty("name");
        String faction = provinceToOwningFactionMap.get(province);

        TextSymbol t = new TextSymbol(10,
            faction + "\n" + province + "\n" + provinceToNumberTroopsMap.get(province), 0xFFFF0000,
            HorizontalAlignment.CENTER, VerticalAlignment.BOTTOM);

        switch (faction) {
          case "Gaul":
            // note can instantiate a PictureMarkerSymbol using the JavaFX Image class - so could
            // construct it with custom-produced BufferedImages stored in Ram
            // http://jens-na.github.io/2013/11/06/java-how-to-concat-buffered-images/
            // then you could convert it to JavaFX image https://stackoverflow.com/a/30970114

            // you can pass in a filename to create a PictureMarkerSymbol...
            s = new PictureMarkerSymbol(new Image((new File("images/Celtic_Druid.png")).toURI().toString()));
            break;
          case "Rome":
            s = new PictureMarkerSymbol("images/legionary.png");
            break;
          case "Spanish":
            s = new PictureMarkerSymbol("src/image/Soldier1resize.png");
            break;
          case "Gallic":
            s = new PictureMarkerSymbol("src/image/Soldier2resize.png");
            break;
        }
        t.setHaloColor(0xFFFFFFFF);
        t.setHaloWidth(2);
        Graphic gPic = new Graphic(curPoint, s);
        Graphic gText = new Graphic(curPoint, t);
        graphicsOverlay.getGraphics().add(gPic);
        graphicsOverlay.getGraphics().add(gText);
      } else {
        System.out.println("Non-point geo json object in file");
      }

    }

    inputStream.close();
    mapView.getGraphicsOverlays().add(graphicsOverlay);
  }

  private FeatureLayer createFeatureLayer(GeoPackage gpkg_provinces) {
    FeatureTable geoPackageTable_provinces = gpkg_provinces.getGeoPackageFeatureTables().get(0);

    // Make sure a feature table was found in the package
    if (geoPackageTable_provinces == null) {
      System.out.println("no geoPackageTable found");
      return null;
    }

    // Create a layer to show the feature table
    FeatureLayer flp = new FeatureLayer(geoPackageTable_provinces);

    // https://developers.arcgis.com/java/latest/guide/identify-features.htm
    // listen to the mouse clicked event on the map view
    mapView.setOnMouseClicked(e -> {
      // was the main button pressed?
      if (e.getButton() == MouseButton.PRIMARY) {
        // get the screen point where the user clicked or tapped
        Point2D screenPoint = new Point2D(e.getX(), e.getY());

        // specifying the layer to identify, where to identify, tolerance around point,
        // to return pop-ups only, and
        // maximum results
        // note - if select right on border, even with 0 tolerance, can select multiple
        // features - so have to check length of result when handling it
        final ListenableFuture<IdentifyLayerResult> identifyFuture = mapView.identifyLayerAsync(flp,
            screenPoint, 0, false, 25);

        // add a listener to the future
        identifyFuture.addDoneListener(() -> {
          try {
            // get the identify results from the future - returns when the operation is
            // complete
            IdentifyLayerResult identifyLayerResult = identifyFuture.get();
            // a reference to the feature layer can be used, for example, to select
            // identified features
            if (identifyLayerResult.getLayerContent() instanceof FeatureLayer) {
              FeatureLayer featureLayer = (FeatureLayer) identifyLayerResult.getLayerContent();
              // select all features that were identified
              List<Feature> features = identifyLayerResult.getElements().stream().map(f -> (Feature) f).collect(Collectors.toList());

              if (features.size() > 1){
                printMessageToTerminal("Have more than 1 element - you might have clicked on boundary!");
              }
              else if (features.size() == 1){
                // note maybe best to track whether selected...
                Feature f = features.get(0);
                String province = (String)f.getAttributes().get("name");
                // System.out.println(province);

                if (provinceToOwningFactionMap.get(province).equals(humanFaction)){
                  // province owned by human
                  if (currentlySelectedHumanProvince != null){
                    featureLayer.unselectFeature(currentlySelectedHumanProvince);
                  }

                  if(currentMenu.getKey() instanceof UnitMenuController) {
                    ((UnitMenuController)currentMenu.getKey()).setDestination(province);
                  }

                  //update my province textfield
                  currentlySelectedHumanProvince = f;
                  if (currentMenu.getKey() instanceof ProvinceMenuController){
                    ((ProvinceMenuController)currentMenu.getKey()).setInvadingProvince(province);
                  }

                  //int provinceWealth = 0;
                  for(Faction fa: sys.getFaction()) {
                    Boolean flag = false;
                    for(Province p: fa.getProvinces()) {
                      if(p.getName().equals(province) && currentMenu.getKey() instanceof ProvinceMenuController) {
                        int provinceWealth = p.getWealth();
                        String wealth = provinceWealth + "$";
                        ((ProvinceMenuController)currentMenu.getKey()).setWealth(wealth);
                        ((ProvinceMenuController)currentMenu.getKey()).setWealthRate(Double.toString(p.getTownWealthRate() * 100) + "%");
                        String tax = p.getTaxRate() * 100 + "%";
                        ((ProvinceMenuController)currentMenu.getKey()).setTaxRate(tax);
                        //System.out.println(currentlySelectedHumanProvince.getAttributes().get("name"));
                        flag = true;
                        break;
                      }
                    }
                    if(flag) {
                      break;
                    }
                  }

                }else{
                  if (currentlySelectedEnemyProvince != null){
                    featureLayer.unselectFeature(currentlySelectedEnemyProvince);
                  }
                  currentlySelectedEnemyProvince = f;
                  if (currentMenu.getKey() instanceof ProvinceMenuController){
                    ((ProvinceMenuController)currentMenu.getKey()).setOpponentProvince(province);
                  }
                }

                featureLayer.selectFeature(f);                
              }

              
            }
          } catch (InterruptedException | ExecutionException ex) {
            // ... must deal with checked exceptions thrown from the async identify
            // operation
            System.out.println("InterruptedException occurred");
          }
        });
      }
    });
    return flp;
  }

  public void setTaxRate(String rate) {
    if(currentlySelectedHumanProvince != null) {
      String provinceName = currentlySelectedHumanProvince.getAttributes().get("name").toString();
      for(Province p: sys.getCurrentPlayer().getProvinces()) {
        if(p.getName().equals(provinceName)) {
          p.setTaxRate(Double.parseDouble(rate));
          String tax = Double.parseDouble(rate) * 100 + "%";
          ((ProvinceMenuController)currentMenu.getKey()).setTaxRate(tax);
          successMsg("Tax rate set successfully!");
          return;
        }
      }
    } else {
      failMsg("Please select one of your province");
    }
  }

  private String getGoals() {
    List<String> goals = sys.getGoals();
    List<String> goalsFormated = new ArrayList<>();
    String str = "";
    for(int i = 0; i < goals.size(); i++) {
      if(goals.get(i).equals("WEALTH")) {
        goalsFormated.add("Wealth >= 400000");
      } else if(goals.get(i).equals("TREASURY")) {
        goalsFormated.add("Treasury >= 100000");
      } else {
        goalsFormated.add("Conquest");
      }
    }
    //System.out.println(sys.getSigns().size());
    if(goalsFormated.size() == 3 && !sys.getSigns().get(0).equals(sys.getSigns().get(1))) {
      str += "Achieve one of the following:\n\n";
      if(sys.getSigns().get(0).equals("AND")) {
        str += goalsFormated.get(0) + " AND " + goalsFormated.get(1) + "\n" + goalsFormated.get(0) + " AND " + goalsFormated.get(2);
      } else {
        str += goalsFormated.get(0) + " AND " + goalsFormated.get(2) + "\n" + goalsFormated.get(1) + " AND " + goalsFormated.get(2);
      }
    } else if(goalsFormated.size() == 3 && sys.getSigns().get(0).equals("OR")) {
      str += "Achieve one of the following:\n\n";
      str += goalsFormated.get(0) + "\n" + goalsFormated.get(1) + "\n" + goalsFormated.get(2) + "\n";
    } else if(goalsFormated.size() == 2 && sys.getSigns().get(0).equals("OR")) {
      str += "Achieve one of the following:\n\n";
      str += goalsFormated.get(0) + "\n" + goalsFormated.get(1);
    } else {
      str += "Achieve the following:\n\n";
      for(int i = 0; i < goalsFormated.size(); i++) {
        str += goalsFormated.get(i) + " ";
        if(sys.getSigns().size() == 2 && i < 2) {
          str += sys.getSigns().get(i) + " ";
        } else if(sys.getSigns().size() == 1 && i < 1) {
          str += sys.getSigns().get(i) + " ";
        }
      }
    }
    return str;
  }

  private Map<String, String> getProvinceToOwningFactionMap() throws IOException{
    Map<String, String> m = new HashMap<String, String>();
    if(sys == null) {
      String content = ReadJson.getFaction(sys.getPlayers());
      JSONObject ownership = new JSONObject(content);
      for (String key : ownership.keySet()) {
        // key will be the faction name
        JSONArray ja = ownership.getJSONArray(key);
        // value is province name
        for (int i = 0; i < ja.length(); i++) {
          String value = ja.getString(i);
          m.put(value, key);
        }
      }
    }else{
      for(Faction f: sys.getFaction()){
        String key = f.getName();
        for(Province p: f.getProvinces()) {
          String value = p.getName();
          m.put(value, key);
        }
      }
    }
    return m;
  }

  private boolean confirmIfProvincesConnected(String province1, String province2) throws IOException {
    String content = Files.readString(Paths.get("src/unsw/gloriaromanus/province_adjacency_matrix_fully_connected.json"));
    JSONObject provinceAdjacencyMatrix = new JSONObject(content);
    return provinceAdjacencyMatrix.getJSONObject(province1).getBoolean(province2);
  }

  private void resetSelections(){
    if(currentlySelectedEnemyProvince != null || currentlySelectedHumanProvince != null) {
      for(Pair<MenuController, VBox> p: controllerParentPairs) {
        if (p.getKey() instanceof ProvinceMenuController){
          ((ProvinceMenuController)p.getKey()).setInvadingProvince("");
          ((ProvinceMenuController)p.getKey()).setOpponentProvince("");
          break;
        }
      }
    }
    if(currentlySelectedEnemyProvince != null && currentlySelectedHumanProvince != null) {
      featureLayer_provinces.unselectFeatures(Arrays.asList(currentlySelectedEnemyProvince, currentlySelectedHumanProvince));
      currentlySelectedEnemyProvince = null;
      currentlySelectedHumanProvince = null;
    } else if (currentlySelectedEnemyProvince != null) {
      featureLayer_provinces.unselectFeatures(Arrays.asList(currentlySelectedEnemyProvince));
      currentlySelectedEnemyProvince = null;
    } else if (currentlySelectedHumanProvince != null) {
      featureLayer_provinces.unselectFeatures(Arrays.asList(currentlySelectedHumanProvince));
      currentlySelectedHumanProvince = null;
    }
  }

  private void printMessageToTerminal(String message){
    ((ProvinceMenuController)controllerParentPairs.get(1).getKey()).appendToTerminal(message);
  }

  /**
   * Stops and releases all resources used in application.
   */
  void terminate() {
    if (mapView != null) {
      mapView.dispose();
    }
  }

  public void endTurn() throws JsonParseException, JsonMappingException, IOException {
    int num = sys.getGoals().size() - 1;
    if(!achieved && num > 0 && sys.checkGoals(num)) {
      switchVictory();
      achieved = true;
      sys.endTurn();
      updateTroopNum();
      setCurrentHumanFaction(sys.getCurrentPlayer());
      addAllPointGraphics();
      resetSelections();
      sys.save();
      return;
    } else if(!achieved && sys.checkGoal(sys.getGoals().get(0))) {
      switchVictory();
      achieved = true;
      sys.endTurn();
      updateTroopNum();
      setCurrentHumanFaction(sys.getCurrentPlayer());
      addAllPointGraphics();
      resetSelections();
      sys.save();
      return;
    }

    if(sys.checkFail()) {
      sys.failGame();
      switchFail();
      setCurrentHumanFaction(sys.getCurrentPlayer());
      return;
    }

    sys.endTurn();
    setCurrentHumanFaction(sys.getCurrentPlayer());
    ((FactionMenuController)currentMenu.getKey()).setup(sys);
    
    updateTroopNum();
    addAllPointGraphics();
    resetSelections();
  }

  public void updateTroopNum() {
    for(Faction f:sys.getFaction()){
      for(Province p:f.getProvinces()){
        int num = 0;
        for(Unit u: p.getMyUnits()) {
          num += u.getNumTroops();
        }
        provinceToNumberTroopsMap.put(p.getName(), num);
      }
    }
  }

  public void updateTroopNum(Province p) {
    int num = 0;
    for(Unit u:p.getMyUnits()){
      num += u.getNumTroops();
    }
    provinceToNumberTroopsMap.put(p.getName(), num);
  }

  public void updateOwingFaction(Province p) {
    provinceToOwningFactionMap.put(p.getName(), p.getOwnerName());
  }

  public Faction getCurrentFaction() {
    return sys.getCurrentPlayer();
  }

  public void setCurrentHumanFaction(Faction f) {
    this.humanFaction = f.getName();
  }

  public void setSys(GloriaRomanusSystem sys) {
    this.sys = sys;
  }

  public void start() throws JsonParseException, JsonMappingException, IOException {
    // Done = load this from a configuration file you create (user should be able to
    // select in loading screen)
    humanFaction = sys.getCurrentPlayerName();

    currentlySelectedHumanProvince = null;
    currentlySelectedEnemyProvince = null;

    provinceToOwningFactionMap = getProvinceToOwningFactionMap();
    //will update in start()
    provinceToNumberTroopsMap = new HashMap<String, Integer>();
    for (String provinceName : provinceToOwningFactionMap.keySet()) {
      provinceToNumberTroopsMap.put(provinceName, 0);
    }

    //update the troop nums
    for(Faction f: sys.getFaction()){
      for(Province p: f.getProvinces()){
        String key = p.getName();
        int value = 0;
        for(Unit u: p.getMyUnits()){
          value += u.getNumTroops();
        }
        provinceToNumberTroopsMap.put(key, value);
      }
    }

    initializeProvinceLayers();

    ((FactionMenuController)controllerParentPairs.get(0).getKey()).setup(sys, getGoals());
    // System.out.println(getGoals());
  }
  
  public void switchProvince() {
    stackPaneMain.getChildren().remove(currentMenu.getValue());
    currentMenu = controllerParentPairs.get(1);
    stackPaneMain.getChildren().add(currentMenu.getValue());
    ((ProvinceMenuController)currentMenu.getKey()).setTreasury(Integer.toString(sys.getCurrentPlayer().getTreasury()) + "$");

  }

  public void switchFaction() {
    stackPaneMain.getChildren().remove(currentMenu.getValue());
    currentMenu = controllerParentPairs.get(0);
    stackPaneMain.getChildren().add(currentMenu.getValue());

    ((FactionMenuController)currentMenu.getKey()).setup(sys);

  }

  public void switchVictory() {
    stackPaneMain.getChildren().remove(currentMenu.getValue());
    currentMenu = controllerParentPairs.get(6);
    stackPaneMain.getChildren().add(currentMenu.getValue());
    ((VictoryController)currentMenu.getKey()).setGoalText();
  }

  public void switchDiplomacy() {
    stackPaneMain.getChildren().remove(currentMenu.getValue());
    currentMenu = controllerParentPairs.get(4);
    stackPaneMain.getChildren().add(currentMenu.getValue());
    ((DiplomacyMenuController)currentMenu.getKey()).setup(sys);
  }
  
  public void switchRecruit() {
    Province p = getCurrentHumanProvince();
    if(p == null) {
      failMsg("Please select your province");
      return;
    }
    stackPaneMain.getChildren().remove(currentMenu.getValue());
    currentMenu = controllerParentPairs.get(2);
    stackPaneMain.getChildren().add(currentMenu.getValue());

    ((RecruitMenuController)currentMenu.getKey()).setup(sys, getCurrentHumanProvince());
  }

  public void switchUnitInfo(String name) {
    stackPaneMain.getChildren().remove(currentMenu.getValue());
    currentMenu = controllerParentPairs.get(5);
    stackPaneMain.getChildren().add(currentMenu.getValue());
    
    ((UnitInfoController)currentMenu.getKey()).settup(name);
  }

  public void switchFail() {
    stackPaneMain.getChildren().remove(currentMenu.getValue());
    currentMenu = controllerParentPairs.get(7);
    stackPaneMain.getChildren().add(currentMenu.getValue());
  }

  public void switchUnit() {
    Province p = getCurrentHumanProvince();
    if(p != null) {
      UnitMenuController unitMenu = (UnitMenuController)controllerParentPairs.get(3).getKey();
      unitMenu.setup(p);

      stackPaneMain.getChildren().remove(currentMenu.getValue());
      currentMenu = controllerParentPairs.get(3);
      stackPaneMain.getChildren().add(currentMenu.getValue());
    } else {
      failMsg("Please select one of your province!");
    }
  }

  public void saveGame() {
    sys.save();
    successMsg("Saved Successfully!");
  }

  public Province getCurrentHumanProvince() {
    if(currentlySelectedHumanProvince == null) return null;
    String humanProvince = (String)currentlySelectedHumanProvince.getAttributes().get("name");
    return getProvince(humanProvince);
  }
  /**
   * 
   * @param content setContentText
   * @return a alert has not been show
   */
  public void successMsg(String content) {
    Alert infoAlert = new Alert(AlertType.INFORMATION);
    infoAlert.setHeaderText("Success!");
    infoAlert.setContentText(content);
    infoAlert.showAndWait();
  }

  public void failMsg(String content) {
    Alert errorAlert = new Alert(AlertType.ERROR);
    errorAlert.setHeaderText("Invalid Action");
    errorAlert.setContentText(content);
    errorAlert.showAndWait();
  }

  public Province getProvince(String name) {
    for(String f:sys.getPlayers()){
      Faction faction = sys.toFaction(f);
      for(Province p: faction.getProvinces()) {
        if(p.getName().equals(name)){
          return p;
        }
      }
    }
    return null;
  }

  public String getAchieveGoals() {
    String achieved = null;
    if(sys.getGoals().size() == 1 && sys.checkGoal(sys.getGoals().get(0))) {
      achieved = sys.getGoals().get(0);
      achieved = "You have achieved the following goals:\n" + achieved;
    } else {
      if(sys.checkGoals(sys.getSigns().size())) {
        String str = getGoals();
        achieved = str.substring(str.indexOf("\n") + 1);
        if(sys.getSigns().size() == 2) {
          if(sys.getSigns().get(0).equals("OR") || sys.getSigns().get(1).equals("OR")) {
            achieved = "You have achieved one or more following goals:\n" + achieved;
          }
        } else if(sys.getSigns().get(0).equals("OR")) {
          achieved = "You have achieved one or more following goals:\n" + achieved;
        } else {
          achieved = "You have achieved the following goals:\n" + achieved;
        }
      }
    }
    return achieved;
  }
}
