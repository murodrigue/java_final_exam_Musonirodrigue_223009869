package Energy;



import java.sql.Connection;
import java.sql.Statement;

public class DatabaseSetup {
    public static void createDatabase() {
        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement()) {
            
            // Create tables
            String[] createTables = {
                // Customer table
                "CREATE TABLE IF NOT EXISTS Customer (" +
                "CustomerID INT PRIMARY KEY AUTO_INCREMENT, " +
                "Username VARCHAR(50) UNIQUE NOT NULL, " +
                "PasswordHash VARCHAR(255) NOT NULL, " +
                "Email VARCHAR(100), " +
                "FullName VARCHAR(100) NOT NULL, " +
                "Role VARCHAR(20) DEFAULT 'user', " +
                "CreatedAt DATETIME DEFAULT CURRENT_TIMESTAMP, " +
                "LastLogin DATETIME, " +
                "IsActive BOOLEAN DEFAULT 1)",
                
                // Meter table
                "CREATE TABLE IF NOT EXISTS Meter (" +
                "MeterID INT PRIMARY KEY AUTO_INCREMENT, " +
                "CustomerID INT, " +
                "Name VARCHAR(100) NOT NULL, " +
                "Description TEXT, " +
                "Category VARCHAR(50), " +
                "PriceOrValue DECIMAL(10,2), " +
                "Status VARCHAR(20) DEFAULT 'Active', " +
                "CreatedAt DATETIME DEFAULT CURRENT_TIMESTAMP, " +
                "FOREIGN KEY (CustomerID) REFERENCES Customer(CustomerID))",
                
                // Reading table
                "CREATE TABLE IF NOT EXISTS Reading (" +
                "ReadingID INT PRIMARY KEY AUTO_INCREMENT, " +
                "CustomerID INT, " +
                "MeterID INT, " +
                "Amount DECIMAL(10,2) NOT NULL, " +
                "Date DATE NOT NULL, " +
                "Type VARCHAR(50), " +
                "Reference VARCHAR(100), " +
                "Status VARCHAR(20) DEFAULT 'Recorded', " +
                "CreatedAt DATETIME DEFAULT CURRENT_TIMESTAMP, " +
                "FOREIGN KEY (CustomerID) REFERENCES Customer(CustomerID), " +
                "FOREIGN KEY (MeterID) REFERENCES Meter(MeterID))",
                
                // Plan table
                "CREATE TABLE IF NOT EXISTS Plan (" +
                "PlanID INT PRIMARY KEY AUTO_INCREMENT, " +
                "CustomerID INT, " +
                "PlanName VARCHAR(100) NOT NULL, " +
                "Description TEXT, " +
                "TariffRate DECIMAL(8,4), " +
                "MonthlyFee DECIMAL(8,2), " +
                "CreatedAt DATETIME DEFAULT CURRENT_TIMESTAMP, " +
                "FOREIGN KEY (CustomerID) REFERENCES Customer(CustomerID))",
                
                // Outage table
                "CREATE TABLE IF NOT EXISTS Outage (" +
                "OutageID INT PRIMARY KEY AUTO_INCREMENT, " +
                "CustomerID INT, " +
                "OutageType VARCHAR(50), " +
                "StartTime DATETIME, " +
                "EndTime DATETIME, " +
                "Area VARCHAR(100), " +
                "Status VARCHAR(20) DEFAULT 'Reported', " +
                "CreatedAt DATETIME DEFAULT CURRENT_TIMESTAMP, " +
                "FOREIGN KEY (CustomerID) REFERENCES Customer(CustomerID))",
                
                // Maintenance table
                "CREATE TABLE IF NOT EXISTS Maintenance (" +
                "MaintenanceID INT PRIMARY KEY AUTO_INCREMENT, " +
                "CustomerID INT, " +
                "ReferenceID VARCHAR(100), " +
                "Description TEXT, " +
                "ScheduleDate DATE, " +
                "CompletionDate DATE, " +
                "Status VARCHAR(20) DEFAULT 'Scheduled', " +
                "Remarks TEXT, " +
                "CreatedAt DATETIME DEFAULT CURRENT_TIMESTAMP, " +
                "FOREIGN KEY (CustomerID) REFERENCES Customer(CustomerID))",
                
                // Junction table for Plan-Outage many-to-many
                "CREATE TABLE IF NOT EXISTS PlanOutage (" +
                "PlanID INT, " +
                "OutageID INT, " +
                "CreatedAt DATETIME DEFAULT CURRENT_TIMESTAMP, " +
                "PRIMARY KEY (PlanID, OutageID), " +
                "FOREIGN KEY (PlanID) REFERENCES Plan(PlanID), " +
                "FOREIGN KEY (OutageID) REFERENCES Outage(OutageID))"
            };
            
            for (String sql : createTables) {
                stmt.execute(sql);
            }
            
            // Insert default admin user
            String insertAdmin = "INSERT IGNORE INTO Customer (Username, PasswordHash, Email, FullName, Role) " +
                               "VALUES ('admin', 'admin123', 'admin@energy.com', 'System Administrator', 'admin')";
            stmt.execute(insertAdmin);
            
            System.out.println("Energy Assistant Database setup completed successfully!");
            
        } catch (Exception e) {
            System.out.println("Error setting up database: " + e.getMessage());
            e.printStackTrace();
        }
    }
}