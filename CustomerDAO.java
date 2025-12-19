package Energy;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CustomerDAO {
    
    public List<String[]> getAllCustomers() {
        List<String[]> customers = new ArrayList<>();
        try (Connection conn = DBConnection.getConnection()) {
            String sql = "SELECT CustomerID, Username, Email, FullName, Role, LastLogin, IsActive FROM customer";
            PreparedStatement pstmt = conn.prepareStatement(sql);
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                String[] customer = {
                    String.valueOf(rs.getInt("CustomerID")),
                    rs.getString("Username"),
                    rs.getString("Email"),
                    rs.getString("FullName"),
                    rs.getString("Role"),
                    rs.getString("LastLogin"),
                    rs.getBoolean("IsActive") ? "Active" : "Inactive"
                };
                customers.add(customer);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return customers;
    }
    
    public String[] getCustomerById(int customerId) {
        try (Connection conn = DBConnection.getConnection()) {
            String sql = "SELECT CustomerID, Username, Email, FullName, Role, LastLogin, IsActive FROM customer WHERE CustomerID = ?";
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, customerId);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                return new String[]{
                    String.valueOf(rs.getInt("CustomerID")),
                    rs.getString("Username"),
                    rs.getString("Email"),
                    rs.getString("FullName"),
                    rs.getString("Role"),
                    rs.getString("LastLogin"),
                    rs.getBoolean("IsActive") ? "Active" : "Inactive"
                };
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
    
    public boolean addCustomer(String username, String password, String email, String fullName, String role, boolean isActive) {
        try (Connection conn = DBConnection.getConnection()) {
            String sql = "INSERT INTO customer (Username, PasswordHash, Email, FullName, Role, IsActive) VALUES (?, ?, ?, ?, ?, ?)";
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, username);
            pstmt.setString(2, password);
            pstmt.setString(3, email);
            pstmt.setString(4, fullName);
            pstmt.setString(5, role);
            pstmt.setBoolean(6, isActive);
            
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    public boolean updateCustomer(int customerId, String username, Object object, String email, String fullName, String role, boolean isActive) {
        try (Connection conn = DBConnection.getConnection()) {
            String sql;
            PreparedStatement pstmt;
            
            if (object != null && !((List<String[]>) object).isEmpty()) {
                sql = "UPDATE customer SET Username=?, PasswordHash=?, Email=?, FullName=?, Role=?, IsActive=? WHERE CustomerID=?";
                pstmt = conn.prepareStatement(sql);
                pstmt.setString(1, username);
                pstmt.setLong(2, (long) object);
                pstmt.setString(3, email);
                pstmt.setString(4, fullName);
                pstmt.setString(5, role);
                pstmt.setBoolean(6, isActive);
                pstmt.setInt(7, customerId);
            } else {
                sql = "UPDATE customer SET Username=?, Email=?, FullName=?, Role=?, IsActive=? WHERE CustomerID=?";
                pstmt = conn.prepareStatement(sql);
                pstmt.setString(1, username);
                pstmt.setString(2, email);
                pstmt.setString(3, fullName);
                pstmt.setString(4, role);
                pstmt.setBoolean(5, isActive);
                pstmt.setInt(6, customerId);
            }
            
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    public boolean deleteCustomer(int customerId) {
        try (Connection conn = DBConnection.getConnection()) {
            String sql = "DELETE FROM customer WHERE CustomerID = ?";
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, customerId);
            
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    public int getTotalCustomers() {
        try (Connection conn = DBConnection.getConnection()) {
            String sql = "SELECT COUNT(*) FROM customer WHERE IsActive = 1";
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
}
