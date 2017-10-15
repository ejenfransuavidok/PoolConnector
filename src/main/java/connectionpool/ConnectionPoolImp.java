package connectionpool;

import connexception.ConnectionNullPointerException;
import connexception.ConnectionPoolException;
import connexception.ConnectionPoolLimitException;
import org.apache.log4j.Logger;
import javax.naming.Reference;
import java.io.PrintWriter;
import java.sql.*;
import java.util.HashMap;
import static connectionpool.ConnectionStatus.CONNECTION_BUSY;
import static connectionpool.ConnectionStatus.CONNECTION_FREE;

public class ConnectionPoolImp extends Thread implements ConnectionPool {

    private volatile boolean isalive = true;
    private final int POOL_CAPACITY = 10;
    private final HashMap<Integer, Conn> Connections
            = new HashMap<>(POOL_CAPACITY);
    private final Logger logger = Logger.getLogger(ConnectionPoolImp.class.getName());
    private final Logger loggerThreaded = Logger.getLogger("Thread." + ConnectionPoolImp.class.getName());
    private PrintWriter printWriter;
    private String databaseName;
    private String description;
    private int loginTimeout;
    private String password;
    private String port;
    private String portNumber;
    private String serverName;
    private String url;
    private String user;

    public ConnectionPoolImp () {
        initConnections();
    }

    private void initConnections() {
        synchronized (Connections) {
            for (int i = 0;
                 i < POOL_CAPACITY;
                 i++, Connections.put(i, new Conn(null, CONNECTION_FREE, -1))
            );
        }
    }

    private int getBusyConnections() {
        int i = 0;
        synchronized (Connections) {
            for (int j = 0;
                 j < POOL_CAPACITY;
                 j++, i += (Connections.get(j).getConnectionStatus() == CONNECTION_BUSY) ? 1 : 0)
                ;
        }
        return i;
    }

    private boolean containThreadId(long threadId) {
        synchronized (Connections) {
            for (Conn conn : Connections.values()) {
                if (conn.getThreadId() == threadId) {
                    return true;
                }
            }
        }
        return false;
    }

    private Connection getByThreadId(long threadId) {
        synchronized (Connections) {
            for (Conn conn : Connections.values()) {
                if (conn.getThreadId() == threadId) {
                    return conn.getConnection();
                }
            }
        }
        return null;
    }

    private Conn createNextConnection() throws SQLException {
        String url = String.format("%s://%s:%s/%s", this.url, this.serverName, this.port, this.databaseName);
        Connection connection =
                DriverManager.getConnection(url, this.user, this.password);
        synchronized (Connections) {
            for (Conn conn : Connections.values()) {
                if (conn.getConnectionStatus() != CONNECTION_BUSY) {
                    conn.setConnection(connection);
                    conn.setConnectionStatus(CONNECTION_BUSY);
                    conn.setThreadId(Thread.currentThread().getId());
                    logger.debug("connection created successfully...");
                    return conn;
                }
            }
        }
        logger.debug("connection have not created... (limit have reach)");
        return null;
    }

    int getFreeConnections() {
        return POOL_CAPACITY - this.getBusyConnections();
    }

    @Override
    public String getDatabaseName() {
        return databaseName;
    }

    @Override
    public String getDescription() {
        return description;
    }

    @Override
    public int getLoginTimeout() {
        return loginTimeout;
    }

    @Override
    public PrintWriter getLogWriter() {
        return this.printWriter;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public Connection getPooledConnection()
            throws ConnectionPoolException, ConnectionPoolLimitException, ConnectionNullPointerException {
        long threadId = Thread.currentThread().getId();
        if(this.containThreadId(threadId)) {
            return this.getByThreadId(threadId);
        }
        else if(POOL_CAPACITY > this.getBusyConnections()) {
            try {
                Conn conn = this.createNextConnection();
                if (conn == null) {
                    throw new ConnectionPoolLimitException("have not could create new connection ...");
                }
                return conn.getConnection();
            } catch (SQLException e) {
                throw new ConnectionPoolException(e.getMessage());
            }
        }
        throw new ConnectionNullPointerException("refuse connection ... for pid = " + threadId);
    }

    @Override
    public String getPort() {
        return port;
    }

    @Override
    public String getPortNumber() {
        return portNumber;
    }

    @Override
    public Reference getReference() {
        return null;
    }

    @Override
    public String getServerName() {
        return serverName;
    }

    @Override
    public String getURL() {
        return url;
    }

    @Override
    public String getUser() {
        return user;
    }

    @Override
    public void setDatabaseName(String databaseName) {
        this.databaseName = databaseName;
    }

    @Override
    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public void setLoginTimeout(int loginTimeout) {
        this.loginTimeout = loginTimeout;
    }

    @Override
    public void setLogWriter(PrintWriter printWriter) {
        this.printWriter = printWriter;
    }

    @Override
    public void setPassword(String password) {
        this.password = password;
    }

    @Override
    public void setPort(String port) {
        this.port = port;
    }

    @Override
    public void setPortNumber(String portNumber) {
        this.portNumber = portNumber;
    }

    @Override
    public void setServerName(String serverName) {
        this.serverName = serverName;
    }

    @Override
    public void setURL(String url) {
        this.url = url;
    }

    @Override
    public void setUser(String user) {
        this.user = user;
    }

    private void resetConn(Conn conn) {
        conn.setConnection(null);
        conn.setThreadId(-1);
        conn.setConnectionStatus(CONNECTION_FREE);
    }

    private boolean checkConnection(Conn conn) throws SQLException {
        Statement stmt = null;
        ResultSet rs =null;
        Connection connection = conn.getConnection();
        if(connection == null) {
            this.resetConn(conn);
            return false;
        }
        try {
            stmt = connection.createStatement();
            rs = stmt.executeQuery("SELECT 1");
            if (rs.next()) {
                return true;
            }
            else {
                connection.close();
                this.resetConn(conn);
            }
        }
        catch (SQLException e) {
            logger.error("connection failed!!!");
            this.resetConn(conn);
            return false;
        }
        finally {
            if (stmt != null) stmt.close();
            if (rs != null) rs.close();
        }
        return false;
    }

    private void closeAll() {
        synchronized (Connections) {
            for (Conn conn : Connections.values()) {
                try {
                    if (conn.getConnection() != null && this.checkConnection(conn)) {
                        if (!conn.getConnection().isClosed()) {
                            conn.getConnection().close();
                            this.resetConn(conn);
                        }
                    }
                } catch (SQLException e) {
                    logger.error(e.getMessage());
                }
            }
        }
    }

    @Override
    public void run() {
        while (isalive) {
            synchronized (Connections) {
                for(Conn conn : Connections.values()) {
                    try {
                        this.checkConnection(conn);
                    } catch (SQLException e) {
                        loggerThreaded.error(e.getMessage());
                    }
                }
            }
            try {
                this.sleep(5000);
            } catch (InterruptedException e) {
                loggerThreaded.error(e.getMessage());
            }
        }
        this.closeAll();
    }

    public void halt() {
        isalive = false;
    }

}
