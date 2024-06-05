import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import com.keivsc.SQLiteJava.*;
import org.json.JSONObject;

import static java.lang.System.exit;

public class ExampleMain {
    public static void main(String[] args) {

        try {
            Database db = new Database("Example.db");

            //Exporting Database to json
            JSONObject databaseJson = db.toJSON();

            //Connecting to tables
            Table albumTable = db.ConnectTable("Album");

            //Creating a Table
            String[] Columns = {
                    "ID INTEGER PRIMARY KEY NOT NULL AUTO_INCREMENT",
                    "Name TEXT NOT NULL",
                    "AGE INTEGER NOT NULL",
            };
            Table exampleTable = db.CreateTable("Example", Columns);
            if (exampleTable == null) {
                System.out.println("Table not found");
                db.close();
                exit(1);
            }

            //Adding items to a table
            Value newItem = new Value();
            newItem.addItem("ID", 1); //usually not needed since its auto_increment
            newItem.addItem("Name", "John Doe");
            newItem.addItem("Age", 22);
            exampleTable.addItem(newItem);

            //Query Items from a table
            List<Value> queryItem = exampleTable.getItems("Name = 'John Doe'");
            Value johnDoe = queryItem.getFirst(); // all items are type Value
            System.out.println(johnDoe.get("ID"));
            System.out.println(johnDoe.data); // Prints the entire row of data where name = john doe

            //Deleting items from a table
            exampleTable.deleteItem("ID = 100"); // Void function

            //Editing items from a table
            Value newValue = new Value();
            newValue.addItem("ID", 1);
            newValue.addItem("Name", "John Smith");
            newValue.addItem("Age", 25);
            exampleTable.editItem("ID = 1", newValue); // Void Function

            //Clear Table
            exampleTable.ClearTable();

            //Running custom sql command / query
            exampleTable.runCommand("SQL COMMAND"); // Use this when nothing is returned such as inserting or editing rows
            ResultSet rs = exampleTable.runQuery("SQL Query"); //Use this when it returns something
            // format rs set like this
            while (rs.next()) {
                int id = rs.getInt("ColumnName ie. ID");
                String name = rs.getString("ColumnName ie. Name");
            }


            //Exporting Table to a json format
            exampleTable.toJSON();

            db.close(); // Always end with db.close or the database won't update
        }catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
