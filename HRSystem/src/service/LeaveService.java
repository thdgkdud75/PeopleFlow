package service;

import dao.LeaveDAO;
import model.LeaveRequest;

import java.sql.SQLException;
import java.util.List;

public class LeaveService {
    private final LeaveDAO dao = new LeaveDAO();

    public void applyLeave(LeaveRequest lr) throws SQLException {
        dao.insert(lr);
    }

    public void approveLeave(int leaveId, int approverId) throws SQLException {
        dao.updateStatus(leaveId, "APPROVED", approverId);
    }

    public void rejectLeave(int leaveId, int approverId) throws SQLException {
        dao.updateStatus(leaveId, "REJECTED", approverId);
    }

    public List<LeaveRequest> getMyLeaves(int empId) throws SQLException {
        return dao.findByEmpId(empId);
    }

    public List<LeaveRequest> getPendingLeaves() throws SQLException {
        return dao.findPending();
    }

    public List<LeaveRequest> getAllLeaves() throws SQLException {
        return dao.findAll();
    }
}
