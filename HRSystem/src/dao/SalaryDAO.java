package dao;

import model.Salary;
import util.DBUtil;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class SalaryDAO {

    public int insert(Salary sal) throws SQLException {
        String sql = "INSERT INTO salary (emp_id, sal_month, base_pay, allowance, deduction, net_pay, pay_status) " +
                     "VALUES (?, ?, ?, ?, ?, ?, 'PENDING')";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, sal.getEmpId());
            ps.setString(2, sal.getSalMonth());
            ps.setLong(3, sal.getBasePay());
            ps.setLong(4, sal.getAllowance());
            ps.setLong(5, sal.getDeduction());
            ps.setLong(6, sal.getNetPay());
            return ps.executeUpdate();
        }
    }

    public int updateStatus(int salId, String status) throws SQLException {
        String sql = "UPDATE salary SET pay_status=? WHERE sal_id=?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, status);
            ps.setInt(2, salId);
            return ps.executeUpdate();
        }
    }

    public List<Salary> findByEmpId(int empId) throws SQLException {
        List<Salary> list = new ArrayList<>();
        String sql = "SELECT s.*, e.name as emp_name FROM salary s " +
                     "JOIN employee e ON s.emp_id = e.emp_id " +
                     "WHERE s.emp_id=? ORDER BY s.sal_month DESC";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, empId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) list.add(mapRow(rs));
        }
        return list;
    }

    public List<Salary> findByMonth(String yearMonth) throws SQLException {
        List<Salary> list = new ArrayList<>();
        String sql = "SELECT s.*, e.name as emp_name FROM salary s " +
                     "JOIN employee e ON s.emp_id = e.emp_id " +
                     "WHERE s.sal_month=? ORDER BY e.emp_id";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, yearMonth);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) list.add(mapRow(rs));
        }
        return list;
    }

    private Salary mapRow(ResultSet rs) throws SQLException {
        Salary sal = new Salary();
        sal.setSalId(rs.getInt("sal_id"));
        sal.setEmpId(rs.getInt("emp_id"));
        sal.setEmpName(rs.getString("emp_name"));
        sal.setSalMonth(rs.getString("sal_month"));
        sal.setBasePay(rs.getLong("base_pay"));
        sal.setAllowance(rs.getLong("allowance"));
        sal.setDeduction(rs.getLong("deduction"));
        sal.setNetPay(rs.getLong("net_pay"));
        sal.setPayStatus(rs.getString("pay_status"));
        return sal;
    }
}
