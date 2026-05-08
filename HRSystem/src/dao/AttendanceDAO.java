package dao;

import model.Attendance;
import util.DBUtil;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class AttendanceDAO {

    public int insert(Attendance att) throws SQLException {
        String sql = "INSERT INTO attendance (emp_id, att_date, check_in, status) VALUES (?, ?, ?, ?) " +
                     "ON DUPLICATE KEY UPDATE check_in = VALUES(check_in), status = VALUES(status)";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, att.getEmpId());
            ps.setDate(2, Date.valueOf(att.getAttDate()));
            ps.setTime(3, Time.valueOf(att.getCheckIn()));
            ps.setString(4, att.getStatus());
            return ps.executeUpdate();
        }
    }

    public int updateCheckOut(int empId, LocalDate date, java.time.LocalTime checkOut) throws SQLException {
        // LATE는 유지하고, 정시 이전 퇴근이면 EARLY_LEAVE로, 그 외엔 현재 상태 유지
        String sql = "UPDATE attendance SET check_out=?, " +
                     "status = CASE " +
                     "  WHEN ? < '18:00:00' AND status = 'LATE' THEN 'LATE' " +
                     "  WHEN ? < '18:00:00' THEN 'EARLY_LEAVE' " +
                     "  ELSE status " +
                     "END " +
                     "WHERE emp_id=? AND att_date=?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setTime(1, Time.valueOf(checkOut));
            ps.setTime(2, Time.valueOf(checkOut));
            ps.setTime(3, Time.valueOf(checkOut));
            ps.setInt(4, empId);
            ps.setDate(5, Date.valueOf(date));
            return ps.executeUpdate();
        }
    }

    public List<Attendance> findByEmpId(int empId) throws SQLException {
        List<Attendance> list = new ArrayList<>();
        String sql = "SELECT a.*, e.name as emp_name FROM attendance a " +
                     "JOIN employee e ON a.emp_id = e.emp_id " +
                     "WHERE a.emp_id=? ORDER BY a.att_date DESC";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, empId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) list.add(mapRow(rs));
        }
        return list;
    }

    public List<Attendance> findByMonth(String yearMonth) throws SQLException {
        List<Attendance> list = new ArrayList<>();
        String sql = "SELECT a.*, e.name as emp_name FROM attendance a " +
                     "JOIN employee e ON a.emp_id = e.emp_id " +
                     "WHERE DATE_FORMAT(a.att_date, '%Y-%m') = ? ORDER BY a.emp_id, a.att_date";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, yearMonth);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) list.add(mapRow(rs));
        }
        return list;
    }

    private Attendance mapRow(ResultSet rs) throws SQLException {
        Attendance att = new Attendance();
        att.setAttId(rs.getInt("att_id"));
        att.setEmpId(rs.getInt("emp_id"));
        att.setEmpName(rs.getString("emp_name"));
        att.setAttDate(rs.getDate("att_date").toLocalDate());
        Time checkIn = rs.getTime("check_in");
        if (checkIn != null) att.setCheckIn(checkIn.toLocalTime());
        Time checkOut = rs.getTime("check_out");
        if (checkOut != null) att.setCheckOut(checkOut.toLocalTime());
        att.setStatus(rs.getString("status"));
        return att;
    }
}
