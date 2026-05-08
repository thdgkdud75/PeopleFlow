package dao;

import model.Salary;
import util.DBUtil;

import java.sql.*;
import java.util.*;

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

    public int confirmAllByMonth(String month) throws SQLException {
        String sql = "UPDATE salary SET pay_status='PAID' WHERE sal_month=? AND pay_status='PENDING'";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, month);
            return ps.executeUpdate();
        }
    }

    public Salary findById(int salId) throws SQLException {
        String sql = "SELECT s.*, e.name AS emp_name, d.dept_name, p.pos_name " +
                     "FROM salary s " +
                     "JOIN employee e ON s.emp_id = e.emp_id " +
                     "LEFT JOIN department d ON e.dept_id = d.dept_id " +
                     "LEFT JOIN `position` p ON e.pos_id = p.pos_id " +
                     "WHERE s.sal_id=?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, salId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return mapRow(rs);
        }
        return null;
    }

    public List<Salary> findByEmpId(int empId) throws SQLException {
        List<Salary> list = new ArrayList<>();
        String sql = "SELECT s.*, e.name AS emp_name, d.dept_name, p.pos_name " +
                     "FROM salary s " +
                     "JOIN employee e ON s.emp_id = e.emp_id " +
                     "LEFT JOIN department d ON e.dept_id = d.dept_id " +
                     "LEFT JOIN `position` p ON e.pos_id = p.pos_id " +
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
        String sql = "SELECT s.*, e.name AS emp_name, d.dept_name, p.pos_name " +
                     "FROM salary s " +
                     "JOIN employee e ON s.emp_id = e.emp_id " +
                     "LEFT JOIN department d ON e.dept_id = d.dept_id " +
                     "LEFT JOIN `position` p ON e.pos_id = p.pos_id " +
                     "WHERE s.sal_month=? ORDER BY d.dept_id, e.emp_id";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, yearMonth);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) list.add(mapRow(rs));
        }
        return list;
    }

    // 해당 월에 아직 급여가 생성되지 않은 재직 직원의 {emp_id → base_salary} 반환
    public Map<Integer, Long> findEmployeesNeedingSalary(String month) throws SQLException {
        Map<Integer, Long> map = new LinkedHashMap<>();
        String sql = "SELECT e.emp_id, p.base_salary " +
                     "FROM employee e " +
                     "JOIN `position` p ON e.pos_id = p.pos_id " +
                     "WHERE e.status = 'ACTIVE' " +
                     "AND e.emp_id NOT IN (SELECT emp_id FROM salary WHERE sal_month = ?)";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, month);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) map.put(rs.getInt("emp_id"), rs.getLong("base_salary"));
        }
        return map;
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
        try { sal.setDeptName(rs.getString("dept_name")); } catch (Exception ignored) {}
        try { sal.setPosName(rs.getString("pos_name"));  } catch (Exception ignored) {}
        return sal;
    }
}
