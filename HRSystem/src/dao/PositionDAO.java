package dao;

import model.Position;
import util.DBUtil;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PositionDAO {

    public List<Position> findAll() throws SQLException {
        List<Position> list = new ArrayList<>();
        String sql = "SELECT p.*, " +
                     "(SELECT COUNT(*) FROM employee e WHERE e.pos_id = p.pos_id AND e.status != 'RESIGNED') AS emp_count " +
                     "FROM `position` p ORDER BY p.level";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) list.add(mapRow(rs));
        }
        return list;
    }

    public Position findById(int posId) throws SQLException {
        String sql = "SELECT * FROM `position` WHERE pos_id=?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, posId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return mapRow(rs);
        }
        return null;
    }

    public int insert(Position pos) throws SQLException {
        String sql = "INSERT INTO `position` (pos_name, level, base_salary) VALUES (?, ?, ?)";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, pos.getPosName());
            ps.setInt(2, pos.getLevel());
            ps.setLong(3, pos.getBaseSalary());
            return ps.executeUpdate();
        }
    }

    public int update(Position pos) throws SQLException {
        String sql = "UPDATE `position` SET pos_name=?, level=?, base_salary=? WHERE pos_id=?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, pos.getPosName());
            ps.setInt(2, pos.getLevel());
            ps.setLong(3, pos.getBaseSalary());
            ps.setInt(4, pos.getPosId());
            return ps.executeUpdate();
        }
    }

    public int delete(int posId) throws SQLException {
        String sql = "DELETE FROM `position` WHERE pos_id=?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, posId);
            return ps.executeUpdate();
        }
    }

    public int countEmployees(int posId) throws SQLException {
        String sql = "SELECT COUNT(*) FROM employee WHERE pos_id=? AND status != 'RESIGNED'";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, posId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return rs.getInt(1);
        }
        return 0;
    }

    private Position mapRow(ResultSet rs) throws SQLException {
        Position pos = new Position();
        pos.setPosId(rs.getInt("pos_id"));
        pos.setPosName(rs.getString("pos_name"));
        pos.setLevel(rs.getInt("level"));
        pos.setBaseSalary(rs.getLong("base_salary"));
        try { pos.setEmpCount(rs.getInt("emp_count")); } catch (Exception ignored) {}
        return pos;
    }
}
