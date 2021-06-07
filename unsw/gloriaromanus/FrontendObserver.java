package unsw.gloriaromanus;

public interface FrontendObserver{
  public void notifyFail(String msg);
  public void notifySucceess(String msg);
}
