package dao;

import model.MeetingMinutes;
import util.DBUtil;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class MeetingMinutesDAO {

    public void insert(MeetingMinutes m) throws SQLException {
        String sql = "INSERT INTO meeting_minutes (emp_id, meeting_date, title, attendees, content, ai_summary) VALUES (?,?,?,?,?,?)";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, m.getEmpId());
            ps.setDate(2, Date.valueOf(m.getMeetingDate()));
            ps.setString(3, m.getTitle());
            ps.setString(4, m.getAttendees());
            ps.setString(5, m.getContent());
            ps.setString(6, m.getAiSummary());
            ps.executeUpdate();
        }
    }

    public void updateSummary(int meetingId, String aiSummary) throws SQLException {
        String sql = "UPDATE meeting_minutes SET ai_summary=? WHERE meeting_id=?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, aiSummary);
            ps.setInt(2, meetingId);
            ps.executeUpdate();
        }
    }

    public List<MeetingMinutes> findByEmpId(int empId) throws SQLException {
        List<MeetingMinutes> list = new ArrayList<>();
        String sql = "SELECT m.*, e.name as emp_name FROM meeting_minutes m " +
                     "JOIN employee e ON m.emp_id = e.emp_id " +
                     "WHERE m.emp_id=? ORDER BY m.meeting_date DESC";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, empId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) list.add(mapRow(rs));
        }
        return list;
    }

    public List<MeetingMinutes> findAll() throws SQLException {
        List<MeetingMinutes> list = new ArrayList<>();
        String sql = "SELECT m.*, e.name as emp_name FROM meeting_minutes m " +
                     "JOIN employee e ON m.emp_id = e.emp_id ORDER BY m.meeting_date DESC";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ResultSet rs = ps.executeQuery();
            while (rs.next()) list.add(mapRow(rs));
        }
        return list;
    }

    public MeetingMinutes findById(int meetingId) throws SQLException {
        String sql = "SELECT m.*, e.name as emp_name FROM meeting_minutes m " +
                     "JOIN employee e ON m.emp_id = e.emp_id WHERE m.meeting_id=?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, meetingId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return mapRow(rs);
        }
        return null;
    }

    private MeetingMinutes mapRow(ResultSet rs) throws SQLException {
        MeetingMinutes m = new MeetingMinutes();
        m.setMeetingId(rs.getInt("meeting_id"));
        m.setEmpId(rs.getInt("emp_id"));
        m.setEmpName(rs.getString("emp_name"));
        m.setMeetingDate(rs.getDate("meeting_date").toLocalDate());
        m.setTitle(rs.getString("title"));
        m.setAttendees(rs.getString("attendees"));
        m.setContent(rs.getString("content"));
        m.setAiSummary(rs.getString("ai_summary"));
        return m;
    }
}
