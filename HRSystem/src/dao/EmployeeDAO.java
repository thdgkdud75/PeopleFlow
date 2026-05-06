package dao;

import model.Employee;
import util.DBUtil;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class EmployeeDAO {

    public Employee findByEmpNo(String empNo) throws SQLException {
        String sql = "SELECT * FROM employee WHERE emp_no = ?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, empNo);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return mapRow(rs);
        }
        return null;
    }

    public Employee findById(int empId) throws SQLException {
        String sql = "SELECT * FROM employee WHERE emp_id = ?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, empId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return mapRow(rs);
        }
        return null;
    }

    public List<Employee> findAll() throws SQLException {
        List<Employee> list = new ArrayList<>();
        String sql = "SELECT e.*, d.dept_name, p.pos_name FROM employee e " +
                     "LEFT JOIN department d ON e.dept_id = d.dept_id " +
                     "LEFT JOIN position p ON e.pos_id = p.pos_id " +
                     "WHERE e.status != 'RESIGNED' ORDER BY e.emp_id";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) list.add(mapRow(rs));
        }
        return list;
    }

    public int insert(Employee emp) throws SQLException {
        String sql = "INSERT INTO employee (emp_no, name, email, phone, dept_id, pos_id, hire_date, status, role, password) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, emp.getEmpNo());
            ps.setString(2, emp.getName());
            ps.setString(3, emp.getEmail());
            ps.setString(4, emp.getPhone());
            ps.setInt(5, emp.getDeptId());
            ps.setInt(6, emp.getPosId());
            ps.setDate(7, Date.valueOf(emp.getHireDate()));
            ps.setString(8, emp.getStatus());
            ps.setString(9, emp.getRole());
            ps.setString(10, emp.getPassword());
            return ps.executeUpdate();
        }
    }

    public int update(Employee emp) throws SQLException {
        String sql = "UPDATE employee SET name=?, email=?, phone=?, dept_id=?, pos_id=?, status=? WHERE emp_id=?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, emp.getName());
            ps.setString(2, emp.getEmail());
            ps.setString(3, emp.getPhone());
            ps.setInt(4, emp.getDeptId());
            ps.setInt(5, emp.getPosId());
            ps.setString(6, emp.getStatus());
            ps.setInt(7, emp.getEmpId());
            return ps.executeUpdate();
        }
    }

    public int delete(int empId) throws SQLException {
        String sql = "UPDATE employee SET status='RESIGNED' WHERE emp_id=?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, empId);
            return ps.executeUpdate();
        }
    }

    private Employee mapRow(ResultSet rs) throws SQLException {
        Employee emp = new Employee();
        emp.setEmpId(rs.getInt("emp_id"));
        emp.setEmpNo(rs.getString("emp_no"));
        emp.setName(rs.getString("name"));
        emp.setEmail(rs.getString("email"));
        emp.setPhone(rs.getString("phone"));
        emp.setDeptId(rs.getInt("dept_id"));
        emp.setPosId(rs.getInt("pos_id"));
        Date hireDate = rs.getDate("hire_date");
        if (hireDate != null) emp.setHireDate(hireDate.toLocalDate());
        emp.setStatus(rs.getString("status"));
        emp.setRole(rs.getString("role"));
        emp.setPassword(rs.getString("password"));
        return emp;
    }
}
