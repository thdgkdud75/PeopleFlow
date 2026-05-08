package dao;

import model.LeaveRequest;
import util.DBUtil;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class LeaveDAO {

    public int insert(LeaveRequest lr) throws SQLException {
        String sql = "INSERT INTO leave_request (emp_id, leave_type, start_date, end_date, leave_days, reason, status, applied_at) " +
                     "VALUES (?, ?, ?, ?, ?, ?, 'PENDING', NOW())";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, lr.getEmpId());
            ps.setString(2, lr.getLeaveType());
            ps.setDate(3, Date.valueOf(lr.getStartDate()));
            ps.setDate(4, Date.valueOf(lr.getEndDate()));
            ps.setInt(5, lr.getLeaveDays());
            ps.setString(6, lr.getReason());
            return ps.executeUpdate();
        }
    }

    public int updateStatus(int leaveId, String status, int approvedBy) throws SQLException {
        String sql = "UPDATE leave_request SET status=?, approved_by=? WHERE leave_id=?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, status);
            ps.setInt(2, approvedBy);
            ps.setInt(3, leaveId);
            return ps.executeUpdate();
        }
    }

    public List<LeaveRequest> findByEmpId(int empId) throws SQLException {
        List<LeaveRequest> list = new ArrayList<>();
        String sql = "SELECT lr.*, e.name as emp_name FROM leave_request lr " +
                     "JOIN employee e ON lr.emp_id = e.emp_id " +
                     "WHERE lr.emp_id=? ORDER BY lr.applied_at DESC";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, empId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) list.add(mapRow(rs));
        }
        return list;
    }

    public List<LeaveRequest> findPending() throws SQLException {
        List<LeaveRequest> list = new ArrayList<>();
        String sql = "SELECT lr.*, e.name as emp_name FROM leave_request lr " +
                     "JOIN employee e ON lr.emp_id = e.emp_id " +
                     "WHERE lr.status='PENDING' ORDER BY lr.applied_at";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) list.add(mapRow(rs));
        }
        return list;
    }

    public int getRemainLeave(int empId) throws SQLException {
        String sql = "SELECT remain_leave FROM employee WHERE emp_id=?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, empId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return rs.getInt(1);
        }
        return 0;
    }

    public void deductLeave(int empId, int days) throws SQLException {
        String sql = "UPDATE employee SET remain_leave = remain_leave - ? WHERE emp_id=?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, days);
            ps.setInt(2, empId);
            ps.executeUpdate();
        }
    }

    public LeaveRequest findById(int leaveId) throws SQLException {
        String sql = "SELECT lr.*, e.name as emp_name FROM leave_request lr " +
                     "JOIN employee e ON lr.emp_id = e.emp_id WHERE lr.leave_id=?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, leaveId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return mapRow(rs);
        }
        return null;
    }

    public List<LeaveRequest> findAll() throws SQLException {
        List<LeaveRequest> list = new ArrayList<>();
        String sql = "SELECT lr.*, e.name as emp_name FROM leave_request lr " +
                     "JOIN employee e ON lr.emp_id = e.emp_id ORDER BY lr.applied_at DESC";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) list.add(mapRow(rs));
        }
        return list;
    }

    private LeaveRequest mapRow(ResultSet rs) throws SQLException {
        LeaveRequest lr = new LeaveRequest();
        lr.setLeaveId(rs.getInt("leave_id"));
        lr.setEmpId(rs.getInt("emp_id"));
        lr.setEmpName(rs.getString("emp_name"));
        lr.setLeaveType(rs.getString("leave_type"));
        lr.setStartDate(rs.getDate("start_date").toLocalDate());
        lr.setEndDate(rs.getDate("end_date").toLocalDate());
        lr.setLeaveDays(rs.getInt("leave_days"));
        lr.setReason(rs.getString("reason"));
        lr.setStatus(rs.getString("status"));
        lr.setApprovedBy(rs.getInt("approved_by"));
        Date applied = rs.getDate("applied_at");
        if (applied != null) lr.setAppliedAt(applied.toLocalDate());
        return lr;
    }
}
