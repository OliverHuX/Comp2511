package unsw.gloriaromanus;

import java.io.IOException;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.HPos;
import javafx.scene.Node;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.RowConstraints;

public class UnitMenuController extends MenuController implements FrontendObserver{
    @FXML
    private GridPane unitGrid;
    @FXML
    private GridPane waitGrid;
    @FXML
    private ScrollPane unitScrollPane;
    @FXML
    private TextField origin;
    @FXML
    private TextField destination;

    private ArrayList<String> units = new ArrayList<>();
    private Province src;

    public void setUnitGrid(Node node) {
        int row = unitGrid.getRowCount();
        unitGrid.add(node, 0, row);
        CheckBox b = new CheckBox();
        b.setSelected(false);
        unitGrid.add(b, 0, row);
        GridPane.setHalignment(b, HPos.RIGHT);
    }

    public void setWaitGrid(Node node) {
        int row = waitGrid.getRowCount();
        waitGrid.add(node, 0, row);
    }

    public void addBottomRowAtUG(int height) {
        RowConstraints row = new RowConstraints();
        row.setMaxHeight(height);
        row.setMinHeight(height);
        unitGrid.getRowConstraints().add(row);
    }

    public void addBottomRowAtWG(int height) {
        waitGrid.addRow(unitGrid.getRowCount());
        RowConstraints row = new RowConstraints();
        row.setMaxHeight(height);
        row.setMinHeight(height);
        waitGrid.getRowConstraints().add(row);
    }

    public void resetGrid() {
        while (unitGrid.getRowConstraints().size() > 0) {
            unitGrid.getRowConstraints().remove(0);
        }
        while (waitGrid.getRowConstraints().size() > 0) {
            waitGrid.getRowConstraints().remove(0);
        }
        while (unitGrid.getRowCount() > 0) {
            unitGrid.getChildren().remove(0);
        }
        while(waitGrid.getRowCount() > 0) {
            waitGrid.getChildren().remove(0);
        }
    }

    public void clickWaitBox(int position) {
        for (Node b : waitGrid.getChildren()) {
            if (b instanceof CheckBox && getPosition(b) == position) {
                CheckBox nb = (CheckBox) b;
                if (nb.isSelected()) {
                    nb.setSelected(false);
                } else {
                    nb.setSelected(true);
                }
                break;
            }
        }
    }

    public void clickUnitBox(int position) {
        for (Node b : unitGrid.getChildren()) {
            if (b instanceof CheckBox && getPosition(b) == position) {
                CheckBox nb = (CheckBox) b;
                if (nb.isSelected()) {
                    nb.setSelected(false);
                } else {
                    nb.setSelected(true);
                }
                break;
            }
        }
    }

    public Boolean checkSelect(int position, GridPane gridPane) {
        Boolean selected = false;
        for (Node b : gridPane.getChildren()) {
            if (b instanceof CheckBox && getPosition(b) == position) {
                CheckBox nb = (CheckBox) b;
                selected = nb.isSelected();
            }
        }
        return selected;
    }

    public int getPosition(Node node) {
        return GridPane.getRowIndex(node);
    }

    public String getNameFromTextArea(String str) {
        String name = new String();
        String format = "(.* )(.*)\n(.*)";
        Pattern pattern = Pattern.compile(format);
        Matcher match = pattern.matcher(str);
        if (match.find()) {
            name = match.group(2);
        }
        return name;
    }

    public void addUnit(String name) {
        units.add(name);
    }

    public ArrayList<String> getUnits() {
        return units;
    }

    public void handleConfirm() {
        for(Node node: unitGrid.getChildren()) {
            if(node instanceof TextArea && checkSelect(getPosition(node), unitGrid)) {
                TextArea nt = (TextArea) node;
                addUnit(getNameFromTextArea(nt.getText()));
            }
        }
        if(units.isEmpty()) {
            notifyFail("Please select one or more units before Confirming!");
        } else {
            notifySucceess("You have selected the following:\n" + getUnits().toString());
        }
    }

    @FXML
    public void handleMove(ActionEvent event) throws IOException {
        handleConfirm();
        if(units.isEmpty()) return;
        Province dest = super.getParent().getCurrentHumanProvince();
        if(src == null) {
            notifyFail("Please confrim selection before moving!");
            return;
        }
        if(dest.equals(src)) {
            notifyFail("You can't move units to source!\nPlease select one of province except source and enemy!");
            return;
        } else if (src.isEnemy(dest.getName())) {
            notifyFail("You try to move units to enemy province which is prohibitive!");
            return;
        }
        
        ArrayList<Unit> us = new ArrayList<Unit>();

        for(Node node: unitGrid.getChildren()) {
            if(node instanceof CheckBox && checkSelect(getPosition(node), unitGrid)) {
                us.add(src.getMyUnits().get(getPosition(node)));
            }
        }

        String content = ReadJson.readProvinceAdjacency();
        if(us.size() == 1 && src.moveUnit(us.get(0), dest, content)) {
            notifySucceess("Your have moved unit " + us.toString() + " from " + src.getName() + " to " + dest.getName());
        } else if(us.size() != 1 && src.moveUnits(us, dest, content)) {
            notifySucceess("Your have moved the following units:\n" + us.toString() + "\nFrom " + src.getName() + " to " + dest.getName());
        } else {
            notifyFail("Invalid movement, please check province state and move points");
            return;
        }

        resetGrid();
        setMyUnit();

        units.clear();

        super.getParent().updateTroopNum();
        super.getParent().addAllPointGraphics();

    }

    @FXML
    public void handleCancel(MouseEvent e){
        Node node = (Node)e.getSource();
        int i = GridPane.getRowIndex(node);
        RecruitItem r =  src.getRecruitWaitList().get(i);
        src.cancleRecruit(r);

        resetGrid();
        setRecruit();
    }

    public void setSrc(Province src) {
        this.src = src;
        origin.setText(src.getName());
    }

    public void setup(Province src) {
        setSrc(src);

        resetGrid();
        setMyUnit();
        setRecruit();
    }

    public void setDestination(String name) {
        this.destination.setText(name);
    }

    private void setMyUnit() {
        unitGrid.getChildren().clear();
        for(Unit u: src.getMyUnits()) {
            TextArea info = new TextArea();
            info.setText("Name: " + u.getName() + "\n" + "Number: " + u.getNumTroops() + "\n" + "Is Mercenary: " + u.isMercenary() + "\n" + "Remain Moving point: " + u.getRemainMovePoint());
            info.setEditable(false);
            info.setOnMouseClicked(event -> {
              getPosition(info);
              clickUnitBox(getPosition(info));
            });
            setUnitGrid(info);
            addBottomRowAtUG(82);
        }
    }

    private void setRecruit() {
        waitGrid.getChildren().clear();
        for(RecruitItem item: src.getRecruitWaitList()) {
            TextArea info = new TextArea();
            info.setText("Name: " + item.getUnit().getName() + "\n" + "Cost: " + item.getCost() + "\n" + "Remain: " + item.getTurn());
            info.setEditable(false);
            info.setOnMouseClicked(event -> handleCancel(event));
            setWaitGrid(info);
            addBottomRowAtWG(61);
        }
    }

    public void notifyFail(String msg) {
        super.getParent().failMsg(msg);
    }

    public void notifySucceess(String msg) {
        super.getParent().successMsg(msg);
    }
    
}
