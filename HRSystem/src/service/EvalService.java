package service;

import dao.EvalDAO;
import model.Evaluation;

import java.sql.SQLException;
import java.util.List;

public class EvalService {
    private final EvalDAO dao = new EvalDAO();

    public void createEvaluation(Evaluation eval) throws SQLException {
        dao.insert(eval);
    }

    public void saveAiReport(int evalId, String aiReport) throws SQLException {
        dao.updateAiReport(evalId, aiReport);
    }

    public Evaluation getEvaluation(int evalId) throws SQLException {
        return dao.findById(evalId);
    }

    public List<Evaluation> getEvalsByEmployee(int empId) throws SQLException {
        return dao.findByEmpId(empId);
    }

    public List<Evaluation> getAllEvals() throws SQLException {
        return dao.findAll();
    }

    public List<Evaluation> getEvalsByPeriod(String period) throws SQLException {
        return dao.findByPeriod(period);
    }
}
