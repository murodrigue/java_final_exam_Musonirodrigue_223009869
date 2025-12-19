package Energy;


import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class MeterDAO {
    
    public List<String[]> getAllMeters() {
        List<String[]> meters = new ArrayList<>();
        try (Connection conn = DBConnection.getConnection()) {
            String sql = "SELECT m.MeterID, c.FullName, m.Name, m.Description, " +
                        "m.Category, m.PriceOrValue, m.Status, m.CreatedAt " +
                        "FROM meter m LEFT JOIN customer c ON m.CustomerID = c.CustomerID " +
                        "ORDER BY m.MeterID";
            PreparedStatement pstmt = conn.prepareStatement(sql);
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                String[] meter = {
                    String.valueOf(rs.getInt("MeterID")),
                    rs.getString("FullName") != null ? rs.getString("FullName") : "N/A",
                    rs.getString("Name"),
                    rs.getString("Description"),
                    rs.getString("Category"),
                    String.format("%.2f", rs.getDouble("PriceOrValue")),
                    rs.getString("Status"),
                    rs.getString("CreatedAt")
                };
                meters.add(meter);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return meters;
    }
    
    public String[] getMeterById(int meterId) {
        try (Connection conn = DBConnection.getConnection()) {
            String sql = "SELECT m.MeterID, m.CustomerID, c.FullName, m.Name, m.Description, " +
                        "m.Category, m.PriceOrValue, m.Status, m.CreatedAt " +
                        "FROM meter m LEFT JOIN customer c ON m.CustomerID = c.CustomerID " +
                        "WHERE m.MeterID = ?";
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, meterId);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                return new String[]{
                    String.valueOf(rs.getInt("MeterID")),
                    String.valueOf(rs.getInt("CustomerID")),
                    rs.getString("FullName"),
                    rs.getString("Name"),
                    rs.getString("Description"),
                    rs.getString("Category"),
                    String.valueOf(rs.getDouble("PriceOrValue")),
                    rs.getString("Status"),
                    rs.getString("CreatedAt")
                };
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
    
    public boolean addMeter(int customerId, String name, String description, String category, double priceOrValue, String status) {
        try (Connection conn = DBConnection.getConnection()) {
            String sql = "INSERT INTO meter (CustomerID, Name, Description, Category, PriceOrValue, Status) VALUES (?, ?, ?, ?, ?, ?)";
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, customerId);
            pstmt.setString(2, name);
            pstmt.setString(3, description);
            pstmt.setString(4, category);
            pstmt.setDouble(5, priceOrValue);
            pstmt.setString(6, status);
            
            int result = pstmt.executeUpdate();
            return result > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    public boolean updateMeter(int meterId, int customerId, String name, String description, String category, double priceOrValue, String status) {
        try (Connection conn = DBConnection.getConnection()) {
            String sql = "UPDATE meter SET CustomerID=?, Name=?, Description=?, Category=?, PriceOrValue=?, Status=? WHERE MeterID=?";
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, customerId);
            pstmt.setString(2, name);
            pstmt.setString(3, description);
            pstmt.setString(4, category);
            pstmt.setDouble(5, priceOrValue);
            pstmt.setString(6, status);
            pstmt.setInt(7, meterId);
            
            int result = pstmt.executeUpdate();
            return result > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    public boolean deleteMeter(int meterId) {
        try (Connection conn = DBConnection.getConnection()) {
            String sql = "DELETE FROM meter WHERE MeterID = ?";
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, meterId);
            
            int result = pstmt.executeUpdate();
            return result > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    public int getTotalMeters() {
        try (Connection conn = DBConnection.getConnection()) {
            String sql = "SELECT COUNT(*) FROM meter WHERE Status = 'Active'";
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
    
    public boolean meterExists(int meterId) {
        try (Connection conn = DBConnection.getConnection()) {
            String sql = "SELECT COUNT(*) FROM meter WHERE MeterID = ?";
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, meterId);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
    
    public List<String> getCategories() {
        List<String> categories = new ArrayList<>();
        categories.add("Residential");
        categories.add("Commercial");
        categories.add("Industrial");
        categories.add("Agricultural");
        return categories;
    }
    
    public List<String> getStatusOptions() {
        List<String> statusOptions = new ArrayList<String>();
        statusOptions.add("Active");
        statusOptions.add("Inactive");
        statusOptions.add("Maintenance");
        statusOptions.add("Disconnected");
        return statusOptions;
    }
}