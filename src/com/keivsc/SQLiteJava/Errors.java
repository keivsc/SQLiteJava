package com.keivsc.SQLiteJava;

import java.sql.SQLException;

public class Errors {
    public static class DatabaseException extends SQLException {
        public DatabaseException(String message) {
            super(message);
        }
    }

    public static class QueryException extends DatabaseException {
        public QueryException(String message) {
            super(message);
        }
    }

    public static class TableException extends DatabaseException {
        public TableException(String message) {
            super(message);
        }
    }

    public static class CommandException extends DatabaseException {
        public CommandException(String message) {
            super(message);
        }
    }
}
