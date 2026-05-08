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

    public List<Employee> search(String keyword, int deptId, int posId, String status, int offset, int pageSize) throws SQLException {
        List<Employee> list = new ArrayList<>();
        StringBuilder sql = new StringBuilder(
            "SELECT e.*, d.dept_name, p.pos_name FROM employee e " +
            "LEFT JOIN department d ON e.dept_id = d.dept_id " +
            "LEFT JOIN `position` p ON e.pos_id = p.pos_id WHERE 1=1 ");
        List<Object> params = new ArrayList<>();
        appendConditions(sql, params, keyword, deptId, posId, status);
        sql.append("ORDER BY e.emp_id LIMIT ? OFFSET ?");
        params.add(pageSize);
        params.add(offset);

        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql.toString())) {
            for (int i = 0; i < params.size(); i++) ps.setObject(i + 1, params.get(i));
            ResultSet rs = ps.executeQuery();
            while (rs.next()) list.add(mapRow(rs));
        }
        return list;
    }

    public int count(String keyword, int deptId, int posId, String status) throws SQLException {
        StringBuilder sql = new StringBuilder("SELECT COUNT(*) FROM employee e WHERE 1=1 ");
        List<Object> params = new ArrayList<>();
        appendConditions(sql, params, keyword, deptId, posId, status);

        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql.toString())) {
            for (int i = 0; i < params.size(); i++) ps.setObject(i + 1, params.get(i));
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return rs.getInt(1);
        }
        return 0;
    }

    private void appendConditions(StringBuilder sql, List<Object> params,
                                  String keyword, int deptId, int posId, String status) {
        if (keyword != null && !keyword.isBlank()) {
            sql.append("AND (e.name LIKE ? OR e.emp_no LIKE ?) ");
            params.add("%" + keyword + "%");
            params.add("%" + keyword + "%");
        }
        if (deptId > 0) { sql.append("AND e.dept_id = ? "); params.add(deptId); }
        if (posId  > 0) { sql.append("AND e.pos_id = ? ");  params.add(posId);  }
        if ("ALL".equals(status)) {
            // 퇴직 포함 전체
        } else if (status != null && !status.isBlank()) {
            sql.append("AND e.status = ? "); params.add(status);
        } else {
            sql.append("AND e.status != 'RESIGNED' ");
        }
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
        try { emp.setRemainLeave(rs.getInt("remain_leave")); } catch (Exception ignored) {}
        try { emp.setDeptName(rs.getString("dept_name")); } catch (Exception ignored) {}
        try { emp.setPosName(rs.getString("pos_name")); } catch (Exception ignored) {}
        return emp;
    }
}
