package service;

import dao.AttendanceDAO;
import model.Attendance;

import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

public class AttendanceService {
    private final AttendanceDAO dao = new AttendanceDAO();

    public void checkIn(int empId) throws SQLException {
        Attendance att = new Attendance();
        att.setEmpId(empId);
        att.setAttDate(LocalDate.now());
        att.setCheckIn(LocalTime.now());
        String status = LocalTime.now().isAfter(LocalTime.of(9, 0)) ? "LATE" : "NORMAL";
        att.setStatus(status);
        dao.insert(att);
    }

    public void checkOut(int empId) throws SQLException {
        dao.updateCheckOut(empId, LocalDate.now(), LocalTime.now());
    }

    public List<Attendance> getMyAttendance(int empId) throws SQLException {
        return dao.findByEmpId(empId);
    }

    public List<Attendance> getMonthlyAttendance(String yearMonth) throws SQLException {
        return dao.findByMonth(yearMonth);
    }
}
