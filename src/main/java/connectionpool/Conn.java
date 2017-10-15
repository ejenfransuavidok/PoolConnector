package connectionpool;

import java.sql.Connection;

public class Conn extends Thread {

    private Connection connection;
    private ConnectionStatus connectionStatus;
    private long threadId;

    public Conn(Connection connection, ConnectionStatus connectionStatus, long threadId) {
        this.connection = connection;
        this.connectionStatus = connectionStatus;
        this.threadId = threadId;
    }

    public ConnectionStatus getConnectionStatus() {
        return connectionStatus;
    }

    public void setConnectionStatus(ConnectionStatus connectionStatus) {
        this.connectionStatus = connectionStatus;
    }

    public Connection getConnection() {
        return connection;
    }

    public void setConnection(Connection connection) {
        this.connection = connection;
    }

    public long getThreadId() {
        return threadId;
    }

    public void setThreadId(long threadId) {
        this.threadId = threadId;
    }
}
