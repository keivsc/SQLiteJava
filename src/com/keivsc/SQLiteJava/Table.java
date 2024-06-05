package com.keivsc.SQLiteJava;

import java.sql.*;
import java.util.*;

/**
 * Columns in the current table
 */
class Columns{
    private final List<Map<String, Object>> fieldValues = new ArrayList<>();
    private static final Map<String, Class<?>> types = new HashMap<>();
    //        NULL- It is a NULL value.
//        INTEGER- It is an integer, stored in 1, 2, 3, 4, 6, or 8 bytes depending on the value.
//        REAL- It is a floating point value, stored as an 8-byte floating number.
//        TEXT- It is a string, stored using the database encoding (UTF).
//        BLOB- It is a group of data, stored exactly as it was entered.
    static {
        types.put("INTEGER", Integer.class);
        types.put("REAL", double.class);
        types.put("TEXT", String.class);
        types.put("BLOB", byte[].class);
    }

    private Class<?> parseTypes(String type){
        return types.get(type);
    }

    public Columns(List<Map<String, Object>> items){
        for (Map<String, Object> item : items) {
            Map<String, Object> fieldValue = new HashMap<>();
            String name = (String) item.get("name");
            Object type = parseTypes((String) item.get("type"));
            fieldValue.put(name, type);
            fieldValue.put("primary_key", item.get("primary_key"));
            fieldValues.add(fieldValue);
        }
    };

    public Object getType(String key){
        for (Map<String, Object> fieldValue : fieldValues) {
            if (fieldValue.containsKey(key)) {
                return fieldValue.get(key);
            }
        }
        return null;
    }

    public int size(){
        return fieldValues.size();
    }

}


public class Table {
    private String Name;
    private Columns Column;
    private Connection conn;
    private Map<String, Object> items = new HashMap<>();
    public Table(String tableName, List<Map<String, Object>>  items, Connection conn){

        this.Name = tableName;
        this.conn = conn;
        this.Column = new Columns(items);
    };

    /**
     * Runs a SQL Command directly with no formatting
     * @param command String | SQL Command following SQLite Formatting
     * @throws SQLException
     */
    public void runCommand(String command) throws SQLException {
        Statement stmt = conn.createStatement();
        stmt.executeUpdate(command);
    }

    /**
     * Runs a SQL Query
     * @param query String | SQL Query following the SQLite Formatting
     * @return <code>java.sql.ResultSet</code>
     * @throws SQLException
     */
    public ResultSet runQuery(String query) throws SQLException {
        Statement stmt = conn.createStatement();
        return stmt.executeQuery(query);
    }


    /**
     * Clears the entire table, This is dangerous and should almost never be used
     * @throws SQLException
     */
    public void clearTable() throws SQLException{
        this.runCommand("DELETE * FROM " + Name);
    }

    /**
     * Add item to the current table
     * @param items Value
     * @throws SQLException
     */
    public void addItem(Value items) throws SQLException {
        for (var entry : items.entrySet()) {
            var key = entry.getKey();
            var value = entry.getValue();
            if (items.get(key).getClass() != this.Column.getType(key)){
                throw new SQLException("Item '" + key + "' is in the wrong Type");
            }
        }
        StringBuilder query = new StringBuilder("INSERT INTO " + Name + " VALUES(");
        for (int i = 0; i < this.Column.size(); i++){
            Object value = items.get(items.keys.get(i));
            if (value.getClass() == String.class || value.getClass() == Byte.class){
                query.append("'" + value + "'");
            }else{
                query.append(value);
            }
            if (i != this.Column.size() - 1){
                query.append(",");
            }
        }
        query.append(")");
        this.runCommand(query.toString());
    }

    /**
     * Edit an item in the current table, if identifer found more than one item, the operation will not go through (SQLException is thrown)
     * @param Identifier String | SQLite WHERE <code>{identifier}</code>
     * @param items Value
     * @throws SQLException
     */
    public void editItem(String Identifier, Value items) throws SQLException {
        for (var entry : items.entrySet()) {
            var key = entry.getKey();
            var value = entry.getValue();
            if (items.get(key) != this.Column.getType(key)){
                throw new SQLException("Item '" + key + "' is in the wrong Type");
            }
        }
        List<Value> oldItem = this.getItems(Identifier);
        if (oldItem.size() > 1){
            throw new SQLException("Multiple items with the same identifier '" + Identifier + "'");
        }else {
            StringBuilder query = new StringBuilder("INSERT INTO " + Name + "VALUES(");
            for (int i = 0; i < this.Column.size(); i++) {
                query.append("?");
                if (i != this.Column.size() - 1) {
                    query.append(",");
                }
            }
            query.append("WHERE " + Identifier + ")");
            this.runCommand(query.toString());
        }
    }

