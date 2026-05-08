package dao;

import util.DBUtil;

import java.sql.*;
import java.util.*;

public class DashboardDAO {

    public int countActiveEmployees() throws SQLException {
        String sql = "SELECT COUNT(*) FROM employee WHERE status = 'ACTIVE'";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            if (rs.next()) return rs.getInt(1);
        }
        return 0;
    }

    public int countTodayAttendance() throws SQLException {
        String sql = "SELECT COUNT(*) FROM attendance WHERE att_date = CURDATE()";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            if (rs.next()) return rs.getInt(1);
        }
        return 0;
    }

    public int countPendingLeaves() throws SQLException {
        String sql = "SELECT COUNT(*) FROM leave_request WHERE status = 'PENDING'";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            if (rs.next()) return rs.getInt(1);
        }
        return 0;
    }

    public long getMonthTotalNetPay(String month) throws SQLException {
        String sql = "SELECT COALESCE(SUM(net_pay), 0) FROM salary WHERE sal_month = ?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, month);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return rs.getLong(1);
        }
        return 0L;
    }

    // 최근 미결 휴가 신청 목록 (최대 5건)
    public List<Map<String, Object>> getRecentPendingLeaves() throws SQLException {
        List<Map<String, Object>> list = new ArrayList<>();
        String sql = "SELECT lr.leave_id, e.name, lr.leave_type, lr.start_date, lr.end_date, lr.leave_days " +
                     "FROM leave_request lr JOIN employee e ON lr.emp_id = e.emp_id " +
                     "WHERE lr.status = 'PENDING' ORDER BY lr.applied_at LIMIT 5";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                Map<String, Object> row = new LinkedHashMap<>();
                row.put("leaveId",   rs.getInt("leave_id"));
                row.put("empName",   rs.getString("name"));
                row.put("leaveType", rs.getString("leave_type"));
                row.put("startDate", rs.getDate("start_date"));
                row.put("endDate",   rs.getDate("end_date"));
                row.put("leaveDays", rs.getInt("leave_days"));
                list.add(row);
            }
        }
        return list;
    }

    // 이번달 근태 요약: NORMAL / LATE / EARLY_LEAVE / 결석(check_out null) 건수
    public Map<String, Integer> getMonthAttendanceSummary(String month) throws SQLException {
        Map<String, Integer> summary = new LinkedHashMap<>();
        String sql = "SELECT status, COUNT(*) cnt FROM attendance " +
                     "WHERE DATE_FORMAT(att_date, '%Y-%m') = ? GROUP BY status";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, month);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) summary.put(rs.getString("status"), rs.getInt("cnt"));
        }
        return summary;
    }

    // 부서별 인원수
    public List<Map<String, Object>> getDeptHeadcount() throws SQLException {
        List<Map<String, Object>> list = new ArrayList<>();
        String sql = "SELECT d.dept_name, COUNT(e.emp_id) cnt " +
                     "FROM department d LEFT JOIN employee e ON d.dept_id = e.dept_id AND e.status = 'ACTIVE' " +
                     "GROUP BY d.dept_id, d.dept_name ORDER BY cnt DESC";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                Map<String, Object> row = new LinkedHashMap<>();
                row.put("deptName", rs.getString("dept_name"));
                row.put("cnt",      rs.getInt("cnt"));
                list.add(row);
            }
        }
        return list;
    }
}
