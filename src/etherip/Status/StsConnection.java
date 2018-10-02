package etherip.Status;


import java.util.ArrayList;
import java.util.List;

public class StsConnection {
    private int attempt;
    private int maxAttempt;
    private List<ConnectionFailListener> listeners = new ArrayList<>();


    public void setAttempt() {
        attempt++;
        if (attempt >= maxAttempt) {
            if (!listeners.isEmpty()) {
                for (ConnectionFailListener listener : listeners) {
                    listener.actionFailure();
                }
                resetAttempt();
            } else {
                System.out.println("any Connection listener was found");
            }
        }
    }

    public void resetAttempt() {
        attempt = 0;
    }

    public StsConnection(int maxAttempt) {
        this.maxAttempt = maxAttempt;
    }

    public void setListeners(ConnectionFailListener listener) {
        this.listeners.add(listener);
    }

    public List<ConnectionFailListener> getListeners() {
        return listeners;
    }
}
