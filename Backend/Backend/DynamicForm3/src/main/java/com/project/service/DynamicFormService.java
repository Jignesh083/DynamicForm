package com.project.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class DynamicFormService {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    public void createTableIfNotExists(String tableName, Map<String, Object> fields) {
        try {
            // Table name sanitization and validation (same as before)
            String sanitizedTableName = sanitizeTableName(tableName);

            // SQL query to check if table exists
            String checkTableQuery = "SELECT COUNT(*) FROM information_schema.tables WHERE table_name = ? AND table_schema = 'public'";
            int tableExists = jdbcTemplate.queryForObject(checkTableQuery, Integer.class, sanitizedTableName);

            // If table does not exist, create it
            if (tableExists == 0) {
                // Create a new table if it doesn't exist
                StringBuilder createTableQuery = new StringBuilder("CREATE TABLE " + sanitizedTableName + " (");

                // Loop through fields to build the table creation query
                for (Map.Entry<String, Object> entry : fields.entrySet()) {
                    String columnName = sanitizeColumnName(entry.getKey());
                    Object columnValue = entry.getValue(); // This will be the value (e.g., Integer or String)

                    // Determine column type based on the value
                    String columnType = "";
                    if (columnValue instanceof Integer) {
                        columnType = "INTEGER";
                    } else if (columnValue instanceof String) {
                        columnType = "VARCHAR(255)"; // You can change the length here as needed
                    } else if (columnValue instanceof Boolean) {
                        columnType = "BOOLEAN";
                    } else {
                        columnType = "TEXT";  // Default to TEXT if the type is unknown
                    }

                    // Add the column definition to the CREATE TABLE query
                    createTableQuery.append(columnName).append(" ").append(columnType).append(", ");
                }

                // Remove the trailing comma and space
                createTableQuery.setLength(createTableQuery.length() - 2);
                createTableQuery.append(")");

                // Execute the query to create the table
                jdbcTemplate.execute(createTableQuery.toString());
                System.out.println("Table created successfully: " + sanitizedTableName);
            } else {
                System.out.println("Table already exists: " + sanitizedTableName);
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Error creating table: " + tableName + " - " + e.getMessage());
        }
    }

    public void insertOrUpdateFormData(String tableName, Map<String, Object> fields, Map<String, String> fileData) {
        try {
            // Table name sanitization and validation (same as before)
            String sanitizedTableName = sanitizeTableName(tableName);

            // Check if table exists
            String checkTableQuery = "SELECT COUNT(*) FROM information_schema.tables WHERE table_name = ? AND table_schema = 'public'";
            int tableExists = jdbcTemplate.queryForObject(checkTableQuery, Integer.class, sanitizedTableName);

            if (tableExists == 0) {
                throw new RuntimeException("Table " + sanitizedTableName + " does not exist!");
            }

            // Prepare SQL for inserting data into the table
            StringBuilder insertQuery = new StringBuilder("INSERT INTO " + sanitizedTableName + " (");

            StringBuilder valuesPlaceholder = new StringBuilder("VALUES (");

            // Add fields to query
            for (String key : fields.keySet()) {
                insertQuery.append(sanitizeColumnName(key)).append(", ");
                valuesPlaceholder.append("?, ");
            }

            // Add the file fields to the query
            for (Map.Entry<String, String> entry : fileData.entrySet()) {
                String fileFieldName = entry.getKey();
                String fileName = entry.getValue();
                
                insertQuery.append(sanitizeColumnName(fileFieldName)).append(", ");
                valuesPlaceholder.append("?, ");
            }

            // Finalize query
            insertQuery.setLength(insertQuery.length() - 2);
            valuesPlaceholder.setLength(valuesPlaceholder.length() - 2);

            insertQuery.append(") ").append(valuesPlaceholder).append(")");

            // Prepare values array based on the field types
            Object[] values = new Object[fields.size() + fileData.size()];
            int index = 0;

            // Add field values to the array
            for (Object value : fields.values()) {
                values[index++] = value;
            }

            // Add file name values to the array
            for (String fileName : fileData.values()) {
                values[index++] = fileName;  // Store the file name in its corresponding field
            }

            // Execute the insert query with the values (including file name if available)
            jdbcTemplate.update(insertQuery.toString(), values);

            System.out.println("Data inserted or updated successfully into: " + sanitizedTableName);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Error inserting data into table: " + tableName + " - " + e.getMessage());
        }
    }

    // Sanitization methods (same as before)
    private String sanitizeTableName(String tableName) {
        return tableName.replaceAll("[^a-zA-Z0-9_]", "_").toLowerCase();
    }

    private String sanitizeColumnName(String columnName) {
        if (columnName.matches("^[0-9].*")) {
            return "\"" + columnName + "\"";
        }
        return columnName.replaceAll("[^a-zA-Z0-9_]", "_");
    }
}













//complete work
//package com.project.service;
//
//import java.util.Map;
//
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.jdbc.core.JdbcTemplate;
//import org.springframework.stereotype.Service;
//
//@Service	
//public class DynamicFormService {
//
//    @Autowired
//    private JdbcTemplate jdbcTemplate;
//
//    public void createTableIfNotExists(String tableName, Map<String, String> fields) {
//        try {
//            // Table name ko sanitize karte hain jisse SQL query safe ho sake sql injection attack se
//            String sanitizedTableName = sanitizeTableName(tableName);
//
//            // Yeh query check karti hai ki table database mein already hai ya nahi
//            String checkTableQuery = "SELECT COUNT(*) FROM information_schema.tables WHERE table_name = ? AND table_schema = 'public'";
//            int tableExists = jdbcTemplate.queryForObject(checkTableQuery, Integer.class, sanitizedTableName);
//
//            if (tableExists == 0) {
//                // Agar table nahi hai, toh table create karenge 'id' column ke saath (jo primary key hoga)
//                StringBuilder createTableQuery = new StringBuilder("CREATE TABLE " + sanitizedTableName + " (id SERIAL PRIMARY KEY");
//
//                // Fields map ke according columns add kiye jaate hain
//                for (String field : fields.keySet()) {
//                    createTableQuery.append(", ").append(sanitizeColumnName(field)).append(" VARCHAR(255)");
//                }
//
//                createTableQuery.append(")");
//                // Query ko execute karke table create karte hain
//                jdbcTemplate.execute(createTableQuery.toString());
//                System.out.println("Table created: " + sanitizedTableName);
//            } else {
//                // Agar table already hai, toh 'id' column ka check karte hain
//                String checkIdColumnQuery = "SELECT COUNT(*) FROM information_schema.columns WHERE table_name = ? AND column_name = 'id'";
//                int idColumnExists = jdbcTemplate.queryForObject(checkIdColumnQuery, Integer.class, sanitizedTableName);
//
//                if (idColumnExists == 0) {
//                    // Agar 'id' column nahi hai, toh usse add karte hain
//                    jdbcTemplate.execute("ALTER TABLE " + sanitizedTableName + " ADD COLUMN id SERIAL PRIMARY KEY");
//                    System.out.println("Added 'id' column to existing table: " + sanitizedTableName);
//                } else {
//                    // Agar 'id' column already hai
//                    System.out.println("Table already exists with 'id' column: " + sanitizedTableName);
//                }
//            }
//        } catch (Exception e) {
//            // Agar koi error aati hai toh print karte hain aur exception throw karte hain
//            e.printStackTrace();
//            throw new RuntimeException("Error creating table: " + tableName);
//        }
//    }
//
//    // Yeh method data ko insert ya update karta hai table mein
//    public void insertOrUpdateFormData(String tableName, Map<String, String> fields) {
//        try {
//            // Table name ko sanitize karte hain jisse SQL query safe ho sake
//            String sanitizedTableName = sanitizeTableName(tableName);
//
//            // Yeh query check karti hai ki table database mein exist karta hai ya nahi
//            String checkTableQuery = "SELECT COUNT(*) FROM information_schema.tables WHERE table_name = ? AND table_schema = 'public'";
//            int tableExists = jdbcTemplate.queryForObject(checkTableQuery, Integer.class, sanitizedTableName);
//
//            // Agar table nahi milta, toh exception throw karte hain
//            if (tableExists == 0) {
//                throw new RuntimeException("Table " + sanitizedTableName + " does not exist!");
//            }
//
//            // SQL query construct karte hain data insert/update karne ke liye
//            StringBuilder insertQuery = new StringBuilder("INSERT INTO " + sanitizedTableName + " (");
//            StringBuilder valuesPlaceholder = new StringBuilder("VALUES (");
//            StringBuilder updateQuery = new StringBuilder(" ON CONFLICT (id) DO UPDATE SET ");
//
//            // Har field ko query mein add karte hain
//            for (String key : fields.keySet()) {
//                insertQuery.append(sanitizeColumnName(key)).append(", ");
//                valuesPlaceholder.append("?, ");
//                updateQuery.append(sanitizeColumnName(key)).append(" = EXCLUDED.").append(sanitizeColumnName(key)).append(", ");
//            }
//
//            // Query ke last mein jo extra commas hain unhe hata dete hain
//            insertQuery.setLength(insertQuery.length() - 2);
//            valuesPlaceholder.setLength(valuesPlaceholder.length() - 2);
//            updateQuery.setLength(updateQuery.length() - 2);
//
//            // Query ko complete karte hain
//            insertQuery.append(") ").append(valuesPlaceholder).append(")").append(updateQuery);
//
//            System.out.println("Executing SQL: " + insertQuery);
//
//            // Query execute karte hain fields ki values ke saath
//            jdbcTemplate.update(insertQuery.toString(), fields.values().toArray());
//
//            System.out.println("Data inserted or updated successfully into: " + sanitizedTableName);
//        } catch (Exception e) {
//            e.printStackTrace();
//            throw new RuntimeException("Error inserting data into table: " + tableName + " - " + e.getMessage());
//        }
//    }
//
//    // Table name ko sanitize karte hain jisse SQL injection se bach sakein
//    private String sanitizeTableName(String tableName) {
//        return tableName.replaceAll("[^a-zA-Z0-9_]", "_").toLowerCase();
//    }
//
//    // Column names ko sanitize karte hain jisse SQL injection se bach sakein
//    private String sanitizeColumnName(String columnName) {
//        // Agar column name number se start hota hai toh usse double quotes mein daalenge
//        if (columnName.matches("^[0-9].*")) {
//            return "\"" + columnName + "\"";  // Double quotes lagayenge agar number se start ho raha hai
//        }
//        return columnName.replaceAll("[^a-zA-Z0-9_]", "_");  // Special characters ko underscore se replace karenge
//    }
//
//}