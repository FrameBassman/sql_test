package ru.crystals.testingtools.sql;

import java.sql.Connection;
import java.sql.DriverManager;
import java.util.ArrayList;
import java.util.Vector;

public class ConnectionPool {
    private static ConnectionPool instance;
    private ArrayList<Connection> availableConns = new ArrayList<>();
    private ArrayList<Connection> usedConns = new ArrayList<>();
    private String url;
    private String connectionString = "jdbc:postgresql://%s:%s/%s?user=%s&password=%s";

    private ConnectionPool(String address, String base, String port, String username, String password, int initConnCnt) {
        url = String.format(connectionString, address, port, base, username, password);
        for (int i = 0; i < initConnCnt; i++) {
            availableConns.add(getConnection());
        }
    }

    public static ConnectionPool getInstance(String address, String base, String port, String username, String password, int initConnCnt) {
        if (instance == null) {
            instance = new ConnectionPool(address, base, port, username, password, initConnCnt);
        }
        return instance;
    }

    private Connection getConnection() {
        Connection conn = null;
        try {
            conn = DriverManager.getConnection(url);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return conn;
    }

    public synchronized Connection retrieve() {
        Connection newConn = null;
        if (availableConns.size() == 0) {
            System.out.println(Thread.currentThread().getId() + ": get new connection");
            newConn = getConnection();
        } else {
            System.out.println(Thread.currentThread().getId() + ": get last available");
            newConn = availableConns.get(availableConns.size() - 1);
            availableConns.remove(newConn);
        }
        usedConns.add(newConn);
        return newConn;
    }

    public synchronized void putback(Connection c) throws NullPointerException {
        if (c != null) {
            if (usedConns.remove(c)) {
                availableConns.add(c);
            } else {
                throw new NullPointerException("Connection not in the usedConns array");
            }
        }
    }

    public int getAvailableConnsCnt() {
        return availableConns.size();
    }
}
