package connectionpool;

import connexception.ConnectionNullPointerException;
import connexception.ConnectionPoolException;
import connexception.ConnectionPoolLimitException;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.SQLException;

class ConnectionPoolImpTest {

    static Logger logger = Logger.getLogger(ConnectionPoolImpTest.class.getName());

    @Test
    void testAll() {
        PropertyConfigurator.configure("/home/vidok/IdeaProjects/Postgre/src/main/resources/log4j.properties");
        try {
            Class.forName("org.postgresql.Driver");
        } catch (ClassNotFoundException e) {
            logger.error(e.getMessage());
        }
        ConnectionPoolImp connectionPoolImp = new ConnectionPoolImp();
        connectionPoolImp.setURL("jdbc:postgresql");
        connectionPoolImp.setServerName("localhost");
        connectionPoolImp.setPort("5432");
        connectionPoolImp.setDatabaseName("students");
        connectionPoolImp.setUser("postgres");
        connectionPoolImp.setPassword("root");
        connectionPoolImp.start();
        try {
            int i = 100;
            while (i-- > 0) {
                Thread thread = new Thread(()->{
                    try {
                        Connection connection = connectionPoolImp.getPooledConnection();
                        Logger logger = Logger.getLogger(Thread.currentThread().getName());
                        Thread.sleep(3000);
                        if(connection != null) {
                            connection.close();
                        }
                    } catch (ConnectionPoolException e) {
                        logger.error(e.getMessage());
                    } catch (ConnectionPoolLimitException e) {
                        logger.error(e.getMessage());
                    } catch (InterruptedException e) {
                        logger.error(e.getMessage());
                    } catch (SQLException e) {
                        logger.error(e.getMessage());
                    }
                    catch (ConnectionNullPointerException e) {
                        logger.error(e.getMessage());
                    }
                });
                thread.start();
                Thread.currentThread().sleep(1000);
            }
            connectionPoolImp.halt();
        } catch (InterruptedException e) {
            logger.error(e.getMessage());
        }
    }

}