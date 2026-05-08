package dao;

import model.Department;
import util.DBUtil;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DeptDAO {

    public List<Department> findAll() throws SQLException {
        List<Department> list = new ArrayList<>();
        String sql = "SELECT d.*, e.name AS manager_name, " +
                     "(SELECT COUNT(*) FROM employee em WHERE em.dept_id = d.dept_id AND em.status != 'RESIGNED') AS emp_count " +
                     "FROM department d LEFT JOIN employee e ON d.manager_id = e.emp_id ORDER BY d.dept_id";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) list.add(mapRow(rs));
        }
        return list;
    }

    public Department findById(int deptId) throws SQLException {
        String sql = "SELECT d.*, e.name AS manager_name FROM department d " +
                     "LEFT JOIN employee e ON d.manager_id = e.emp_id WHERE d.dept_id=?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, deptId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return mapRow(rs);
        }
        return null;
    }

    public int insert(Department dept) throws SQLException {
        String sql = "INSERT INTO department (dept_name, manager_id) VALUES (?, ?)";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, dept.getDeptName());
            if (dept.getManagerId() > 0) ps.setInt(2, dept.getManagerId());
            else ps.setNull(2, Types.INTEGER);
            return ps.executeUpdate();
        }
    }

    public int update(Department dept) throws SQLException {
        String sql = "UPDATE department SET dept_name=?, manager_id=? WHERE dept_id=?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, dept.getDeptName());
            if (dept.getManagerId() > 0) ps.setInt(2, dept.getManagerId());
            else ps.setNull(2, Types.INTEGER);
            ps.setInt(3, dept.getDeptId());
            return ps.executeUpdate();
        }
    }

    public int delete(int deptId) throws SQLException {
        String sql = "DELETE FROM department WHERE dept_id=?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, deptId);
            return ps.executeUpdate();
        }
    }

    public int countEmployees(int deptId) throws SQLException {
        String sql = "SELECT COUNT(*) FROM employee WHERE dept_id=? AND status != 'RESIGNED'";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, deptId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return rs.getInt(1);
        }
        return 0;
    }

    private Department mapRow(ResultSet rs) throws SQLException {
        Department dept = new Department();
        dept.setDeptId(rs.getInt("dept_id"));
        dept.setDeptName(rs.getString("dept_name"));
        dept.setManagerId(rs.getInt("manager_id"));
        try { dept.setManagerName(rs.getString("manager_name")); } catch (Exception ignored) {}
        try { dept.setEmpCount(rs.getInt("emp_count")); } catch (Exception ignored) {}
        return dept;
    }
}
