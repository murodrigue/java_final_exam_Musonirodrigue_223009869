package Energy;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ReadingDAO {
    
    public List<String[]> getAllReadings() {
        List<String[]> readings = new ArrayList<>();
        try (Connection conn = DBConnection.getConnection()) {
            String sql = "SELECT r.ReadingID, c.FullName, m.Name as MeterName, r.Amount, r.ReadingDate, " +
                        "r.ReadingType, r.ReferenceNumber, r.Status, r.CreatedAt " +
                        "FROM reading r " +
                        "LEFT JOIN customer c ON r.CustomerID = c.CustomerID " +
                        "LEFT JOIN meter m ON r.MeterID = m.MeterID " +
                        "ORDER BY r.ReadingID";
            PreparedStatement pstmt = conn.prepareStatement(sql);
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                String[] reading = {
                    String.valueOf(rs.getInt("ReadingID")),
                    rs.getString("FullName") != null ? rs.getString("FullName") : "N/A",
                    rs.getString("MeterName") != null ? rs.getString("MeterName") : "N/A",
                    String.format("%.2f", rs.getDouble("Amount")),
                    rs.getString("ReadingDate"),
                    rs.getString("ReadingType"),
                    rs.getString("ReferenceNumber"),
                    rs.getString("Status"),
                    rs.getString("CreatedAt")
                };
                readings.add(reading);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return readings;
    }
    
    public String[] getReadingById(int readingId) {
        try (Connection conn = DBConnection.getConnection()) {
            String sql = "SELECT r.ReadingID, r.CustomerID, r.MeterID, c.FullName, m.Name as MeterName, " +
                        "r.Amount, r.ReadingDate, r.ReadingType, r.ReferenceNumber, r.Status, r.CreatedAt " +
                        "FROM reading r " +
                        "LEFT JOIN customer c ON r.CustomerID = c.CustomerID " +
                        "LEFT JOIN meter m ON r.MeterID = m.MeterID " +
                        "WHERE r.ReadingID = ?";
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, readingId);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                return new String[]{
                    String.valueOf(rs.getInt("ReadingID")),
                    String.valueOf(rs.getInt("CustomerID")),
                    String.valueOf(rs.getInt("MeterID")),
                    rs.getString("FullName"),
                    rs.getString("MeterName"),
                    String.valueOf(rs.getDouble("Amount")),
                    rs.getString("ReadingDate"),
                    rs.getString("ReadingType"),
                    rs.getString("ReferenceNumber"),
                    rs.getString("Status"),
                    rs.getString("CreatedAt")
                };
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
    
    public boolean addReading(int customerId, int meterId, double amount, String readingDate, 
                            String readingType, String referenceNumber, String status) {
        try (Connection conn = DBConnection.getConnection()) {
            String sql = "INSERT INTO reading (CustomerID, MeterID, Amount, ReadingDate, ReadingType, ReferenceNumber, Status) VALUES (?, ?, ?, ?, ?, ?, ?)";
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, customerId);
            pstmt.setInt(2, meterId);
            pstmt.setDouble(3, amount);
            pstmt.setString(4, readingDate);
            pstmt.setString(5, readingType);
            pstmt.setString(6, referenceNumber);
            pstmt.setString(7, status);
            
            int result = pstmt.executeUpdate();
            return result > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    public boolean updateReading(int readingId, int customerId, int meterId, double amount, 
                               String readingDate, String readingType, String referenceNumber, String status) {
        try (Connection conn = DBConnection.getConnection()) {
            String sql = "UPDATE reading SET CustomerID=?, MeterID=?, Amount=?, ReadingDate=?, ReadingType=?, ReferenceNumber=?, Status=? WHERE ReadingID=?";
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, customerId);
            pstmt.setInt(2, meterId);
            pstmt.setDouble(3, amount);
            pstmt.setString(4, readingDate);
            pstmt.setString(5, readingType);
            pstmt.setString(6, referenceNumber);
            pstmt.setString(7, status);
            pstmt.setInt(8, readingId);
            
            int result = pstmt.executeUpdate();
            return result > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    public boolean deleteReading(int readingId) {
        try (Connection conn = DBConnection.getConnection()) {
            String sql = "DELETE FROM reading WHERE ReadingID = ?";
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, readingId);
            
            int result = pstmt.executeUpdate();
            return result > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    public int getTotalReadings() {
        try (Connection conn = DBConnection.getConnection()) {
            String sql = "SELECT COUNT(*) FROM reading";
            PreparedStatement pstmt = conn.prepareStatement(sql);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }
    
    public List<String[]> getAllCustomersForDropdown() {
        List<String[]> customers = new ArrayList<>();
        try (Connection conn = DBConnection.getConnection()) {
            String sql = "SELECT CustomerID, FullName FROM customer WHERE IsActive = 1 ORDER BY FullName";
            PreparedStatement pstmt = conn.prepareStatement(sql);
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                String[] customer = {
                    String.valueOf(rs.getInt("CustomerID")),
                    rs.getString("FullName")
                };
                customers.add(customer);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return customers;
    }
    
    public List<String[]> getMetersForCustomer(int customerId) {
        List<String[]> meters = new ArrayList<>();
        try (Connection conn = DBConnection.getConnection()) {
            String sql = "SELECT MeterID, Name FROM meter WHERE CustomerID = ? AND Status = 'Active' ORDER BY Name";
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, customerId);
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                String[] meter = {
                    String.valueOf(rs.getInt("MeterID")),
                    rs.getString("Name")
                };
                meters.add(meter);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return meters;
    }
    
    public List<String[]> getAllMetersForDropdown() {
        List<String[]> meters = new ArrayList<>();
        try (Connection conn = DBConnection.getConnection()) {
            String sql = "SELECT MeterID, Name FROM meter WHERE Status = 'Active' ORDER BY Name";
            PreparedStatement pstmt = conn.prepareStatement(sql);
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                String[] meter = {
                    String.valueOf(rs.getInt("MeterID")),
                    rs.getString("Name")
                };
                meters.add(meter);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return meters;
    }
    
    public boolean readingExists(int readingId) {
        try (Connection conn = DBConnection.getConnection()) {
            String sql = "SELECT COUNT(*) FROM reading WHERE ReadingID = ?";
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, readingId);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
    
    public List<String> getReadingTypes() {
        List<String> readingTypes = new ArrayList<>();
        readingTypes.add("Monthly");
        readingTypes.add("Bimonthly");
        readingTypes.add("Quarterly");
        readingTypes.add("Annual");
        readingTypes.add("Initial");
        readingTypes.add("Final");
        readingTypes.add("Estimated");
        readingTypes.add("Actual");
        return readingTypes;
    }
    
    public List<String> getStatusOptions() {
        List<String> statusOptions = new ArrayList<>();
        statusOptions.add("Pending");
        statusOptions.add("Approved");
        statusOptions.add("Rejected");
        statusOptions.add("Verified");
        statusOptions.add("Billed");
        return statusOptions;
    }
    
    public double getTotalConsumptionByCustomer(int customerId) {
        try (Connection conn = DBConnection.getConnection()) {
            String sql = "SELECT SUM(Amount) FROM reading WHERE CustomerID = ?";
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, customerId);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                return rs.getDouble(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0.0;
    }
    
    public List<String[]> getRecentReadings(int limit) {
        List<String[]> readings = new ArrayList<>();
        try (Connection conn = DBConnection.getConnection()) {
            String sql = "SELECT r.ReadingID, c.FullName, m.Name as MeterName, r.Amount, r.ReadingDate " +
                        "FROM reading r " +
                        "LEFT JOIN customer c ON r.CustomerID = c.CustomerID " +
                        "LEFT JOIN meter m ON r.MeterID = m.MeterID " +
                        "ORDER BY r.ReadingDate DESC LIMIT ?";
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, limit);
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                String[] reading = {
                    String.valueOf(rs.getInt("ReadingID")),
                    rs.getString("FullName"),
                    rs.getString("MeterName"),
                    String.format("%.2f", rs.getDouble("Amount")),
                    rs.getString("ReadingDate")
                };
                readings.add(reading);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return readings;
    }
}