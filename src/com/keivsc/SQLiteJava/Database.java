package com.keivsc.SQLiteJava.Database;

import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Database {

    static class databaseMethods{
        public static final int CreateTable = 0;
        public static final int ClearTable = 1;
        public static final int DeleteTable = 2;
        public static final int InsertItems = 3;
        public static final int UpdateItems = 4;
        public static final int DeleteItems = 5;
        public static final int SelectItems = 6;
        public static final int SelectAllItems = 7;

    }

    private Connection conn;
    private boolean debug;
    private static final Logger logger = LoggerFactory.getLogger(Database.class);

    private List<Map<String, Object>> getTableInfo(String tableName) throws Errors.DatabaseException {
        try{
            Statement stmt = this.conn.createStatement();

            // Execute the PRAGMA table_info query
            String sql = "PRAGMA table_info(" + tableName + ")";
            ResultSet rs = stmt.executeQuery(sql);

            // Get the table information
            List<Map<String, Object>> tableInfo = new ArrayList<>();
            while (rs.next()) {
                Map<String, Object> columnInfo = new HashMap<>();
                columnInfo.put("name", rs.getString("name"));
                columnInfo.put("type", rs.getString("type"));
                columnInfo.put("notnull", rs.getInt("notnull") == 1);
                columnInfo.put("default_value", rs.getString("dflt_value"));
                columnInfo.put("primary_key", rs.getInt("pk") == 1);
                tableInfo.add(columnInfo);
            }

            return tableInfo;
        }catch (SQLException e) {
            throw new Errors.DatabaseException("Error executing SQL statement: " + e.getMessage());
        }

    }

    public Database connectDatabase(String filename) throws Errors.DatabaseException {
        String dbUrl = "jdbc:sqlite:" + filename;

        try {
            Class.forName("org.sqlite.JDBC");
        } catch (ClassNotFoundException e) {
            logger.error("SQLite JDBC driver not found. Please add it to your project.", e);
            throw new Errors.DatabaseException("SQLite JDBC driver not found. Please add it to your project.");
        }

        try {
            Connection conn = DriverManager.getConnection(dbUrl);
            if (conn != null) {
                this.conn = conn;
                return this;
            }
        } catch (SQLException e) {
            logger.error("Could not connect to database.", e);
            throw new Errors.DatabaseException("Could not connect to database: " + e.getMessage());
        }
        throw new Errors.DatabaseException("Could not connect to database.");
    }

    public Database(boolean debug) {
        this.debug = debug;
    }

    public void close() throws SQLException {
        this.conn.close();
    }

    public Table CreateTable(String tableName, String[] items) throws Errors.TableException {
        StringBuilder query = new StringBuilder("CREATE TABLE IF NOT EXISTS ").append(tableName).append(" (");
        for (int i = 0; i < items.length; i++) {
            query.append(items[i]);
            if (i < items.length - 1) {
                query.append(", ");
            }
        }
        query.append(");");
        try {
            Statement stmt = this.conn.createStatement();
            stmt.execute(query.toString());
            return new Table(tableName, getTableInfo(tableName), this.conn);
        }catch(SQLException e) {
            throw new Errors.TableException("Error executing SQL statement: " + e.getMessage());
        }
    }

    public Table ConnectTable(String tableName) {
        try{
            List<Map<String, Object>> tableInfo = getTableInfo(tableName);
            if (tableInfo.isEmpty()) {
                return null;
            }
            return new Table(tableName, tableInfo, this.conn);
        } catch (Errors.DatabaseException e) {
            return null;
        }
    }

    public JSONObject toJSON() throws SQLException {
        JSONObject json = new JSONObject();
        Statement stmt = this.conn.createStatement();
        ResultSet rs = stmt.executeQuery("SELECT name FROM sqlite_master WHERE type='table'");
        while (rs.next()) {
            List<Object> itemValues = new ArrayList<>();
            Table currentTable = new Table(rs.getString("name"), getTableInfo(rs.getString("name")), this.conn);
            List<Value> allItems = currentTable.getAllItems();
            for (Value item : allItems) {
                itemValues.add(item.data);
            }
            json.put(rs.getString("name"), itemValues);
        }
        return json;
    }
}
