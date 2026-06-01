package dao;

import model.WorkLog;
import util.DBUtil;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class WorkLogDAO {

    public void insert(WorkLog w) throws SQLException {
        String sql = "INSERT INTO work_log (emp_id, log_date, content, ai_summary) VALUES (?,?,?,?)";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, w.getEmpId());
            ps.setDate(2, Date.valueOf(w.getLogDate()));
            ps.setString(3, w.getContent());
            ps.setString(4, w.getAiSummary());
            ps.executeUpdate();
        }
    }

    public void updateSummary(int logId, String aiSummary) throws SQLException {
        String sql = "UPDATE work_log SET ai_summary=? WHERE log_id=?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, aiSummary);
            ps.setInt(2, logId);
            ps.executeUpdate();
        }
    }

    public List<WorkLog> findByEmpId(int empId) throws SQLException {
        List<WorkLog> list = new ArrayList<>();
        String sql = "SELECT w.*, e.name as emp_name FROM work_log w " +
                     "JOIN employee e ON w.emp_id = e.emp_id " +
                     "WHERE w.emp_id=? ORDER BY w.log_date DESC";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, empId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) list.add(mapRow(rs));
        }
        return list;
    }

    public List<WorkLog> findAll() throws SQLException {
        List<WorkLog> list = new ArrayList<>();
        String sql = "SELECT w.*, e.name as emp_name FROM work_log w " +
                     "JOIN employee e ON w.emp_id = e.emp_id ORDER BY w.log_date DESC";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ResultSet rs = ps.executeQuery();
            while (rs.next()) list.add(mapRow(rs));
        }
        return list;
    }

    public WorkLog findById(int logId) throws SQLException {
        String sql = "SELECT w.*, e.name as emp_name FROM work_log w " +
                     "JOIN employee e ON w.emp_id = e.emp_id WHERE w.log_id=?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, logId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return mapRow(rs);
        }
        return null;
    }

    private WorkLog mapRow(ResultSet rs) throws SQLException {
        WorkLog w = new WorkLog();
        w.setLogId(rs.getInt("log_id"));
        w.setEmpId(rs.getInt("emp_id"));
        w.setEmpName(rs.getString("emp_name"));
        w.setLogDate(rs.getDate("log_date").toLocalDate());
        w.setContent(rs.getString("content"));
        w.setAiSummary(rs.getString("ai_summary"));
        return w;
    }
}
