package unsw.gloriaromanus;

public class RecruitItem {
    private int remainTurn;
    private Unit unit;
    private int cost;

    public RecruitItem(Unit unit, int remainTurn, int cost) {
        this.unit = unit;
        this.remainTurn = remainTurn;
        this.cost = cost;
    }

    public int getCost() {
        return cost;
    }

    public int getTurn() {
        return remainTurn;
    }

    public void updateTurn() {
        this.remainTurn -= 1;
    }

    public Unit getUnit() {
        return unit;
    }
}
