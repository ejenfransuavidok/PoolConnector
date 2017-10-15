package connectionpool;

import connexception.ConnectionNullPointerException;
import connexception.ConnectionPoolException;
import connexception.ConnectionPoolLimitException;
import org.apache.log4j.Logger;

import javax.naming.Reference;
import java.io.PrintWriter;
import java.sql.Connection;

public interface ConnectionPool {
    /**
     * getDatabaseName
     * Input parameters: None
     * Return type: java.lang.String
     * Description: Returns the name of the database. Returns null if the database name is not set.
     * The database name corresponds to the DATASOURCE name which is the query processor service name.
     * The service name is defined in the CACQP task entry in the data server configuration.
     * You can define one or more query processors in the configuration file.
     * The DATASOURCE name should correspond to the query processor that the client will to connect to.
     */
    String getDatabaseName();

    /**
     * getDescription
     * Input parameters: None
     * Return type: java.lang.String
     * Description: Returns the description that was set for the object.
     * Returns null if the description is not set.
     */
    String getDescription();

    /**
     * getLoginTimeout
     * Input parameters: None
     * Return type: integer
     * Description: Returns the timeout value for logging into the database.
     */
    int getLoginTimeout();

    /**
     * getLogWriter
     * Input parameters: None
     * Return type: java.io.PrintWriter
     * Description: Returns the PrintWriter that writes to the log.
     * Returns null if the log PrintWriter is not set.
     */
    PrintWriter getLogWriter();

    /**
     * getPassword
     * Input parameters: None
     * Return type: java.lang.String
     * Description: Returns the specified password. Returns null if the password is not specified.
     */
    String getPassword();

    /**
     * getPooledConnection
     * Input parameters: None or two java.lang.String parameters
     * Return type: javax.sql.PooledConnection
     * Description: This method uses two signatures.
     * The first one does not take any input parameters, and returns a connection from the connection pool. The second one takes two strings as input parameters. The first string specifies the URL. The second string specifies the connection properties.
     */
    Connection getPooledConnection() throws ConnectionPoolException, ConnectionPoolLimitException, ConnectionNullPointerException;

    /**
     * getPort
     * Input parameters: None
     * Return type: java.lang.String
     * Description: Returns the port for the object. Returns null if the port is not set.
     */
    String getPort();

    /**
     * getPortNumber
     * See getPort.
     */
    String getPortNumber();

    /**
     * getReference
     * Input parameters: None
     * Return type: javax.naming.Reference
     * Description: Returns the object properties of the data source.
     */
    Reference getReference();

    /**
     * getServerName
     * Input parameters: None
     * Return type: java.lang.String
     * Description: Returns the name of the mainframe where the data server runs.
     * Returns null if the server name is not specified.
     */
    String getServerName();

    /**
     * getURL
     * Input parameters: None
     * Return type: java.lang.String
     * Description: Returns the connection URL that provides the object with enough information to make a connection.
     */
    String getURL();

    /**
     * getUser
     * Input parameters: None
     * Return type: java.lang.String
     * Description: Returns the user name. Returns null if the user name is not specified.
     */
    String getUser();

    /**
     * setDatabaseName
     * Input parameters: java.lang.String
     * Return type: None
     * Description: Sets the database name equal to the input parameter.
     */
    void setDatabaseName(String databaseName);

    /**
     * setDescription
     * Input parameters: java.lang.String
     * Return type: None
     * Description: Sets the description equal to the input parameter.
     */
    void setDescription(String description);

    /**
     * setLoginTimeout
     * Input parameters: Integer
     * Return type: None
     * Description: Sets the login timeout equal to the input parameter.
     */
    void setLoginTimeout(int loginTimeout);

    /**
     * setLogWriter
     * Input parameters: java.io.PrintWriter
     * Return type: None
     * Description: Sets the log writer equal to the input parameter.
     */
    void setLogWriter(PrintWriter printWriter);

    /**
     *
     * setPassword
     * Input parameters: java.lang.String
     * Return type: None
     * Description: Sets the password equal to the input parameter.
     */
    void setPassword(String password);

    /**
     * setPort
     * Input parameters: java.lang.String
     * Return type: None
     * Description: Sets the port equal to the input parameter.
     */
    void setPort(String port);

    /**
     * setPortNumber
     * See setPort.
     */
    void setPortNumber(String portNumber);

    /**
     * setServerName
     * Input parameters: java.lang.String
     * Return type: None
     * Description: Sets the server name equal to the input parameter.
     * The server name is the name of the mainframe where the data server runs.
     */
    void setServerName(String serverName);

    /**
     * setURL
     * Input parameters: java.lang.String
     * Return parameters: None
     * Description: Sets the URL for the object.
     * The URL provides all of the required connection information for the object in a single location.
     * For certain ConnectionPool managers, you should use this method instead of the individual
     * setDatabaseName, setServerName, setPort, setPassword, and setUser methods.
     */
    void setURL(String url);

    /**
     * setUser
     * Input parameters: java.lang.String
     * Return type: None
     * Description: Sets the user name equal to the input parameter.
     */
    void setUser(String user);

}