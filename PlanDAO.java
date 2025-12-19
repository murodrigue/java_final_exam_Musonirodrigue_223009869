package Energy;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PlanDAO {
    
    public List<String[]> getAllPlans() {
        List<String[]> plans = new ArrayList<>();
        try (Connection conn = DBConnection.getConnection()) {
            String sql = "SELECT p.PlanID, c.FullName, p.PlanName, p.Description, p.TariffRate, p.MonthlyFee, p.CreatedAt " +
                        "FROM plan p LEFT JOIN customer c ON p.CustomerID = c.CustomerID " +
                        "ORDER BY p.PlanID";
            PreparedStatement pstmt = conn.prepareStatement(sql);
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                String[] plan = {
                    String.valueOf(rs.getInt("PlanID")),
                    rs.getString("FullName") != null ? rs.getString("FullName") : "N/A",
                    rs.getString("PlanName"),
                    rs.getString("Description"),
                    String.format("%.4f", rs.getDouble("TariffRate")),
                    String.format("%.2f", rs.getDouble("MonthlyFee")),
                    rs.getString("CreatedAt")
                };
                plans.add(plan);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return plans;
    }
    
    public String[] getPlanById(int planId) {
        try (Connection conn = DBConnection.getConnection()) {
            String sql = "SELECT p.PlanID, p.CustomerID, c.FullName, p.PlanName, p.Description, p.TariffRate, p.MonthlyFee, p.CreatedAt " +
                        "FROM plan p LEFT JOIN customer c ON p.CustomerID = c.CustomerID WHERE p.PlanID = ?";
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, planId);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                return new String[]{
                    String.valueOf(rs.getInt("PlanID")),
                    String.valueOf(rs.getInt("CustomerID")),
                    rs.getString("FullName"),
                    rs.getString("PlanName"),
                    rs.getString("Description"),
                    String.valueOf(rs.getDouble("TariffRate")),
                    String.valueOf(rs.getDouble("MonthlyFee")),
                    rs.getString("CreatedAt")
                };
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
    
    public boolean addPlan(int customerId, String planName, String description, double tariffRate, double monthlyFee) {
        try (Connection conn = DBConnection.getConnection()) {
            String sql = "INSERT INTO plan (CustomerID, PlanName, Description, TariffRate, MonthlyFee) VALUES (?, ?, ?, ?, ?)";
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, customerId);
            pstmt.setString(2, planName);
            pstmt.setString(3, description);
            pstmt.setDouble(4, tariffRate);
            pstmt.setDouble(5, monthlyFee);
            
            int result = pstmt.executeUpdate();
            return result > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    public boolean updatePlan(int planId, int customerId, String planName, String description, double tariffRate, double monthlyFee) {
        try (Connection conn = DBConnection.getConnection()) {
            String sql = "UPDATE plan SET CustomerID=?, PlanName=?, Description=?, TariffRate=?, MonthlyFee=? WHERE PlanID=?";
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, customerId);
            pstmt.setString(2, planName);
            pstmt.setString(3, description);
            pstmt.setDouble(4, tariffRate);
            pstmt.setDouble(5, monthlyFee);
            pstmt.setInt(6, planId);
            
            int result = pstmt.executeUpdate();
            return result > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    public boolean deletePlan(int planId) {
        try (Connection conn = DBConnection.getConnection()) {
            String sql = "DELETE FROM plan WHERE PlanID = ?";
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, planId);
            
            int result = pstmt.executeUpdate();
            return result > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    public int getTotalPlans() {
        try (Connection conn = DBConnection.getConnection()) {
            String sql = "SELECT COUNT(*) FROM plan";
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
    
    public boolean planExists(int planId) {
        try (Connection conn = DBConnection.getConnection()) {
            String sql = "SELECT COUNT(*) FROM plan WHERE PlanID = ?";
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, planId);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
    
    public boolean isPlanNameUnique(String planName, int excludePlanId) {
        try (Connection conn = DBConnection.getConnection()) {
            String sql = "SELECT COUNT(*) FROM plan WHERE PlanName = ? AND PlanID != ?";
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, planName);
            pstmt.setInt(2, excludePlanId);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                return rs.getInt(1) == 0;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
    
    public List<String> getPlanTypes() {
        List<String> planTypes = new ArrayList<>();
        planTypes.add("Residential Basic");
        planTypes.add("Residential Premium");
        planTypes.add("Commercial Standard");
        planTypes.add("Commercial Premium");
        planTypes.add("Industrial");
        planTypes.add("Time-of-Use");
        planTypes.add("Fixed Rate");
        planTypes.add("Variable Rate");
        return planTypes;
    }
    
    public List<String> getBillingCycles() {
        List<String> billingCycles = new ArrayList<>();
        billingCycles.add("Monthly");
        billingCycles.add("Bi-Monthly");
        billingCycles.add("Quarterly");
        billingCycles.add("Annual");
        return billingCycles;
    }
}