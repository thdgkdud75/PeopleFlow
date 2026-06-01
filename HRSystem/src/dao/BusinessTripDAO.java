package dao;

import model.BusinessTrip;
import util.DBUtil;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class BusinessTripDAO {

    public void insert(BusinessTrip t) throws SQLException {
        String sql = "INSERT INTO business_trip (emp_id, trip_start, trip_end, destination, purpose, content, ai_summary) VALUES (?,?,?,?,?,?,?)";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, t.getEmpId());
            ps.setDate(2, Date.valueOf(t.getTripStart()));
            ps.setDate(3, Date.valueOf(t.getTripEnd()));
            ps.setString(4, t.getDestination());
            ps.setString(5, t.getPurpose());
            ps.setString(6, t.getContent());
            ps.setString(7, t.getAiSummary());
            ps.executeUpdate();
        }
    }

    public void updateSummary(int tripId, String aiSummary) throws SQLException {
        String sql = "UPDATE business_trip SET ai_summary=? WHERE trip_id=?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, aiSummary);
            ps.setInt(2, tripId);
            ps.executeUpdate();
        }
    }

    public List<BusinessTrip> findByEmpId(int empId) throws SQLException {
        List<BusinessTrip> list = new ArrayList<>();
        String sql = "SELECT t.*, e.name as emp_name FROM business_trip t " +
                     "JOIN employee e ON t.emp_id = e.emp_id " +
                     "WHERE t.emp_id=? ORDER BY t.trip_start DESC";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, empId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) list.add(mapRow(rs));
        }
        return list;
    }

    public List<BusinessTrip> findAll() throws SQLException {
        List<BusinessTrip> list = new ArrayList<>();
        String sql = "SELECT t.*, e.name as emp_name FROM business_trip t " +
                     "JOIN employee e ON t.emp_id = e.emp_id ORDER BY t.trip_start DESC";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ResultSet rs = ps.executeQuery();
            while (rs.next()) list.add(mapRow(rs));
        }
        return list;
    }

    public BusinessTrip findById(int tripId) throws SQLException {
        String sql = "SELECT t.*, e.name as emp_name FROM business_trip t " +
                     "JOIN employee e ON t.emp_id = e.emp_id WHERE t.trip_id=?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, tripId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return mapRow(rs);
        }
        return null;
    }

    private BusinessTrip mapRow(ResultSet rs) throws SQLException {
        BusinessTrip t = new BusinessTrip();
        t.setTripId(rs.getInt("trip_id"));
        t.setEmpId(rs.getInt("emp_id"));
        t.setEmpName(rs.getString("emp_name"));
        t.setTripStart(rs.getDate("trip_start").toLocalDate());
        t.setTripEnd(rs.getDate("trip_end").toLocalDate());
        t.setDestination(rs.getString("destination"));
        t.setPurpose(rs.getString("purpose"));
        t.setContent(rs.getString("content"));
        t.setAiSummary(rs.getString("ai_summary"));
        return t;
    }
}
