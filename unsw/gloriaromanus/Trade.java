package unsw.gloriaromanus;


public class Trade {
  private int gold;
  private Faction creditor;
  private Faction debtor;
  private String creditorName;
  private String debtorName;

  private int profit;

  public Trade(int gold, Faction creditor, Faction debtor) {
    this.gold = gold;
    this.creditor = creditor;
    this.debtor = debtor;
    this.profit = 0;
    this.creditorName = creditor.getName();
    this.debtorName = debtor.getName();
  }
  public Trade(String creditor, String debtor, int gold, int profit) {
    this.gold = gold;
    this.creditor = null;
    this.debtor = null;
    this.profit = profit;
    this.creditorName = creditor;
    this.debtorName = debtor;
  }

  public void approve() {
    debtor.sucecessBorrow(this);
  }

  public int getAmount() {
    return gold;
  }

  public int getFinalPay() {
    return gold + profit;
  }

  public void updateProfit() {
    int wealth = debtor.getWealth();
    double rate = (double)(gold) * 50/(double)wealth;
    profit += (int)Math.round((profit + gold) * rate);
  }

  public void end() {
    creditor.getPay(this);
  }

  public String getDebtor() {
    return debtorName;
  }

  public String getCreditor() {
    return creditorName;
  }

  public String tmp() {
    return creditor.getName();
  }

  public void setCreditor(Faction creditor) {
    this.creditor = creditor;
  }

  public void setDebtor(Faction debtor) {
    this.debtor = debtor;
  }

  public int getProfit() {
    return profit;
  }
}
