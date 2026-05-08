package service;

import dao.LeaveDAO;
import model.LeaveRequest;

import java.sql.SQLException;
import java.util.List;

public class LeaveService {
    private final LeaveDAO dao = new LeaveDAO();

    public void applyLeave(LeaveRequest lr) throws SQLException {
        if ("ANNUAL".equals(lr.getLeaveType())) {
            int remain = dao.getRemainLeave(lr.getEmpId());
            if (remain < lr.getLeaveDays()) {
                throw new IllegalStateException("잔여 연차가 부족합니다. (잔여: " + remain + "일, 신청: " + lr.getLeaveDays() + "일)");
            }
        }
        dao.insert(lr);
    }

    public void approveLeave(int leaveId, int approverId) throws SQLException {
        LeaveRequest lr = dao.findById(leaveId);
        dao.updateStatus(leaveId, "APPROVED", approverId);
        if (lr != null && "ANNUAL".equals(lr.getLeaveType())) {
            dao.deductLeave(lr.getEmpId(), lr.getLeaveDays());
        }
    }

    public void rejectLeave(int leaveId, int approverId) throws SQLException {
        dao.updateStatus(leaveId, "REJECTED", approverId);
    }

    public int getRemainLeave(int empId) throws SQLException {
        return dao.getRemainLeave(empId);
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
