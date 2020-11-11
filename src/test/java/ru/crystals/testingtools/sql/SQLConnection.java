package ru.crystals.testingtools.sql;

import java.io.IOException;
import java.sql.*;
import java.util.Map;

/**
 * Class for working with PostgreSQL
 */
public class SQLConnection implements AutoCloseable {
    protected String username;
    protected String password;
    protected String address;
    protected String port;
    protected String base;
    protected Connection sql;
    private String connectionString = "jdbc:postgresql://%s:%s/%s?user=%s&password=%s";


    public SQLConnection(String address, String base, String port, String username, String password) {
        this.address = address;
        this.port = port;
        this.username = username;
        this.password = password;
        this.base = base;
    }

    @Override
    public void close() throws Exception {
        closeConnection();
    }

    public void initConnection() throws SQLException{
        sql = DriverManager.getConnection(String.format(connectionString, address, port, base, username, password));
    }

    public void closeConnection() throws SQLException {
        sql.close();
    }

    /**
     * Executes SQL query and returns the ResultSet.
     * Should be used if we need to get some data FROM database
     * @param query
     * @return
     * @throws SQLException
     */
    public ResultSet executeQuery(String query) throws SQLException {
        initConnection();
        try {
            Statement statement = sql.createStatement();
            return statement.executeQuery(query);
        } catch (SQLException e) {
            throw new SQLException("Выполнение SQL-запроса закончилось неудачей", e);
        } finally {
            closeConnection();
        }
    }

    /**
     * Executes SQL query and returns status of execution
     * Should be used if we need to write some data TO database
     * @param query
     * @return
     * @throws SQLException
     */
    public int execute(String query) throws SQLException {
        initConnection();
        try {
            Statement statement = sql.createStatement();
            return statement.executeUpdate(query);
        } catch (SQLException e) {
            throw new SQLException("Выполнение SQL-запроса закончилось неудачей", e);
        } finally {
            closeConnection();
        }
    }
}
