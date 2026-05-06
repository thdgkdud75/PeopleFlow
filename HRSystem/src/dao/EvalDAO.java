package dao;

import model.Evaluation;
import util.DBUtil;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class EvalDAO {

    public int insert(Evaluation eval) throws SQLException {
        String sql = "INSERT INTO evaluation (emp_id, eval_period, score, grade, comments, ai_report, evaluator_id, eval_date) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?, NOW())";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, eval.getEmpId());
            ps.setString(2, eval.getEvalPeriod());
            ps.setInt(3, eval.getScore());
            ps.setString(4, eval.getGrade());
            ps.setString(5, eval.getComments());
            ps.setString(6, eval.getAiReport());
            ps.setInt(7, eval.getEvaluatorId());
            return ps.executeUpdate();
        }
    }

    public int updateAiReport(int evalId, String aiReport) throws SQLException {
        String sql = "UPDATE evaluation SET ai_report=? WHERE eval_id=?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, aiReport);
            ps.setInt(2, evalId);
            return ps.executeUpdate();
        }
    }

    public List<Evaluation> findByEmpId(int empId) throws SQLException {
        List<Evaluation> list = new ArrayList<>();
        String sql = "SELECT ev.*, e.name as emp_name FROM evaluation ev " +
                     "JOIN employee e ON ev.emp_id = e.emp_id " +
                     "WHERE ev.emp_id=? ORDER BY ev.eval_date DESC";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, empId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) list.add(mapRow(rs));
        }
        return list;
    }

    public List<Evaluation> findByPeriod(String period) throws SQLException {
        List<Evaluation> list = new ArrayList<>();
        String sql = "SELECT ev.*, e.name as emp_name FROM evaluation ev " +
                     "JOIN employee e ON ev.emp_id = e.emp_id " +
                     "WHERE ev.eval_period=? ORDER BY ev.score DESC";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, period);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) list.add(mapRow(rs));
        }
        return list;
    }

    public Evaluation findById(int evalId) throws SQLException {
        String sql = "SELECT ev.*, e.name as emp_name FROM evaluation ev " +
                     "JOIN employee e ON ev.emp_id = e.emp_id WHERE ev.eval_id=?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, evalId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return mapRow(rs);
        }
        return null;
    }

    private Evaluation mapRow(ResultSet rs) throws SQLException {
        Evaluation eval = new Evaluation();
        eval.setEvalId(rs.getInt("eval_id"));
        eval.setEmpId(rs.getInt("emp_id"));
        eval.setEmpName(rs.getString("emp_name"));
        eval.setEvalPeriod(rs.getString("eval_period"));
        eval.setScore(rs.getInt("score"));
        eval.setGrade(rs.getString("grade"));
        eval.setComments(rs.getString("comments"));
        eval.setAiReport(rs.getString("ai_report"));
        eval.setEvaluatorId(rs.getInt("evaluator_id"));
        Date evalDate = rs.getDate("eval_date");
        if (evalDate != null) eval.setEvalDate(evalDate.toLocalDate());
        return eval;
    }
}
