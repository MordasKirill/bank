package org.international.bank.repository.db.impl;

import org.international.bank.repository.db.ConnectionPool;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ConnectionPoolImpl implements ConnectionPool {
    private static final String URL = "jdbc:mysql://localhost:3306/bank";
    private static final String USERNAME = "your username";
    private static final String PASSWORD = "your pass";
    private final List<Connection> connections;
    private final List<Connection> usedConnections = new ArrayList<>();
    private static final int INITIAL_POOL_SIZE = 10;
    private static ConnectionPoolImpl connectionPool;

    public static ConnectionPoolImpl create() {
        List<Connection> pool = new ArrayList<>(INITIAL_POOL_SIZE);
        for (int i = 0; i < INITIAL_POOL_SIZE; i++) {
            pool.add(createConnection());
        }
        return new ConnectionPoolImpl(pool);
    }

    public ConnectionPoolImpl(List<Connection> connections) {
        this.connections = connections;
    }

    public static ConnectionPoolImpl getInstance() {
        if (connectionPool == null) {
            connectionPool = create();
        }
        return connectionPool;
    }


    @Override
    public Connection getConnection() {
        Connection connection = connections.remove(connections.size() - 1);
        usedConnections.add(connection);
        return connection;
    }

    @Override
    public void releaseConnection(Connection connection) {
        connections.add(connection);
        usedConnections.remove(connection);
    }

    private static Connection createConnection() {
        try {
            return DriverManager.getConnection(URL, USERNAME, PASSWORD);
        } catch (SQLException e) {
            throw new RuntimeException("Credentials invalid.");
        }
    }

    public int getSize() {
        return connections.size() + usedConnections.size();
    }

    public void shutdown() throws SQLException {
        usedConnections.forEach(this::releaseConnection);
        for (Connection c : connections) {
            c.close();
        }
        connections.clear();
    }
}