    /**
     * Get a list of items following the identifer, Identifier can be any length
     * @param identifier String | SQLite WHERE <code>{identifier}</code>
     * @return <code>List&lt;Value&gt;</code> Returns a list of Values
     * @throws SQLException
     */
    public List<Value> getItems(String identifier) throws SQLException {
        StringBuilder query = new StringBuilder("SELECT * ");
        query.append(" FROM ").append(Name).append(" WHERE "+identifier);
        ResultSet rs = runQuery(query.toString());
        List<Map<String, Object>> itemValues = new ArrayList<>();
        ResultSetMetaData rsmd = rs.getMetaData();
        int columnCount = rsmd.getColumnCount();
        while (rs.next()){
            Map<String, Object> item = new HashMap<>();
            for (int i = 1; i <= columnCount; i++) {
                String columnName = rsmd.getColumnName(i);
                Object columnValue = rs.getObject(i);
                item.put(columnName, columnValue);
            }
            itemValues.add(item);
        }
        List<Value> fieldValues = new ArrayList<>();
        for (Map<String, Object> item : itemValues) {
            Value items = new Value();
            items.ValuesInit(itemValues);
            fieldValues.add(items);
        }
        return fieldValues;
    };

    /**
     * Get all items from the current table
     * @return <code>List&lt;Value&gt;</code> Returns a list of Values
     * @throws SQLException
     */
    public List<Value> getAllItems() throws SQLException {
        StringBuilder query = new StringBuilder("SELECT * ");
        query.append(" FROM ").append(Name);
        ResultSet rs = runQuery(query.toString());
        List<Map<String, Object>> itemValues = new ArrayList<>();
        ResultSetMetaData rsmd = rs.getMetaData();
        int columnCount = rsmd.getColumnCount();
        while (rs.next()){
            Map<String, Object> item = new HashMap<>();
            for (int i = 1; i <= columnCount; i++) {
                String columnName = rsmd.getColumnName(i);
                Object columnValue = rs.getObject(i);
                item.put(columnName, columnValue);
            }
            itemValues.add(item);
        }
        List<Value> fieldValues = new ArrayList<>();
        for (Map<String, Object> item : itemValues) {
            Value items = new Value();
            items.ValuesInit(itemValues);
            fieldValues.add(items);
        }
        return fieldValues;
    }

    /**
     * Get a list of values from items following the identifer, Identifier can be any length
     * @param identifier String | SQLite WHERE <code>{identifier}</code>
     * @param values String[] | The values you want to get
     * @return <code>List&lt;Value&gt;</code> Returns a list of Values
     * @throws SQLException
     */
    public List<Value> getValues(String identifier, String[] values) throws SQLException {
        StringBuilder query = new StringBuilder("SELECT ");
        for (int i=0; i < values.length; i++) {
            query.append(values[i]);
            if (i != values.length-1) {
                query.append(", ");
            }
        }
        query.append(" FROM ").append(Name).append(" WHERE "+identifier);
        ResultSet rs = runQuery(query.toString());
        List<Map<String, Object>> itemValues = new ArrayList<>();
        ResultSetMetaData rsmd = rs.getMetaData();
        int columnCount = rsmd.getColumnCount();
        while (rs.next()){
            Map<String, Object> item = new HashMap<>();
            for (int i = 1; i <= columnCount; i++) {
                String columnName = rsmd.getColumnName(i);
                Object columnValue = rs.getObject(i);
                item.put(columnName, columnValue);
            }
            itemValues.add(item);
        }
        List<Value> fieldValues = new ArrayList<>();
        for (Map<String, Object> item : itemValues) {
            Value items = new Value();
            items.ValuesInit(itemValues);
            fieldValues.add(items);
        }
        return fieldValues;
    }

    /**
     * Delete item from the table, if more than one item is found, all items will be deleted
     * @param identfier String | SQLite WHERE <code>{identifier}</code>
     * @throws SQLException
     */
    public void deleteItem(String identfier) throws SQLException{
        this.runCommand("DELETE FROM " + Name + " WHERE " + identfier);
    }

    /**
     * Convert the current table to a JSONObject
     * @return <code>JSONObject</code>
     * @throws SQLException
     */
    public JSONObj toJSON() throws SQLException {
        List<Value> allItems = this.getAllItems();
        Map<String, Object> table = new HashMap<>();
        List<Object> itemValues = new ArrayList<>();
        for (Value item : allItems) {
            itemValues.add(item.data);
        }
        table.put(this.Name, itemValues);
        return new JSONObj(table);
    }

}

