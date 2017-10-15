package connexception;

public class ConnectionPoolLimitException extends Exception {

    public ConnectionPoolLimitException(String message) {
        super(message);
    }

}
